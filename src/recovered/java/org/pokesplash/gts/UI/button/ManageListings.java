package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.class_3222;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.util.ColorUtil;

public abstract class ManageListings {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getManageListingsButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getManageListingsButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page page = new org.pokesplash.gts.UI.ManageListings().getPage(action.getPlayer().method_5667());
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
