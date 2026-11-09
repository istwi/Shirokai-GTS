package org.pokesplash.gts.economy;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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

   public record RecoveryEntry(UUID id, String phase, boolean eligible) {}

   public static java.util.List<RecoveryEntry> recoveryReport() {
      requireServerThread();
      java.util.Map<UUID, Listing> all = new java.util.LinkedHashMap<>();
      Gts.listings.getListings().forEach(l -> all.put(l.getId(), l));
      Gts.listings.getExpiredListings().values().forEach(entries -> entries.forEach(l -> all.put(l.getId(), l)));
      java.util.List<RecoveryEntry> report = new java.util.ArrayList<>();
      for (Listing listing : all.values()) {
         var inspection = JOURNAL.inspect(listing.getId());
         if (inspection.blocked()) report.add(new RecoveryEntry(listing.getId(), inspection.phase(), recoveryMatches(listing, inspection.record())));
      }
      return report;
   }

   private static boolean recoveryMatches(Listing listing, TransactionJournal.Record receipt) {
      if (org.pokesplash.gts.api.provider.ListingAPI.getHighestPriority() != null || !listing.isListingValid()) return false;
      if (Gts.listings.getActiveListingById(listing.getId()) != null && Gts.listings.getExpiredListingById(listing.getId()) != null) return false;
      try {
         if (listing.getIcon().isEmpty()) return false;
         if (listing instanceof PokemonListing pokemon) {
            ServerPlayerEntity seller = Gts.server.getPlayerManager().getPlayer(listing.getSellerUuid());
            if (seller != null) {
               UUID pokemonId = pokemon.getListing().getUuid();
               if (Cobblemon.INSTANCE.getStorage().getParty(seller).get(pokemonId) != null
                   || Cobblemon.INSTANCE.getStorage().getPC(seller).get(pokemonId) != null) return false;
            }
         }
         return RecoveryPolicy.matches(receipt, Utils.newGson().toJsonTree(listing).getAsJsonObject());
      } catch (RuntimeException error) { return false; }
   }

   public static int recoverCompletedCreations(String actor) {
      requireServerThread();
      int restored = 0;
      for (RecoveryEntry entry : recoveryReport()) {
         if (!entry.eligible()) continue;
         Listing listing = Gts.listings.getListingById(entry.id());
         var receipt = JOURNAL.inspect(entry.id()).record();
         if (listing == null || !recoveryMatches(listing, receipt)) continue;
         try {
            JOURNAL.restoreCompletedCreation(receipt, actor);
            restored++;
            Gts.LOGGER.info("Recovered completed creation " + entry.id() + " by " + actor + "; receipt backed up");
         } catch (IOException error) { Gts.LOGGER.error("Could not recover " + entry.id() + ": " + error.getMessage()); }
      }
      return restored;
   }

   public static String claimFailure(ServerPlayerEntity player, Listing listing, boolean expired) {
      if (!player.getUuid().equals(listing.getSellerUuid())) return "§cSomente o dono pode retirar este anúncio.";
      var inspection = JOURNAL.inspect(listing.getId());
      if (inspection.blocked()) {
         if (RecoveryPolicy.completedCreation(inspection.record())) {
            return "§cAnúncio antigo retido após reinício. Peça a um administrador para usar /gts recuperar. ID: " + listing.getId();
         }
         return "§cTransação em verificação (" + inspection.phase() + "). Contate a administração. ID: " + listing.getId();
      }
      Listing canonical = expired ? Gts.listings.getExpiredListingById(listing.getId()) : Gts.listings.getActiveListingById(listing.getId());
      if (canonical == null) return "§cEste anúncio saiu desta lista. Atualize Meu GTS e tente novamente.";
      if (canonical instanceof ItemListing item && !Utils.hasSpace(player, item.getListing())) return "§cLibere espaço no inventário para receber o item.";
      return "§cNão foi possível concluir a retirada. Contate a administração com o ID: " + listing.getId();
   }

   public static boolean permitted(ServerPlayerEntity player, String currency) {
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
      if (Gts.server == null || !Gts.server.isOnThread()) {
         throw new IllegalStateException("Server thread required");
      }
   }

   public static void savePlayer(ServerPlayerEntity player) {
      ((PlayerListAccessor)player.getServer().getPlayerManager()).gts$savePlayer(player);
   }

   public static TransactionJournal.Record beginCreation(Listing listing, ServerPlayerEntity seller) throws IOException {
      if (blocked(listing)) {
         throw new IllegalStateException("Listing held");
      } else {
         TransactionJournal.Record record = new TransactionJournal.Record(
            listing.getId(),
            seller.getUuid(),
            seller.getUuid(),
            listing.getCurrency(),
            BigDecimal.valueOf(listing.getPrice()),
            BigDecimal.ZERO,
            Utils.newGson().toJson(listing),
            seller.getName().getString()
         );
         JOURNAL.phase(record, "CREATE_INTENT");
         return record;
      }
   }

   public static void finishCreation(TransactionJournal.Record record) throws IOException {
      JOURNAL.phase(record, "CREATED");
   }

   public static boolean claim(ServerPlayerEntity player, Listing listing, boolean expired) {
      requireServerThread();
      if (player.getUuid().equals(listing.getSellerUuid()) && !blocked(listing)) {
         Listing canonical = expired ? Gts.listings.getExpiredListingById(listing.getId()) : Gts.listings.getActiveListingById(listing.getId());
         if (canonical != null && player.getUuid().equals(canonical.getSellerUuid()) && !player.isDisconnected()) {
            listing = canonical;
            if (canonical instanceof ItemListing item && !Utils.hasSpace(player, item.getListing())) {
               return false;
            } else {
               TransactionJournal.Record record = new TransactionJournal.Record(
                  canonical.getId(),
                  player.getUuid(),
                  canonical.getSellerUuid(),
                  canonical.getCurrency(),
                  BigDecimal.ZERO,
                  BigDecimal.ZERO,
                  Utils.newGson().toJson(canonical),
                  player.getName().getString()
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
                           ItemStack item = ((ItemListing)listing).getListing();
                           delivered = player.getInventory().insertStack(item) && item.isEmpty();
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

   public static boolean sale(ServerPlayerEntity buyer, Listing supplied) throws Exception {
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
            buyer.getUuid(),
            listing.getSellerUuid(),
            listing.getCurrency(),
            price,
            proceeds,
            Utils.newGson().toJson(listing),
            buyer.getName().getString()
         );

         try {
            return SALES.execute(
               record,
               new SaleCoordinator.Port() {
                  @Override
                  public void validate() {
                     if (!listing.getSellerUuid().equals(buyer.getUuid())
                        && !buyer.isDisconnected()
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
                        } else if (price.compareTo(economy.decimalBalance(buyer.getUuid())) > 0) {
                           throw new IllegalStateException(GtsTransactions.message(Gts.language.getMultiCurrency().insufficientFunds, listing));
                        } else {
                           if (listing instanceof PokemonListing pokemon) {
                              UUID id = pokemon.getListing().getUuid();
                              PokemonStoreManager storage = Cobblemon.INSTANCE.getStorage();
                              if (storage.getParty(listing.getSellerUuid(), buyer.getRegistryManager()).get(id) != null
                                 || storage.getPC(listing.getSellerUuid(), buyer.getRegistryManager()).get(id) != null) {
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
                     return economy.withdraw(buyer.getUuid(), price);
                  }

                  @Override
                  public boolean credit() {
                     return economy.deposit(listing.getSellerUuid(), proceeds);
                  }

                  @Override
                  public boolean refund() {
                     return economy.deposit(buyer.getUuid(), price);
                  }

                  @Override
                  public void deliver() {
                     boolean delivered;
                     if (listing instanceof PokemonListing pokemon) {
                        PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(buyer);
                        delivered = party.add(pokemon.getListing());
                     } else {
                        ItemStack item = ((ItemListing)listing).getListing();
                        delivered = buyer.getInventory().insertStack(item) && item.isEmpty();
                     }

                     if (!delivered) {
                        throw new IllegalStateException("Delivery uncertain");
                     } else {
                        GtsTransactions.savePlayer(buyer);
                     }
                  }

                  @Override
                  public void history() {
                     Gts.history.addHistoryItem(listing, buyer.getName().getString());
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
