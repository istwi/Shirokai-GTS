package org.pokesplash.gts.config.layout;

import com.google.gson.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import org.pokesplash.gts.economy.AtomicFiles;

/** Invalid reloads never replace the last valid configuration or rewrite user input. */
public final class LayoutManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public static LayoutConfig load(Path file) throws IOException {
        JsonObject defaults = GSON.toJsonTree(LayoutConfig.defaults()).getAsJsonObject();
        if (!Files.exists(file)) {
            LayoutConfig config = LayoutConfig.defaults();
            config.validate();
            AtomicFiles.write(file, GSON.toJson(defaults) + "\n");
            return config;
        }
        JsonElement parsed = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
        if (!parsed.isJsonObject()) throw new IllegalArgumentException("layouts.json must be an object");
        JsonObject json = parsed.getAsJsonObject();
        int oldVersion = json.has("version") ? json.get("version").getAsInt() : 1;
        boolean migrate = oldVersion == 1 || oldVersion == 2;
        if (migrate) {
            JsonObject menus = json.has("menus") && json.get("menus").isJsonObject() ? json.getAsJsonObject("menus") : null;
            if (oldVersion == 1 && menus != null && menus.has("gts") && menus.get("gts").isJsonObject()) {
                JsonObject old = menus.getAsJsonObject("gts");
                JsonObject buttons = old.has("buttons") && old.get("buttons").isJsonObject() ? old.getAsJsonObject("buttons") : null;
                if (buttons != null && (buttons.has("pokemon") || buttons.has("items") || buttons.has("relist"))) {
                    JsonObject replacement = defaults.getAsJsonObject("menus").getAsJsonObject("gts").deepCopy();
                    if (old.has("title")) replacement.add("title", old.get("title").deepCopy());
                    if (old.has("filler") && old.get("filler").isJsonObject()
                            && old.getAsJsonObject("filler").entrySet().stream().anyMatch(e -> !e.getValue().isJsonNull())) {
                        replacement.add("filler", old.get("filler").deepCopy());
                    }
                    menus.add("gts", replacement);
                }
            }
            if (menus != null && menus.has("manage") && menus.get("manage").isJsonObject()) {
                JsonObject old = menus.getAsJsonObject("manage");
                JsonObject replacement = defaults.getAsJsonObject("menus").getAsJsonObject("manage").deepCopy();
                if (old.has("title") && !old.get("title").isJsonNull()) replacement.add("title", old.get("title").deepCopy());
                menus.add("manage", replacement);
            }
            if (menus != null && menus.has("listing") && menus.get("listing").isJsonObject()) {
                JsonObject listing = menus.getAsJsonObject("listing");
                if (listing.has("buttons") && listing.get("buttons").isJsonObject()) listing.getAsJsonObject("buttons").remove("edit_price");
            }
            json.addProperty("version", 3);
        }
        mergeMissing(json, defaults);
        LayoutConfig config = GSON.fromJson(json, LayoutConfig.class);
        config.validate();
        if (migrate) {
            Path backup = file.resolveSibling(file.getFileName() + ".v" + oldVersion + ".backup");
            if (!Files.exists(backup)) Files.copy(file, backup);
            AtomicFiles.write(file, GSON.toJson(json) + "\n");
        }
        return config;
    }

    private static void mergeMissing(JsonObject target, JsonObject defaults) {
        for (var entry : defaults.entrySet()) {
            if (!target.has(entry.getKey())) target.add(entry.getKey(), entry.getValue().deepCopy());
            else if (target.get(entry.getKey()).isJsonObject() && entry.getValue().isJsonObject()) {
                mergeMissing(target.getAsJsonObject(entry.getKey()), entry.getValue().getAsJsonObject());
            }
        }
    }
}
