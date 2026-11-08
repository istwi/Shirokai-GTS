package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.class_3222;
import net.minecraft.class_3902;
import net.minecraft.class_9334;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.PokemonListings;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

public abstract class SeePokemonListings {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getPokemonListingsButtonItem())
         .with(class_9334.field_49631, ColorUtil.parse(Gts.language.getPokemonListingsButtonLabel()))
         .with(class_9334.field_49638, class_3902.field_17274)
         .onClick(action -> {
            class_3222 sender = action.getPlayer();
            Page page = new PokemonListings().getPage(Sort.NONE);
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
