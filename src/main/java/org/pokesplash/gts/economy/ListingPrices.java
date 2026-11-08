package org.pokesplash.gts.economy;

import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.Arrays;
import java.util.function.DoubleSupplier;
import net.minecraft.item.ItemStack;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.DataComponentTypes;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.config.Config;
import org.pokesplash.gts.config.options.ItemPrices;
import org.pokesplash.gts.config.options.PokemonPrices;
import org.pokesplash.gts.util.CodecUtils;
import org.pokesplash.gts.util.Utils;

public final class ListingPrices {
   private ListingPrices() {
   }

   public static MinimumPricePolicy.Quote pokemon(Pokemon pokemon, String currency, double price) {
      Config config = Gts.config;
      return MinimumPricePolicy.selected(config.getMultiCurrency(), currency, price, config.getMaximumPrice(), () -> pokemonMinimum(pokemon, config));
   }

   public static MinimumPricePolicy.Quote item(ItemStack item, String currency, double price) {
      Config config = Gts.config;
      return MinimumPricePolicy.selected(config.getMultiCurrency(), currency, price, config.getMaximumPrice(), () -> itemMinimum(item, config));
   }

   public static MinimumPricePolicy.Quote listing(Listing listing) {
      Config config = Gts.config;
      return listing(
         listing,
         config.getMultiCurrency(),
         config.getMaximumPrice(),
         () -> listing instanceof PokemonListing pokemon
            ? pokemonMinimum(pokemon.getListing(), config)
            : itemMinimum(((ItemListing)listing).getListing(), config)
      );
   }

   public static MinimumPricePolicy.Quote listing(Listing listing, CurrencySettings settings, double maximum, DoubleSupplier automaticMinimum) {
      return MinimumPricePolicy.selected(settings, listing.getCurrency(), listing.getPrice(), maximum, automaticMinimum);
   }

   private static double ivMinimum(int count, Config config) {
      return switch (count) {
         case 1 -> config.getMinPrice1IV();
         case 2 -> config.getMinPrice2IV();
         case 3 -> config.getMinPrice3IV();
         case 4 -> config.getMinPrice4IV();
         case 5 -> config.getMinPrice5IV();
         case 6 -> config.getMinPrice6IV();
         default -> 0.0;
      };
   }

   private static double pokemonMinimum(Pokemon pokemon, Config config) {
      int[] perfectIVs = new int[]{0};
      pokemon.getIvs().forEach(stat -> {
         if ((Integer)stat.getValue() == 31) {
            perfectIVs[0]++;
         }
      });
      double minimum = ivMinimum(perfectIVs[0], config);
      if (Utils.isHA(pokemon)) {
         minimum += config.getMinPriceHA();
      }

      if (pokemon.isLegendary()) {
         minimum += config.getMinPriceLegendary();
      }

      if (pokemon.isUltraBeast()) {
         minimum += config.getMinPriceUltrabeast();
      }

      for (PokemonPrices rule : config.getCustomPokemonPrices()) {
         if (rule.getPokemon().equals(pokemon)) {
            minimum += rule.getPrice();
            break;
         }
      }

      return minimum;
   }

   private static double itemMinimum(ItemStack item, Config config) {
      double minimum = 0.0;

      for (ItemPrices rule : config.getCustomItemPrices()) {
         ItemStack configured = CodecUtils.decodeItem(rule.getItem());
         if (configured.getItem().equals(item.getItem()) && ItemStack.areItemsAndComponentsEqual(configured, item)) {
            minimum += rule.getMinPrice();
            break;
         }
      }

      NbtComponent custom = (NbtComponent)item.get(DataComponentTypes.CUSTOM_DATA);
      if (custom != null && custom.contains("ivs")) {
         int count = (int)Arrays.stream(custom.copyNbt().getIntArray("ivs")).filter(iv -> iv == 31).count();
         minimum += ivMinimum(count, config);
      }

      return minimum;
   }

   public static String rejection(MinimumPricePolicy.Rejected failure, String currency, String asset, String seller) {
      return switch (failure.reason) {
         case INVALID_PRICE -> Gts.language.getMultiCurrency().invalidPrice;
         case TOKENS_BELOW_ONE -> Gts.language.getMultiCurrency().tokensBelowOne;
         case AUTOMATIC_MINIMUM -> Utils.formatPlaceholders(Gts.language.getMinimumListingPrice(), failure.minimum, asset, seller, null);
      };
   }
}
