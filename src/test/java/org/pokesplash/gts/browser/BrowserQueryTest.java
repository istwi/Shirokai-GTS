package org.pokesplash.gts.browser;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BrowserQueryTest {
    private final UUID owner = UUID.randomUUID(), other = UUID.randomUUID();
    private BrowserQuery.Entry entry(String name, double price, long created, long end, boolean pokemon) {
        return new BrowserQuery.Entry(UUID.randomUUID(), owner, name, "impactor:dollars", price, created, end, pokemon, pokemon, pokemon, pokemon, pokemon, !pokemon, !pokemon);
    }
    @Test void categoryCycleWrapsBothWays() {
        var q = new BrowserQuery();
        q.cycleCategory(true); assertEquals(BrowserQuery.Category.TOKENS, q.category);
        q.cycleCategory(false); assertEquals(BrowserQuery.Category.ALL, q.category);
        for (int i = 0; i < BrowserQuery.Category.values().length; i++) q.cycleCategory(false);
        assertEquals(BrowserQuery.Category.ALL, q.category);
    }
    @Test void pokemonFiltersExcludeItemsAndItemFiltersExcludePokemon() {
        var item = entry("Caixa", 10, 1, 10, false);
        var pokemon = entry("Mewtwo", 20, 2, 20, true);
        var q = new BrowserQuery();
        for (var c : List.of(BrowserQuery.Category.POKEMON, BrowserQuery.Category.SHINY, BrowserQuery.Category.HA,
                BrowserQuery.Category.LEGENDARY, BrowserQuery.Category.ULTRA_BEAST)) {
            q.category = c; assertTrue(q.matches(pokemon, owner)); assertFalse(q.matches(item, owner));
        }
        for (var c : List.of(BrowserQuery.Category.ITEMS, BrowserQuery.Category.CRATES, BrowserQuery.Category.SPECIAL)) {
            q.category = c; assertTrue(q.matches(item, owner)); assertFalse(q.matches(pokemon, owner));
        }
    }
    @Test void ownListingsUseViewerUuidAndCurrenciesAreExact() {
        var e = entry("Item", 1, 1, 1, false); var q = new BrowserQuery();
        q.category = BrowserQuery.Category.MINE; assertTrue(q.matches(e, owner)); assertFalse(q.matches(e, other));
        q.category = BrowserQuery.Category.DOLLARS; assertTrue(q.matches(e, other));
        q.category = BrowserQuery.Category.TOKENS; assertFalse(q.matches(e, owner));
    }
    @Test void specialFlagsAreRequiredNotJustPokemonType() {
        var plain = new BrowserQuery.Entry(UUID.randomUUID(), owner, "Eevee", "impactor:tokens", 2, 1, 0, true, false, false, false, false, false, false);
        var q = new BrowserQuery();
        for (var c : List.of(BrowserQuery.Category.SHINY, BrowserQuery.Category.HA, BrowserQuery.Category.LEGENDARY, BrowserQuery.Category.ULTRA_BEAST)) {
            q.category = c; assertFalse(q.matches(plain, owner));
        }
        q.category = BrowserQuery.Category.TOKENS; assertTrue(q.matches(plain, owner));
    }
    @Test void allSortOrdersAndReversalWork() {
        var first = entry("Zubat", 1, 1, 10, false); var second = entry("Abra", 10, 2, 20, true);
        var q = new BrowserQuery();
        q.sort = BrowserQuery.Sort.RECENT; assertEquals(second, List.of(first, second).stream().sorted(q.comparator()).findFirst().orElseThrow());
        q.sort = BrowserQuery.Sort.NAME; assertTrue(q.comparator().compare(second, first) < 0);
        q.sort = BrowserQuery.Sort.PRICE; assertTrue(q.comparator().compare(first, second) < 0);
        q.sort = BrowserQuery.Sort.EXPIRATION; assertTrue(q.comparator().compare(first, second) < 0);
        q.reversed = true; assertTrue(q.comparator().compare(first, second) > 0);
    }
    @Test void noExpirationStaysLastInBothDirections() {
        var forever = entry("Never", 1, 1, -1, false); var expires = entry("Soon", 1, 1, 10, false);
        var q = new BrowserQuery(); q.sort = BrowserQuery.Sort.EXPIRATION;
        assertTrue(q.comparator().compare(forever, expires) > 0);
        q.reversed = true; assertTrue(q.comparator().compare(forever, expires) > 0);
    }
    @Test void itemRulesMatchIdsTagsAndNames() {
        var rule = new BrowserSettings.ItemRule();
        rule.itemIds.add("mod:crate"); rule.itemTags.add("mod:keys"); rule.nameContains.add("Voucher");
        assertTrue(rule.matches("mod:crate", Set.of(), "anything"));
        assertTrue(rule.matches("mod:key", Set.of("mod:keys"), "anything"));
        assertTrue(rule.matches("minecraft:paper", Set.of(), "VOUCHER VIP"));
        assertFalse(rule.matches("minecraft:dirt", Set.of(), "Dirt"));
    }
    @Test void defaultsPersistIndependentlyForEachPlayer(@TempDir Path directory) throws Exception {
        var file = directory.resolve("preferences.json"); var first = new BrowserPreferences(file);
        first.save(owner, BrowserQuery.Sort.PRICE, true);
        first.save(other, BrowserQuery.Sort.NAME, false);
        var reloaded = new BrowserPreferences(file); reloaded.load();
        assertEquals(new BrowserPreferences.Preference(BrowserQuery.Sort.PRICE, true), reloaded.get(owner));
        assertEquals(new BrowserPreferences.Preference(BrowserQuery.Sort.NAME, false), reloaded.get(other));
    }
}
