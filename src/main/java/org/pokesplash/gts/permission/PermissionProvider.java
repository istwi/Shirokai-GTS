package org.pokesplash.gts.permission;

import java.util.HashMap;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class PermissionProvider {
   private HashMap<String, GtsPermission> permissions = new HashMap<>();

   public PermissionProvider() {
      this.permissions.put("currencyDollars", new GtsPermission("currencyDollars", "gts.currency.dollars", 0));
      this.permissions.put("currencyTokens", new GtsPermission("currencyTokens", "gts.currency.tokens", 0));
      this.permissions.put("base", new GtsPermission("base", "gts.user.base", 0));
      this.permissions.put("debug", new GtsPermission("debug", "gts.admin.debug", 3));
      this.permissions.put("expired", new GtsPermission("expired", "gts.user.expired", 0));
      this.permissions.put("getprice", new GtsPermission("getprice", "gts.user.price", 0));
      this.permissions.put("history", new GtsPermission("history", "gts.user.history", 0));
      this.permissions.put("sell", new GtsPermission("sell", "gts.user.sell", 0));
      this.permissions.put("manage", new GtsPermission("manage", "gts.user.manage", 0));
      this.permissions.put("reload", new GtsPermission("reload", "gts.admin.reload", 3));
      this.permissions.put("recover", new GtsPermission("recover", "gts.admin.recover", 4));
      this.permissions.put("saveitem", new GtsPermission("saveitem", "gts.admin.saveitem", 3));
      this.permissions.put("search", new GtsPermission("search", "gts.user.search", 0));
      this.permissions.put("timeout", new GtsPermission("timeout", "gts.moderation.timeout", 2));
      this.permissions.put("remove", new GtsPermission("remove", "gts.moderation.remove", 2));
      this.permissions.put("itemdesc", new GtsPermission("itemdesc", "gts.admin.itemdesc", 3));
      this.permissions.put("version", new GtsPermission("version", "gts.admin.version", 3));
   }

   public HashMap<String, GtsPermission> getPermissions() {
      return this.permissions;
   }

   public GtsPermission getPermission(String permission) {
      return this.permissions.get(permission);
   }

   public abstract boolean hasPermission(ServerPlayerEntity var1, String var2);
}
