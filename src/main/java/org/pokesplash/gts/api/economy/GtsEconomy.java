package org.pokesplash.gts.api.economy;

import java.util.UUID;

public interface GtsEconomy {
   boolean add(UUID var1, double var2);

   boolean remove(UUID var1, double var2);

   double balance(UUID var1);
}
