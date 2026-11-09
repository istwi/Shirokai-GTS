package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
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
import org.pokesplash.gts.UI.module.ListingInfo;
import org.pokesplash.gts.UI.module.PokemonInfo;

public class ManageListings {
   public Page getPage(UUID owner) {
      List<Listing> listings = Gts.listings.getListingsByPlayer(owner);
      List<Button> buttons = new ArrayList<>();
      if (!listings.isEmpty()) {
         listings.forEach(
            listing -> {
               List<Text> lore = ListingInfo.parse(listing);
               if (listing.isPokemon()) {
                  PokemonListing pokemonListing = (PokemonListing)listing;
                  lore.addAll(PokemonInfo.parse(pokemonListing.getListing()));
               }

               Button button = GooeyButton.builder()
                  .display(listing.getIcon())
                  .with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
                  .with(DataComponentTypes.LORE, new LoreComponent(lore))
                  .onClick(action -> {
                     ServerPlayerEntity sender = action.getPlayer();
                     Page pagex = new SingleListing().getPage(sender, listing);
                     UIManager.openUIForcefully(sender, pagex);
                  })
                  .build();
               buttons.add(button);
            }
         );
      }

      ChestTemplate template = ChestTemplate.builder(6)
         .fill(Filler.getButton())
         .set(49, ListingBrowser.button(net.minecraft.item.Items.ARROW, "<yellow>Voltar ao GTS",
            java.util.List.of("<gray>Esquerdo: voltar ao GTS normal.",
               "<gray>Se houver mais páginas de vendas:",
               "<aqua>Direito: próxima página.", "<aqua>Shift + direito: página anterior."), action -> {
                  switch (action.getClickType()) {
                     case LEFT_CLICK -> ListingBrowser.open(action.getPlayer());
                     case RIGHT_CLICK, SHIFT_RIGHT_CLICK -> {
                        if (action.getPage() instanceof LinkedPage current) {
                           LinkedPage target = action.getClickType() == ca.landonjw.gooeylibs2.api.button.ButtonClick.RIGHT_CLICK
                              ? current.getNext() : current.getPrevious();
                           if (target != null) UIManager.openUIForcefully(action.getPlayer(), target);
                        }
                     }
                     default -> {}
                  }
               }))
         .build();
      LinkedPage first = PaginationHelper.createPagesFromPlaceholders(Layouts.apply("manage", template), buttons, null);
      for (LinkedPage page = first; page != null; page = page.getNext()) {
         page.setTitle(Layouts.title("manage", "Meu GTS | {current}/{total}"));
      }
      return first;
   }
}
