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
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_3902;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
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
      List<class_2561> lore = new ArrayList<>();
      lore.add(ColorUtil.parse(Gts.language.getSeller() + listing.getSellerName()));
      lore.add(ColorUtil.parse(Gts.language.getPrice() + Gts.config.getMultiCurrency().colored(listing.getCurrency(), listing.getPrice())));
      if (listing.isPokemon()) {
         lore.addAll(PokemonInfo.parse((PokemonListing)listing));
      }

      Button listingDisplay = GooeyButton.builder()
         .display(listing.getIcon())
         .with(class_9334.field_49631, listing.getDisplayName())
         .with(class_9334.field_49638, class_3902.field_17274)
         .with(class_9334.field_49632, new class_9290(lore))
         .build();
      Button receiveListing = GooeyButton.builder()
         .display(Gts.language.getPurchaseButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getReceiveListingButtonLabel()))
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
                     action.getPlayer().method_5477().getString()
                  );
                  action.getPlayer().method_43496(ColorUtil.parse(message));
               } else {
                  message = Utils.formatPlaceholders(
                     Gts.language.getReturnListingFail(), 0.0, listing.getListingName(), listing.getSellerName(), action.getPlayer().method_5477().getString()
                  );
                  action.getPlayer().method_43496(ColorUtil.parse(message));
               }

               UIManager.openUIForcefully(action.getPlayer(), new ExpiredListings().getPage(action.getPlayer().method_5667()));
            }
         )
         .build();
      Button cancel = GooeyButton.builder()
         .display(Gts.language.getCancelButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getCancelPurchaseButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page page = new ExpiredListings().getPage(action.getPlayer().method_5667());
            UIManager.openUIForcefully(sender, page);
         })
         .build();
      Builder template = ChestTemplate.builder(3).fill(Filler.getButton()).set(11, receiveListing).set(13, listingDisplay).set(15, cancel);
      return GooeyPage.builder().template(template.build()).title(listing.getUiTitle()).build();
   }
}
