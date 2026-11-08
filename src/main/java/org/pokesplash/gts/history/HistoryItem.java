package org.pokesplash.gts.history;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;
import net.minecraft.text.MutableText;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.api.provider.HistoryAPI;
import org.pokesplash.gts.economy.AtomicFiles;
import org.pokesplash.gts.economy.CurrencySettings;
import org.pokesplash.gts.util.Utils;

public abstract class HistoryItem<T> {
   private String version = "2.0";
   private boolean isPokemon;
   private UUID id;
   private UUID sellerUuid;
   private String sellerName;
   private double price;
   protected String currency;
   private long soldDate;
   private String buyerName;

   public String getCurrency() {
      return CurrencySettings.legacy(this.currency);
   }

   public HistoryItem(boolean isPokemon, UUID sellerUuid, String sellerName, double price, String buyerName) {
      this.isPokemon = isPokemon;
      this.id = UUID.randomUUID();
      this.sellerUuid = sellerUuid;
      this.sellerName = sellerName;
      this.price = price;
      this.soldDate = new Date().getTime();
      this.buyerName = buyerName;
   }

   public boolean isPokemon() {
      return this.isPokemon;
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

   public long getSoldDate() {
      return this.soldDate;
   }

   public String getBuyerName() {
      return this.buyerName;
   }

   public abstract T getListing();

   public String getVersion() {
      return this.version;
   }

   public abstract boolean isHistoryItemValid();

   public void write() {
      if (HistoryAPI.getHighestPriority() != null) {
         HistoryAPI.getHighestPriority().write(this);
      } else {
         try {
            AtomicFiles.write(
               Path.of(new File("").getAbsolutePath() + "/config/gts/history/" + this.sellerUuid + "/", this.id + ".json"), Utils.newGson().toJson(this)
            );
         } catch (IOException var2) {
            throw new IllegalStateException("History persistence failed", var2);
         }
      }
   }

   public abstract MutableText getDisplayName();
}
