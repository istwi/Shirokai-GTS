package org.pokesplash.gts.Listing;

import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
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

   @Override
   public String getUiTitle() {
      return Gts.language.getPokemonTitle();
   }

   @Override
   public ItemStack getIcon() {
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
