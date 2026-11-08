package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.Page;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.item.PokemonItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_2561;
import net.minecraft.class_3902;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.button.NextPage;
import org.pokesplash.gts.UI.button.PreviousPage;
import org.pokesplash.gts.UI.button.SeeItemListings;
import org.pokesplash.gts.UI.button.SeePokemonListings;
import org.pokesplash.gts.UI.module.PokemonInfo;
import org.pokesplash.gts.history.HistoryItem;
import org.pokesplash.gts.history.ItemHistoryItem;
import org.pokesplash.gts.history.PlayerHistory;
import org.pokesplash.gts.history.PokemonHistoryItem;
import org.pokesplash.gts.util.ColorUtil;

public class History {
   public Page getPage(UUID owner) {
      PlaceholderButton placeholder = new PlaceholderButton();
      PlayerHistory playerHistory = Gts.history.getPlayerHistory(owner);
      List<Button> buttons = new ArrayList<>();
      if (playerHistory != null) {
         List<HistoryItem> items = playerHistory.getListings();
         items.sort(Comparator.comparing(HistoryItem::getSoldDate));
         Collections.reverse(items);

         for (HistoryItem item : items) {
            List<class_2561> lore = new ArrayList<>();
            lore.add(ColorUtil.parse(Gts.language.getSeller() + item.getSellerName()));
            lore.add(ColorUtil.parse(Gts.language.getPrice() + Gts.config.getMultiCurrency().colored(item.getCurrency(), item.getPrice())));
            lore.add(ColorUtil.parse(Gts.language.getBuyer() + item.getBuyerName()));
            String pattern = "d MMMM yyyy";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            lore.add(ColorUtil.parse(Gts.language.getSold_date() + format.format(new Date(item.getSoldDate()))));
            Button button = null;
            if (item.isPokemon()) {
               PokemonHistoryItem pokemonItem = (PokemonHistoryItem)item;
               lore.addAll(PokemonInfo.parse(pokemonItem.getListing()));
               button = GooeyButton.builder()
                  .display(PokemonItem.from(pokemonItem.getListing(), 1))
                  .with(class_9334.field_49631, pokemonItem.getDisplayName())
                  .with(class_9334.field_49632, new class_9290(lore))
                  .build();
            } else {
               ItemHistoryItem itemHistoryItem = (ItemHistoryItem)item;
               if (itemHistoryItem.getListing() != null) {
                  button = GooeyButton.builder()
                     .display(itemHistoryItem.getListing())
                     .with(class_9334.field_49631, itemHistoryItem.getDisplayName())
                     .with(class_9334.field_49632, new class_9290(lore))
                     .with(class_9334.field_49638, class_3902.field_17274)
                     .build();
               }
            }

            if (button != null) {
               buttons.add(button);
            }
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
         .build();
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, null);
      page.setTitle(Gts.language.getHistoryTitle());
      this.setPageTitle(page);
      return page;
   }

   private void setPageTitle(LinkedPage page) {
      LinkedPage next = page.getNext();
      if (next != null) {
         next.setTitle(Gts.language.getHistoryTitle());
         this.setPageTitle(next);
      }
   }
}
