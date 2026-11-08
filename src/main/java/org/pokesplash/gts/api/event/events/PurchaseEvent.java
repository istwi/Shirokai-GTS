package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;
import org.pokesplash.gts.Listing.Listing;

public class PurchaseEvent {
   private UUID buyer;
   private Listing listing;

   public PurchaseEvent(ServerPlayerEntity buyer, Listing listing) {
      this.buyer = buyer.getUuid();
      this.listing = listing;
   }

   public UUID getBuyer() {
      return this.buyer;
   }

   public Listing getProduct() {
      return this.listing;
   }
}
