package org.pokesplash.gts.config.layout;

import java.util.*;

/** Serializable menu settings. Slots are zero-based, left to right. */
public final class LayoutConfig {
    public int version = 4;
    public Map<String, Menu> menus = new LinkedHashMap<>();

    public static final class Visual {
        public String item;
        public String name;
        public List<String> lore;
    }

    public static final class Control {
        public int slot;
        public Visual appearance = new Visual();
        public Control() {}
        public Control(int slot) { this.slot = slot; }
    }

    public static final class Menu {
        public int rows;
        /** Null preserves the language file's title (including dynamic listing names). */
        public String title;
        public Visual filler = new Visual();
        public List<Integer> contentSlots = new ArrayList<>();
        public Map<String, Control> buttons = new LinkedHashMap<>();
        public List<Control> decorations = new ArrayList<>();
    }

    public static LayoutConfig defaults() {
        LayoutConfig c = new LayoutConfig();
        c.menu("gts", 6, "category_filter", 46, "previous", 47, "sort_filter", 48,
                "create", 49, "refresh", 50, "next", 51, "manage", 52);
        Menu main = c.menus.get("gts");
        main.filler.item = "minecraft:gray_stained_glass_pane";
        main.filler.name = " ";
        for (int row = 1; row <= 4; row++) for (int col = 1; col <= 7; col++) main.contentSlots.add(row * 9 + col);
        c.list("items", "sort_price", 47, "sort_newest", 48, "sort_name", 49, "pokemon", 50, "manage", 51, "previous", 45, "next", 53, "relist", 52);
        c.list("pokemon", "sort_price", 47, "sort_newest", 48, "sort_name", 49, "items", 50, "manage", 51, "previous", 45, "next", 53, "relist", 52);
        c.menu("manage", 6, "back", 49);
        Menu manage = c.menus.get("manage");
        manage.title = "Meu GTS | {current}/{total}";
        manage.filler.item = "minecraft:gray_stained_glass_pane";
        manage.filler.name = " ";
        manage.contentSlots.addAll(main.contentSlots);
        c.list("expired", "pokemon", 48, "manage", 49, "items", 50, "previous", 45, "next", 53, "relist", 52);
        c.list("history", "pokemon", 48, "manage", 49, "items", 50, "previous", 45, "next", 53);
        c.list("search", "pokemon", 48, "manage", 49, "items", 50, "previous", 45, "next", 53, "relist", 52);
        c.menu("listing", 3, "primary", 11, "asset", 13, "back", 15, "admin_remove", 22);
        c.menu("own_listing", 3, "edit_price", 11, "asset", 13, "withdraw", 15, "back", 22);
        c.menu("expired_listing", 3, "receive", 11, "asset", 13, "back", 15);
        c.menu("sell_currency", 3, "cancel", 22);
        for (int i = 10; i <= 16; i++) c.menus.get("sell_currency").contentSlots.add(i);
        c.menu("sell_confirm", 3, "confirm", 11, "asset", 13, "cancel", 15, "change_currency", 22);
        c.menu("sell_create", 3, "item", 11, "pokemon", 15, "back", 22);
        c.menu("sell_party", 3, "party_1", 10, "party_2", 11, "party_3", 12,
                "party_4", 13, "party_5", 14, "party_6", 15, "back", 22);
        c.menu("price_confirm", 3, "confirm", 11, "asset", 13, "cancel", 15);
        return c;
    }

    private void list(String id, Object... buttons) {
        menu(id, 6, buttons);
        Menu menu = menus.get(id);
        menu.filler.item = "minecraft:gray_stained_glass_pane";
        menu.filler.name = " ";
        for (int row = 1; row <= 4; row++) for (int col = 1; col <= 7; col++) menu.contentSlots.add(row * 9 + col);
    }

    private void menu(String id, int rows, Object... buttons) {
        Menu m = new Menu();
        m.rows = rows;
        for (int i = 0; i < buttons.length; i += 2) m.buttons.put((String) buttons[i], new Control((Integer) buttons[i + 1]));
        menus.put(id, m);
    }

    public void validate() {
        if (version != 4) throw new IllegalArgumentException("Unsupported layouts version: " + version);
        LayoutConfig defaults = defaults();
        if (menus == null || !menus.keySet().equals(defaults.menus.keySet())) throw new IllegalArgumentException("Unknown or missing menu id");
        for (var entry : menus.entrySet()) {
            String id = entry.getKey();
            Menu m = entry.getValue();
            Menu baseline = defaults.menus.get(id);
            if (m == null || m.rows < 1 || m.rows > 6) throw new IllegalArgumentException(id + ": rows must be 1..6");
            if (m.buttons == null || !m.buttons.keySet().equals(baseline.buttons.keySet())) throw new IllegalArgumentException(id + ": invalid button ids");
            if (m.contentSlots == null || m.decorations == null || m.filler == null) throw new IllegalArgumentException(id + ": null layout section");
            if (baseline.contentSlots.isEmpty() != m.contentSlots.isEmpty()) throw new IllegalArgumentException(id + ": contentSlots must match menu type");
            // Currency buttons retain their callbacks in seven original source slots.
            if (id.equals("sell_currency") && m.contentSlots.size() != 7) throw new IllegalArgumentException(id + ": exactly seven currency slots required");
            Set<Integer> used = new HashSet<>();
            for (Integer slot : m.contentSlots) reserve(id, m, used, slot);
            for (Control b : m.buttons.values()) {
                if (b == null || b.appearance == null) throw new IllegalArgumentException(id + ": null button");
                reserve(id, m, used, b.slot);
            }
            for (Control b : m.decorations) {
                if (b == null || b.appearance == null || b.appearance.item == null) throw new IllegalArgumentException(id + ": decoration needs an item");
                reserve(id, m, used, b.slot);
            }
        }
    }

    private static void reserve(String id, Menu menu, Set<Integer> used, Integer slot) {
        if (slot == null || slot < 0 || slot >= menu.rows * 9) throw new IllegalArgumentException(id + ": slot outside menu: " + slot);
        if (!used.add(slot)) throw new IllegalArgumentException(id + ": overlapping slot: " + slot);
    }
}
