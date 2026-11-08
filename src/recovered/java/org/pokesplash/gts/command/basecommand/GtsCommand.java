package org.pokesplash.gts.command.basecommand;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.Page;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
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
   public int run(CommandContext<class_2168> context) {
      if (!((class_2168)context.getSource()).method_43737()) {
         ((class_2168)context.getSource()).method_45068(class_2561.method_43470("This command must be ran by a player."));
         return 1;
      } else {
         class_3222 sender = ((class_2168)context.getSource()).method_44023();
         if (Gts.config.isEnableAsyncSearches()) {
            this.runAsync(sender);
         } else {
            this.runSync(sender);
         }

         return 1;
      }
   }

   public void runSync(class_3222 player) {
      try {
         Page page = new AllListings().getPage();
         UIManager.openUIForcefully(player, page);
      } catch (Exception var3) {
         var3.printStackTrace();
         player.method_43496(class_2561.method_43470("§cSomething went wrong, please tell an admin to check the console."));
      }
   }

   public void runAsync(class_3222 player) {
      player.method_43496(class_2561.method_43470("§2Loading GTS Listings."));
      ASYNC_EXEC.submit(() -> {
         try {
            Page page = new AllListings().getPage();
            Gts.server.execute(() -> UIManager.openUIForcefully(player, page));
         } catch (Exception var2) {
            var2.printStackTrace();
            player.method_43496(class_2561.method_43470("§cSomething went wrong, please tell an admin to check the console."));
         }
      });
   }
}
