package org.pokesplash.gts.history;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;

public class ItemHistoryItem extends HistoryItem<ItemStack> {
   private JsonElement item;

   public ItemHistoryItem(ItemListing listing, String buyerName) {
      super(listing.isPokemon(), listing.getSellerUuid(), listing.getSellerName(), listing.getPrice(), buyerName);
      this.currency = listing.getCurrency();
      this.item = (JsonElement)ItemStack.CODEC.encodeStart(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), listing.getListing()).getOrThrow();
   }

   public ItemStack getListing() {
      try {
         return (ItemStack)((Pair)ItemStack.CODEC.decode(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), this.item).getOrThrow()).getFirst();
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
   public MutableText getDisplayName() {
      Style dark_aqua = Style.EMPTY.withItalic(false).withColor((TextColor)TextColor.parse("dark_aqua").getOrThrow());
      return Text.empty().setStyle(dark_aqua).append(this.getListing().getName());
   }
}
