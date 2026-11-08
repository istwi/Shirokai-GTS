package org.pokesplash.gts.command.subcommand;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.JsonOps;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.command.superclass.Subcommand;
import org.pokesplash.gts.util.Utils;

public class SaveItem extends Subcommand {
   public SaveItem() {
      super("§9Usage:\n§3- gts saveitem");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("saveitem")
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "saveitem") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         try {
            class_1799 item = ((class_2168)context.getSource()).method_44023().method_6047();
            if (item.method_7909().equals(class_1802.field_8162)) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cYou must be holding an item."));
               return 1;
            }

            JsonElement jsonElement = (JsonElement)class_1799.field_24671
               .encodeStart(Gts.server.method_30611().method_57093(JsonOps.INSTANCE), item)
               .getOrThrow();
            String data = Utils.newGson().toJson(jsonElement);
            CompletableFuture<Boolean> result = Utils.writeFileAsync("/config/gts/", "item.json", data);
            if (result.join()) {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§2Item successfully saved."));
            } else {
               ((class_2168)context.getSource()).method_45068(class_2561.method_43470("§cUnable to save item!."));
            }
         } catch (Exception var6) {
            ((class_2168)context.getSource()).method_45068(class_2561.method_43470(var6.getMessage()));
            var6.printStackTrace();
         }

         return 1;
      }
   }
}
