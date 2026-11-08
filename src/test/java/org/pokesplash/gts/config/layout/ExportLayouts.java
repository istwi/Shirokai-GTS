package org.pokesplash.gts.config.layout;

import com.google.gson.GsonBuilder;
import java.nio.file.*;

public final class ExportLayouts {
    public static void main(String[] args) throws Exception {
        Path output = Path.of(args[0]);
        Files.createDirectories(output.getParent());
        Files.writeString(output, new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(LayoutConfig.defaults()) + "\n");
    }
}
