package org.pokesplash.gts.config;

import com.cobblemon.mod.common.CobblemonItems;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import org.pokesplash.gts.config.options.CurrencyMessages;
import org.pokesplash.gts.util.CodecUtils;
import org.pokesplash.gts.util.Utils;

public class Lang {
   private CurrencyMessages multiCurrency = new CurrencyMessages();
   private int version = 3;
   private String title = "§3Gts";
   private String itemListingsTitle;
   private String pokemonListingsTitle;
   private String expiredListingsTitle = "§3Gts - Expired";
   private String pokemonTitle;
   private String itemTitle = "§3Gts - Item";
   private String filteredListingsTitle;
   private String historyTitle;
   private String manageTitle;
   private String purchaseMessageBuyer;
   private String cancelListing;
   private String returnListingSuccess;
   private String returnListingFail;
   private String maximumListings;
   private String minimumListingPrice;
   private String maximumListingPrice;
   private String listingSuccess;
   private String listingFail;
   private String noPokemonInSlot;
   private String noItemInHand;
   private String bannedItem;
   private String bannedPokemon;
   private String insufficientItems;
   private String itemIdNotFound;
   private String zeroItemAmount;
   private String reloadMessage;
   private String insufficientFunds;
   private String listingBought;
   private String newListingBroadcast;
   private String insufficientInventorySpace;
   private String onlyOnePokemonInParty;
   private String confirmPurchaseButtonLabel;
   private String cancelPurchaseButtonLabel;
   private String removeListingButtonLabel;
   private String itemListingsButtonLabel;
   private String pokemonListingsButtonLabel;
   private String manageListingsButtonLabel;
   private String nextPageButtonLabel;
   private String previousPageButtonLabel;
   private String sortByPriceButtonLabel;
   private String sortByNewestButtonLabel;
   private String sortByPokemonButtonLabel;
   private String sortByNameButtonLabel;
   private String receiveListingButtonLabel;
   private String relistExpiredButtonLabel;
   private String expiredListingButtonLabel;
   private String pokemonBall;
   private JsonElement itemListingsButtonItem;
   private JsonElement pokemonListingsButtonItem;
   private JsonElement manageListingsButtonItem;
   private JsonElement expiredListingsButtonItem;
   private JsonElement nextPageButtonItems;
   private JsonElement previousPageButtonItems;
   private JsonElement fillerItem;
   private JsonElement purchaseButtonItem;
   private JsonElement cancelButtonItem;
   private JsonElement removeListingButtonItem;
   private JsonElement sortByPriceButtonItem;
   private JsonElement sortByNewestButtonItem;
   private JsonElement sortByNameButtonItem;
   private JsonElement relistExpiredButtonItem;
   private String seller;
   private String price;
   private String remainingTime;
   private String sold_date;
   private String buyer;

   public CurrencyMessages getMultiCurrency() {
      if (this.multiCurrency == null) {
         this.multiCurrency = new CurrencyMessages();
      }

      return this.multiCurrency;
   }

