package org.pokesplash.gts.config.layout;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LayoutConfigTest {
    @TempDir Path directory;

    @Test void defaultsCoverEveryMenuAndValidate() {
        LayoutConfig c = LayoutConfig.defaults();
        assertEquals(11, c.menus.size());
        assertDoesNotThrow(c::validate);
    }

    @Test void rejectsCollidingButtonsAndContent() {
        LayoutConfig c = LayoutConfig.defaults();
        c.menus.get("gts").buttons.get("next").slot = 0;
        assertThrows(IllegalArgumentException.class, c::validate);
    }

    @Test void rejectsNegativeAndOutOfBoundsSlots() {
        for (int slot : new int[] {-1, 27}) {
            LayoutConfig c = LayoutConfig.defaults();
            c.menus.get("sell_confirm").buttons.get("confirm").slot = slot;
            assertThrows(IllegalArgumentException.class, c::validate);
        }
    }

    @Test void smallerPaginatedMenuCanUseCustomContentAndNavigation() {
        LayoutConfig c = LayoutConfig.defaults();
        var m = c.menus.get("gts");
        m.rows = 3;
        m.contentSlots = new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        int slot = 18;
        for (var button : m.buttons.values()) button.slot = slot++;
        assertDoesNotThrow(c::validate);
    }

    @Test void decorationsCannotOverwriteActions() {
        LayoutConfig c = LayoutConfig.defaults();
        var decoration = new LayoutConfig.Control(11);
        decoration.appearance.item = "minecraft:diamond";
        c.menus.get("sell_confirm").decorations.add(decoration);
        assertThrows(IllegalArgumentException.class, c::validate);
    }

    @Test void initializesOnceAndPreservesUserChangesOnReload() throws Exception {
        Path file = directory.resolve("gts/layouts.json");
        LayoutManager.load(file);
        var json = com.google.gson.JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        json.getAsJsonObject("menus").getAsJsonObject("gts").addProperty("title", "Meu GTS");
        String text = json.toString();
        Files.writeString(file, text);
        assertEquals("Meu GTS", LayoutManager.load(file).menus.get("gts").title);
        assertEquals(text, Files.readString(file));
    }

    @Test void missingSectionsUseDefaultsWithoutRewritingFile() throws Exception {
        Path file = directory.resolve("layouts.json");
        String partial = "{\"version\":1,\"menus\":{\"sell_confirm\":{\"title\":\"Confirmar\"}}}";
        Files.writeString(file, partial);
        LayoutConfig c = LayoutManager.load(file);
        assertEquals("Confirmar", c.menus.get("sell_confirm").title);
        assertEquals(11, c.menus.get("sell_confirm").buttons.get("confirm").slot);
        assertEquals(partial, Files.readString(file));
    }

    @Test void malformedAndNullConfigNeverGetOverwritten() throws Exception {
        Path file = directory.resolve("layouts.json");
        for (String input : List.of("{", "[]", "{\"menus\":null}", "{\"version\":99}", "{\"menus\":{\"unknown\":{}}}")) {
            Files.writeString(file, input);
            assertThrows(Exception.class, () -> LayoutManager.load(file));
            assertEquals(input, Files.readString(file));
        }
    }

    @Test void documentedCustomLayoutIsValid() throws Exception {
        var config = LayoutManager.load(Path.of("docs/examples/layouts-custom.json"));
        assertEquals(4, config.menus.get("sell_confirm").rows);
        assertEquals(31, config.menus.get("sell_confirm").buttons.get("change_currency").slot);
    }
}
