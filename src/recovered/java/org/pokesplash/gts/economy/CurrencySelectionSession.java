package org.pokesplash.gts.economy;

import java.util.function.Consumer;
import java.util.function.Function;

public final class CurrencySelectionSession {
   private final Function<String, MinimumPricePolicy.Quote> validate;
   private String selected;
   private boolean consumed;

   public CurrencySelectionSession(Function<String, MinimumPricePolicy.Quote> validate) {
      this.validate = validate;
   }

   public synchronized MinimumPricePolicy.Quote select(String currency) {
      if (this.consumed) {
         return null;
      } else {
         this.selected = null;
         MinimumPricePolicy.Quote quote = this.validate.apply(currency);
         if (quote != null) {
            this.selected = currency;
         }

         return quote;
      }
   }

   public synchronized boolean confirm(String currency, Consumer<String> publish) {
      if (!this.consumed && currency.equals(this.selected)) {
         this.selected = null;
         if (this.validate.apply(currency) == null) {
            return false;
         } else {
            this.consumed = true;
            publish.accept(currency);
            return true;
         }
      } else {
         return false;
      }
   }

   public synchronized void cancel() {
      this.consumed = true;
      this.selected = null;
   }

   public synchronized void clearSelection() {
      this.selected = null;
   }

   public synchronized boolean isConsumed() {
      return this.consumed;
   }
}
