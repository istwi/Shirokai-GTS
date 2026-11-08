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
import java.util.Locale;
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
import org.pokesplash.gts.api.provider.ListingAPI;

public class FilteredListings {
   public Page getPage(String searchValue) {
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Button> buttons = new ArrayList<>();

      for (Listing listing : ListingAPI.getHighestPriority() == null
         ? Gts.listings.getListings()
         : Gts.listings.getListings().stream().map(Listing::deepClone).toList()) {
         if (listing.getListingName().toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT))
            || listing.getSellerName().toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT))) {
            List<class_2561> lore = ListingInfo.parse(listing);
            if (listing.isPokemon()) {
               lore.addAll(PokemonInfo.parse((PokemonListing)listing));
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
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
      page.setTitle(Gts.language.getFilteredListingsTitle().replaceAll("%search%", searchValue));
      this.setPageTitle(page, searchValue);
      return page;
   }

   private void setPageTitle(LinkedPage page, String searchValue) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Gts.language.getFilteredListingsTitle().replaceAll("%search%", searchValue));
         this.setPageTitle(next, searchValue);
      }
   }
}
