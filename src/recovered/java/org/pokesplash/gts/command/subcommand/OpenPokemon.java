package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.PokemonListings;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.enumeration.Sort;

public class OpenPokemon extends Subcommand {
   public OpenPokemon() {
      super("§9Usage:\n§3- gts pokemon");
   }

   @Override
   public CommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("pokemon")
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "base") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 player = ((class_2168)context.getSource()).method_44023();
         Page page = new PokemonListings().getPage(Sort.NONE);
         UIManager.openUIForcefully(player, page);
         return 1;
      }
   }
}
