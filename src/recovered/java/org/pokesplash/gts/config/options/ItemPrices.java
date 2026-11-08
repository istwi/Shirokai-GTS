package org.pokesplash.gts.config.options;

import com.cobblemon.mod.common.CobblemonItems;
import com.google.gson.JsonElement;
import net.minecraft.class_1799;
import org.pokesplash.gts.util.CodecUtils;

public class ItemPrices {
   private JsonElement item = CodecUtils.encodeItem(new class_1799(CobblemonItems.ASSAULT_VEST));
   private double price = 10000.0;

   public JsonElement getItem() {
      return this.item;
   }

   public double getMinPrice() {
      return this.price;
   }
}