   public Lang() {
      this.pokemonTitle = "§3Gts - Pokemon";
      this.filteredListingsTitle = "§3Gts - %search%";
      this.historyTitle = "§3Gts - History";
      this.itemListingsTitle = "§3Gts - Items";
      this.pokemonListingsTitle = "§3Gts - Pokemon";
      this.manageTitle = "§3Gts - Manage";
      this.purchaseMessageBuyer = "§2You have bought {listing} from {seller}!";
      this.relistExpiredButtonLabel = "§9Relist Expired";
      this.relistExpiredButtonItem = CodecUtils.encodeItem(new class_1799(CobblemonItems.RARE_CANDY));
      this.cancelListing = "§6The {listing} listing has been cancelled!";
      this.returnListingSuccess = "§2You have received the {listing} listing!";
      this.returnListingFail = "§cCould not receive the {listing} listing.";
      this.maximumListings = "§cYou can only have a total of {max_listings} listings.";
      this.minimumListingPrice = "§cYour listing must meet the minimum price of {min_price}";
      this.maximumListingPrice = "§cYour listing must be below the maximum price of {max_price}";
      this.listingSuccess = "§2Successfully added your {listing} to GTS!";
      this.listingFail = "§cFailed to add your {listing} to GTS!";
      this.noPokemonInSlot = "§cCould not find any Pokemon in the given slot!";
      this.noItemInHand = "§cCould not find an item in your hand!";
      this.bannedItem = "§c{listing} is banned from GTS!";
      this.bannedPokemon = "§c{listing} is banned from GTS!";
      this.insufficientItems = "§cYou don't have enough {listing} in your inventory to list this item!";
      this.itemIdNotFound = "§cCould not find an item!";
      this.zeroItemAmount = "§cListing amount can not be zero!";
      this.reloadMessage = "§2Reloaded Configs!";
      this.insufficientFunds = "§cYou do not have enough money to purchase this listing!";
      this.listingBought = "§2Your {listing} has been bought by {buyer}";
      this.itemListingsButtonItem = CodecUtils.encodeItem(new class_1799(CobblemonItems.ASSAULT_VEST));
      this.pokemonListingsButtonItem = CodecUtils.encodeItem(new class_1799(CobblemonItems.POKE_BALL));
      this.manageListingsButtonItem = CodecUtils.encodeItem(new class_1799(CobblemonItems.SACHET));
      this.nextPageButtonItems = CodecUtils.encodeItem(new class_1799(class_1802.field_8107));
      this.previousPageButtonItems = CodecUtils.encodeItem(new class_1799(CobblemonItems.POISON_BARB));
      this.fillerItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8736));
      this.purchaseButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8656));
      this.cancelButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8879));
      this.sortByPriceButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8397));
      this.sortByNewestButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8557));
      this.sortByNameButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8788));
      this.expiredListingsButtonItem = CodecUtils.encodeItem(new class_1799(CobblemonItems.LINK_CABLE));
      this.removeListingButtonItem = CodecUtils.encodeItem(new class_1799(class_1802.field_8761));
      this.newListingBroadcast = "§e{seller} §2has just added a §e{listing} §2to GTS.";
      this.seller = "§9Seller: §b";
      this.price = "§9Price: §b";
      this.sold_date = "§9Sold Date: §b";
      this.buyer = "§9Buyer: §b";
      this.remainingTime = "§9Time Remaining: §b";
      this.confirmPurchaseButtonLabel = "§2Confirm Purchase";
      this.cancelPurchaseButtonLabel = "§cCancel Purchase";
      this.removeListingButtonLabel = "§6Remove Listing";
      this.itemListingsButtonLabel = "§9See Item Listings";
      this.pokemonListingsButtonLabel = "§9See Pokemon Listings";
      this.manageListingsButtonLabel = "§dManage Listings";
      this.nextPageButtonLabel = "§7Next Page";
      this.previousPageButtonLabel = "§7Previous Page";
      this.sortByPriceButtonLabel = "§eSort By Price";
      this.sortByNewestButtonLabel = "§3Sort By Newest";
      this.sortByPokemonButtonLabel = "§6Sort By Pokemon";
      this.sortByNameButtonLabel = "§6Sort By Name";
      this.receiveListingButtonLabel = "§2Receive Listing";
      this.insufficientInventorySpace = "§cYou do not have enough inventory space to receive this listing.";
      this.onlyOnePokemonInParty = "§cYou can not list a Pokemon to GTS if you only have less than 2 Pokemon in your party.";
      this.expiredListingButtonLabel = "§cExpired Listings";
      this.pokemonBall = "§2Ball: ";
   }

   public int getVersion() {
      return this.version;
   }

   public String getPurchaseMessageBuyer() {
      return this.purchaseMessageBuyer;
   }

   public String getTitle() {
      return this.title;
   }

   public String getCancelListing() {
      return this.cancelListing;
   }

   public String getReturnListingSuccess() {
      return this.returnListingSuccess;
   }

   public String getReturnListingFail() {
      return this.returnListingFail;
   }

   public String getMaximumListings() {
      return this.maximumListings;
   }

   public String getMinimumListingPrice() {
      return this.minimumListingPrice;
   }

   public String getMaximumListingPrice() {
      return this.maximumListingPrice;
   }

   public String getListingSuccess() {
      return this.listingSuccess;
   }

   public String getListingFail() {
      return this.listingFail;
   }

   public String getNoPokemonInSlot() {
      return this.noPokemonInSlot;
   }

   public String getNoItemInHand() {
      return this.noItemInHand;
   }

   public String getBannedItem() {
      return this.bannedItem;
   }

   public String getInsufficientItems() {
      return this.insufficientItems;
   }

   public String getItemIdNotFound() {
      return this.itemIdNotFound;
   }

   public String getZeroItemAmount() {
      return this.zeroItemAmount;
   }

   public String getReloadMessage() {
      return this.reloadMessage;
   }

   public String getInsufficientFunds() {
      return this.insufficientFunds;
   }

   public String getListingBought() {
      return this.listingBought;
   }

   public class_1799 getItemListingsButtonItem() {
      return CodecUtils.decodeItem(this.itemListingsButtonItem);
   }

   public class_1799 getPokemonListingsButtonItem() {
      return CodecUtils.decodeItem(this.pokemonListingsButtonItem);
   }

   public class_1799 getManageListingsButtonItem() {
      return CodecUtils.decodeItem(this.manageListingsButtonItem);
   }

   public class_1799 getNextPageButtonItems() {
      return CodecUtils.decodeItem(this.nextPageButtonItems);
   }

   public class_1799 getPreviousPageButtonItems() {
      return CodecUtils.decodeItem(this.previousPageButtonItems);
   }

   public class_1799 getFillerItem() {
      return CodecUtils.decodeItem(this.fillerItem);
   }

   public class_1799 getPurchaseButtonItem() {
      return CodecUtils.decodeItem(this.purchaseButtonItem);
   }

   public class_1799 getCancelButtonItem() {
      return CodecUtils.decodeItem(this.cancelButtonItem);
   }

   public class_1799 getSortByPriceButtonItem() {
      return CodecUtils.decodeItem(this.sortByPriceButtonItem);
   }

   public class_1799 getSortByNewestButtonItem() {
      return CodecUtils.decodeItem(this.sortByNewestButtonItem);
   }

   public class_1799 getSortByNameButtonItem() {
      return CodecUtils.decodeItem(this.sortByNameButtonItem);
   }

   public class_1799 getExpiredListingsButtonItem() {
      return CodecUtils.decodeItem(this.expiredListingsButtonItem);
   }

   public class_1799 getRemoveListingButtonItem() {
      return CodecUtils.decodeItem(this.removeListingButtonItem);
   }

   public class_1799 getRelistExpiredButtonItem() {
      return CodecUtils.decodeItem(this.relistExpiredButtonItem);
   }

   public String getNewListingBroadcast() {
      return this.newListingBroadcast;
   }

   public String getInsufficientInventorySpace() {
      return this.insufficientInventorySpace;
   }

   public String getSeller() {
      return this.seller;
   }

   public String getPrice() {
      return this.price;
   }

   public String getRemainingTime() {
      return this.remainingTime;
   }

   public String getConfirmPurchaseButtonLabel() {
      return this.confirmPurchaseButtonLabel;
   }

   public String getCancelPurchaseButtonLabel() {
      return this.cancelPurchaseButtonLabel;
   }

   public String getRemoveListingButtonLabel() {
      return this.removeListingButtonLabel;
   }

   public String getItemListingsButtonLabel() {
      return this.itemListingsButtonLabel;
   }

   public String getPokemonListingsButtonLabel() {
      return this.pokemonListingsButtonLabel;
   }

   public String getManageListingsButtonLabel() {
      return this.manageListingsButtonLabel;
   }

   public String getNextPageButtonLabel() {
      return this.nextPageButtonLabel;
   }

   public String getPreviousPageButtonLabel() {
      return this.previousPageButtonLabel;
   }

   public String getSortByPriceButtonLabel() {
      return this.sortByPriceButtonLabel;
   }

   public String getSortByNewestButtonLabel() {
      return this.sortByNewestButtonLabel;
   }

   public String getSortByPokemonButtonLabel() {
      return this.sortByPokemonButtonLabel;
   }

   public String getSortByNameButtonLabel() {
      return this.sortByNameButtonLabel;
   }

   public String getReceiveListingButtonLabel() {
      return this.receiveListingButtonLabel;
   }

   public String getSold_date() {
      return this.sold_date;
   }

   public String getBuyer() {
      return this.buyer;
   }

   public String getBannedPokemon() {
      return this.bannedPokemon;
   }

   public String getRelistExpiredButtonLabel() {
      return this.relistExpiredButtonLabel;
   }

   public String getItemTitle() {
      return this.itemTitle;
   }

   public String getExpiredListingsTitle() {
      return this.expiredListingsTitle;
   }

   public String getPokemonTitle() {
      return this.pokemonTitle;
   }

   public String getFilteredListingsTitle() {
      return this.filteredListingsTitle;
   }

   public String getHistoryTitle() {
      return this.historyTitle;
   }

   public String getItemListingsTitle() {
      return this.itemListingsTitle;
   }

   public String getManageTitle() {
      return this.manageTitle;
   }

   public String getPokemonListingsTitle() {
      return this.pokemonListingsTitle;
   }

   public String getOnlyOnePokemonInParty() {
      return this.onlyOnePokemonInParty;
   }

   public String getExpiredListingButtonLabel() {
      return this.expiredListingButtonLabel;
   }

   public String getPokemonBall() {
      return this.pokemonBall;
   }

   public CompletableFuture<Boolean> write() {
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      return Utils.writeFileAsync("/config/gts/", "lang.json", data);
   }
}
