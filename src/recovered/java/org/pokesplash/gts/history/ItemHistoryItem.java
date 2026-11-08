package org.pokesplash.gts.history;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;

public class ItemHistoryItem extends HistoryItem<class_1799> {
   private JsonElement item;

   public ItemHistoryItem(ItemListing listing, String buyerName) {
      super(listing.isPokemon(), listing.getSellerUuid(), listing.getSellerName(), listing.getPrice(), buyerName);
      this.currency = listing.getCurrency();
      this.item = (JsonElement)class_1799.field_24671.encodeStart(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), listing.getListing()).getOrThrow();
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
   public boolean isHistoryItemValid() {
      return this.item != null && this.item.isJsonObject();
   }

   @Override
   public class_5250 getDisplayName() {
      class_2583 dark_aqua = class_2583.field_24360.method_10978(false).method_27703((class_5251)class_5251.method_27719("dark_aqua").getOrThrow());
      return class_2561.method_43473().method_10862(dark_aqua).method_10852(this.getListing().method_7964());
   }
}
