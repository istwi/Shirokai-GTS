package org.pokesplash.gts.api.event.events;

import java.util.UUID;
import net.minecraft.class_3222;
import org.pokesplash.gts.Listing.Listing;

public class AddEvent {
   private Listing listing;
   private UUID source;

   public AddEvent(Listing listing, class_3222 source) {
      this.listing = listing;
      this.source = source.method_5667();
   }

   public Listing getListing() {
      return this.listing;
   }

   public UUID getSource() {
      return this.source;
   }
}
