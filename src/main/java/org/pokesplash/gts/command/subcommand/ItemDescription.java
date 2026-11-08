package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class ItemDescription extends Subcommand {
   public ItemDescription() {
      super("§9Usage:\n§3- gts itemdesc");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("itemdesc")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "itemdesc") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command can only be used by a player."));
         return 1;
      } else {
         ItemStack handItem = ((ServerCommandSource)context.getSource()).getPlayer().getMainHandStack();
         if (handItem.isOf(Items.AIR)) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cYou must be holding an item to use this command"));
            return 1;
         } else {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal(handItem.getItem().getTranslationKey()));
            return 1;
         }
      }
   }
}
