package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate.Builder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.util.ColorUtil;
import org.pokesplash.gts.util.Utils;

public class ExpiredListing {
   public Page getPage(Listing listing) {
      List<Text> lore = new ArrayList<>();
      lore.add(ColorUtil.parse(Gts.language.getSeller() + listing.getSellerName()));
      lore.add(ColorUtil.parse(Gts.language.getPrice() + Gts.config.getMultiCurrency().colored(listing.getCurrency(), listing.getPrice())));
      if (listing.isPokemon()) {
         lore.addAll(PokemonInfo.parse((PokemonListing)listing));
      }

      Button listingDisplay = GooeyButton.builder()
         .display(listing.getIcon())
         .with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
         .with(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
         .with(DataComponentTypes.LORE, new LoreComponent(lore))
         .build();
      Button receiveListing = GooeyButton.builder()
         .display(Gts.language.getPurchaseButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getReceiveListingButtonLabel()))
         .onClick(
            action -> {
               boolean success = GtsAPI.returnListing(action.getPlayer(), listing);
               String message = "";
               if (success) {
                  message = Utils.formatPlaceholders(
                     Gts.language.getReturnListingSuccess(),
                     0.0,
                     listing.getListingName(),
                     listing.getSellerName(),
                     action.getPlayer().getName().getString()
                  );
                  action.getPlayer().sendMessage(ColorUtil.parse(message));
               } else {
                  message = Utils.formatPlaceholders(
                     Gts.language.getReturnListingFail(), 0.0, listing.getListingName(), listing.getSellerName(), action.getPlayer().getName().getString()
                  );
                  action.getPlayer().sendMessage(ColorUtil.parse(message));
               }

               UIManager.openUIForcefully(action.getPlayer(), new ExpiredListings().getPage(action.getPlayer().getUuid()));
            }
         )
         .build();
      Button cancel = GooeyButton.builder()
         .display(Gts.language.getCancelButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getCancelPurchaseButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page page = new ExpiredListings().getPage(action.getPlayer().getUuid());
            UIManager.openUIForcefully(sender, page);
         })
         .build();
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton()).set(11, receiveListing).set(13, listingDisplay).set(15, cancel);
      return GooeyPage.builder().template(Layouts.apply("expired_listing", template.build())).title(Layouts.title("expired_listing", listing.getUiTitle())).build();
   }
}
