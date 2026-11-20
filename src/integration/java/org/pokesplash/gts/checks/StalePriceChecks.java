package org.pokesplash.gts.checks;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.*;
import net.minecraft.entity.player.PlayerInventory;
import ca.landonjw.gooeylibs2.api.button.*;
import ca.landonjw.gooeylibs2.api.page.Page;
import org.pokesplash.gts.UI.SingleListing;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.minecraft.text.Text;
import org.pokesplash.gts.Gts;
import org.pokesplash.gts.Listing.*;
import org.pokesplash.gts.api.GtsAPI;
import org.pokesplash.gts.economy.GtsTransactions;
import org.pokesplash.gts.impactor.ImpactorService;

/** Actual Minecraft/Cobblemon/Impactor APIs with simulated connected players, not native clients. */
public final class StalePriceChecks implements ModInitializer {
    private boolean ran;
    private final JsonArray results = new JsonArray();
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ran || !Boolean.getBoolean("gts.integrationTest") || !"127.0.0.1".equals(server.getServerIp()) || server.getTicks() < 60) return;
            ran = true;
            try {
                scenario(server, false, 9000, true);
                scenario(server, true, 9000, true);
                scenario(server, false, 11000, false);
                scenario(server, true, 11000, false);
                rejectedOperations(server);
                deliveryFailure(server);
            } catch (Throwable failure) {
                Gts.LOGGER.error("GTS CHECK FATAL: " + failure);
                failure.printStackTrace();
                var result = new JsonObject(); result.addProperty("fatal", failure.toString()); results.add(result);
            } finally {
                try { Files.writeString(Path.of("gts-check-results.json"), new GsonBuilder().setPrettyPrinting().create().toJson(results)); }
                catch (Exception error) { throw new RuntimeException(error); }
            }
        });
    }
    private static class TestPlayer extends ServerPlayerEntity {
        boolean failDelivery;
        private final PlayerInventory failingInventory = new PlayerInventory(this) {
            @Override public boolean insertStack(ItemStack stack) { throw new IllegalStateException("Injected delivery failure"); }
        };
        @Override public PlayerInventory getInventory() { return failDelivery ? failingInventory : super.getInventory(); }
        TestPlayer(MinecraftServer server, String name) {
            super(server, server.getOverworld(), new GameProfile(UUID.randomUUID(), name), SyncedClientOptions.createDefault());
            networkHandler = new ServerPlayNetworkHandler(server, new ClientConnection(NetworkSide.SERVERBOUND), this, ConnectedClientData.createDefault(getGameProfile(), false));
        }
        @Override public boolean isDisconnected() { return false; }
        @Override public void sendMessage(Text message) { Gts.LOGGER.info("TEST PLAYER " + getName().getString() + ": " + message.getString()); }
        @Override public void sendMessage(Text message, boolean overlay) { sendMessage(message); }
    }
    private void check(String name, boolean passed) {
        var row = new JsonObject(); row.addProperty("name", name); row.addProperty("passed", passed); results.add(row);
        Gts.LOGGER.info("GTS CHECK " + (passed ? "PASS " : "FAIL ") + name);
    }
    private void scenario(MinecraftServer server, boolean pokemon, double newPrice, boolean withdraw) throws Exception {
        String type = (pokemon ? "pokemon" : "item") + " " + (int)newPrice;
        var seller = new TestPlayer(server, pokemon ? "SellerPokemon" : "SellerItem");
        var buyer = new TestPlayer(server, pokemon ? "BuyerPokemon" : "BuyerItem");
        var economy = new ImpactorService("impactor:dollars");
        economy.deposit(buyer.getUuid(), new BigDecimal("100000"));
        Listing listing;
        if (pokemon) {
            var party = Cobblemon.INSTANCE.getStorage().getParty(seller);
            party.set(0, PokemonProperties.Companion.parse("pikachu").create());
            party.set(1, PokemonProperties.Companion.parse("eevee").create());
            listing = new PokemonListing(seller.getUuid(), seller.getName().getString(), 10000, party.get(0));
        } else {
            seller.getInventory().main.set(0, new ItemStack(Items.DIAMOND, 4));
            listing = new ItemListing(seller.getUuid(), seller.getName().getString(), 10000, seller.getMainHandStack());
        }
        check(type + " actual API listing creation", GtsAPI.addListing(listing, seller, 0));
        check(type + " creation not held", !GtsTransactions.blocked(listing));
        Listing stale = listing.deepClone();
        Page menu = new SingleListing().getPage(buyer, listing);
        GtsAPI.changeListingPrice(seller, listing.getId(), 10000, newPrice);
        check(type + " edited price saved", Gts.listings.getActiveListingById(listing.getId()).getPrice() == newPrice);
        String receipt = Files.readString(Path.of("config/gts/transactions", listing.getId() + ".json"));
        var funds = economy.decimalBalance(buyer.getUuid());
        try {
            boolean sold = GtsAPI.sale(seller.getUuid(), buyer, stale);
            check(type + " stale purchase rejected", !sold);
        } catch (Exception error) {
            Gts.LOGGER.info("STALE PURCHASE: " + error);
            check(type + " stale purchase rejected", error.getClass().getName().equals("org.pokesplash.gts.economy.ListingChangedException"));
        }
        check(type + " stale attempt leaves buyer funds", funds.compareTo(economy.decimalBalance(buyer.getUuid())) == 0);
        check(type + " stale attempt leaves listing", Gts.listings.getActiveListingById(listing.getId()) != null);
        check(type + " stale attempt leaves journal", receipt.equals(Files.readString(Path.of("config/gts/transactions", listing.getId() + ".json"))));
        // Exercise the actual purchase-button callback created BEFORE the price edit.
        var buy = menu.getTemplate().getSlot(11).getButton().orElseThrow();
        buy.onClick(new ButtonAction(buyer, ButtonClick.LEFT_CLICK, buy, menu.getTemplate(), menu, 11));
        check(type + " stale menu callback leaves funds", funds.compareTo(economy.decimalBalance(buyer.getUuid())) == 0);
        check(type + " stale menu callback leaves listing", Gts.listings.getActiveListingById(listing.getId()) != null);
        check(type + " stale menu callback leaves journal", receipt.equals(Files.readString(Path.of("config/gts/transactions", listing.getId() + ".json"))));
        check(type + " stale attempt leaves seller able to edit", canEdit(seller, listing, newPrice));
        if (withdraw) {
            check(type + " seller can withdraw", GtsAPI.cancelAndReturnListing(seller, listing));
            boolean returned = pokemon
                ? Cobblemon.INSTANCE.getStorage().getParty(seller).get(((PokemonListing)listing).getListing().getUuid()) != null
                : seller.getInventory().count(Items.DIAMOND) == 4;
            check(type + " exactly original asset returned", returned);
            check(type + " repeated withdraw cannot duplicate", !GtsAPI.cancelAndReturnListing(seller, listing));
        } else {
            BigDecimal sellerBefore = economy.decimalBalance(seller.getUuid());
            boolean sold = false;
            try {
                Page current = new SingleListing().getPage(buyer, listing);
                var purchase = current.getTemplate().getSlot(11).getButton().orElseThrow();
                purchase.onClick(new ButtonAction(buyer, ButtonClick.LEFT_CLICK, purchase, current.getTemplate(), current, 11));
                sold = Gts.listings.getActiveListingById(listing.getId()) == null
                    && funds.subtract(new BigDecimal("8000")).compareTo(economy.decimalBalance(buyer.getUuid())) == 0;
            }
            catch (Exception failure) { Gts.LOGGER.error("Fresh sale failed: " + failure); }
            check(type + " fresh purchase succeeds after stale rejection", sold);
            check(type + " fresh buyer pays exactly confirmed price", funds.subtract(new BigDecimal("8000")).compareTo(economy.decimalBalance(buyer.getUuid())) == 0);
            check(type + " seller receives configured proceeds", sellerBefore.add(org.pokesplash.gts.economy.Money.proceeds(new BigDecimal("8000"), Gts.config.getTaxRate())).compareTo(economy.decimalBalance(seller.getUuid())) == 0);
            boolean received = pokemon
                ? Cobblemon.INSTANCE.getStorage().getParty(buyer).get(((PokemonListing)listing).getListing().getUuid()) != null
                : buyer.getInventory().count(Items.DIAMOND) == 4;
            check(type + " buyer receives original asset", received);
            var balanceAfter = economy.decimalBalance(buyer.getUuid());
            try { GtsAPI.sale(seller.getUuid(), buyer, listing.deepClone()); } catch (Exception expected) {}
            check(type + " repeated purchase cannot charge twice", balanceAfter.compareTo(economy.decimalBalance(buyer.getUuid())) == 0);
        }
        check(type + " completed operation removes escrow file", !Files.exists(Path.of("config/gts/listings", listing.getId() + ".json")));
    }
    private void rejectedOperations(MinecraftServer server) throws Exception {
        var seller = new TestPlayer(server, "Rejections");
        var buyer = new TestPlayer(server, "NoFunds");
        seller.getInventory().main.set(0, new ItemStack(Items.DIAMOND, 4));
        var listing = new ItemListing(seller.getUuid(), "Rejections", 10000, seller.getMainHandStack());
        check("rejection fixture created", GtsAPI.addListing(listing, seller, 0));
        GtsAPI.changeListingPrice(seller, listing.getId(), 10000, 9000);
        Path file = Path.of("config/gts/transactions", listing.getId() + ".json");
        String receipt = Files.readString(file);
        boolean rejected = false;
        try { rejected = !GtsAPI.sale(seller.getUuid(), buyer, listing.deepClone()); } catch (Exception expected) { rejected = true; }
        check("fresh quote with insufficient funds rejected", rejected);
        check("rejected sale does not alter journal", receipt.equals(Files.readString(file)) && !GtsTransactions.blocked(listing));
        check("seller can edit after insufficient funds", canEdit(seller, listing, 9000));
        for (int i = 0; i < 36; i++) seller.getInventory().main.set(i, new ItemStack(Items.STONE, 64));
        check("full seller inventory rejects withdrawal", !GtsAPI.cancelAndReturnListing(seller, listing));
        check("full inventory leaves listing and receipt untouched", Gts.listings.getActiveListingById(listing.getId()) != null && receipt.equals(Files.readString(file)));
        seller.getInventory().clear();
        check("seller can withdraw after freeing space", GtsAPI.cancelAndReturnListing(seller, listing) && seller.getInventory().count(Items.DIAMOND) == 4);
    }
    private void deliveryFailure(MinecraftServer server) throws Exception {
        var seller = new TestPlayer(server, "FailedDelivery");
        seller.getInventory().main.set(0, new ItemStack(Items.EMERALD, 3));
        var listing = new ItemListing(seller.getUuid(), "FailedDelivery", 10000, seller.getMainHandStack());
        check("fault fixture created", GtsAPI.addListing(listing, seller, 0));
        seller.failDelivery = true;
        check("injected delivery error is reported", !GtsAPI.cancelAndReturnListing(seller, listing));
        seller.failDelivery = false;
        check("failed delivery keeps visible listing", Gts.listings.getActiveListingById(listing.getId()) != null);
        check("failed delivery keeps escrow file", Files.isRegularFile(Path.of("config/gts/listings", listing.getId() + ".json")));
        check("failed delivery holds journal against duplicate retry", GtsTransactions.blocked(listing) && !GtsAPI.cancelAndReturnListing(seller, listing));
        check("failed delivery retains asset snapshot in journal", Files.readString(Path.of("config/gts/transactions", listing.getId() + ".json")).contains("minecraft:emerald"));
    }
    private boolean canEdit(ServerPlayerEntity seller, Listing listing, double expected) {
        try { GtsAPI.changeListingPrice(seller, listing.getId(), expected, 8000); return true; }
        catch (Exception error) { Gts.LOGGER.error("EDIT: " + error); return false; }
    }
}
