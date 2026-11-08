package org.pokesplash.gts.api.provider;

import java.util.HashMap;
import java.util.Map;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ListingsProvider;

public abstract class ListingsProviderAPI {
   private static HashMap<Priority, ListingsProvider> providers = new HashMap<>();

   public static void add(Priority priority, ListingsProvider provider) {
      providers.put(priority, provider);
      Gts.listings = getHighestPriority();
      Gts.listings.init();
   }

   public static void remove(Priority priority) {
      providers.remove(priority);
      Gts.listings = getHighestPriority();
      Gts.listings.init();
   }

   public static Map<Priority, ListingsProvider> getProviders() {
      return providers;
   }

   public static ListingsProvider get(Priority priority) {
      return providers.get(priority);
   }

   public static ListingsProvider getHighestPriority() {
      if (providers.get(Priority.HIGHEST) != null) {
         return providers.get(Priority.HIGHEST);
      } else if (providers.get(Priority.HIGH) != null) {
         return providers.get(Priority.HIGH);
      } else if (providers.get(Priority.MEDIUM) != null) {
         return providers.get(Priority.MEDIUM);
      } else if (providers.get(Priority.LOW) != null) {
         return providers.get(Priority.LOW);
      } else {
         return providers.get(Priority.LOW) != null ? providers.get(Priority.LOWEST) : new ListingsProvider();
      }
   }
}
