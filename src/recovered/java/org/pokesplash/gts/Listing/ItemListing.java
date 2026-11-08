package org.pokesplash.gts.Listing;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.pokesplash.gts.Gts;

public class ItemListing extends Listing<class_1799> {
   private final JsonElement item;

   public ItemListing(UUID sellerUuid, String sellerName, double price, class_1799 item) {
      this(sellerUuid, sellerName, price, item, Gts.config.getMultiCurrency().defaultCurrency);
   }

   public ItemListing(UUID sellerUuid, String sellerName, double price, class_1799 item, String currency) {
      super(sellerUuid, sellerName, price, false, currency);
      this.item = (JsonElement)class_1799.field_24671.encodeStart(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), item).getOrThrow();
   }

   public ItemListing(ItemListing other) {
      super(UUID.fromString(other.getSellerUuid().toString()), String.copyValueOf(other.getSellerName().toCharArray()), other.getPrice(), false);
      this.item = (JsonElement)class_1799.field_24671.encodeStart(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), other.getListing()).getOrThrow();
      super.id = UUID.fromString(other.getId().toString());
      super.version = String.copyValueOf(other.getVersion().toCharArray());
      super.setEndTime(other.getEndTime());
      super.currency = other.getCurrency();
   }

   public class_1799 getListing() {
      try {
         return (class_1799)((Pair)class_1799.field_24671.decode(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), this.item).getOrThrow()).getFirst();
      } catch (IllegalStateException var2) {
         Gts.LOGGER.fatal("Failed to parse item for " + this.item);
         Gts.LOGGER.fatal("Stacktrace: ");
         var2.printStackTrace();
         return null;
      }
   }

   @Override
   public boolean isListingValid() {
      return this.item != null && this.item.isJsonObject();
   }

   @Override
   public Listing deepClone() {
      return new ItemListing(this);
   }

   @Override
   public String getListingName() {
      return this.getListing().method_7954().getString().replaceAll("\\[", "").replaceAll("\\]", "");
   }

   @Override
   public class_5250 getDisplayName() {
      class_2583 dark_aqua = class_2583.field_24360.method_10978(false).method_27703((class_5251)class_5251.method_27719("dark_aqua").getOrThrow());
      return class_2561.method_43473().method_10862(dark_aqua).method_10852(this.getListing().method_7964());
   }

   @Override
   public String getUiTitle() {
      return Gts.language.getItemTitle();
   }

   @Override
   public class_1799 getIcon() {
      return this.getListing();
   }
}
