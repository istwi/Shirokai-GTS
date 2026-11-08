package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class ItemDescription extends Subcommand {
   public ItemDescription() {
      super("§9Usage:\n§3- gts itemdesc");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("itemdesc")
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "itemdesc") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command can only be used by a player."));
         return 1;
      } else {
         class_1799 handItem = ((class_2168)context.getSource()).method_44023().method_6047();
         if (handItem.method_31574(class_1802.field_8162)) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cYou must be holding an item to use this command"));
            return 1;
         } else {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470(handItem.method_7909().method_7876()));
            return 1;
         }
      }
   }
}
