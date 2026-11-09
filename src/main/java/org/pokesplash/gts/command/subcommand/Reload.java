package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class Reload extends Subcommand {
   public Reload() {
      super("§9Usage:\n§3- gts reload");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "reload") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      Gts.reload();
      if (org.pokesplash.gts.UI.Layouts.lastError() != null) {
         context.getSource().sendMessage(Text.literal(org.pokesplash.gts.UI.Layouts.lastError()));
         return 0;
      }
      if (org.pokesplash.gts.UI.ListingBrowser.lastError() != null) {
         context.getSource().sendMessage(Text.literal(org.pokesplash.gts.UI.ListingBrowser.lastError()));
         return 0;
      }
      ((ServerCommandSource)context.getSource())
         .sendMessage(
            Text.literal(
               Utils.formatMessage(
                  Utils.formatPlaceholders(Gts.language.getReloadMessage(), 0.0, null, null, null), ((ServerCommandSource)context.getSource()).isExecutedByPlayer()
               )
            )
         );
      return 1;
   }
}
