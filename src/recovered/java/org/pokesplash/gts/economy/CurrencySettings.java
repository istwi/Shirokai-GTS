package org.pokesplash.gts.economy;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CurrencySettings {
   public static final String DOLLARS = "impactor:dollars";
   public static final String TOKENS = "impactor:tokens";
   public boolean enabled = true;
   public boolean allowZeroPrices = false;
   public boolean enforceMinimumPricesForTokens = false;
   public String defaultCurrency = "impactor:dollars";
   public Map<String, CurrencySettings.Display> currencies = defaults();

   public static boolean addMissingDefaults(JsonObject config, Gson gson) {
      if (config.has("multiCurrency") && !config.get("multiCurrency").isJsonNull()) {
         JsonObject section = config.getAsJsonObject("multiCurrency");
         if (!section.has("enforceMinimumPricesForTokens")) {
            section.addProperty("enforceMinimumPricesForTokens", false);
            return true;
         } else {
            return false;
         }
      } else {
         config.add("multiCurrency", gson.toJsonTree(new CurrencySettings()));
         return true;
      }
   }

   private static Map<String, CurrencySettings.Display> defaults() {
      Map<String, CurrencySettings.Display> out = new LinkedHashMap<>();
      out.put("impactor:dollars", new CurrencySettings.Display("PokéDollars", "$", "green", "minecraft:gold_ingot"));
      out.put("impactor:tokens", new CurrencySettings.Display("PokéTokens", "✦", "light_purple", "minecraft:amethyst_shard"));
      return out;
   }

   public static String legacy(String id) {
      return id != null && !id.isBlank() ? id : "impactor:dollars";
   }

   public CurrencySettings.Display display(String id) {
      CurrencySettings.Display result = this.currencies == null ? null : this.currencies.get(id);
      if (result != null && result.displayName != null && result.symbol != null && result.color != null && result.icon != null) {
         return result;
      } else {
         throw new IllegalArgumentException("Unknown currency: " + id);
      }
   }

   public void requireSelectable(String id) {
      if (!this.display(id).enabled || !this.enabled && !id.equals(this.defaultCurrency)) {
         throw new IllegalArgumentException("Disabled currency: " + id);
      }
   }

   public String color(String id) {
      CurrencySettings.Display d = this.currencies == null ? null : this.currencies.get(id);
      return d != null && d.color != null ? d.color : "gray";
   }

   public String colored(String id, double value) {
      return "<" + this.color(id) + ">" + this.format(id, value) + "</" + this.color(id) + ">";
   }

   public String format(String id, double value) {
      CurrencySettings.Display d = this.currencies == null ? null : this.currencies.get(id);
      DecimalFormat f = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.forLanguageTag("pt-BR")));
      String number = f.format(BigDecimal.valueOf(value));
      if (d == null) {
         return number + " " + id;
      } else {
         return "impactor:dollars".equals(id) ? d.symbol + number : d.symbol + " " + number + " " + d.displayName;
      }
   }

   public static final class Display {
      public boolean enabled = true;
      public String displayName;
      public String symbol;
      public String color;
      public String icon;

      public Display() {
      }

      public Display(String name, String symbol, String color, String icon) {
         this.displayName = name;
         this.symbol = symbol;
         this.color = color;
         this.icon = icon;
      }
   }
}
