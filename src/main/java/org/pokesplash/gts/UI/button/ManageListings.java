package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.util.ColorUtil;

public abstract class ManageListings {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getManageListingsButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getManageListingsButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page page = new org.pokesplash.gts.UI.ManageListings().getPage(action.getPlayer().getUuid());
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
