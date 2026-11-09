package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.browser.SaleInput;
import org.pokesplash.gts.util.ColorUtil;

public final class PriceEditor {
    public static void open(ServerPlayerEntity player, Listing listing) {
        UUID id = listing.getId();
        double previous = listing.getPrice();
        if (!player.getUuid().equals(listing.getSellerUuid()) || !Gts.permissions.hasPermission(player, "sell")) {
            player.sendMessage(Text.literal("§cVocê não pode editar este anúncio.")); return;
        }
        if (org.pokesplash.gts.api.provider.ListingAPI.getHighestPriority() != null) {
            player.sendMessage(Text.literal("§cSeu provedor externo de anúncios não oferece edição segura de preço.")); return;
        }
        PrivateInputs.request(player, "Digite o novo preço do anúncio (moeda: " + listing.getCurrency() + ").", text -> {
            double price = SaleInput.parse(text, false, 1).price().doubleValue();
            try { GtsAPI.validatePriceChange(player, id, previous, price); }
            catch (IllegalArgumentException error) {
                player.sendMessage(Text.literal("§c" + error.getMessage()));
                UIManager.openUIForcefully(player, new ManageListings().getPage(player.getUuid()));
                return;
            }
            var template = ChestTemplate.builder(3).fill(Filler.getButton());
            var lore = List.of(ColorUtil.parse("<gray>Antes: " + Gts.config.getMultiCurrency().colored(listing.getCurrency(), previous)),
                    ColorUtil.parse("<green>Novo preço: " + Gts.config.getMultiCurrency().colored(listing.getCurrency(), price)));
            template.set(13, GooeyButton.builder().display(listing.getIcon()).with(DataComponentTypes.LORE, new LoreComponent(lore)).build());
            AtomicBoolean consumed = new AtomicBoolean();
            template.set(11, ListingBrowser.button(Items.LIME_DYE, "<green>Confirmar novo preço", List.of(), a -> {
                if (!consumed.compareAndSet(false, true)) return;
                try {
                    GtsAPI.changeListingPrice(a.getPlayer(), id, previous, price);
                    a.getPlayer().sendMessage(Text.literal("§aPreço atualizado."));
                } catch (RuntimeException error) { a.getPlayer().sendMessage(Text.literal("§c" + error.getMessage())); }
                UIManager.openUIForcefully(a.getPlayer(), new ManageListings().getPage(a.getPlayer().getUuid()));
            }));
            template.set(15, ListingBrowser.button(Items.RED_DYE, "<red>Cancelar", List.of(), a -> {
                consumed.set(true); UIManager.openUIForcefully(a.getPlayer(), new ManageListings().getPage(a.getPlayer().getUuid()));
            }));
            UIManager.openUIForcefully(player, GooeyPage.builder().template(Layouts.apply("price_confirm", template.build())).title(Layouts.title("price_confirm", "Confirmar novo preço")).build());
        });
    }
}
