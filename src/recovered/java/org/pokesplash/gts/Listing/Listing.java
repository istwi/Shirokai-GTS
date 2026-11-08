package org.pokesplash.gts.Listing;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.class_1799;
import net.minecraft.class_5250;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.api.provider.ListingAPI;
import org.pokesplash.gts.economy.AtomicFiles;
import org.pokesplash.gts.economy.CurrencySettings;
import org.pokesplash.gts.util.Utils;

public abstract class Listing<T> {
   protected String version = "2.0";
   protected UUID id;
   private UUID sellerUuid;
   private String sellerName;
   private double price;
   protected String currency;
   private long endTime;
   private boolean isPokemon;

   public String getCurrency() {
      return CurrencySettings.legacy(this.currency);
   }

   public Listing(UUID sellerUuid, String sellerName, double price, boolean isPokemon) {
      this(sellerUuid, sellerName, price, isPokemon, Gts.config == null ? "impactor:dollars" : Gts.config.getMultiCurrency().defaultCurrency);
   }

   protected Listing(UUID sellerUuid, String sellerName, double price, boolean isPokemon, String currency) {
      this.currency = Objects.requireNonNull(currency);
      this.id = UUID.randomUUID();
      this.sellerUuid = sellerUuid;
      this.sellerName = sellerName;
      this.price = price;
      if (Gts.isDebugMode) {
         this.endTime = new Date().getTime() + 1000L * Gts.debugTime;
      } else if (Gts.config.getListingDuration() <= 0) {
         this.endTime = -1L;
      } else {
         this.endTime = new Date().getTime() + Gts.config.getListingDuration() * 3600000L;
      }

      this.isPokemon = isPokemon;
   }

   public boolean isPokemon() {
      return this.isPokemon;
   }

   public String getVersion() {
      return this.version;
   }

   public UUID getId() {
      return this.id;
   }

   public UUID getSellerUuid() {
      return this.sellerUuid;
   }

   public String getSellerName() {
      return this.sellerName;
   }

   public double getPrice() {
      return this.price;
   }

   public String getPriceAsString() {
      return Gts.config.getMultiCurrency().format(this.getCurrency(), this.price);
   }

   public long getEndTime() {
      return this.endTime;
   }

   public void setEndTime(long endTime) {
      this.endTime = endTime;
   }

   public void renewEndTime() {
      if (Gts.isDebugMode) {
         this.endTime = new Date().getTime() + 1000L * Gts.debugTime;
      } else if (Gts.config.getListingDuration() <= 0) {
         this.endTime = -1L;
      } else {
         this.endTime = new Date().getTime() + Gts.config.getListingDuration() * 3600000L;
      }
   }

   public abstract T getListing();

   public abstract boolean isListingValid();

   public boolean write(String filePath) {
      if (ListingAPI.getHighestPriority() != null) {
         ListingAPI.getHighestPriority().write(this);
         return true;
      } else {
         Gson gson = Utils.newGson();
         String data = gson.toJson(this);

         try {
            AtomicFiles.write(Path.of(new File("").getAbsolutePath() + filePath, this.getId() + ".json"), data);
            return true;
         } catch (IOException var5) {
            Gts.LOGGER.error("Failed to persist listing " + this.getId());
            return false;
         }
      }
   }

   public boolean delete(String filePath) {
      if (ListingAPI.getHighestPriority() != null) {
         ListingAPI.getHighestPriority().delete(this);
         return true;
      } else {
         return Utils.deleteFile(filePath, this.getId() + ".json");
      }
   }

   public void update(boolean isPokemon) {
      this.version = "2.0";
      this.isPokemon = isPokemon;
   }

   public abstract Listing deepClone();

   public abstract String getListingName();

   @Override
   public boolean equals(Object o) {
      return o instanceof Listing ? ((Listing)o).getId().equals(this.getId()) : false;
   }

   public abstract class_5250 getDisplayName();

   public abstract String getUiTitle();

   public abstract class_1799 getIcon();
}
