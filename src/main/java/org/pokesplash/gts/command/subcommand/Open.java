package org.pokesplash.gts.command.subcommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import java.util.UUID;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.command.argument.UuidArgumentType;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.UI.SingleListing;
import org.pokesplash.gts.command.superclass.Subcommand;

public class Open extends Subcommand {
   public Open() {
      super("§9Usage:\n§3- gts <listing id>");
   }

   @Override
   public CommandNode<ServerCommandSource> build() {
      return ((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("id", UuidArgumentType.uuid())
               .requires(ctx -> ctx.isExecutedByPlayer() ? Gts.permissions.hasPermission(ctx.getPlayer(), "base") : true))
            .executes(this::run))
         .build();
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
         UUID id = UuidArgumentType.getUuid(context, "id");
         Listing listing = Gts.listings.getActiveListingById(id);
         if (listing != null) {
            UIManager.openUIForcefully(sender, new SingleListing().getPage(sender, listing));
         }

         return 1;
      }
   }
}
