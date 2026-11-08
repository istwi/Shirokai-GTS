package org.pokesplash.gts.Listing;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import org.pokesplash.gts.Gts;

public class ItemListing extends Listing<ItemStack> {
   private final JsonElement item;

   public ItemListing(UUID sellerUuid, String sellerName, double price, ItemStack item) {
      this(sellerUuid, sellerName, price, item, Gts.config.getMultiCurrency().defaultCurrency);
   }

   public ItemListing(UUID sellerUuid, String sellerName, double price, ItemStack item, String currency) {
      super(sellerUuid, sellerName, price, false, currency);
      this.item = (JsonElement)ItemStack.CODEC.encodeStart(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), item).getOrThrow();
   }

   public ItemListing(ItemListing other) {
      super(UUID.fromString(other.getSellerUuid().toString()), String.copyValueOf(other.getSellerName().toCharArray()), other.getPrice(), false);
      this.item = (JsonElement)ItemStack.CODEC.encodeStart(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), other.getListing()).getOrThrow();
      super.id = UUID.fromString(other.getId().toString());
      super.version = String.copyValueOf(other.getVersion().toCharArray());
      super.setEndTime(other.getEndTime());
      super.currency = other.getCurrency();
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
   public boolean isListingValid() {
      return this.item != null && this.item.isJsonObject();
   }

   @Override
   public Listing deepClone() {
      return new ItemListing(this);
   }

   @Override
   public String getListingName() {
      return this.getListing().toHoverableText().getString().replaceAll("\\[", "").replaceAll("\\]", "");
   }

   @Override
   public MutableText getDisplayName() {
      Style dark_aqua = Style.EMPTY.withItalic(false).withColor((TextColor)TextColor.parse("dark_aqua").getOrThrow());
      return Text.empty().setStyle(dark_aqua).append(this.getListing().getName());
   }

   @Override
   public String getUiTitle() {
      return Gts.language.getItemTitle();
   }

   @Override
   public ItemStack getIcon() {
      return this.getListing();
   }
}
