package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Test extends Subcommand {
   public Test() {
      super("§9Usage:\n§3- gts test");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test")
               .requires(ctx -> ctx.isExecutedByPlayer() ? ctx.getPlayer().getUuidAsString().equalsIgnoreCase("b5c833a0-c6f7-4e89-9ad5-d36faef37ab2") : false))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      ItemStack item = ((ServerCommandSource)context.getSource()).getPlayer().getMainHandStack();
      NbtCompound tag = new NbtCompound();
      tag.putIntArray("ivs", new ArrayList<>(java.util.List.of(31, 1, 3, 31, 31, 6)));
      item.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
      return 1;
   }
}
