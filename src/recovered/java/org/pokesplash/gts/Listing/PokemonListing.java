package org.pokesplash.gts.Listing;

import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import org.pokesplash.gts.Gts;

public class PokemonListing extends Listing<Pokemon> {
   private final JsonElement pokemon;

   public PokemonListing(UUID sellerUuid, String sellerName, double price, Pokemon pokemon) {
      this(sellerUuid, sellerName, price, pokemon, Gts.config.getMultiCurrency().defaultCurrency);
   }

   public PokemonListing(UUID sellerUuid, String sellerName, double price, Pokemon pokemon, String currency) {
      super(sellerUuid, sellerName, price, true, currency);
      this.pokemon = (JsonElement)Pokemon.getCODEC().encodeStart(JsonOps.INSTANCE, pokemon).getOrThrow();
   }

   public PokemonListing(PokemonListing other) {
      super(UUID.fromString(other.getSellerUuid().toString()), String.copyValueOf(other.getSellerName().toCharArray()), other.getPrice(), true);
      this.pokemon = (JsonElement)Pokemon.getCODEC().encodeStart(JsonOps.INSTANCE, other.getListing()).getOrThrow();
      super.id = UUID.fromString(other.getId().toString());
      super.version = String.copyValueOf(other.getVersion().toCharArray());
      super.setEndTime(other.getEndTime());
      super.currency = other.getCurrency();
   }

   public Pokemon getListing() {
      return (Pokemon)((Pair)Pokemon.getCODEC().decode(JsonOps.INSTANCE, this.pokemon).getOrThrow()).getFirst();
   }

   @Override
   public boolean isListingValid() {
      return this.pokemon != null && this.pokemon.isJsonObject();
   }

   public JsonElement getListingAsJsonObject() {
      return this.pokemon;
   }

   @Override
   public class_5250 getDisplayName() {
      class_2583 base = class_2583.field_24360.method_10978(false);
      class_2583 blue = base.method_27703((class_5251)class_5251.method_27719("blue").getOrThrow());
      class_2583 dark_aqua = base.method_27703((class_5251)class_5251.method_27719("dark_aqua").getOrThrow());
      class_2583 red = base.method_27703((class_5251)class_5251.method_27719("red").getOrThrow());
      class_2583 yellow = base.method_27703((class_5251)class_5251.method_27719("yellow").getOrThrow());
      class_2583 white = base.method_27703((class_5251)class_5251.method_27719("white").getOrThrow());
      Pokemon pokemon = this.getListing();
      boolean isShiny = pokemon.getShiny();
      class_5250 displayName = pokemon.getDisplayName(Gts.showPokemonDisplayName).method_10862(isShiny ? yellow : dark_aqua);
      if (isShiny) {
         displayName.method_10852(class_2561.method_43470("★").method_10862(red));
      }

      displayName.method_27693(" ").method_10852(class_2561.method_43469("cobblemon.ui.lv.number", new Object[]{pokemon.getLevel()}).method_10862(white));
      String var10 = pokemon.getGender().toString();
      switch (var10) {
         case "MALE":
            displayName.method_10852(class_2561.method_43470(" ♂").method_10862(blue));
            break;
         case "FEMALE":
            displayName.method_10852(class_2561.method_43470(" ♀").method_10862(red));
      }

      return displayName;
   }

   @Override
   public String getUiTitle() {
      return Gts.language.getPokemonTitle();
   }

   @Override
   public class_1799 getIcon() {
      return PokemonItem.from(this.getListing(), 1);
   }

   @Override
   public Listing deepClone() {
      return new PokemonListing(this);
   }

   @Override
   public String getListingName() {
      return this.getListing().getSpecies().toString();
   }
}
