package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;

public abstract class Filler {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getFillerItem())
         .with(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE)
         .with(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
         .with(DataComponentTypes.CUSTOM_NAME, Text.empty())
         .build();
   }
}
