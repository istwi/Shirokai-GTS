package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.item.PokemonItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.button.NextPage;
import org.pokesplash.gts.UI.button.PreviousPage;
import org.pokesplash.gts.UI.button.RelistAll;
import org.pokesplash.gts.UI.button.SeeItemListings;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

public class PokemonListings {
   public Page getPage(@NotNull Sort sort) {
      List<PokemonListing> pkmListings = Gts.listings.getPokemonListings();
      if (sort.equals(Sort.PRICE)) {
         pkmListings.sort(Comparator.comparingDouble(Listing::getPrice));
      } else if (sort.equals(Sort.DATE)) {
         pkmListings.sort(Comparator.comparingLong(Listing::getEndTime));
      } else if (sort.equals(Sort.NAME)) {
         pkmListings.sort(Comparator.comparing(PokemonListing::getListingName));
      }

      Button sortByPriceButton = GooeyButton.builder()
         .display(Gts.language.getSortByPriceButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByPriceButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new PokemonListings().getPage(Sort.PRICE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNewestButton = GooeyButton.builder()
         .display(Gts.language.getSortByNewestButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByNewestButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new PokemonListings().getPage(Sort.DATE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNameButton = GooeyButton.builder()
         .display(Gts.language.getSortByNameButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByPokemonButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new PokemonListings().getPage(Sort.NAME);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Button> pokemonButtons = new ArrayList<>();

      for (PokemonListing listing : pkmListings) {
         List<class_2561> lore = ListingInfo.parse(listing);
         lore.addAll(PokemonInfo.parse(listing));
         Button button = GooeyButton.builder()
            .display(PokemonItem.from(listing.getListing(), 1))
            .with(class_9334.field_49631, listing.getDisplayName())
            .with(class_9334.field_49632, new class_9290(lore))
            .onClick(action -> {
               class_3222 sender = action.getPlayer();
               Page pagex = new SingleListing().getPage(sender, listing);
               UIManager.openUIForcefully(sender, pagex);
            })
            .build();
         pokemonButtons.add(button);
      }

      ChestTemplate template = ChestTemplate.builder(6)
         .rectangle(0, 0, 5, 9, placeholder)
         .fill(Filler.getButton())
         .set(47, sortByPriceButton)
         .set(48, sortByNewestButton)
         .set(49, sortByNameButton)
         .set(50, SeeItemListings.getButton())
         .set(51, org.pokesplash.gts.UI.button.ManageListings.getButton())
         .set(53, NextPage.getButton())
         .set(45, PreviousPage.getButton())
         .set(52, RelistAll.getButton())
         .build();
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, pokemonButtons, null);
      page.setTitle(Gts.language.getPokemonListingsTitle());
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Gts.language.getPokemonListingsTitle());
         this.setPageTitle(next);
      }
   }
}
