package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.class_3222;
import org.pokesplash.gts.Listing.Listing;

public class PurchaseEvent {
   private UUID buyer;
   private Listing listing;

   public PurchaseEvent(class_3222 buyer, Listing listing) {
      this.buyer = buyer.method_5667();
      this.listing = listing;
   }

   public UUID getBuyer() {
      return this.buyer;
   }

   public Listing getProduct() {
      return this.listing;
   }
}
