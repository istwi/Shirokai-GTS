package org.pokesplash.gts.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({PlayerManager.class})
public interface PlayerListAccessor {
   @Invoker("savePlayerData")
   void gts$savePlayer(ServerPlayerEntity var1);
}
