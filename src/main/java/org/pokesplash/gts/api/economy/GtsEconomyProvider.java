package org.pokesplash.gts.api.economy;

import java.util.HashMap;
import org.pokesplash.gts.enumeration.Priority;
import org.pokesplash.gts.impactor.ImpactorService;

public abstract class GtsEconomyProvider {
   private static HashMap<Priority, GtsEconomy> providers = new HashMap<>();

   public static void putEconomy(Priority priority, GtsEconomy economy) {
      providers.put(priority, economy);
   }

   public static GtsEconomy getEconomy(Priority priority) {
      return providers.get(priority);
   }

   public static GtsEconomy getHighestEconomy() {
      return new ImpactorService();
   }
}
