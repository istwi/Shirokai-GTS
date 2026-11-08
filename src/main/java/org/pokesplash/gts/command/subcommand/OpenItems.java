package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.ItemListings;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.enumeration.Sort;

public class OpenItems extends Subcommand {
   public OpenItems() {
      super("§9Usage:\n§3- gts item");
   }

   @Override
   public CommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("item")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "base") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity player = ((ServerCommandSource)context.getSource()).getPlayer();
         Page page = new ItemListings().getPage(Sort.NONE);
         UIManager.openUIForcefully(player, page);
         return 1;
      }
   }
}
