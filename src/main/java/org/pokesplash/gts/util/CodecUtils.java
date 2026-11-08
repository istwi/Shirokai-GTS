package org.pokesplash.gts.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;
import org.pokesplash.gts.Gts;

public abstract class CodecUtils {
   public static JsonElement encodeItem(ItemStack stack) {
      return (JsonElement)ItemStack.CODEC.encodeStart(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), stack).getOrThrow();
   }

   public static ItemStack decodeItem(JsonElement json) {
      try {
         return (ItemStack)((Pair)ItemStack.CODEC.decode(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), json).getOrThrow()).getFirst();
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
