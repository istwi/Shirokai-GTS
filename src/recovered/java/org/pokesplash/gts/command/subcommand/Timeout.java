package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Date;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class Timeout extends Subcommand {
   public Timeout() {
      super("§9Usage:\n§3- gts timeout <player> <time> <amount>");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("timeout")
                  .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "timeout") : true))
               .executes(this::showUsage))
            .then(((RequiredArgumentBuilder)class_2170.method_9244("player", StringArgumentType.string()).suggests((ctx, builder) -> {
               for (String name : Gts.server.method_3760().method_14580()) {
                  builder.suggest(name);
               }

               return builder.buildFuture();
            }).executes(this::showUsage)).then(
               ((RequiredArgumentBuilder)class_2170.method_9244("time", StringArgumentType.string()).suggests((ctx, builder) -> {
                  String[] times = new String[]{"days", "hours", "minutes", "seconds"};

                  for (String time : times) {
                     builder.suggest(time);
                  }

                  return builder.buildFuture();
               }).executes(this::showUsage)).then(class_2170.method_9244("amount", IntegerArgumentType.integer()).suggests((ctx, builder) -> {
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
   public int run(CommandContext<class_2168> context) {
      String playerName = StringArgumentType.getString(context, "player");
      class_3222 player = Gts.server.method_3760().method_14566(playerName);
      if (player == null) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cCould not find player with name: " + playerName));
         return 1;
      } else {
         String time = StringArgumentType.getString(context, "time");
         Integer amount = IntegerArgumentType.getInteger(context, "amount");

         long endTime;
         try {
            endTime = this.parseTime(time, amount);
         } catch (Exception var9) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cCould not parse time " + time));
            return 1;
         }

         if (!Gts.timeouts.hasTimeoutExpired(player.method_5667())) {
            Gts.timeouts.removeTimeout(player.method_5667());
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cRemoved timeout for " + playerName));
            return 1;
         } else {
            Gts.timeouts.addTimeout(player.method_5667(), endTime);
            ((class_2168)context.getSource())
               .method_45068(class_2561.method_43470("§b" + playerName + " has been timed out for " + Utils.parseLongDate(endTime - new Date().getTime())));
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
