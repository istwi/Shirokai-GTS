package org.pokesplash.gts.fabric.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.class_3222;
import org.pokesplash.gts.permission.GtsPermission;
import org.pokesplash.gts.permission.PermissionProvider;

public class FabricPermissions extends PermissionProvider {
   @Override
   public boolean hasPermission(class_3222 player, String base) {
      GtsPermission permission = super.getPermission(base);
      return Permissions.check(player, permission.getNode(), permission.getLevel());
   }
}
