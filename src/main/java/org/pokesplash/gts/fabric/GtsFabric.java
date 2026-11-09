package org.pokesplash.gts.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
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
      net.fabricmc.fabric.api.message.v1.ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) ->
         !org.pokesplash.gts.UI.PrivateInputs.capture(sender, message.getSignedContent()));
      net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
         org.pokesplash.gts.UI.PrivateInputs.cancel(handler.player.getUuid());
         org.pokesplash.gts.UI.ListingBrowser.disconnect(handler.player.getUuid());
      });
      ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)server -> GtsTransactions.startSession());
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)server -> GtsTransactions.closeSession());
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
