package org.pokesplash.gts.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.fabric.permission.FabricPermissions;
import org.pokesplash.gts.util.CommandsRegistry;

public class GtsFabric implements ModInitializer {
   public void onInitialize() {
      ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)server -> GtsTransactions.startSession());
      ServerLifecycleEvents.SERVER_STARTED
         .register((ServerStarted)server -> ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)stopped -> GtsTransactions.closeSession()));
      CommandRegistrationCallback.EVENT.register(CommandsRegistry::registerCommands);
      ServerWorldEvents.LOAD.register((Load)(t, e) -> {
         Gts.server = t;
         Gts.reloadSensitive();
      });
      ServerTickEvents.END_SERVER_TICK.register((EndTick)e -> {
         if (e.getTicks() % Gts.ticksPerCheck == 0) {
            Gts.listings.check();
         }
      });
      Gts.init();
      Gts.permissions = new FabricPermissions();
   }
}
