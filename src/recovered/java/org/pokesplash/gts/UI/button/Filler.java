package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import net.minecraft.class_2561;
import net.minecraft.class_3902;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;

public abstract class Filler {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getFillerItem())
         .with(class_9334.field_50074, class_3902.field_17274)
         .with(class_9334.field_49638, class_3902.field_17274)
         .with(class_9334.field_49631, class_2561.method_43473())
         .build();
   }
}
