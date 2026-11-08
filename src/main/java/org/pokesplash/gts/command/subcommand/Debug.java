package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Debug extends Subcommand {
   public Debug() {
      super("§9Usage:\n§3- gts debug");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debug")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "debug") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      Gts.isDebugMode = !Gts.isDebugMode;
      ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("Set Debug mode to " + Gts.isDebugMode));
      return 1;
   }
}
