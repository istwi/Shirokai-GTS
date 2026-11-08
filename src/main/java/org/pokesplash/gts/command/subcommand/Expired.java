package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.ExpiredListings;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Expired extends Subcommand {
   public Expired() {
      super("§9Usage:\n§3- gts expired");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("expired")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "expired") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
         Page page = new ExpiredListings().getPage(sender.getUuid());
         UIManager.openUIForcefully(sender, page);
         return 1;
      }
   }
}
