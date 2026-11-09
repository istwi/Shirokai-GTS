package org.pokesplash.gts.browser;

import java.util.*;

/** Query semantics are independent of rendering and Minecraft registries. */
public final class BrowserQuery {
    public enum Category {
        ALL("Todos os anúncios"), MINE("Meus anúncios"), ITEMS("Itens"), POKEMON("Pokémon"),
        SHINY("Pokémon Shiny"), HA("Pokémon HA"), LEGENDARY("Pokémon lendários"),
        ULTRA_BEAST("Ultra Beasts"), CRATES("Caixas"), SPECIAL("Itens especiais"),
        DOLLARS("Anúncios em PokéDollars"), TOKENS("Anúncios em PokéTokens");
        public final String label;
        Category(String label) { this.label = label; }
    }
    public enum Sort {
        RECENT("Publicação"), EXPIRATION("Expiração"), NAME("Nome"), PRICE("Preço");
        public final String label;
        Sort(String label) { this.label = label; }
    }
    public record Entry(UUID id, UUID seller, String name, String currency, double price,
                        long createdAt, long endTime, boolean pokemon, boolean shiny,
                        boolean ha, boolean legendary, boolean ultraBeast, boolean crate, boolean special) {}

    public Category category = Category.ALL;
    public Sort sort = Sort.RECENT;
    public boolean reversed;

    public void cycleCategory(boolean backwards) {
        category = Category.values()[Math.floorMod(category.ordinal() + (backwards ? -1 : 1), Category.values().length)];
    }
    public void cycleSort() { sort = Sort.values()[(sort.ordinal() + 1) % Sort.values().length]; }

    public boolean matches(Entry e, UUID viewer) {
        return switch (category) {
            case ALL -> true;
            case MINE -> e.seller().equals(viewer);
            case ITEMS -> !e.pokemon();
            case POKEMON -> e.pokemon();
            case SHINY -> e.pokemon() && e.shiny();
            case HA -> e.pokemon() && e.ha();
            case LEGENDARY -> e.pokemon() && e.legendary();
            case ULTRA_BEAST -> e.pokemon() && e.ultraBeast();
            case CRATES -> !e.pokemon() && e.crate();
            case SPECIAL -> !e.pokemon() && e.special();
            case DOLLARS -> e.currency().equals("impactor:dollars");
            case TOKENS -> e.currency().equals("impactor:tokens");
        };
    }

    public Comparator<Entry> comparator() {
        Comparator<Entry> primary = switch (sort) {
            case RECENT -> Comparator.comparingLong(Entry::createdAt).reversed();
            case NAME -> Comparator.comparing(Entry::name, String.CASE_INSENSITIVE_ORDER);
            case PRICE -> Comparator.comparingDouble(Entry::price);
            case EXPIRATION -> Comparator.comparingLong(Entry::endTime);
        };
        if (reversed) primary = primary.reversed();
        if (sort == Sort.EXPIRATION) {
            // Listings without expiration stay last in either direction.
            primary = Comparator.<Entry, Boolean>comparing(e -> e.endTime() <= 0).thenComparing(primary);
        }
        // Different currencies have no exchange rate: group them before comparing amounts.
        if (sort == Sort.PRICE) primary = Comparator.comparing(Entry::currency).thenComparing(primary);
        return primary.thenComparing(Entry::id);
    }

    public String direction() {
        return switch (sort) {
            case RECENT -> reversed ? "Antigos → recentes" : "Recentes → antigos";
            case EXPIRATION -> reversed ? "Mais distante → mais próxima" : "Mais próxima → mais distante";
            case NAME -> reversed ? "Z → A" : "A → Z";
            case PRICE -> reversed ? "Maior → menor" : "Menor → maior";
        };
    }
}
