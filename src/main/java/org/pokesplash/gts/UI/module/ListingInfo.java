package org.pokesplash.gts.UI.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.item.Item.TooltipContext;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public abstract class ListingInfo {
   public static List<Text> parse(Listing listing) {
      List<Text> lore = new ArrayList<>();
      Style base = Style.EMPTY.withItalic(false);
      lore.add(ColorUtil.parse(Gts.language.getSeller() + listing.getSellerName()));
      lore.add(ColorUtil.parse(Gts.language.getPrice() + Gts.config.getMultiCurrency().colored(listing.getCurrency(), listing.getPrice())));
      if (listing.getEndTime() != -1L && listing.getEndTime() > new Date().getTime()) {
         lore.add(ColorUtil.parse(Gts.language.getRemainingTime() + Utils.parseLongDate(listing.getEndTime() - new Date().getTime())));
      }

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
}
