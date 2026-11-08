package org.pokesplash.gts.command.subcommand;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;

public class GetPrice extends Subcommand {
   public GetPrice() {
      super("§9Usage:\n§3- gts getprice [slot]");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("getprice")
                  .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "getprice") : true))
               .executes(this::runItem))
            .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 6)).executes(this::runPokemon)))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      return 0;
   }

   public int runItem(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         try {
            ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
            ItemStack item = sender.getMainHandStack();
            if (item.isEmpty()) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cYou are not holding an item."));
               return 1;
            }

            double price = Gts.history.getAveragePrice(item);
            if (price != 0.0) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§aAverage price is $" + price + "."));
            } else {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cThis item has not been sold before."));
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         return 1;
      }
   }

   public int runPokemon(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
         int slot = IntegerArgumentType.getInteger(context, "slot");
         Pokemon pokemon = Cobblemon.INSTANCE.getStorage().getParty(sender).get(slot - 1);
         if (pokemon == null) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cNo Pokemon found in slot " + slot + "."));
            return 1;
         } else {
            double price = Gts.history.getAveragePrice(pokemon);
            if (price != 0.0) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§aAverage price is $" + price + "."));
               return 1;
            } else {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cThis Pokemon has not been sold before."));
               return 1;
            }
         }
      }
   }
}
