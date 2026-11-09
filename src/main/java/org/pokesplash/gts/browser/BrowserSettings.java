package org.pokesplash.gts.browser;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import org.pokesplash.gts.economy.AtomicFiles;

public final class BrowserSettings {
    public ItemRule crates = new ItemRule(List.of("caixa", "crate", "chave", "key"));
    public ItemRule specialItems = new ItemRule(List.of("especial", "special", "voucher", "ticket", "passe"));

    public static final class ItemRule {
        public List<String> itemIds = new ArrayList<>();
        public List<String> itemTags = new ArrayList<>();
        public List<String> nameContains = new ArrayList<>();
        public ItemRule() {}
        ItemRule(List<String> names) { nameContains.addAll(names); }
        public boolean matches(String itemId, Set<String> tags, String name) {
            String lower = name.toLowerCase(Locale.ROOT);
            return itemIds.contains(itemId) || itemTags.stream().anyMatch(tags::contains)
                    || nameContains.stream().anyMatch(s -> lower.contains(s.toLowerCase(Locale.ROOT)));
        }
        void validate() {
            if (itemIds == null || itemTags == null || nameContains == null) throw new IllegalArgumentException("Null item filter list");
            for (List<String> values : List.of(itemIds, itemTags, nameContains)) {
                if (values.stream().anyMatch(s -> s == null || s.isBlank())) throw new IllegalArgumentException("Empty item filter rule");
            }
            for (String id : itemIds) if (!id.matches("[a-z0-9_.-]+:[a-z0-9/._-]+")) throw new IllegalArgumentException("Invalid item id: " + id);
            for (String id : itemTags) if (!id.matches("[a-z0-9_.-]+:[a-z0-9/._-]+")) throw new IllegalArgumentException("Invalid item tag (omit #): " + id);
        }
    }

    public static BrowserSettings load(Path file) throws java.io.IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BrowserSettings config;
        if (Files.exists(file)) config = gson.fromJson(Files.readString(file), BrowserSettings.class);
        else {
            config = new BrowserSettings();
            AtomicFiles.write(file, gson.toJson(config) + "\n");
        }
        if (config == null || config.crates == null || config.specialItems == null) throw new IllegalArgumentException("Invalid browser.json");
        config.crates.validate(); config.specialItems.validate();
        return config;
    }
}
