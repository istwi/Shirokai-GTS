package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.AllListings;
import org.pokesplash.gts.util.ColorUtil;

public abstract class RelistAll {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getRelistExpiredButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getRelistExpiredButtonLabel()))
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Gts.listings.relistAllExpiredListings(sender.getUuid());
            Page page = new AllListings().getPage();
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
