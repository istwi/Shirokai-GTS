package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Listing.Listing;

public class ReturnEvent {
   private UUID player;
   private Listing listing;

   public ReturnEvent(ServerPlayerEntity player, Listing listing) {
      this.player = player.getUuid();
      this.listing = listing;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public Listing getListing() {
      return this.listing;
   }
}
