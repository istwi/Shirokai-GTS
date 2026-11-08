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
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_3902;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.jetbrains.annotations.NotNull;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.button.NextPage;
import org.pokesplash.gts.UI.button.PreviousPage;
import org.pokesplash.gts.UI.button.RelistAll;
import org.pokesplash.gts.UI.button.SeePokemonListings;
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

public class ItemListings {
   public Page getPage(@NotNull Sort sort) {
      List<ItemListing> itmListings = Gts.listings.getItemListings();
      if (sort.equals(Sort.PRICE)) {
         itmListings.sort(Comparator.comparingDouble(Listing::getPrice));
      } else if (sort.equals(Sort.DATE)) {
         itmListings.sort(Comparator.comparingLong(Listing::getEndTime));
      } else if (sort.equals(Sort.NAME)) {
         itmListings.sort(Comparator.comparing(ItemListing::getListingName));
      }

      Button sortByPriceButton = GooeyButton.builder()
         .display(Gts.language.getSortByPriceButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByPriceButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.PRICE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNewestButton = GooeyButton.builder()
         .display(Gts.language.getSortByNewestButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByNewestButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.DATE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNameButton = GooeyButton.builder()
         .display(Gts.language.getSortByNameButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getSortByNameButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.NAME);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Button> itemButtons = new ArrayList<>();

      for (ItemListing listing : itmListings) {
         List<class_2561> lore = ListingInfo.parse(listing);
         Button button = GooeyButton.builder()
            .display(listing.getListing())
            .with(class_9334.field_49631, listing.getDisplayName())
            .with(class_9334.field_49632, new class_9290(lore))
            .with(class_9334.field_49638, class_3902.field_17274)
            .onClick(action -> {
               class_3222 sender = action.getPlayer();
               Page pagex = new SingleListing().getPage(sender, listing);
               UIManager.openUIForcefully(sender, pagex);
            })
            .build();
         itemButtons.add(button);
      }

      ChestTemplate template = ChestTemplate.builder(6)
         .rectangle(0, 0, 5, 9, placeholder)
         .fill(Filler.getButton())
         .set(47, sortByPriceButton)
         .set(48, sortByNewestButton)
         .set(49, sortByNameButton)
         .set(50, SeePokemonListings.getButton())
         .set(51, org.pokesplash.gts.UI.button.ManageListings.getButton())
         .set(53, NextPage.getButton())
         .set(45, PreviousPage.getButton())
         .set(52, RelistAll.getButton())
         .build();
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, itemButtons, null);
      page.setTitle(Gts.language.getItemListingsTitle());
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Gts.language.getItemListingsTitle());
         this.setPageTitle(next);
      }
   }
}
