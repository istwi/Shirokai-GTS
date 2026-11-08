package org.pokesplash.gts.mixin;

import net.minecraft.class_3222;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_3324.class})
public interface PlayerListAccessor {
   @Invoker("save")
   void gts$savePlayer(class_3222 var1);
}
