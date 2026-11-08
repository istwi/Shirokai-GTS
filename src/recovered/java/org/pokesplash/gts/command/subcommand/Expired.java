package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.ExpiredListings;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Expired extends Subcommand {
   public Expired() {
      super("§9Usage:\n§3- gts expired");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("expired")
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "expired") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 sender = ((class_2168)context.getSource()).method_44023();
         Page page = new ExpiredListings().getPage(sender.method_5667());
         UIManager.openUIForcefully(sender, page);
         return 1;
      }
   }
}
