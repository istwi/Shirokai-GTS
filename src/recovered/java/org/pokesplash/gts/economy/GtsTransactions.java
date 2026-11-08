package org.pokesplash.gts.economy;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.class_1799;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.impactor.ImpactorService;
import org.pokesplash.gts.mixin.PlayerListAccessor;
import org.pokesplash.gts.util.Utils;

public final class GtsTransactions {
   private static final TransactionJournal JOURNAL = new TransactionJournal(Path.of("config/gts/transactions"));
   private static final SaleCoordinator SALES = new SaleCoordinator(JOURNAL);

   private GtsTransactions() {
   }

   public static void startSession() {
      try {
         int held = JOURNAL.beginSession();
         if (held > 0) {
            Gts.LOGGER.error("GTS retained " + held + " transactions after unclean shutdown; reconcile config/gts/transactions");
         }
      } catch (IOException var1) {
         throw new IllegalStateException("GTS transaction storage unavailable", var1);
      }
   }

   public static void closeSession() {
      try {
         JOURNAL.endSession();
      } catch (IOException var1) {
         Gts.LOGGER.error("GTS clean shutdown marker could not be saved; recovery will be required");
      }
   }

   public static boolean blocked(Listing listing) {
      return JOURNAL.blocked(listing.getId());
   }

   public static boolean permitted(class_3222 player, String currency) {
      String permission = currency.equals("impactor:dollars") ? "currencyDollars" : (currency.equals("impactor:tokens") ? "currencyTokens" : null);
      return permission == null || Gts.permissions.hasPermission(player, permission);
   }

   public static String message(String template, Listing listing) {
      return message(template, listing, "");
   }

   public static String message(String template, Listing listing, String buyerName) {
      String name;
      try {
         name = Gts.config.getMultiCurrency().display(listing.getCurrency()).displayName;
      } catch (IllegalArgumentException var5) {
         name = listing.getCurrency();
      }

      return template.replace("{currency}", name)
         .replace("{listing}", listing.getListingName())
         .replace("{price}", listing.getPriceAsString())
         .replace("{seller}", listing.getSellerName())
         .replace("{buyer}", buyerName)
         .replace("{min_price}", Double.toString(listing.getPrice()))
         .replace("{max_price}", Double.toString(Gts.config.getMaximumPrice()))
         .replace("{max_listings}", Integer.toString(Gts.config.getMaxListingsPerPlayer()));
   }

   public static void requireServerThread() {
      if (Gts.server == null || !Gts.server.method_18854()) {
         throw new IllegalStateException("Server thread required");
      }
   }

   public static void savePlayer(class_3222 player) {
      ((PlayerListAccessor)player.method_5682().method_3760()).gts$savePlayer(player);
   }

   public static TransactionJournal.Record beginCreation(Listing listing, class_3222 seller) throws IOException {
      if (blocked(listing)) {
         throw new IllegalStateException("Listing held");
      } else {
         TransactionJournal.Record record = new TransactionJournal.Record(
            listing.getId(),
            seller.method_5667(),
            seller.method_5667(),
            listing.getCurrency(),
            BigDecimal.valueOf(listing.getPrice()),
            BigDecimal.ZERO,
            Utils.newGson().toJson(listing),
            seller.method_5477().getString()
         );
         JOURNAL.phase(record, "CREATE_INTENT");
         return record;
      }
   }

   public static void finishCreation(TransactionJournal.Record record) throws IOException {
      JOURNAL.phase(record, "CREATED");
   }

