package org.pokesplash.gts.api;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PartyPosition;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.UUID;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.api.event.GtsEvents;
import org.pokesplash.gts.api.event.events.AddEvent;
import org.pokesplash.gts.api.event.events.CancelEvent;
import org.pokesplash.gts.api.event.events.PurchaseEvent;
import org.pokesplash.gts.api.event.events.ReturnEvent;
import org.pokesplash.gts.discord.Webhook;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.economy.ListingPrices;
import org.pokesplash.gts.economy.MinimumPricePolicy;
import org.pokesplash.gts.economy.Money;
import org.pokesplash.gts.economy.TransactionJournal;
import org.pokesplash.gts.impactor.ImpactorService;
import org.pokesplash.gts.util.Utils;

public abstract class GtsAPI {
   public static boolean cancelListing(Listing listing) {
      GtsTransactions.requireServerThread();
      if (GtsTransactions.blocked(listing)) {
         return false;
      } else {
         listing = Gts.listings.getActiveListingById(listing.getId());
         if (listing == null) {
            return false;
         } else {
            long originalEnd = listing.getEndTime();
            listing.setEndTime(-20L);
            if (!listing.write("/config/gts/listings")) {
               listing.setEndTime(originalEnd);
               return false;
            } else {
               boolean success = Gts.listings.removeListing(listing);
               if (success) {
                  Gts.listings.addExpiredListing(listing);
                  GtsEvents.CANCEL.trigger(new CancelEvent(listing));
               }

               return success;
            }
         }
      }
   }

   public static boolean addListing(Listing listing, class_3222 player, Integer slot) {
      GtsTransactions.requireServerThread();
      Money.amount(listing.getPrice(), Gts.config.getMultiCurrency().allowZeroPrices);
      Gts.config.getMultiCurrency().requireSelectable(listing.getCurrency());
      if (player.method_5667().equals(listing.getSellerUuid())
         && !player.method_14239()
         && Gts.permissions.hasPermission(player, "sell")
         && GtsTransactions.permitted(player, listing.getCurrency())
         && Gts.timeouts.hasTimeoutExpired(player.method_5667())
         && !(listing.getPrice() > Gts.config.getMaximumPrice())
         && Gts.listings.getListingsByPlayer(player.method_5667()).size() + Gts.listings.getExpiredListingsOfPlayer(player.method_5667()).size()
            < Gts.config.getMaxListingsPerPlayer()) {
         new ImpactorService(listing.getCurrency()).currency();
         if (listing instanceof PokemonListing pokemon) {
            if (!Gts.config.isEnablePokemonSales() || slot == null || slot < 0 || slot >= 6) {
               return false;
            }

            PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);
            Pokemon current = party.get(slot);
            if (current == null || !current.getUuid().equals(pokemon.getListing().getUuid()) || !current.getTradeable() || party.occupied() < 2) {
               return false;
            }
         } else {
            if (!Gts.config.isEnableItemSales()) {
               return false;
            }

            class_1799 item = ((ItemListing)listing).getListing();
            if (item.method_7960()
               || item.method_7947() <= 0
               || player.method_6047().method_7947() < item.method_7947()
               || !class_1799.method_31577(player.method_6047(), item)) {
               return false;
            }
         }

         try {
            ListingPrices.listing(listing);
         } catch (MinimumPricePolicy.Rejected var7) {
            player.method_43496(
               class_2561.method_43470(ListingPrices.rejection(var7, listing.getCurrency(), listing.getListingName(), listing.getSellerName()))
            );
            return false;
         }

         try {
            TransactionJournal.Record receipt = GtsTransactions.beginCreation(listing, player);
            if (!Gts.listings.addListing(listing)) {
               Gts.LOGGER.error("Creation " + listing.getId() + " held after persistence failure");
               return false;
            }

            if (listing instanceof PokemonListing) {
               if (!Cobblemon.INSTANCE.getStorage().getParty(player).remove(new PartyPosition(slot))) {
                  throw new IllegalStateException("Source removal failed");
               }
            } else {
               player.method_6047().method_7934(((ItemListing)listing).getListing().method_7947());
            }

            GtsTransactions.savePlayer(player);
            GtsTransactions.finishCreation(receipt);
         } catch (Exception var8) {
            Gts.LOGGER.error("Creation " + listing.getId() + " held; cause=" + var8.getClass().getSimpleName());
            return false;
         }

         try {
            GtsEvents.ADD.trigger(new AddEvent(listing, player));
            if (Gts.config.isBroadcastListings()) {
               Utils.broadcastClickable(
                  Utils.formatPlaceholders(Gts.language.getNewListingBroadcast(), 0.0, listing.getListingName(), listing.getSellerName(), null),
                  "/gts " + listing.getId()
               );
            }

            if (Gts.config.getDiscord().isUseWebhooks()) {
               Webhook.newListing(listing);
            }
         } catch (Exception var6) {
            Gts.LOGGER.error("Listing notification failed: " + listing.getId());
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean sale(UUID seller, class_3222 buyer, Listing listing) throws Exception {
      if (!seller.equals(listing.getSellerUuid())) {
         throw new IllegalArgumentException("Invalid seller");
      } else if (!GtsTransactions.sale(buyer, listing)) {
         return false;
      } else {
         try {
            GtsEvents.PURCHASE.trigger(new PurchaseEvent(buyer, listing));
            if (Gts.config.getDiscord().isUseWebhooks()) {
               Webhook.soldListing(listing);
            }
         } catch (Exception var4) {
            Gts.LOGGER.error("Sale notification failed: " + listing.getId());
         }

         return true;
      }
   }

   public static boolean hasEnoughFunds(UUID player, double amount) {
      return new ImpactorService().decimalBalance(player).compareTo(Money.amount(amount, true)) >= 0;
   }

   public static boolean hasEnoughFunds(UUID player, Listing listing) {
      return new ImpactorService(listing.getCurrency())
            .decimalBalance(player)
            .compareTo(Money.amount(listing.getPrice(), Gts.config.getMultiCurrency().allowZeroPrices))
         >= 0;
   }

   public static boolean returnListing(class_3222 player, Listing listing) {
      boolean success = GtsTransactions.claim(player, listing, true);
      if (success) {
         try {
            GtsEvents.RETURN.trigger(new ReturnEvent(player, listing));
         } catch (Exception var4) {
            Gts.LOGGER.error("Return notification failed: " + listing.getId());
         }
      }

      return success;
   }

   public static boolean cancelAndReturnListing(class_3222 player, Listing listing) {
      boolean success = GtsTransactions.claim(player, listing, false);
      if (success) {
         try {
            GtsEvents.CANCEL.trigger(new CancelEvent(listing));
            GtsEvents.RETURN.trigger(new ReturnEvent(player, listing));
         } catch (Exception var4) {
            Gts.LOGGER.error("Return notification failed: " + listing.getId());
         }
      }

      return success;
   }
}
