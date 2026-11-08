package org.pokesplash.gts.economy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public final class AtomicFiles {
   private AtomicFiles() {
   }

   public static void write(Path path, String contents) throws IOException {
      Path target = path.toAbsolutePath().normalize();
      if (Files.isDirectory(target)) {
         throw new FileSystemException(target.toString(), null, "Atomic write requires a file, not a directory");
      } else {
         Path parent = target.getParent();
         Files.createDirectories(parent);
         Path temp = Files.createTempFile(parent, ".gts-", ".tmp");

         try {
            try (FileChannel channel = FileChannel.open(temp, StandardOpenOption.WRITE)) {
               ByteBuffer data = StandardCharsets.UTF_8.encode(contents);

               while (data.hasRemaining()) {
                  channel.write(data);
               }

               channel.force(true);
            }

            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            if (!"\\".equals(parent.getFileSystem().getSeparator())) {
               try (FileChannel directory = FileChannel.open(parent, StandardOpenOption.READ)) {
                  directory.force(true);
               }
            }
         } finally {
            Files.deleteIfExists(temp);
         }
      }
   }
}
