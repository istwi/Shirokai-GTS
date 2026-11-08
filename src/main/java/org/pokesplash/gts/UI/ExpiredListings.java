package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.button.NextPage;
import org.pokesplash.gts.UI.button.PreviousPage;
import org.pokesplash.gts.UI.button.RelistAll;
import org.pokesplash.gts.UI.button.SeeItemListings;
import org.pokesplash.gts.UI.button.SeePokemonListings;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;

public class ExpiredListings {
   public Page getPage(UUID owner) {
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Listing> listings = Gts.listings.getExpiredListingsOfPlayer(owner);
      List<Button> buttons = new ArrayList<>();
      if (listings != null) {
         for (Listing listing : listings) {
            List<Text> lore = new ArrayList<>(ListingInfo.parse(listing));
            if (listing.isPokemon()) {
               lore.addAll(PokemonInfo.parse((PokemonListing)listing));
            }

            Button button = GooeyButton.builder()
               .display(listing.getIcon())
               .with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
               .with(DataComponentTypes.LORE, new LoreComponent(lore))
               .onClick(action -> {
                  ServerPlayerEntity sender = action.getPlayer();
                  Page pagex = new ExpiredListing().getPage(listing);
                  UIManager.openUIForcefully(sender, pagex);
               })
               .build();
            buttons.add(button);
         }
      }

      ChestTemplate template = ChestTemplate.builder(6)
         .rectangle(0, 0, 5, 9, placeholder)
         .fill(Filler.getButton())
         .set(48, SeePokemonListings.getButton())
         .set(49, org.pokesplash.gts.UI.button.ManageListings.getButton())
         .set(50, SeeItemListings.getButton())
         .set(53, NextPage.getButton())
         .set(45, PreviousPage.getButton())
         .set(52, RelistAll.getButton())
         .build();
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(Layouts.apply("expired", template), buttons, null);
      page.setTitle(Layouts.title("expired", Gts.language.getExpiredListingsTitle()));
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Layouts.title("expired", Gts.language.getExpiredListingsTitle()));
         this.setPageTitle(next);
      }
   }
}