   public static boolean claim(class_3222 player, Listing listing, boolean expired) {
      requireServerThread();
      if (player.method_5667().equals(listing.getSellerUuid()) && !blocked(listing)) {
         Listing canonical = expired ? Gts.listings.getExpiredListingById(listing.getId()) : Gts.listings.getActiveListingById(listing.getId());
         if (canonical != null && player.method_5667().equals(canonical.getSellerUuid()) && !player.method_14239()) {
            listing = canonical;
            if (canonical instanceof ItemListing item && !Utils.hasSpace(player, item.getListing())) {
               return false;
            } else {
               TransactionJournal.Record record = new TransactionJournal.Record(
                  canonical.getId(),
                  player.method_5667(),
                  canonical.getSellerUuid(),
                  canonical.getCurrency(),
                  BigDecimal.ZERO,
                  BigDecimal.ZERO,
                  Utils.newGson().toJson(canonical),
                  player.method_5477().getString()
               );

               try {
                  JOURNAL.phase(record, "RETURN_INTENT");
                  if (!listing.delete("/config/gts/listings")) {
                     throw new IllegalStateException("Delete failed");
                  } else {
                     boolean removed = expired ? Gts.listings.removeExpiredListing(listing) : Gts.listings.removeListing(listing);
                     if (!removed) {
                        throw new IllegalStateException("Remove failed");
                     } else {
                        JOURNAL.phase(record, "DELIVERY_INTENT");
                        boolean delivered;
                        if (listing instanceof PokemonListing pokemon) {
                           PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
                           UUID id = pokemon.getListing().getUuid();
                           if (storage.getParty(player).get(id) != null || storage.getPC(player).get(id) != null) {
                              throw new IllegalStateException("Pokemon already in storage");
                           }

                           delivered = storage.getParty(player).add(pokemon.getListing());
                        } else {
                           class_1799 item = ((ItemListing)listing).getListing();
                           delivered = player.method_31548().method_7394(item) && item.method_7960();
                        }

                        if (!delivered) {
                           throw new IllegalStateException("Delivery uncertain");
                        } else {
                           savePlayer(player);
                           JOURNAL.phase(record, "RETURNED");
                           return true;
                        }
                     }
                  }
               } catch (Exception var10) {
                  Gts.LOGGER.error("Claim " + canonical.getId() + " held at " + record.phase + "; cause=" + var10.getClass().getSimpleName());
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean sale(class_3222 buyer, Listing supplied) throws Exception {
      requireServerThread();
      final Listing listing = Gts.listings.getActiveListingById(supplied.getId());
      if (listing != null && !blocked(listing)) {
         final BigDecimal price;
         final BigDecimal proceeds;
         try {
            price = Money.amount(listing.getPrice(), Gts.config.getMultiCurrency().allowZeroPrices);
            proceeds = Money.proceeds(price, Gts.config.getTaxRate());
         } catch (IllegalArgumentException var8) {
            throw new IllegalStateException(Gts.language.getMultiCurrency().invalidPrice, var8);
         }

         final ImpactorService economy = new ImpactorService(listing.getCurrency());
         TransactionJournal.Record record = new TransactionJournal.Record(
            listing.getId(),
            buyer.method_5667(),
            listing.getSellerUuid(),
            listing.getCurrency(),
            price,
            proceeds,
            Utils.newGson().toJson(listing),
            buyer.method_5477().getString()
         );

         try {
            return SALES.execute(
               record,
               new SaleCoordinator.Port() {
                  @Override
                  public void validate() {
                     if (!listing.getSellerUuid().equals(buyer.method_5667())
                        && !buyer.method_14239()
                        && Gts.listings.getActiveListingById(listing.getId()) == listing
                        && (listing.getEndTime() == -1L || listing.getEndTime() > System.currentTimeMillis())) {
                        try {
                           Gts.config.getMultiCurrency().requireSelectable(listing.getCurrency());
                           economy.currency();
                        } catch (RuntimeException var4) {
                           throw new IllegalStateException(Gts.language.getMultiCurrency().currencyUnavailable, var4);
                        }

                        if (!GtsTransactions.permitted(buyer, listing.getCurrency())) {
                           throw new IllegalStateException(Gts.language.getMultiCurrency().currencyUnavailable);
                        } else if (price.compareTo(economy.decimalBalance(buyer.method_5667())) > 0) {
                           throw new IllegalStateException(GtsTransactions.message(Gts.language.getMultiCurrency().insufficientFunds, listing));
                        } else {
                           if (listing instanceof PokemonListing pokemon) {
                              UUID id = pokemon.getListing().getUuid();
                              PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
                              if (storage.getParty(listing.getSellerUuid(), buyer.method_56673()).get(id) != null
                                 || storage.getPC(listing.getSellerUuid(), buyer.method_56673()).get(id) != null) {
                                 throw new IllegalStateException(Gts.language.getMultiCurrency().unavailable);
                              }
                           } else if (!Utils.hasSpace(buyer, ((ItemListing)listing).getListing())) {
                              throw new IllegalStateException(Gts.language.getInsufficientInventorySpace());
                           }
                        }
                     } else {
                        throw new IllegalStateException(Gts.language.getMultiCurrency().unavailable);
                     }
                  }

                  @Override
                  public void removeListing() {
                     if (!listing.delete("/config/gts/listings") || !Gts.listings.removeListing(listing)) {
                        throw new IllegalStateException("Listing removal failed");
                     }
                  }

                  @Override
                  public void restoreListing() {
                     if (!Gts.listings.addListing(listing)) {
                        throw new IllegalStateException("Listing restoration failed");
                     }
                  }

                  @Override
                  public boolean debit() {
                     return economy.withdraw(buyer.method_5667(), price);
                  }

                  @Override
                  public boolean credit() {
                     return economy.deposit(listing.getSellerUuid(), proceeds);
                  }

                  @Override
                  public boolean refund() {
                     return economy.deposit(buyer.method_5667(), price);
                  }

                  @Override
                  public void deliver() {
                     boolean delivered;
                     if (listing instanceof PokemonListing pokemon) {
                        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(buyer);
                        delivered = party.add(pokemon.getListing());
                     } else {
                        class_1799 item = ((ItemListing)listing).getListing();
                        delivered = buyer.method_31548().method_7394(item) && item.method_7960();
                     }

                     if (!delivered) {
                        throw new IllegalStateException("Delivery uncertain");
                     } else {
                        GtsTransactions.savePlayer(buyer);
                     }
                  }

                  @Override
                  public void history() {
                     Gts.history.addHistoryItem(listing, buyer.method_5477().getString());
                  }
               }
            );
         } catch (Exception var9) {
            Gts.LOGGER
               .error(
                  "Purchase " + listing.getId() + " failed at " + record.phase + "; held=" + blocked(listing) + "; cause=" + var9.getClass().getSimpleName()
               );
            if (record.phase == null) {
               throw var9;
            } else {
               throw new IllegalStateException(Gts.language.getMultiCurrency().economyError, var9);
            }
         }
      } else {
         throw new IllegalStateException(Gts.language.getMultiCurrency().unavailable);
      }
   }
}
