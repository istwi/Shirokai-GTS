package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.DataComponentTypes;
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
   public Page getPage(ServerPlayerEntity viewer, Listing listing) {
      List<Text> lore = ListingInfo.parse(listing);
      if (listing.isPokemon()) {
         lore.addAll(PokemonInfo.parse((PokemonListing)listing));
      }

      Button pokemon = GooeyButton.builder()
         .display(listing.getIcon())
         .with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
         .with(DataComponentTypes.LORE, new LoreComponent(lore))
         .build();
      Button purchase = GooeyButton.builder()
         .display(Gts.language.getPurchaseButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getConfirmPurchaseButtonLabel()))
         .onClick(
            action -> {
               try {
                  boolean success = GtsAPI.sale(listing.getSellerUuid(), action.getPlayer(), listing);
                  if (!success) {
                     action.getPlayer().sendMessage(ColorUtil.parse(Gts.language.getMultiCurrency().economyError));
                     return;
                  }

                  action.getPlayer()
                     .sendMessage(
                        ColorUtil.parse(GtsTransactions.message(Gts.language.getMultiCurrency().bought, listing, action.getPlayer().getName().getString()))
                     );
                  ServerPlayerEntity seller = action.getPlayer().getServer().getPlayerManager().getPlayer(listing.getSellerUuid());
                  if (seller != null) {
                     seller.sendMessage(
                        ColorUtil.parse(GtsTransactions.message(Gts.language.getMultiCurrency().sold, listing, action.getPlayer().getName().getString()))
                     );
                  }
               } catch (Exception var4x) {
                  action.getPlayer()
                     .sendMessage(ColorUtil.parse(var4x.getMessage() == null ? Gts.language.getMultiCurrency().economyError : var4x.getMessage()));
               }

               UIManager.closeUI(action.getPlayer());
            }
         )
         .build();
      Button cancel = GooeyButton.builder()
         .display(Gts.language.getCancelButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getCancelPurchaseButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page page = new AllListings().getPage();
            UIManager.openUIForcefully(sender, page);
         })
         .build();
      Button removeListing = GooeyButton.builder()
         .display(Gts.language.getRemoveListingButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getRemoveListingButtonLabel()))
         .onClick(
            action -> {
               boolean success;
               if (action.getPlayer().getUuid().equals(listing.getSellerUuid())) {
                  success = GtsAPI.cancelAndReturnListing(action.getPlayer(), listing);
               } else {
                  success = Gts.permissions.hasPermission(action.getPlayer(), "remove") && GtsAPI.cancelListing(listing);
               }

               if (!success) {
                  action.getPlayer().sendMessage(ColorUtil.parse(Gts.language.getMultiCurrency().unavailable));
                  UIManager.closeUI(action.getPlayer());
               } else {
                  Text message = ColorUtil.parse(
                     Utils.formatPlaceholders(
                        Gts.language.getCancelListing(), 0.0, listing.getListingName(), listing.getSellerName(), action.getPlayer().getName().getString()
                     )
                  );
                  action.getPlayer().sendMessage(message);
                  ServerPlayerEntity seller = action.getPlayer().getServer().getPlayerManager().getPlayer(listing.getSellerUuid());
                  if (seller != null && !seller.getUuid().equals(action.getPlayer().getUuid())) {
                     seller.sendMessage(message);
                  }

                  UIManager.closeUI(action.getPlayer());
               }
            }
         )
         .build();
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton()).set(13, pokemon).set(15, cancel);
      if (viewer.getUuid().equals(listing.getSellerUuid())) {
         template.set(11, removeListing);
      } else {
         template.set(11, purchase);
      }

      if (Gts.permissions.hasPermission(viewer, "remove") && !viewer.getUuid().equals(listing.getSellerUuid())) {
         template.set(22, removeListing);
      }

      return GooeyPage.builder().template(Layouts.apply("listing", template.build())).title(Layouts.title("listing", listing.getUiTitle())).build();
   }
}
