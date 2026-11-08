package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.button.*;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import java.nio.file.Path;
import java.util.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.button.Filler;
import org.pokesplash.gts.config.layout.*;
import org.pokesplash.gts.util.ColorUtil;

public final class Layouts {
    private static volatile LayoutConfig current = LayoutConfig.defaults();
    private static final LayoutConfig BASELINE = LayoutConfig.defaults();
    private static volatile String lastError;
    private Layouts() {}

    public static void reload() {
        try {
            LayoutConfig next = LayoutManager.load(Path.of("config/gts/layouts.json"));
            for (var menu : next.menus.values()) {
                validateVisual(menu.filler);
                menu.buttons.values().forEach(b -> validateVisual(b.appearance));
                menu.decorations.forEach(b -> validateVisual(b.appearance));
            }
            current = next;
            lastError = null;
        } catch (Exception error) {
            lastError = "Could not load config/gts/layouts.json; previous layouts kept: " + error.getMessage();
            Gts.LOGGER.error(lastError);
        }
    }

    public static String lastError() { return lastError; }

    private static void validateVisual(LayoutConfig.Visual visual) {
        if (visual.item != null) {
            Identifier id = Identifier.tryParse(visual.item);
            if (id == null || !Registries.ITEM.containsId(id) || Registries.ITEM.get(id) == Items.AIR) {
                throw new IllegalArgumentException("Invalid menu item: " + visual.item);
            }
        }
        if (visual.lore != null && visual.lore.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Null lore line");
        if (visual.name != null) ColorUtil.parse(visual.name);
        if (visual.lore != null) visual.lore.forEach(ColorUtil::parse);
    }

    public static String title(String id, String fallback) {
        String title = current.menus.get(id).title;
        return title == null ? fallback : title;
    }

    public static ChestTemplate apply(String id, ChestTemplate original) {
        LayoutConfig.Menu config = current.menus.get(id);
        LayoutConfig.Menu base = BASELINE.menus.get(id);
        ChestTemplate result = ChestTemplate.builder(config.rows)
                .fill(appearance(Filler.getButton(), config.filler)).build();
        if (!base.contentSlots.isEmpty()) {
            if (id.equals("sell_currency")) {
                for (int i = 0; i < base.contentSlots.size(); i++) {
                    Button button = original.getSlot(base.contentSlots.get(i)).getButton().orElse(null);
                    result.set(config.contentSlots.get(i), button);
                }
            } else {
                // PaginationHelper discovers these slots and creates as many pages as needed.
                for (int slot : config.contentSlots) result.set(slot, new PlaceholderButton());
            }
        }
        for (var entry : base.buttons.entrySet()) {
            Button source = original.getSlot(entry.getValue().slot).getButton().orElse(null);
            LayoutConfig.Control control = config.buttons.get(entry.getKey());
            if (source != null) result.set(control.slot, appearance(source, control.appearance));
        }
        for (var decoration : config.decorations) result.set(decoration.slot, appearance(Filler.getButton(), decoration.appearance));
        return result;
    }

    static Button appearance(Button source, LayoutConfig.Visual visual) {
        if (visual.item == null && visual.name == null && visual.lore == null) return source;
        ItemStack original = source.getDisplay();
        ItemStack display = visual.item == null ? original.copy()
                : new ItemStack(Registries.ITEM.get(Identifier.of(visual.item)), original.getCount());
        // Changing only an icon must retain the action label and economic information.
        if (visual.item != null) {
            var name = original.get(DataComponentTypes.CUSTOM_NAME);
            var lore = original.get(DataComponentTypes.LORE);
            if (name != null) display.set(DataComponentTypes.CUSTOM_NAME, name);
            if (lore != null) display.set(DataComponentTypes.LORE, lore);
        }
        if (visual.name != null) display.set(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(visual.name));
        if (visual.lore != null) display.set(DataComponentTypes.LORE, new LoreComponent(visual.lore.stream().map(ColorUtil::parse).toList()));
        return GooeyButton.builder().display(display).onClick(source::onClick).build();
    }
}
