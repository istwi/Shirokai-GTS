package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Version extends Subcommand {
   public Version() {
      super("§9Usage:\n§3- gts version");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("version")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "version") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("Gts Version - " + Gts.version));
      return 1;
   }
}
