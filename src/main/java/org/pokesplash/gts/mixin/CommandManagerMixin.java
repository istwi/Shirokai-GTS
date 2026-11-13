package org.pokesplash.gts.mixin;

import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import org.pokesplash.gts.command.ClientCommandTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @ModifyArg(method = "sendCommandTree", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/packet/s2c/play/CommandTreeS2CPacket;<init>(Lcom/mojang/brigadier/tree/RootCommandNode;)V"), index = 0)
    private RootCommandNode<CommandSource> gts$hideSubcommands(RootCommandNode<CommandSource> root) {
        return ClientCommandTree.hideGtsSubcommands(root);
    }
}
