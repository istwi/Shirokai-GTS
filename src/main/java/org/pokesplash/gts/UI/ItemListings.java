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
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.DataComponentTypes;
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
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getSortByPriceButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.PRICE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNewestButton = GooeyButton.builder()
         .display(Gts.language.getSortByNewestButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getSortByNewestButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.DATE);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      Button sortByNameButton = GooeyButton.builder()
         .display(Gts.language.getSortByNameButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getSortByNameButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page pagex = new ItemListings().getPage(Sort.NAME);
            UIManager.openUIForcefully(sender, pagex);
         })
         .build();
      PlaceholderButton placeholder = new PlaceholderButton();
      List<Button> itemButtons = new ArrayList<>();

      for (ItemListing listing : itmListings) {
         List<Text> lore = ListingInfo.parse(listing);
         Button button = GooeyButton.builder()
            .display(listing.getListing())
            .with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
            .with(DataComponentTypes.LORE, new LoreComponent(lore))
            .with(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
            .onClick(action -> {
               ServerPlayerEntity sender = action.getPlayer();
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
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(Layouts.apply("items", template), itemButtons, null);
      page.setTitle(Layouts.title("items", Gts.language.getItemListingsTitle()));
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Layouts.title("items", Gts.language.getItemListingsTitle()));
         this.setPageTitle(next);
      }
   }
}
