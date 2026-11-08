package org.pokesplash.gts.UI.button;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.UI.PokemonListings;
import org.pokesplash.gts.enumeration.Sort;
import org.pokesplash.gts.util.ColorUtil;

public abstract class SeePokemonListings {
   public static Button getButton() {
      return GooeyButton.builder()
         .display(Gts.language.getPokemonListingsButtonItem())
         .with(DataComponentTypes.CUSTOM_NAME, ColorUtil.parse(Gts.language.getPokemonListingsButtonLabel()))
         .with(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE)
         .onClick(action -> {
            ServerPlayerEntity sender = action.getPlayer();
            Page page = new PokemonListings().getPage(Sort.NONE);
            UIManager.openUIForcefully(sender, page);
         })
         .build();
   }
}
