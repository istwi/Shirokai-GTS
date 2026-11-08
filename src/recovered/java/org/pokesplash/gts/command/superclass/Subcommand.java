package org.pokesplash.gts.command.superclass;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import org.pokesplash.gts.util.Utils;

public abstract class Subcommand {
   private String usage;

   public Subcommand(String usageString) {
      this.usage = usageString;
   }

   public int showUsage(CommandContext<class_2168> context) {
      ((class_2168)context.getSource())
         .method_45068(class_2561.method_43470(Utils.formatMessage(this.usage, ((class_2168)context.getSource()).method_43737())));
      return 1;
   }

   public abstract CommandNode<class_2168> build();

   public abstract int run(CommandContext<class_2168> var1);
}
