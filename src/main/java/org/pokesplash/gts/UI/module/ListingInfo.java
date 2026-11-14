package org.pokesplash.gts.UI.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.item.Item.TooltipContext;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public abstract class ListingInfo {
   public static List<Text> parse(Listing listing) {
      List<Text> lore = new ArrayList<>();
      boolean pokemon = listing instanceof PokemonListing;
      String price = Gts.config.getMultiCurrency().colored(listing.getCurrency(), listing.getPrice());
      if (pokemon) {
         lore.addAll(PokemonInfo.parse((PokemonListing) listing));
         lore.add(priceLine(ColorUtil.parse(Gts.language.getPrice()), ColorUtil.parse(price)));
         lore.add(tradeLine(Gts.language.getSeller(), listing.getSellerName(), net.minecraft.util.Formatting.WHITE));
      } else {
         lore.add(ColorUtil.parse(Gts.language.getSeller() + listing.getSellerName()));
         lore.add(ColorUtil.parse(Gts.language.getPrice() + price));
      }
      long remaining = listing.getEndTime() - System.currentTimeMillis();
      if (listing.getEndTime() != -1L && remaining > 0) {
         lore.add(pokemon
            ? tradeLine(Gts.language.getRemainingTime(), compactDuration(remaining), net.minecraft.util.Formatting.WHITE)
            : ColorUtil.parse(Gts.language.getRemainingTime() + Utils.parseLongDate(remaining)));
      }
      if (pokemon) lore.add(PokemonInfo.separator());

      if (!listing.isPokemon()) {
         ItemListing itemListing = (ItemListing)listing;
         List<String> blockedItemDescriptions = Gts.config.getRemovedModDescriptions();
         boolean isItemBlocked = false;

         for (String blockedItemId : blockedItemDescriptions) {
            if (itemListing.getListing().getItem().getTranslationKey().contains(blockedItemId)) {
               isItemBlocked = true;
               break;
            }
         }

         if (!isItemBlocked) {
            try {
               List<Text> itemTooltips = itemListing.getListing().getTooltip(TooltipContext.DEFAULT, null, TooltipType.BASIC);
               lore.addAll(itemTooltips.subList(1, itemTooltips.size()));
            } catch (Exception var8) {
            }
         }
      }

      return lore;
   }

   // Preserve the configured currency colors instead of flattening all prices to green.
   static Text priceLine(Text label, Text value) {
      return Text.literal(label.getString())
         .styled(style -> style.withColor(net.minecraft.util.Formatting.GRAY).withItalic(false))
         .append(value.copy());
   }

   private static Text tradeLine(String label, String value, net.minecraft.util.Formatting color) {
      return Text.literal(ColorUtil.parse(label).getString())
         .styled(style -> style.withColor(net.minecraft.util.Formatting.GRAY).withItalic(false))
         .append(Text.literal(value).formatted(color));
   }

   static String compactDuration(long millis) {
      long seconds = Math.max(0, millis / 1000);
      long days = seconds / 86400, hours = seconds / 3600 % 24, minutes = seconds / 60 % 60;
      if (days > 0) return days + "d " + hours + "h " + minutes + "m";
      if (hours > 0) return hours + "h " + minutes + "m";
      return minutes > 0 ? minutes + "m" : seconds + "s";
   }
}
