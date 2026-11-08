package org.pokesplash.gts.command.basecommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.AllListings;
import org.pokesplash.gts.command.subcommand.Debug;
import org.pokesplash.gts.command.subcommand.Expired;
import org.pokesplash.gts.command.subcommand.GetPrice;
import org.pokesplash.gts.command.subcommand.History;
import org.pokesplash.gts.command.subcommand.ItemDescription;
import org.pokesplash.gts.command.subcommand.List;
import org.pokesplash.gts.command.subcommand.Manage;
import org.pokesplash.gts.command.subcommand.Open;
import org.pokesplash.gts.command.subcommand.OpenItems;
import org.pokesplash.gts.command.subcommand.OpenPokemon;
import org.pokesplash.gts.command.subcommand.Reload;
import org.pokesplash.gts.command.subcommand.SaveItem;
import org.pokesplash.gts.command.subcommand.Search;
import org.pokesplash.gts.command.subcommand.Timeout;
import org.pokesplash.gts.command.subcommand.Version;
import org.pokesplash.gts.command.superclass.BaseCommand;

public class GtsCommand extends BaseCommand {
   private static final ScheduledExecutorService ASYNC_EXEC = Executors.newScheduledThreadPool(
      2, new ThreadFactoryBuilder().setNameFormat("GTS-Search-Thread-#%d").setDaemon(true).build()
   );

   public GtsCommand() {
      super(
         "gts",
         Arrays.asList("gts"),
         "base",
         Arrays.asList(
            new Manage(),
            new Expired(),
            new List(),
            new History(),
            new Reload(),
            new Open(),
            new Debug(),
            new Search(),
            new GetPrice(),
            new Timeout(),
            new SaveItem(),
            new ItemDescription(),
            new OpenItems(),
            new OpenPokemon(),
            new Version()
         )
      );
   }

   @Override
   public int run(CommandContext<ServerCommandSource> context) {
      if (!((ServerCommandSource)context.getSource()).isExecutedByPlayer()) {
         ((ServerCommandSource)context.getSource()).sendMessage(Text.literal("This command must be ran by a player."));
         return 1;
      } else {
         ServerPlayerEntity sender = ((ServerCommandSource)context.getSource()).getPlayer();
         if (Gts.config.isEnableAsyncSearches()) {
            this.runAsync(sender);
         } else {
            this.runSync(sender);
         }

         return 1;
      }
   }

   public void runSync(ServerPlayerEntity player) {
      try {
         Page page = new AllListings().getPage();
         UIManager.openUIForcefully(player, page);
      } catch (Exception var3) {
         var3.printStackTrace();
         player.sendMessage(Text.literal("§cSomething went wrong, please tell an admin to check the console."));
      }
   }

   public void runAsync(ServerPlayerEntity player) {
      player.sendMessage(Text.literal("§2Loading GTS Listings."));
      ASYNC_EXEC.submit(() -> {
         try {
            Page page = new AllListings().getPage();
            Gts.server.execute(() -> UIManager.openUIForcefully(player, page));
         } catch (Exception var2) {
            var2.printStackTrace();
            player.sendMessage(Text.literal("§cSomething went wrong, please tell an admin to check the console."));
         }
      });
   }
}
