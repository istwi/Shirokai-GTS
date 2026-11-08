package org.pokesplash.gts.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1799;
import org.pokesplash.gts.Gts;

public abstract class CodecUtils {
   public static JsonElement encodeItem(class_1799 stack) {
      return (JsonElement)class_1799.field_24671.encodeStart(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), stack).getOrThrow();
   }

   public static class_1799 decodeItem(JsonElement json) {
      try {
         return (class_1799)((Pair)class_1799.field_24671.decode(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), json).getOrThrow()).getFirst();
      } catch (Exception var2) {
         Gts.LOGGER.error("Could not decode item: " + getJsonData(json));
         return null;
      }
   }

   private static String getJsonData(JsonElement json) {
      if (json.isJsonArray()) {
         return json.getAsJsonArray().toString();
      } else if (json.isJsonObject()) {
         return json.getAsJsonObject().toString();
      } else {
         return json.isJsonPrimitive() ? json.getAsJsonPrimitive().toString() : null;
      }
   }
}
