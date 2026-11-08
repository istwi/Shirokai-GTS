package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.util.ColorUtil;

public abstract class ExpiredListings {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getExpiredListingsButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getExpiredListingButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page page = new org.pokesplash.gts.UI.ExpiredListings().getPage(action.getPlayer().getUuid());
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
