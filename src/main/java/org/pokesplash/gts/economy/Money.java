package org.pokesplash.gts.economy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money {
   private Money() {
   }

   public static BigDecimal amount(double value, boolean allowZero) {
      if (Double.isFinite(value) && !(value < 0.0) && (allowZero || value != 0.0) && !(value > 1.0E12)) {
         try {
            return BigDecimal.valueOf(value).setScale(2, RoundingMode.UNNECESSARY);
         } catch (ArithmeticException var4) {
            throw new IllegalArgumentException("Invalid price precision", var4);
         }
      } else {
         throw new IllegalArgumentException("Invalid price");
      }
   }

   public static BigDecimal proceeds(BigDecimal price, double tax) {
      if (Double.isFinite(tax) && !(tax < 0.0) && !(tax > 1.0)) {
         return price.multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(tax))).setScale(2, RoundingMode.DOWN);
      } else {
         throw new IllegalArgumentException("Invalid tax rate");
      }
   }
}
