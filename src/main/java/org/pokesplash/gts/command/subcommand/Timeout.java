package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Date;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class Timeout extends Subcommand {
   public Timeout() {
      super("§9Usage:\n§3- gts timeout <player> <time> <amount>");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("timeout")
                  .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "timeout") : true))
               .executes(this::showUsage))
            .then(((RequiredArgumentBuilder)CommandManager.argument("player", StringArgumentType.string()).suggests((ctx, builder) -> {
               for (String name : Gts.server.getPlayerManager().getPlayerNames()) {
                  builder.suggest(name);
               }

               return builder.buildFuture();
            }).executes(this::showUsage)).then(
               ((RequiredArgumentBuilder)CommandManager.argument("time", StringArgumentType.string()).suggests((ctx, builder) -> {
                  String[] times = new String[]{"days", "hours", "minutes", "seconds"};

                  for (String time : times) {
                     builder.suggest(time);
                  }

                  return builder.buildFuture();
               }).executes(this::showUsage)).then(CommandManager.argument("amount", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
                  builder.suggest(1);

                  for (int x = 5; x <= 30; x++) {
                     builder.suggest(x);
                  }

                  return builder.buildFuture();
               }).executes(this::run))
            )))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      String playerName = StringArgumentType.getString(context, "player");
      ServerPlayerEntity player = Gts.server.getPlayerManager().getPlayer(playerName);
      if (player == null) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cCould not find player with name: " + playerName));
         return 1;
      } else {
         String time = StringArgumentType.getString(context, "time");
         Integer amount = IntegerArgumentType.getInteger(context, "amount");

         long endTime;
         try {
            endTime = this.parseTime(time, amount);
         } catch (Exception var9) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cCould not parse time " + time));
            return 1;
         }

         if (!Gts.timeouts.hasTimeoutExpired(player.getUuid())) {
            Gts.timeouts.removeTimeout(player.getUuid());
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cRemoved timeout for " + playerName));
            return 1;
         } else {
            Gts.timeouts.addTimeout(player.getUuid(), endTime);
            ((ServerCommandSource)context.getSource())
               .sendMessage(Text.literal("§b" + playerName + " has been timed out for " + Utils.parseLongDate(endTime - new Date().getTime())));
            return 1;
         }
      }
   }

   private long parseTime(String type, int amount) throws Exception {
      Date date = new Date();
      switch (type) {
         case "days":
            date.setTime(date.getTime() + amount * 24L * 60L * 60L * 1000L);
            break;
         case "hours":
            date.setTime(date.getTime() + amount * 60L * 60L * 1000L);
            break;
         case "minutes":
            date.setTime(date.getTime() + amount * 60L * 1000L);
            break;
         case "seconds":
            date.setTime(date.getTime() + amount * 1000L);
            break;
         default:
            throw new Exception("Could not parse time");
      }

      return date.getTime();
   }
}
