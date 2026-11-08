package org.pokesplash.gts.command.superclass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import org.pokesplash.gts.Gts;

public abstract class BaseCommand {
   private String commandString;
   private ArrayList<String> aliases;
   private String permission;
   private ArrayList<Subcommand> subcommands;

   public BaseCommand(String commandString, List<String> aliases, String permission, List<Subcommand> subcommands) {
      this.commandString = commandString;
      this.aliases = new ArrayList<>();
      this.aliases.addAll(aliases);
      this.permission = permission;
      this.subcommands = new ArrayList<>();
      this.subcommands.addAll(subcommands);
   }

   public void register(CommandDispatcher<class_2168> dispatcher) {
      this.createCommand(dispatcher);
   }

   private void createCommand(CommandDispatcher<class_2168> dispatcher) {
      LiteralCommandNode<class_2168> root = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247(this.commandString).requires(ctx -> {
         if (Gts.config.isEnablePermissionNodes()) {
            return ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), this.permission) : true;
         } else {
            return true;
         }
      })).executes(this::run)).build();
      dispatcher.getRoot().addChild(root);

      for (String alias : this.aliases) {
         dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247(alias).redirect(root)).executes(this::run));
      }

      for (Subcommand subcommand : this.subcommands) {
         root.addChild(subcommand.build());
      }
   }

   public abstract int run(CommandContext<class_2168> var1);
}
