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
      List<Listing> listings = new ArrayList<>(Gts.listings.getListingsByPlayer(owner));
      java.util.Set<UUID> seen = new java.util.HashSet<>();
      java.util.Set<UUID> expiredIds = new java.util.HashSet<>();
      listings.forEach(listing -> seen.add(listing.getId()));
      for (Listing listing : Gts.listings.getExpiredListingsOfPlayer(owner)) {
         if (seen.add(listing.getId())) { listings.add(listing); expiredIds.add(listing.getId()); }
      }
      List<Button> buttons = new ArrayList<>();
      if (!listings.isEmpty()) {
         listings.forEach(
            listing -> {
               boolean expired = expiredIds.contains(listing.getId());
               List<Text> lore = ListingInfo.parse(listing);
               if (expired) {
                  lore.add(0, org.pokesplash.gts.util.ColorUtil.parse("<red><bold>ANÚNCIO EXPIRADO</bold></red>"));
                  lore.add(org.pokesplash.gts.util.ColorUtil.parse("<yellow>Clique para retirar seu item ou Pokémon."));
               }

               Button button = GooeyButton.builder()
                  .display(listing.getIcon())
                  .with(DataComponentTypes.CUSTOM_NAME, expired
                     ? listing.getDisplayName().append(Text.literal(" [EXPIRADO]").formatted(net.minecraft.util.Formatting.RED)) : listing.getDisplayName())
                  .with(DataComponentTypes.LORE, new LoreComponent(lore))
                  .onClick(action -> {
                     ServerPlayerEntity sender = action.getPlayer();
                     Listing current = Gts.listings.getActiveListingById(listing.getId());
                     boolean nowExpired = current == null;
                     if (nowExpired) current = Gts.listings.getExpiredListingById(listing.getId());
                     if (current == null || !sender.getUuid().equals(current.getSellerUuid())) {
                        sender.sendMessage(Text.literal("§cO anúncio mudou. A lista foi atualizada."));
                        UIManager.openUIForcefully(sender, new ManageListings().getPage(sender.getUuid()));
                        return;
                     }
                     Page pagex = new SingleListing().getPage(sender, current, nowExpired);
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
            java.util.List.of("<gray>Ativos: " + (listings.size() - expiredIds.size()) + " | Expirados: " + expiredIds.size(),
               "<gray>Esquerdo: voltar ao GTS normal.",
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
