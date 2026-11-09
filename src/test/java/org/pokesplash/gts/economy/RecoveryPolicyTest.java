package org.pokesplash.gts.economy;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.*;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RecoveryPolicyTest {
    private final UUID id = UUID.randomUUID(), seller = UUID.randomUUID();
    private JsonObject listing() {
        return JsonParser.parseString("{\"id\":\"" + id + "\",\"sellerUuid\":\"" + seller + "\",\"currency\":\"impactor:dollars\",\"isPokemon\":false,\"price\":100,\"item\":{\"id\":\"minecraft:diamond\",\"count\":1}}").getAsJsonObject();
    }
    private TransactionJournal.Record receipt() {
        var r = new TransactionJournal.Record(id, seller, seller, "impactor:dollars", BigDecimal.valueOf(100), BigDecimal.ZERO, listing().toString(), "seller");
        r.phase = "RECOVERY_REQUIRED"; r.phaseBeforeRecovery = "CREATED"; return r;
    }
    @Test void completedCreationMatchesEscrowedAsset() { assertTrue(RecoveryPolicy.matches(receipt(), listing())); }
    @Test void changedPriceAndExpiryDoNotChangeEscrowIdentity() {
        var actual = listing(); actual.addProperty("price", 200); actual.addProperty("endTime", -20);
        assertTrue(RecoveryPolicy.matches(receipt(), actual));
    }
    @Test void changedItemOrCountIsNeverReleased() {
        var actual = listing(); actual.getAsJsonObject("item").addProperty("count", 2);
        assertFalse(RecoveryPolicy.matches(receipt(), actual));
        actual = listing(); actual.getAsJsonObject("item").addProperty("id", "minecraft:dirt");
        assertFalse(RecoveryPolicy.matches(receipt(), actual));
    }
    @Test void changedOwnerIdCurrencyOrTypeIsNeverReleased() {
        for (String key : new String[] {"sellerUuid", "id", "currency"}) {
            var actual = listing(); actual.addProperty(key, "different"); assertFalse(RecoveryPolicy.matches(receipt(), actual));
        }
        var actual = listing(); actual.addProperty("isPokemon", true); assertFalse(RecoveryPolicy.matches(receipt(), actual));
    }
    @Test void salesReturnsAndIncompleteOperationsAreNeverReleased() {
        for (String phase : new String[] {"SOLD", "RETURNED", "DEBIT_INTENT", "DELIVERY_INTENT", "CREATE_INTENT"}) {
            var r = receipt(); r.phaseBeforeRecovery = phase; assertFalse(RecoveryPolicy.matches(r, listing()));
            r.phase = phase; assertFalse(RecoveryPolicy.matches(r, listing()));
        }
    }
    @Test void financialOrMalformedRecordsAreNeverReleased() {
        var r = receipt(); r.buyer = UUID.randomUUID(); assertFalse(RecoveryPolicy.matches(r, listing()));
        r = receipt(); r.proceeds = BigDecimal.ONE; assertFalse(RecoveryPolicy.matches(r, listing()));
        r = receipt(); r.price = null; assertFalse(RecoveryPolicy.matches(r, listing()));
        r = receipt(); r.listingJson = "{"; assertFalse(RecoveryPolicy.matches(r, listing()));
        assertFalse(RecoveryPolicy.matches(null, listing()));
    }
    @Test void legacyDollarCurrencyAndPokemonIdentityAreSupported() {
        var actual = listing(); actual.remove("currency"); assertTrue(RecoveryPolicy.matches(receipt(), actual));
        actual = listing(); actual.addProperty("isPokemon", true); actual.remove("item");
        actual.add("pokemon", JsonParser.parseString("{\"UUID\":\"" + UUID.randomUUID() + "\",\"Species\":\"cobblemon:eevee\"}"));
        var r = receipt(); r.listingJson = actual.toString(); assertTrue(RecoveryPolicy.matches(r, actual));
        actual.getAsJsonObject("pokemon").addProperty("UUID", UUID.randomUUID().toString()); assertFalse(RecoveryPolicy.matches(r, actual));
    }
}
