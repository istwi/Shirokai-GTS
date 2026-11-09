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
      return getPage(viewer, listing, false);
   }

   public Page getPage(ServerPlayerEntity viewer, Listing listing, boolean expired) {
      if (expired && !viewer.getUuid().equals(listing.getSellerUuid())) throw new IllegalArgumentException("Somente o dono pode retirar um anúncio expirado.");
      List<Text> lore = ListingInfo.parse(listing);
      if (expired) lore.add(0, ColorUtil.parse("<red><bold>ANÚNCIO EXPIRADO</bold></red>"));
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
            Page page = new AllListings().getPage(sender);
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
                  success = expired ? GtsAPI.returnListing(action.getPlayer(), listing)
                     : GtsAPI.cancelAndReturnListing(action.getPlayer(), listing);
               } else {
                  success = Gts.permissions.hasPermission(action.getPlayer(), "remove") && GtsAPI.cancelListing(listing);
               }

               if (!success) {
                  action.getPlayer().sendMessage(Text.literal(GtsTransactions.claimFailure(action.getPlayer(), listing, expired)));
               } else {
                  Text message = ColorUtil.parse(
                     Utils.formatPlaceholders(
                        expired ? Gts.language.getReturnListingSuccess() : Gts.language.getCancelListing(),
                        0.0, listing.getListingName(), listing.getSellerName(), action.getPlayer().getName().getString()
                     )
                  );
                  action.getPlayer().sendMessage(message);
                  ServerPlayerEntity seller = action.getPlayer().getServer().getPlayerManager().getPlayer(listing.getSellerUuid());
                  if (seller != null && !seller.getUuid().equals(action.getPlayer().getUuid())) {
                     seller.sendMessage(message);
                  }

               }
               if (action.getPlayer().getUuid().equals(listing.getSellerUuid())) {
                  UIManager.openUIForcefully(action.getPlayer(), new ManageListings().getPage(action.getPlayer().getUuid()));
               } else {
                  UIManager.closeUI(action.getPlayer());
               }
            }
         )
         .build();
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton()).set(13, pokemon).set(15, cancel);
      boolean owner = viewer.getUuid().equals(listing.getSellerUuid());
      if (owner) {
         if (!expired) {
         template.set(11, ListingBrowser.button(net.minecraft.item.Items.NAME_TAG, "<yellow>Editar preço", java.util.List.of("<gray>Escolha e confirme um novo preço."),
            action -> PriceEditor.open(action.getPlayer(), listing)));
         }
         template.set(15, ListingBrowser.button(net.minecraft.item.Items.CHEST, expired ? "<yellow>Retirar anúncio expirado" : "<yellow>Retirar do GTS",
            java.util.List.of("<gray>Devolve seu item ou Pokémon."), removeListing::onClick));
         template.set(22, ListingBrowser.button(net.minecraft.item.Items.ARROW, "<yellow>Voltar para Meu GTS", java.util.List.of(),
            action -> UIManager.openUIForcefully(action.getPlayer(), new ManageListings().getPage(action.getPlayer().getUuid()))));
      } else {
         template.set(11, purchase);
      }

      if (Gts.permissions.hasPermission(viewer, "remove") && !viewer.getUuid().equals(listing.getSellerUuid())) {
         template.set(22, removeListing);
      }

      String menu = owner ? "own_listing" : "listing";
      return GooeyPage.builder().template(Layouts.apply(menu, template.build())).title(Layouts.title(menu, expired ? "Retirar anúncio expirado" : listing.getUiTitle())).build();
   }
}
