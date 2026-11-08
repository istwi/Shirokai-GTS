package org.pokesplash.gts.economy;

import java.util.function.DoubleSupplier;

public final class MinimumPricePolicy {
   private MinimumPricePolicy() {
   }

   public static void beforeSelection(double price, boolean allowZero, double maximum) {
      try {
         Money.amount(price, allowZero);
      } catch (IllegalArgumentException var6) {
         throw new MinimumPricePolicy.Rejected(MinimumPricePolicy.Reason.INVALID_PRICE, 0.0);
      }

      if (price > maximum) {
         throw new MinimumPricePolicy.Rejected(MinimumPricePolicy.Reason.INVALID_PRICE, 0.0);
      }
   }

   public static MinimumPricePolicy.Quote selected(CurrencySettings settings, String currency, double price, double maximum, DoubleSupplier automaticMinimum) {
      beforeSelection(price, settings.allowZeroPrices, maximum);
      settings.requireSelectable(currency);
      if ("impactor:tokens".equals(currency) && price < 1.0) {
         throw new MinimumPricePolicy.Rejected(MinimumPricePolicy.Reason.TOKENS_BELOW_ONE, 1.0);
      } else {
         boolean enforce = !"impactor:tokens".equals(currency) || settings.enforceMinimumPricesForTokens;
         if (!enforce) {
            return new MinimumPricePolicy.Quote(false, 0.0);
         } else {
            double minimum = automaticMinimum.getAsDouble();
            if (!Double.isFinite(minimum) || minimum < 0.0) {
               throw new MinimumPricePolicy.Rejected(MinimumPricePolicy.Reason.INVALID_PRICE, 0.0);
            } else if (price < minimum) {
               throw new MinimumPricePolicy.Rejected(MinimumPricePolicy.Reason.AUTOMATIC_MINIMUM, minimum);
            } else {
               return new MinimumPricePolicy.Quote(true, minimum);
            }
         }
      }
   }

   public record Quote(boolean enforcesAutomaticMinimum, double minimum) {
   }

   public static enum Reason {
      INVALID_PRICE,
      TOKENS_BELOW_ONE,
      AUTOMATIC_MINIMUM;
   }

   public static final class Rejected extends IllegalArgumentException {
      public final MinimumPricePolicy.Reason reason;
      public final double minimum;

      private Rejected(MinimumPricePolicy.Reason reason, double minimum) {
         super(reason.name());
         this.reason = reason;
         this.minimum = minimum;
      }
   }
}
