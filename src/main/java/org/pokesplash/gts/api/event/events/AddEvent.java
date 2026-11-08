package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Listing.Listing;

public class AddEvent {
   private Listing listing;
   private UUID source;

   public AddEvent(Listing listing, ServerPlayerEntity source) {
      this.listing = listing;
      this.source = source.getUuid();
   }

   public Listing getListing() {
      return this.listing;
   }

   public UUID getSource() {
      return this.source;
   }
}
