package org.pokesplash.gts.command.superclass;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.pokesplash.gts.util.Utils;

public abstract class Subcommand {
   private String usage;

   public Subcommand(String usageString) {
      this.usage = usageString;
   }

   public int showUsage(CommandContext<ServerCommandSource> context) {
      ((ServerCommandSource)context.getSource())
         .sendMessage(Text.literal(Utils.formatMessage(this.usage, ((ServerCommandSource)context.getSource()).isExecutedByPlayer())));
      return 1;
   }

   public abstract CommandNode<ServerCommandSource> build();

   public abstract int run(CommandContext<ServerCommandSource> var1);
}
