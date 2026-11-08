package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.FilteredListings;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Search extends Subcommand {
   public Search() {
      super("§9Usage:\n§3- gts search pokemon/item");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("search")
                  .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "search") : true))
               .executes(this::showUsage))
            .then(CommandManager.argument("value", StringArgumentType.greedyString()).executes(this::run)))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
         String argument = StringArgumentType.getString(context, "value");

         try {
            Page page = new FilteredListings().getPage(argument);
            UIManager.openUIForcefully(sender, page);
         } catch (Exception var5) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cSomething went wrong."));
            var5.printStackTrace();
         }

         return 1;
      }
   }
}
