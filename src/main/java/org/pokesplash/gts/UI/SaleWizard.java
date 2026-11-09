package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.item.PokemonItem;
import java.util.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.browser.SaleInput;
import org.pokesplash.gts.util.ColorUtil;

public final class SaleWizard {
    public static void open(ServerPlayerEntity player) {
        PrivateInputs.cancel(player.getUuid());
        if (!Gts.permissions.hasPermission(player, "sell")) { player.sendMessage(Text.literal("§cVocê não tem permissão para vender.")); return; }
        var template = ChestTemplate.builder(3).fill(Filler.getButton());
        template.set(11, ListingBrowser.button(Items.CHEST, "<green>Vender item em mãos", List.of("<gray>Escolha o preço e a quantidade no chat privado."), a -> item(a.getPlayer())));
        template.set(15, ListingBrowser.button(Items.NETHER_STAR, "<aqua>Vender Pokémon", List.of("<gray>Escolha um Pokémon do seu time."), a -> party(a.getPlayer())));
        template.set(22, ListingBrowser.button(Items.ARROW, "<yellow>Voltar", List.of(), a -> ListingBrowser.open(a.getPlayer())));
        UIManager.openUIForcefully(player, GooeyPage.builder().template(Layouts.apply("sell_create", template.build())).title(Layouts.title("sell_create", "Criar uma venda")).build());
    }
    private static void item(ServerPlayerEntity player) {
        if (!Gts.config.isEnableItemSales()) { player.sendMessage(Text.literal("§cAs vendas de itens estão desativadas.")); return; }
        ItemStack expected = player.getMainHandStack().copy();
        if (expected.isEmpty()) { player.sendMessage(Text.literal("§cSegure o item que deseja vender antes de abrir o livro.")); return; }
        PrivateInputs.request(player, "Digite: preço [quantidade]. Exemplo: 100 1.", text -> {
            ItemStack current = player.getMainHandStack();
            if (!ItemStack.areItemsAndComponentsEqual(expected, current)) { player.sendMessage(Text.literal("§cO item em mãos mudou. Inicie a venda novamente.")); return; }
            SaleInput input = SaleInput.parse(text, true, Math.min(expected.getCount(), current.getCount()));
            execute(player, "gts sell item " + input.price().toPlainString() + " " + input.amount());
        });
    }
    private static void party(ServerPlayerEntity player) {
        if (!Gts.config.isEnablePokemonSales()) { player.sendMessage(Text.literal("§cAs vendas de Pokémon estão desativadas.")); return; }
        var party = Cobblemon.INSTANCE.getStorage().getParty(player);
        var template = ChestTemplate.builder(3).fill(Filler.getButton());
        for (int i = 0; i < 6; i++) {
            var pokemon = party.get(i);
            if (pokemon == null) continue;
            int slot = i;
            UUID expected = pokemon.getUuid();
            template.set(10 + i, GooeyButton.builder().display(PokemonItem.from(pokemon, 1))
                    .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse("<aqua>" + pokemon.getDisplayName(Gts.showPokemonDisplayName).getString()))
                    .onClick(a -> PrivateInputs.request(a.getPlayer(), "Digite o preço do Pokémon.", text -> {
                        var current = Cobblemon.INSTANCE.getStorage().getParty(a.getPlayer()).get(slot);
                        if (current == null || !current.getUuid().equals(expected)) { a.getPlayer().sendMessage(Text.literal("§cO Pokémon mudou. Inicie a venda novamente.")); return; }
                        SaleInput input = SaleInput.parse(text, false, 1);
                        execute(a.getPlayer(), "gts sell pokemon " + (slot + 1) + " " + input.price().toPlainString());
                    })).build());
        }
        template.set(22, ListingBrowser.button(Items.ARROW, "<yellow>Voltar", List.of(), a -> open(a.getPlayer())));
        UIManager.openUIForcefully(player, GooeyPage.builder().template(Layouts.apply("sell_party", template.build())).title(Layouts.title("sell_party", "Escolha um Pokémon")).build());
    }
    private static void execute(ServerPlayerEntity player, String command) {
        if (!player.isDisconnected()) player.getServer().getCommandManager().executeWithPrefix(player.getCommandSource(), command);
    }
}
