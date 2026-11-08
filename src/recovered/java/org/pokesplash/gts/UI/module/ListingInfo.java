package org.pokesplash.gts.UI.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_1792.class_9635;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public abstract class ListingInfo {
   public static List<class_2561> parse(Listing listing) {
      List<class_2561> lore = new ArrayList<>();
      class_2583 base = class_2583.field_24360.method_10978(false);
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
            if (itemListing.getListing().method_7909().method_7876().contains(blockedItemId)) {
               isItemBlocked = true;
               break;
            }
         }

         if (!isItemBlocked) {
            try {
               List<class_2561> itemTooltips = itemListing.getListing().method_7950(class_9635.field_51353, null, class_1836.field_41070);
               lore.addAll(itemTooltips.subList(1, itemTooltips.size()));
            } catch (Exception var8) {
            }
         }
      }

      return lore;
   }
}
