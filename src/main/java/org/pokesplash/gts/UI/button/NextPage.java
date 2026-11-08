package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.util.ColorUtil;

public abstract class NextPage {
   public static Button getButton() {
      return LinkedPageButton.builder()
         .linkType(LinkType.Next)
         .display(Gts.language.getNextPageButtonItems())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getNextPageButtonLabel()))
         .build();
   }
}
