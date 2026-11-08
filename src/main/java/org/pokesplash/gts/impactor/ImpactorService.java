package org.pokesplash.gts.impactor;

import java.math.BigDecimal;
import java.util.UUID;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.kyori.adventure.key.Key;
import org.pokesplash.gts.api.economy.GtsEconomy;
import org.pokesplash.gts.economy.Money;

public class ImpactorService implements GtsEconomy {
   private final String currencyId;
   private final EconomyService suppliedService;

   private EconomyService service() {
      return this.suppliedService == null ? EconomyService.instance() : this.suppliedService;
   }

   public ImpactorService() {
      this("impactor:dollars");
   }

   public ImpactorService(String currencyId) {
      this(null, currencyId);
   }

   public ImpactorService(EconomyService service, String currencyId) {
      this.suppliedService = service;
      this.currencyId = currencyId;
   }

   public Currency currency() {
      return (Currency)this.service()
         .currencies()
         .currency(Key.key(this.currencyId))
         .orElseThrow(() -> new IllegalStateException("Currency not registered: " + this.currencyId));
   }

   private Account account(UUID owner) {
      Currency expected = this.currency();
      Account account = (Account)this.service().account(expected, owner).join();
      if (account.currency().key().equals(expected.key()) && account.owner().equals(owner)) {
         return account;
      } else {
         throw new IllegalStateException("Unexpected economy account");
      }
   }

   public BigDecimal decimalBalance(UUID owner) {
      return this.account(owner).balance();
   }

   public boolean deposit(UUID owner, BigDecimal amount) {
      return this.mutate(owner, amount, true);
   }

   public boolean withdraw(UUID owner, BigDecimal amount) {
      return this.mutate(owner, amount, false);
   }

   private boolean mutate(UUID owner, BigDecimal amount, boolean deposit) {
      if (amount.signum() < 0) {
         throw new IllegalArgumentException("Negative amount");
      } else if (amount.signum() == 0) {
         return true;
      } else {
         Account account = this.account(owner);
         EconomyTransaction result = deposit ? account.deposit(amount) : account.withdraw(amount);
         if (!result.successful()) {
            String type = result.result().name();
            if (!type.equals("NOT_ENOUGH_FUNDS") && !type.equals("NO_REMAINING_SPACE") && !type.equals("CANCELLED")) {
               throw new IllegalStateException("Ambiguous economy result: " + type);
            } else {
               return false;
            }
         } else {
            this.service().save(account).join();
            return true;
         }
      }
   }

   @Override
   public boolean add(UUID owner, double amount) {
      return this.deposit(owner, Money.amount(amount, true));
   }

   @Override
   public boolean remove(UUID owner, double amount) {
      return this.withdraw(owner, Money.amount(amount, true));
   }

   @Override
   public double balance(UUID owner) {
      return this.decimalBalance(owner).doubleValue();
   }
}
