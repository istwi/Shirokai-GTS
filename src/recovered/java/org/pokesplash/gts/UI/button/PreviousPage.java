package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.util.ColorUtil;

public abstract class PreviousPage {
   public static Button getButton() {
      return LinkedPageButton.builder()
         .display(Gts.language.getPreviousPageButtonItems())
         .linkType(LinkType.Previous)
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getPreviousPageButtonLabel()))
         .build();
   }
}
