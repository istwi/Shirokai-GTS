package org.pokesplash.gts.command.superclass;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
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

   public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
      this.createCommand(dispatcher);
   }

   private void createCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
      LiteralCommandNode<ServerCommandSource> root = ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(this.commandString).requires(ctx -> {
         if (Gts.config.isEnablePermissionNodes()) {
            return ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), this.permission) : true;
         } else {
            return true;
         }
      })).executes(this::run)).build();
      dispatcher.getRoot().addChild(root);

      for (String alias : this.aliases) {
         dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(alias).redirect(root)).executes(this::run));
      }

      for (Subcommand subcommand : this.subcommands) {
         root.addChild(subcommand.build());
      }
   }

   public abstract int run(CommandContext<ServerCommandSource> var1);
}
