package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.class_3222;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.AllListings;
import org.pokesplash.gts.util.ColorUtil;

public abstract class RelistAll {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getRelistExpiredButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getRelistExpiredButtonLabel()))
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Gts.listings.relistAllExpiredListings(sender.method_5667());
            Page page = new AllListings().getPage();
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
