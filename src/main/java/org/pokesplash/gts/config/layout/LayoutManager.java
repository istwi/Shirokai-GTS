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
        mergeMissing(json, defaults);
        LayoutConfig config = GSON.fromJson(json, LayoutConfig.class);
        config.validate();
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
