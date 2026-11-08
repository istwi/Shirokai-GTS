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
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
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

public class ManageListings {
   public Page getPage(UUID owner) {
      List<Listing> listings = Gts.listings.getListingsByPlayer(owner);
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Button> buttons = new ArrayList<>();
      if (!listings.isEmpty()) {
         listings.forEach(
            listing -> {
               List<class_2561> lore = ListingInfo.parse(listing);
               if (listing.isPokemon()) {
                  PokemonListing pokemonListing = (PokemonListing)listing;
                  lore.addAll(PokemonInfo.parse(pokemonListing.getListing()));
               }

               Button button = GooeyButton.builder()
                  .display(listing.getIcon())
                  .with(class_9334.field_49631, listing.getDisplayName())
                  .with(class_9334.field_49632, new class_9290(lore))
                  .onClick(action -> {
                     class_3222 sender = action.getPlayer();
                     Page pagex = new SingleListing().getPage(sender, listing);
                     UIManager.openUIForcefully(sender, pagex);
                  })
                  .build();
               buttons.add(button);
            }
         );
      }

      ChestTemplate template = ChestTemplate.builder(6)
         .rectangle(0, 0, 5, 9, placeholder)
         .fill(Filler.getButton())
         .set(48, SeePokemonListings.getButton())
         .set(49, org.pokesplash.gts.UI.button.ExpiredListings.getButton())
         .set(50, SeeItemListings.getButton())
         .set(53, NextPage.getButton())
         .set(45, PreviousPage.getButton())
         .set(52, RelistAll.getButton())
         .build();
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
      page.setTitle(Gts.language.getManageTitle());
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Gts.language.getManageTitle());
         this.setPageTitle(next);
      }
   }
}
