package org.pokesplash.gts.config;

import com.cobblemon.mod.common.CobblemonItems;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1799;
import org.pokesplash.gts.config.options.ItemPrices;
import org.pokesplash.gts.config.options.PokemonAspects;
import org.pokesplash.gts.config.options.PokemonPrices;
import org.pokesplash.gts.config.options.Webhook;
import org.pokesplash.gts.economy.CurrencySettings;
import org.pokesplash.gts.util.CodecUtils;
import org.pokesplash.gts.util.Utils;

public class Config {
   private CurrencySettings multiCurrency = new CurrencySettings();
   private int version = 3;
   private boolean enablePokemonSales = true;
   private boolean enableItemSales = true;
   private boolean enableAsyncSearches = false;
   private boolean broadcastListings = true;
   private boolean enablePermissionNodes = true;
   private int maxListingsPerPlayer = 8;
   private int listingDuration = 72;
   private Webhook discord;
   private boolean showBreedable;
   private double taxRate = 0.1;
   private double minPrice1IV = 10000.0;
   private double minPrice2IV = 20000.0;
   private double minPrice3IV = 30000.0;
   private double minPrice4IV = 40000.0;
   private double minPrice5IV = 50000.0;
   private double minPrice6IV = 60000.0;
   private double minPriceHA = 50000.0;
   private double minPriceLegendary = 50000.0;
   private double minPriceUltrabeast = 30000.0;
   private double maximumPrice = 1000000.0;
   private List<ItemPrices> customItemPrices = new ArrayList<>();
   private List<JsonElement> bannedItems;
   private List<PokemonPrices> customPokemonPrices;
   private List<PokemonAspects> bannedPokemon;
   private List<String> removedModDescriptions;

   public CurrencySettings getMultiCurrency() {
      if (this.multiCurrency == null) {
         this.multiCurrency = new CurrencySettings();
      }

      return this.multiCurrency;
   }

   public Config() {
      this.customItemPrices.add(new ItemPrices());
      this.bannedItems = new ArrayList<>();
      this.bannedItems.add(CodecUtils.encodeItem(new class_1799(CobblemonItems.LUCKY_EGG)));
      this.customPokemonPrices = new ArrayList<>();
      this.customPokemonPrices.add(new PokemonPrices());
      this.bannedPokemon = new ArrayList<>();
      this.bannedPokemon.add(new PokemonAspects());
      this.discord = new Webhook();
      this.showBreedable = false;
      this.removedModDescriptions = new ArrayList<>();
      this.removedModDescriptions.add("simpletms");
   }

   public CompletableFuture<Boolean> write() {
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      return Utils.writeFileAsync("/config/gts/", "config.json", data);
   }

   public int getVersion() {
      return this.version;
   }

   public boolean isBroadcastListings() {
      return this.broadcastListings;
   }

   public int getMaxListingsPerPlayer() {
      return this.maxListingsPerPlayer;
   }

   public int getListingDuration() {
      return this.listingDuration;
   }

   public double getMinPrice1IV() {
      return this.minPrice1IV;
   }

   public double getMinPrice2IV() {
      return this.minPrice2IV;
   }

   public double getMinPrice3IV() {
      return this.minPrice3IV;
   }

   public double getMinPrice4IV() {
      return this.minPrice4IV;
   }

   public double getMinPrice5IV() {
      return this.minPrice5IV;
   }

   public double getMinPrice6IV() {
      return this.minPrice6IV;
   }

   public double getMinPriceHA() {
      return this.minPriceHA;
   }

   public double getMaximumPrice() {
      return this.maximumPrice;
   }

   public List<ItemPrices> getCustomItemPrices() {
      return this.customItemPrices;
   }

   public List<JsonElement> getBannedItems() {
      return this.bannedItems;
   }

   public boolean isEnablePermissionNodes() {
      return this.enablePermissionNodes;
   }

   public HashSet<Double> getAllPokemonPrices() {
      HashSet<Double> prices = new HashSet<>();
      prices.add(this.minPrice1IV);
      prices.add(this.minPrice2IV);
      prices.add(this.minPrice3IV);
      prices.add(this.minPrice4IV);
      prices.add(this.minPrice5IV);
      prices.add(this.minPrice6IV);
      prices.add(this.minPriceHA);
      return prices;
   }

   public List<PokemonAspects> getBannedPokemon() {
      return this.bannedPokemon;
   }

   public double getMinPriceLegendary() {
      return this.minPriceLegendary;
   }

   public double getMinPriceUltrabeast() {
      return this.minPriceUltrabeast;
   }

   public Webhook getDiscord() {
      return this.discord;
   }

   public boolean isShowBreedable() {
      return this.showBreedable;
   }

   public List<PokemonPrices> getCustomPokemonPrices() {
      return this.customPokemonPrices;
   }

   public double getTaxRate() {
      return this.taxRate;
   }

   public boolean isEnablePokemonSales() {
      return this.enablePokemonSales;
   }

   public boolean isEnableItemSales() {
      return this.enableItemSales;
   }

   public boolean isEnableAsyncSearches() {
      return this.enableAsyncSearches;
   }

   public List<String> getRemovedModDescriptions() {
      return this.removedModDescriptions;
   }
}
