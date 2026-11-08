package org.pokesplash.gts.economy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public final class TransactionJournal {
   private final Path directory;
   private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private final Set<UUID> uncertain = new HashSet<>();
   private String session;
   private FileChannel lockChannel;
   private FileLock lock;

   public TransactionJournal(Path directory) {
      this.directory = directory;
   }

   public synchronized int beginSession() throws IOException {
      if (this.session != null) {
         throw new IllegalStateException("Session already active");
      } else {
         Files.createDirectories(this.directory);
         this.lockChannel = FileChannel.open(this.directory.resolve("_server.lock"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

         try {
            this.lock = this.lockChannel.tryLock();
            if (this.lock == null) {
               throw new IOException("GTS journal already in use");
            } else {
               Path marker = this.directory.resolve("_session.json");
               TransactionJournal.Session previous = Files.exists(marker)
                  ? (TransactionJournal.Session)this.gson.fromJson(Files.readString(marker), TransactionJournal.Session.class)
                  : null;
               if (!Files.exists(marker) || previous != null && previous.id != null && !previous.id.isBlank()) {
                  int held = 0;
                  if (previous != null && !previous.clean) {
                     try (DirectoryStream<Path> files = Files.newDirectoryStream(this.directory, "*.json")) {
                        Iterator var5 = files.iterator();

                        label86:
                        while (true) {
                           TransactionJournal.Record record;
                           while (true) {
                              if (!var5.hasNext()) {
                                 break label86;
                              }

                              Path path = (Path)var5.next();
                              if (!path.getFileName().toString().startsWith("_")) {
                                 try {
                                    record = (TransactionJournal.Record)this.gson.fromJson(Files.readString(path), TransactionJournal.Record.class);
                                    break;
                                 } catch (RuntimeException var10) {
                                 }
                              }
                           }

                           if (record != null
                              && previous.id != null
                              && previous.id.equals(record.session)
                              && ("CREATED".equals(record.phase) || "SOLD".equals(record.phase) || "RETURNED".equals(record.phase))) {
                              record.phaseBeforeRecovery = record.phase;
                              this.phase(record, "RECOVERY_REQUIRED");
                              held++;
                           }
                        }
                     }
                  }

                  this.session = UUID.randomUUID().toString();
                  AtomicFiles.write(marker, this.gson.toJson(new TransactionJournal.Session(this.session, false)));
                  return held;
               } else {
                  throw new IOException("Invalid GTS session marker");
               }
            }
         } catch (Exception var12) {
            if (this.lock != null) {
               this.lock.close();
            }

            this.lockChannel.close();
            this.lock = null;
            this.lockChannel = null;
            this.session = null;
            if (var12 instanceof IOException io) {
               throw io;
            } else {
               throw new IOException("Cannot initialize GTS journal", var12);
            }
         }
      }
   }

   public synchronized void endSession() throws IOException {
      if (this.session != null) {
         try {
            AtomicFiles.write(this.directory.resolve("_session.json"), this.gson.toJson(new TransactionJournal.Session(this.session, true)));
         } finally {
            this.session = null;
            if (this.lock != null) {
               this.lock.close();
            }

            if (this.lockChannel != null) {
               this.lockChannel.close();
            }

            this.lock = null;
            this.lockChannel = null;
         }
      }
   }

   public synchronized boolean blocked(UUID id) {
      if (this.uncertain.contains(id)) {
         return true;
      } else {
         Path path = this.directory.resolve(id + ".json");
         if (Files.notExists(path)) {
            return false;
         } else {
            try {
               TransactionJournal.Record record = (TransactionJournal.Record)this.gson.fromJson(Files.readString(path), TransactionJournal.Record.class);
               return record == null || !id.equals(record.listingId) || !"ABORTED".equals(record.phase) && !"CREATED".equals(record.phase);
            } catch (Exception var4) {
               return true;
            }
         }
      }
   }

   public synchronized void phase(TransactionJournal.Record record, String phase) throws IOException {
      this.uncertain.add(record.listingId);
      if (record.session == null) {
         record.session = this.session;
      }

      record.phase = phase;
      record.updatedAt = System.currentTimeMillis();
      AtomicFiles.write(this.directory.resolve(record.listingId + ".json"), this.gson.toJson(record));
      if ("ABORTED".equals(phase) || "CREATED".equals(phase)) {
         this.uncertain.remove(record.listingId);
      }
   }

   public static final class Record {
      public UUID listingId;
      public UUID buyer;
      public UUID seller;
      public String currency;
      public BigDecimal price;
      public BigDecimal proceeds;
      public String listingJson;
      public String buyerName;
      public String phase;
      public String session;
      public String phaseBeforeRecovery;
      public long updatedAt;

      public Record(UUID id, UUID buyer, UUID seller, String currency, BigDecimal price, BigDecimal proceeds, String listingJson, String buyerName) {
         this.listingId = id;
         this.buyer = buyer;
         this.seller = seller;
         this.currency = currency;
         this.price = price;
         this.proceeds = proceeds;
         this.listingJson = listingJson;
         this.buyerName = buyerName;
      }
   }

   private static final class Session {
      String id;
      boolean clean;

      Session(String id, boolean clean) {
         this.id = id;
         this.clean = clean;
      }
   }
}
