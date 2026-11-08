package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.class_3222;
import org.pokesplash.gts.Listing.Listing;

public class ReturnEvent {
   private UUID player;
   private Listing listing;

   public ReturnEvent(class_3222 player, Listing listing) {
      this.player = player.method_5667();
      this.listing = listing;
   }

   public UUID getPlayer() {
      return this.player;
   }

   public Listing getListing() {
      return this.listing;
   }
}
