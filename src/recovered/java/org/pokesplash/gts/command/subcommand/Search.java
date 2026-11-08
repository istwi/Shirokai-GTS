package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.FilteredListings;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Search extends Subcommand {
   public Search() {
      super("§9Usage:\n§3- gts search pokemon/item");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("search")
                  .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "search") : true))
               .executes(this::showUsage))
            .then(class_2170.method_9244("value", StringArgumentType.greedyString()).executes(this::run)))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 sender = ((class_2168)context.getSource()).method_44023();
         String argument = StringArgumentType.getString(context, "value");

         try {
            Page page = new FilteredListings().getPage(argument);
            UIManager.openUIForcefully(sender, page);
         } catch (Exception var5) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cSomething went wrong."));
            var5.printStackTrace();
         }

         return 1;
      }
   }
}
