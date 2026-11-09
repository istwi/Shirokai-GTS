package org.pokesplash.gts.browser;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import org.pokesplash.gts.economy.AtomicFiles;

public final class BrowserPreferences {
    private final Path file;
    private final Map<UUID, Preference> values = new HashMap<>();
    public record Preference(BrowserQuery.Sort sort, boolean reversed) {}
    public BrowserPreferences(Path file) { this.file = file; }
    public void load() throws java.io.IOException {
        Map<UUID, Preference> loaded = new HashMap<>();
        if (Files.exists(file)) {
            JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            for (var entry : root.entrySet()) {
                JsonObject value = entry.getValue().getAsJsonObject();
                loaded.put(UUID.fromString(entry.getKey()), new Preference(BrowserQuery.Sort.valueOf(value.get("sort").getAsString()), value.get("reversed").getAsBoolean()));
            }
        }
        values.clear(); values.putAll(loaded);
    }
    public Preference get(UUID player) { return values.getOrDefault(player, new Preference(BrowserQuery.Sort.RECENT, false)); }
    public void save(UUID player, BrowserQuery.Sort sort, boolean reversed) throws java.io.IOException {
        Map<UUID, Preference> next = new HashMap<>(values);
        next.put(player, new Preference(sort, reversed));
        AtomicFiles.write(file, new GsonBuilder().setPrettyPrinting().create().toJson(next) + "\n");
        values.clear(); values.putAll(next);
    }
}
