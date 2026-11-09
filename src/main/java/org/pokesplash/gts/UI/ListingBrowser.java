package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.*;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.*;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.HiddenAbilityProperty;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.*;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.UI.module.*;
import org.pokesplash.gts.browser.*;
import org.pokesplash.gts.util.ColorUtil;

public final class ListingBrowser {
    private static final Map<UUID, State> STATES = new HashMap<>();
    private static final BrowserPreferences PREFERENCES = new BrowserPreferences(Path.of("config/gts/browser-preferences.json"));
    private static BrowserSettings settings = new BrowserSettings();
    private static String lastError;
    private static final class State {
        final BrowserQuery query = new BrowserQuery();
        int page;
    }
    private record Row(Listing listing, BrowserQuery.Entry entry) {}
    public static void reload() {
        try {
            BrowserSettings next = BrowserSettings.load(Path.of("config/gts/browser.json"));
            PREFERENCES.load(); settings = next; lastError = null;
        } catch (Exception e) {
            lastError = "Could not reload browser settings; previous values kept: " + e.getMessage();
            Gts.LOGGER.error(lastError);
        }
    }
    public static String lastError() { return lastError; }
    public static void disconnect(UUID id) { STATES.remove(id); }
    private static State state(UUID id) {
        return STATES.computeIfAbsent(id, key -> {
            State s = new State();
            var p = PREFERENCES.get(key); s.query.sort = p.sort(); s.query.reversed = p.reversed();
            return s;
        });
    }
    public static void open(ServerPlayerEntity player) { UIManager.openUIForcefully(player, page(player)); }
    public static Page page(ServerPlayerEntity player) {
        UUID viewer = player == null ? new UUID(0, 0) : player.getUuid();
        State state = player == null ? new State() : state(viewer);
        List<Row> rows = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Listing listing : Gts.listings.getListings()) {
            if (listing.getEndTime() > 0 && listing.getEndTime() <= now) continue;
            try {
                BrowserQuery.Entry entry = describe(listing);
                if (state.query.matches(entry, viewer)) rows.add(new Row(listing, entry));
            } catch (RuntimeException error) { Gts.LOGGER.error("Could not render listing " + listing.getId() + ": " + error.getMessage()); }
        }
        rows.sort(Comparator.comparing(Row::entry, state.query.comparator()));
        List<Button> listings = new ArrayList<>();
        for (Row row : rows) {
            Listing listing = row.listing();
            List<Text> lore = ListingInfo.parse(listing);
            if (listing instanceof PokemonListing pokemon) lore.addAll(PokemonInfo.parse(pokemon));
            listings.add(GooeyButton.builder().display(listing.getIcon()).with(DataComponentTypes.CUSTOM_NAME, listing.getDisplayName())
                    .with(DataComponentTypes.LORE, new LoreComponent(lore))
                    .onClick(a -> UIManager.openUIForcefully(a.getPlayer(), new SingleListing().getPage(a.getPlayer(), listing))).build());
        }
        var template = ChestTemplate.builder(6).fill(Filler.getButton());
        template.set(46, button(Items.CLOCK, "<aqua>Filtrar por: <white>" + state.query.category.label, categoryLore(state.query), a -> {
            if (a.getClickType() != ButtonClick.LEFT_CLICK && a.getClickType() != ButtonClick.RIGHT_CLICK) return;
            State s = state(a.getPlayer().getUuid());
            s.query.cycleCategory(a.getClickType() == ButtonClick.RIGHT_CLICK); s.page = 0; open(a.getPlayer());
        }));
        template.set(47, arrow(false));
        template.set(48, button(Items.HOPPER, "<aqua>Ordenar por: <white>" + state.query.sort.label, sortLore(state.query), a -> {
            State s = state(a.getPlayer().getUuid());
            switch (a.getClickType()) {
                case LEFT_CLICK -> { s.query.cycleSort(); s.query.reversed = false; }
                case RIGHT_CLICK -> s.query.reversed = !s.query.reversed;
                case SHIFT_LEFT_CLICK -> {
                    try {
                        PREFERENCES.save(a.getPlayer().getUuid(), s.query.sort, s.query.reversed);
                        a.getPlayer().sendMessage(Text.literal("§aOrdenação padrão salva."));
                    } catch (Exception e) { a.getPlayer().sendMessage(Text.literal("§cNão foi possível salvar sua preferência.")); }
                    return;
                }
                default -> { return; }
            }
            s.page = 0; open(a.getPlayer());
        }));
        template.set(49, button(Items.WRITABLE_BOOK, "<green>Criar uma venda", List.of("<gray>Escolha um item ou Pokémon para anunciar."), a -> SaleWizard.open(a.getPlayer())));
        template.set(50, button(Items.WATER_BUCKET, "<aqua>Atualizar GTS", List.of("<gray>Recarrega os anúncios disponíveis.", "<gray>Mantém seu filtro e sua ordenação."), a -> open(a.getPlayer())));
        template.set(51, arrow(true));
        template.set(52, button(Items.CLOCK, "<gold>Meu GTS", List.of("<gray>Veja suas vendas e edite os preços.", "<gray>Acesse anúncios expirados e crie vendas."), a -> {
            if (Gts.permissions.hasPermission(a.getPlayer(), "manage")) UIManager.openUIForcefully(a.getPlayer(), new ManageListings().getPage(a.getPlayer().getUuid()));
            else a.getPlayer().sendMessage(Text.literal("§cVocê não tem permissão para gerenciar anúncios."));
        }));
        LinkedPage first = PaginationHelper.createPagesFromPlaceholders(Layouts.apply("gts", template.build()), listings, null);
        LinkedPage selected = first;
        int index = 0;
        for (LinkedPage page = first; page != null; page = page.getNext(), index++) {
            page.setTitle(Layouts.title("gts", "GTS | {current}/{total}"));
            if (index <= state.page) selected = page;
        }
        state.page = selected.getCurrentPage() - 1;
        return selected;
    }
    private static BrowserQuery.Entry describe(Listing listing) {
        boolean shiny = false, ha = false, legendary = false, ultra = false, crate = false, special = false;
        if (listing instanceof PokemonListing pokemonListing) {
            Pokemon pokemon = pokemonListing.getListing();
            shiny = pokemon.getShiny(); ha = new HiddenAbilityProperty(true).matches(pokemon);
            legendary = pokemon.isLegendary(); ultra = pokemon.isUltraBeast();
        } else if (listing instanceof ItemListing itemListing) {
            ItemStack item = itemListing.getListing();
            String id = Registries.ITEM.getId(item.getItem()).toString();
            Set<String> tags = new HashSet<>(); item.streamTags().forEach(tag -> tags.add(tag.id().toString()));
            crate = settings.crates.matches(id, tags, item.getName().getString());
            special = settings.specialItems.matches(id, tags, item.getName().getString());
        }
        return new BrowserQuery.Entry(listing.getId(), listing.getSellerUuid(), listing.getListingName(), listing.getCurrency(), listing.getPrice(),
                listing.getCreatedAt(), listing.getEndTime(), listing.isPokemon(), shiny, ha, legendary, ultra, crate, special);
    }
    private static Button arrow(boolean forward) {
        return button(Items.ARROW, forward ? "<aqua>Próxima página" : "<aqua>Página anterior", List.of(), a -> {
            if (!(a.getPage() instanceof LinkedPage current)) return;
            LinkedPage target = forward ? current.getNext() : current.getPrevious();
            if (target != null) { state(a.getPlayer().getUuid()).page = target.getCurrentPage() - 1; UIManager.openUIForcefully(a.getPlayer(), target); }
        });
    }
    public static Button button(Item item, String title, List<String> lore, java.util.function.Consumer<ButtonAction> click) {
        return GooeyButton.builder().display(new ItemStack(item)).with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(title))
                .with(DataComponentTypes.LORE, new LoreComponent(lore.stream().map(ColorUtil::parse).toList())).onClick(click).build();
    }
    private static List<String> categoryLore(BrowserQuery query) {
        List<String> lore = new ArrayList<>();
        for (var c : BrowserQuery.Category.values()) lore.add((c == query.category ? "<green>➜ " : "<gray>➜ ") + c.label);
        lore.add("<aqua>Esquerdo: próximo filtro"); lore.add("<aqua>Direito: filtro anterior"); return lore;
    }
    private static List<String> sortLore(BrowserQuery query) {
        List<String> lore = new ArrayList<>();
        for (var s : BrowserQuery.Sort.values()) lore.add((s == query.sort ? "<green>➜ " : "<gray>➜ ") + s.label);
        lore.add("<white>" + query.direction()); lore.add("<aqua>Esquerdo: próxima ordenação");
        lore.add("<aqua>Direito: inverter sentido"); lore.add("<aqua>Shift + esquerdo: salvar como padrão"); return lore;
    }
}
