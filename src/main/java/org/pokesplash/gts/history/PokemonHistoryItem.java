package org.pokesplash.gts.history;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.PokemonListing;

public class PokemonHistoryItem extends HistoryItem<Pokemon> {
   private JsonElement pokemon;

   public PokemonHistoryItem(PokemonListing listing, String buyerName) {
      super(listing.isPokemon(), listing.getSellerUuid(), listing.getSellerName(), listing.getPrice(), buyerName);
      this.currency = listing.getCurrency();
      this.pokemon = listing.getListingAsJsonObject();
   }

   public Pokemon getListing() {
      return (Pokemon)((Pair)Pokemon.getCODEC().decode(JsonOps.INSTANCE, this.pokemon).getOrThrow()).getFirst();
   }

   @Override
   public boolean isHistoryItemValid() {
      return this.pokemon != null && this.pokemon.isJsonObject();
   }

   @Override
   public MutableText getDisplayName() {
      Style base = Style.EMPTY.withItalic(false);
      Style blue = base.withColor((TextColor)TextColor.parse("blue").getOrThrow());
      Style dark_aqua = base.withColor((TextColor)TextColor.parse("dark_aqua").getOrThrow());
      Style red = base.withColor((TextColor)TextColor.parse("red").getOrThrow());
      Style yellow = base.withColor((TextColor)TextColor.parse("yellow").getOrThrow());
      Style white = base.withColor((TextColor)TextColor.parse("white").getOrThrow());
      Pokemon pokemon = this.getListing();
      boolean isShiny = pokemon.getShiny();
      MutableText displayName = pokemon.getDisplayName(Gts.showPokemonDisplayName).setStyle(isShiny ? yellow : dark_aqua);
      if (isShiny) {
         displayName.append(Text.literal("★").setStyle(red));
      }

      displayName.append(" ").append(Text.translatable("cobblemon.ui.lv.number", new Object[]{pokemon.getLevel()}).setStyle(white));
      String var10 = pokemon.getGender().toString();
      switch (var10) {
         case "MALE":
            displayName.append(Text.literal(" ♂").setStyle(blue));
            break;
         case "FEMALE":
            displayName.append(Text.literal(" ♀").setStyle(red));
      }

      return displayName;
   }
}
