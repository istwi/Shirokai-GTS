package org.pokesplash.gts.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import net.minecraft.class_1799;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2487;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Test extends Subcommand {
   public Test() {
      super("§9Usage:\n§3- gts test");
   }

   public LiteralCommandNode<class_2168> build() {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("test")
               .requires(ctx -> ctx.method_43737() ? ctx.method_44023().method_5845().equalsIgnoreCase("b5c833a0-c6f7-4e89-9ad5-d36faef37ab2") : false))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      class_1799 item = ((class_2168)context.getSource()).method_44023().method_6047();
      class_2487 tag = new class_2487();
      tag.method_10572("ivs", new ArrayList<>(java.util.List.of(31, 1, 3, 31, 31, 6)));
      item.method_57379(class_9334.field_49628, class_9279.method_57456(tag));
      return 1;
   }
}
