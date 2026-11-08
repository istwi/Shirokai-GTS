package org.pokesplash.gts.command.subcommand;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.JsonOps;
import java.util.concurrent.CompletableFuture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class SaveItem extends Subcommand {
   public SaveItem() {
      super("§9Usage:\n§3- gts saveitem");
   }

   public LiteralCommandNode<ServerCommandSource> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("saveitem")
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "saveitem") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         try {
            ItemStack item = ((ServerCommandSource)context.getSource()).getPlayer().getMainHandStack();
            if (item.getItem().equals(Items.AIR)) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cYou must be holding an item."));
               return 1;
            }

            JsonElement jsonElement = (JsonElement)ItemStack.CODEC
               .encodeStart(Gts.server.getRegistryManager().getOps(JsonOps.INSTANCE), item)
               .getOrThrow();
            String data = Utils.newGson().toJson(jsonElement);
            CompletableFuture<Boolean> result = Utils.writeFileAsync("/config/gts/", "item.json", data);
            if (result.join()) {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§2Item successfully saved."));
            } else {
               ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("§cUnable to save item!."));
            }
         } catch (Exception var6) {
            ((ServerCommandSource)context.getSource()).sendMessage(Text.literal(var6.getMessage()));
            var6.printStackTrace();
         }

         return 1;
      }
   }
}
