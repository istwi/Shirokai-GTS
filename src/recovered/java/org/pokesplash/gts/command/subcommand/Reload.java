package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class Reload extends Subcommand {
   public Reload() {
      super("§9Usage:\n§3- gts reload");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("reload")
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "reload") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      Gts.reload();
      ((class_2168)context.getSource())
         .method_45068(
            class_2561.method_43470(
               Utils.formatMessage(
                  Utils.formatPlaceholders(Gts.language.getReloadMessage(), 0.0, null, null, null), ((class_2168)context.getSource()).method_43737()
               )
            )
         );
      return 1;
   }
}
