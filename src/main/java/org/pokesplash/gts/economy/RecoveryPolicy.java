package org.pokesplash.gts.economy;

import com.google.gson.*;
import java.math.BigDecimal;

/** Recovery is limited to a completed creation with an unchanged escrowed asset. */
public final class RecoveryPolicy {
    private RecoveryPolicy() {}
    public static boolean completedCreation(TransactionJournal.Record r) {
        return r != null && "RECOVERY_REQUIRED".equals(r.phase) && "CREATED".equals(r.phaseBeforeRecovery)
                && r.listingId != null && r.seller != null && r.seller.equals(r.buyer)
                && r.price != null && r.price.signum() >= 0
                && r.proceeds != null && r.proceeds.compareTo(BigDecimal.ZERO) == 0;
    }
    public static boolean matches(TransactionJournal.Record r, JsonObject listing) {
        if (!completedCreation(r) || listing == null || r.listingJson == null) return false;
        try {
            JsonObject original = JsonParser.parseString(r.listingJson).getAsJsonObject();
            if (!r.listingId.toString().equals(listing.get("id").getAsString())
                    || !r.listingId.toString().equals(original.get("id").getAsString())
                    || !r.seller.toString().equals(listing.get("sellerUuid").getAsString())
                    || !r.seller.toString().equals(original.get("sellerUuid").getAsString())
                    || r.currency == null || !r.currency.equals(currency(listing)) || !r.currency.equals(currency(original))) return false;
            boolean pokemon = listing.get("isPokemon").getAsBoolean();
            if (pokemon != original.get("isPokemon").getAsBoolean()) return false;
            String key = pokemon ? "pokemon" : "item";
            return original.has(key) && !original.get(key).isJsonNull()
                    && original.get(key).isJsonObject() && original.getAsJsonObject(key).size() > 0
                    && original.get(key).equals(listing.get(key));
        } catch (RuntimeException error) { return false; }
    }
    private static String currency(JsonObject listing) {
        return listing.has("currency") && !listing.get("currency").isJsonNull()
                ? CurrencySettings.legacy(listing.get("currency").getAsString()) : CurrencySettings.DOLLARS;
    }
}
