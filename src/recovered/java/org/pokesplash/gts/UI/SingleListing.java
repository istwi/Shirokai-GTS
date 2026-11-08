package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public class SingleListing {
   public Page getPage(class_3222 viewer, Listing listing) {
      List<class_2561> lore = ListingInfo.parse(listing);
      if (listing.isPokemon()) {
         lore.addAll(PokemonInfo.parse((PokemonListing)listing));
      }

      Button pokemon = GooeyButton.builder()
         .display(listing.getIcon())
         .with(class_9334.field_49631, listing.getDisplayName())
         .with(class_9334.field_49632, new class_9290(lore))
         .build();
      Button purchase = GooeyButton.builder()
         .display(Gts.language.getPurchaseButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getConfirmPurchaseButtonLabel()))
         .onClick(
            action -> {
               try {
                  boolean success = GtsAPI.sale(listing.getSellerUuid(), action.getPlayer(), listing);
                  if (!success) {
                     action.getPlayer().method_43496(ColorUtil.parse(Gts.language.getMultiCurrency().economyError));
                     return;
                  }

                  action.getPlayer()
                     .method_43496(
                        ColorUtil.parse(GtsTransactions.message(Gts.language.getMultiCurrency().bought, listing, action.getPlayer().method_5477().getString()))
                     );
                  class_3222 seller = action.getPlayer().method_5682().method_3760().method_14602(listing.getSellerUuid());
                  if (seller != null) {
                     seller.method_43496(
                        ColorUtil.parse(GtsTransactions.message(Gts.language.getMultiCurrency().sold, listing, action.getPlayer().method_5477().getString()))
                     );
                  }
               } catch (Exception var4x) {
                  action.getPlayer()
                     .method_43496(ColorUtil.parse(var4x.getMessage() == null ? Gts.language.getMultiCurrency().economyError : var4x.getMessage()));
               }

               UIManager.closeUI(action.getPlayer());
            }
         )
         .build();
      Button cancel = GooeyButton.builder()
         .display(Gts.language.getCancelButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getCancelPurchaseButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page page = new AllListings().getPage();
            UIManager.openUIForcefully(sender, page);
         })
         .build();
      Button removeListing = GooeyButton.builder()
         .display(Gts.language.getRemoveListingButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getRemoveListingButtonLabel()))
         .onClick(
            action -> {
               boolean success;
               if (action.getPlayer().method_5667().equals(listing.getSellerUuid())) {
                  success = GtsAPI.cancelAndReturnListing(action.getPlayer(), listing);
               } else {
                  success = Gts.permissions.hasPermission(action.getPlayer(), "remove") && GtsAPI.cancelListing(listing);
               }

               if (!success) {
                  action.getPlayer().method_43496(ColorUtil.parse(Gts.language.getMultiCurrency().unavailable));
                  UIManager.closeUI(action.getPlayer());
               } else {
                  class_2561 message = ColorUtil.parse(
                     Utils.formatPlaceholders(
                        Gts.language.getCancelListing(), 0.0, listing.getListingName(), listing.getSellerName(), action.getPlayer().method_5477().getString()
                     )
                  );
                  action.getPlayer().method_43496(message);
                  class_3222 seller = action.getPlayer().method_5682().method_3760().method_14602(listing.getSellerUuid());
                  if (seller != null && !seller.method_5667().equals(action.getPlayer().method_5667())) {
                     seller.method_43496(message);
                  }

                  UIManager.closeUI(action.getPlayer());
               }
            }
         )
         .build();
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton()).set(13, pokemon).set(15, cancel);
      if (viewer.method_5667().equals(listing.getSellerUuid())) {
         template.set(11, removeListing);
      } else {
         template.set(11, purchase);
      }

      if (Gts.permissions.hasPermission(viewer, "remove") && !viewer.method_5667().equals(listing.getSellerUuid())) {
         template.set(22, removeListing);
      }

      return GooeyPage.builder().template(template.build()).title(listing.getUiTitle()).build();
   }
}
