package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import java.util.UUID;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_5242;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.UI.SingleListing;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Open extends Subcommand {
   public Open() {
      super("§9Usage:\n§3- gts <listing id>");
   }

   @Override
   public CommandNode<class_2168> build() {
      return ((RequiredArgumentBuilder)((RequiredArgumentBuilder)class_2170.method_9244("id", class_5242.method_27643())
               .requires(ctx -> ctx.method_43737() ? Gts.permissions.hasPermission(ctx.method_44023(), "base") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 sender = ((class_2168)context.getSource()).method_44023();
         UUID id = class_5242.method_27645(context, "id");
         Listing listing = Gts.listings.getActiveListingById(id);
         if (listing != null) {
            UIManager.openUIForcefully(sender, new SingleListing().getPage(sender, listing));
         }

         return 1;
      }
   }
}
