package org.pokesplash.gts.command.subcommand;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.class_1799;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class GetPrice extends Subcommand {
   public GetPrice() {
      super("§9Usage:\n§3- gts getprice [slot]");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("getprice")
                  .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "getprice") : true))
               .executes(this::runItem))
            .then(class_2170.method_9244("slot", IntegerArgumentType.integer(1, 6)).executes(this::runPokemon)))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      return 0;
   }

   public int runItem(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         try {
            class_3222 sender = ((class_2168)context.getSource()).method_44023();
            class_1799 item = sender.method_6047();
            if (item.method_7960()) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cYou are not holding an item."));
               return 1;
            }

            double price = Gts.history.getAveragePrice(item);
            if (price != 0.0) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§aAverage price is $" + price + "."));
            } else {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cThis item has not been sold before."));
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         return 1;
      }
   }

   public int runPokemon(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 sender = ((class_2168)context.getSource()).method_44023();
         int slot = IntegerArgumentType.getInteger(context, "slot");
         Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getParty(sender).get(slot - 1);
         if (pokemon == null) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cNo Pokemon found in slot " + slot + "."));
            return 1;
         } else {
            double price = Gts.history.getAveragePrice(pokemon);
            if (price != 0.0) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§aAverage price is $" + price + "."));
               return 1;
            } else {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cThis Pokemon has not been sold before."));
               return 1;
            }
         }
      }
   }
}
