package org.pokesplash.gts.history;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.pokesplash.gts.Listing.ItemListing;
import org.pokesplash.gts.Listing.Listing;
import org.pokesplash.gts.Listing.PokemonListing;
import org.pokesplash.gts.api.provider.HistoryAPI;
import org.pokesplash.gts.util.Utils;

public class PlayerHistory implements History {
   private UUID player;
   private List<HistoryItem> listings;

   public PlayerHistory(UUID playerUUID) {
      this.player = playerUUID;
      this.listings = new ArrayList<>();
      if (HistoryAPI.getHighestPriority() == null) {
         Utils.checkForDirectory("/config/gts/history/" + playerUUID + "/");
      }
   }

   public PlayerHistory(UUID player, ArrayList<HistoryItem> items) {
      this.player = player;
      this.listings = items;
   }

   @Override
   public UUID getPlayer() {
      return this.player;
   }

   public List<HistoryItem> getListings() {
      return this.listings;
   }

   public List<PokemonHistoryItem> getPokemonListings() {
      ArrayList<PokemonHistoryItem> pokemonListings = new ArrayList<>();

      for (HistoryItem listing : this.listings) {
         if (listing.isPokemon()) {
            pokemonListings.add((PokemonHistoryItem)listing);
         }
      }

      return pokemonListings;
   }

   public List<ItemHistoryItem> getItemListings() {
      ArrayList<ItemHistoryItem> itemListings = new ArrayList<>();

      for (HistoryItem listing : this.listings) {
         if (!listing.isPokemon()) {
            itemListings.add((ItemHistoryItem)listing);
         }
      }

      return itemListings;
   }

   public void addListing(Listing listing, String buyerName) {
      HistoryItem item = (HistoryItem)(listing.isPokemon()
         ? new PokemonHistoryItem((PokemonListing)listing, buyerName)
         : new ItemHistoryItem((ItemListing)listing, buyerName));
      item.write();
      this.listings.add(item);
   }

   public void addHistory(HistoryItem historyItem) {
      this.listings.add(historyItem);
   }

   public void writeAll() {
      for (HistoryItem item : this.listings) {
         item.write();
      }
   }
}
