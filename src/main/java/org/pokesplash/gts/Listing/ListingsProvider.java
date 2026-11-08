package org.pokesplash.gts.Listing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.pokesplash.gts.api.provider.ListingAPI;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.util.Deserializer;
import org.pokesplash.gts.util.Utils;

public class ListingsProvider {
   protected ArrayList<Listing> listings;
   protected HashMap<UUID, ArrayList<Listing>> expiredListings;
   private final String brokenFilePath = "/config/gts/invalid/listings/";

   public ListingsProvider() {
      this.listings = new ArrayList<>();
      this.expiredListings = new HashMap<>();
   }

   public synchronized void relistAllExpiredListings(UUID player) {
      ArrayList<Listing> expired = this.getExpiredListings().get(player);
      if (expired != null) {
         if (ListingAPI.getHighestPriority() != null) {
            for (Listing listing : expired.stream().map(Listing::deepClone).toList()) {
               if (!GtsTransactions.blocked(listing)) {
                  listing.renewEndTime();
                  ListingAPI.getHighestPriority().update(listing);
               }
            }
         } else {
            for (Listing listingx : new ArrayList<>(expired)) {
               if (!GtsTransactions.blocked(listingx)) {
                  long oldEnd = listingx.getEndTime();
                  listingx.renewEndTime();
                  if (this.addListing(listingx)) {
                     this.removeExpiredListing(listingx);
                  } else {
                     listingx.setEndTime(oldEnd);
                  }
               }
            }
         }
      }
   }

   public synchronized List<Listing> getListings() {
      return new ArrayList<>(this.listings);
   }

   public synchronized List<PokemonListing> getPokemonListings() {
      ArrayList<PokemonListing> pkmListings = new ArrayList<>();

      for (Listing listing : this.getListings()) {
         if (listing instanceof PokemonListing) {
            pkmListings.add((PokemonListing)listing);
         }
      }

      return pkmListings;
   }

   public synchronized List<Listing> getListingsByPlayer(UUID uuid) {
      ArrayList<Listing> playerListings = new ArrayList<>();

      for (Listing listing : this.getListings()) {
         if (listing.getSellerUuid().equals(uuid)) {
            playerListings.add(listing);
         }
      }

      return playerListings;
   }

   public synchronized List<PokemonListing> getPokemonListingsByPlayer(UUID uuid) {
      ArrayList<PokemonListing> playerListings = new ArrayList<>();

      for (PokemonListing pokemonListing : this.getPokemonListings()) {
         if (pokemonListing.getSellerUuid().equals(uuid)) {
            playerListings.add(pokemonListing);
         }
      }

      return playerListings;
   }

   public synchronized List<ItemListing> getItemListings() {
      ArrayList<ItemListing> itemListings = new ArrayList<>();

      for (Listing listing : this.getListings()) {
         if (listing instanceof ItemListing) {
            itemListings.add((ItemListing)listing);
         }
      }

      return itemListings;
   }

   public synchronized List<ItemListing> getItemListingsByPlayer(UUID uuid) {
      ArrayList<ItemListing> playerListings = new ArrayList<>();

      for (ItemListing item : this.getItemListings()) {
         if (item.getSellerUuid().equals(uuid)) {
            playerListings.add(item);
         }
      }

      return playerListings;
   }

   public synchronized boolean addListing(Listing listing) throws IllegalArgumentException {
      if (this.hasListing(listing.getId(), this.getListings())) {
         throw new IllegalArgumentException("This listing already exists!");
      } else {
         return !listing.write("/config/gts/listings") ? false : this.putListing(listing);
      }
   }

   public synchronized boolean removeListing(Listing listing) throws IllegalArgumentException {
      if (!this.hasListing(listing.getId(), this.getListings())) {
         throw new IllegalArgumentException("No listing with the UUID " + listing.getId() + " exists.");
      } else {
         return this.delListing(listing);
      }
   }

   private boolean hasListing(UUID id, List<Listing> listings) {
      for (Listing listing : listings) {
         if (listing.getId().equals(id)) {
            return true;
         }
      }

      return false;
   }

   public synchronized boolean hasExpiredListings(UUID playerUUID) {
      return this.getExpiredListings().containsKey(playerUUID);
   }

   public synchronized boolean addExpiredListing(Listing listing) {
      if (this.getExpiredListings().containsKey(listing.getSellerUuid())) {
         ArrayList<Listing> currentListings = this.getExpiredListings().get(listing.getSellerUuid());
         if (!currentListings.contains(listing)) {
            currentListings.add(listing);
            this.putExpiredListing(listing.getSellerUuid(), currentListings);
         }
      } else {
         this.putExpiredListing(listing.getSellerUuid(), new ArrayList<>(List.of(listing)));
      }

      return true;
   }

   public synchronized boolean removeExpiredListing(Listing listing) {
      if (this.getExpiredListings().get(listing.getSellerUuid()) == null) {
         return false;
      } else {
         ArrayList<Listing> listings = this.getExpiredListings().get(listing.getSellerUuid());
         if (listings.contains(listing)) {
            listings.remove(listing);
            this.putExpiredListing(listing.getSellerUuid(), listings);
            return true;
         } else {
            return false;
         }
      }
   }

   public synchronized Listing getActiveListingById(UUID id) {
      for (Listing listing : this.getListings()) {
         if (listing.getId().equals(id)) {
            return listing;
         }
      }

      return null;
   }

   public synchronized Listing getExpiredListingById(UUID id) {
      for (ArrayList<Listing> playerListings : this.getExpiredListings().values()) {
         for (Listing listing : playerListings) {
            if (listing.getId().equals(id)) {
               return listing;
            }
         }
      }

      return null;
   }

   public synchronized Listing getListingById(UUID id) {
      Listing activeListing = this.getActiveListingById(id);
      return activeListing != null ? activeListing : this.getExpiredListingById(id);
   }

   public synchronized List<Listing> getExpiredListingsOfPlayer(UUID player) {
      return (List<Listing>)(this.getExpiredListings().get(player) == null ? Collections.emptyList() : this.getExpiredListings().get(player));
   }

   public synchronized HashMap<UUID, ArrayList<Listing>> getExpiredListings() {
      HashMap<UUID, ArrayList<Listing>> snapshot = new HashMap<>();
      this.expiredListings.forEach((owner, entries) -> snapshot.put(owner, new ArrayList<>(entries)));
      return snapshot;
   }

   public synchronized void check() {
      ArrayList<Listing> toRemove = new ArrayList<>();

      for (Listing listing : this.getListings()) {
         if (!GtsTransactions.blocked(listing) && listing.getEndTime() < new Date().getTime() && listing.getEndTime() != -1L) {
            toRemove.add(listing);
         }
      }

      for (Listing listingx : toRemove) {
         boolean success = this.removeListing(listingx);
         this.addExpiredListing(listingx);
      }
   }

   protected synchronized boolean putListing(Listing listing) {
      return this.listings.add(listing);
   }

   protected synchronized boolean delListing(Listing listing) {
      return this.listings.remove(listing);
   }

   protected synchronized void putExpiredListing(UUID playerUUID, ArrayList<Listing> listings) {
      this.expiredListings.put(playerUUID, listings);
   }

   protected synchronized void delExpiredListing(UUID playerUUID) {
      this.expiredListings.remove(playerUUID);
   }

   public synchronized void init() {
      this.listings = new ArrayList<>();
      File dir = Utils.checkForDirectory("/config/gts/listings");
      String[] list = dir.list();
      if (list.length != 0) {
         for (String file : list) {
            Utils.readFileAsync("/config/gts/listings", file, el -> {
               GsonBuilder builder = new GsonBuilder();
               builder.registerTypeAdapter(Listing.class, new Deserializer(PokemonListing.class));
               builder.registerTypeAdapter(Listing.class, new Deserializer(ItemListing.class));
               Gson gson = builder.setPrettyPrinting().create();
               Listing listing = (Listing)gson.fromJson(el, Listing.class);
               listing = listing.isPokemon() ? (Listing)gson.fromJson(el, PokemonListing.class) : (Listing)gson.fromJson(el, ItemListing.class);
               if (!listing.isListingValid()) {
                  System.out.println("[GTS] Invalid file: " + file + " has been removed.");
                  Utils.writeFileAsync("/config/gts/invalid/listings/", file, gson.toJson(listing));
                  Utils.deleteFile("/config/gts/listings", file);
               } else {
                  if (!listing.getVersion().equals("2.0")) {
                  }

                  if (listing.getEndTime() <= new Date().getTime() && listing.getEndTime() != -1L) {
                     this.addExpiredListing(listing);
                  } else {
                     this.putListing(listing);
                  }
               }
            });
         }
      }
   }
}
