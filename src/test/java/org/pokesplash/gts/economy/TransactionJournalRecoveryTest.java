package org.pokesplash.gts.economy;

import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TransactionJournalRecoveryTest {
    @TempDir Path directory;
    private TransactionJournal.Record record() {
        UUID seller = UUID.randomUUID();
        return new TransactionJournal.Record(UUID.randomUUID(), seller, seller, "impactor:dollars", BigDecimal.TEN, BigDecimal.ZERO, "{}", "seller");
    }
    private void simulateUncleanMarker() throws Exception {
        Path file = directory.resolve("_session.json");
        var marker = JsonParser.parseString(Files.readString(file)).getAsJsonObject(); marker.addProperty("clean", false);
        Files.writeString(file, marker.toString());
    }
    @Test void reproducesRestartHoldAndRestoresOnlyAfterExplicitRecoveryWithBackup() throws Exception {
        var first = new TransactionJournal(directory); first.beginSession(); var r = record(); first.phase(r, "CREATED"); first.endSession();
        simulateUncleanMarker();
        var second = new TransactionJournal(directory); assertEquals(1, second.beginSession());
        try {
            assertTrue(second.blocked(r.listingId));
            var held = second.inspect(r.listingId); assertEquals("CREATED", held.record().phaseBeforeRecovery);
            String previous = Files.readString(directory.resolve(r.listingId + ".json"));
            second.restoreCompletedCreation(held.record(), "test-admin");
            assertFalse(second.blocked(r.listingId));
            assertEquals("test-admin", second.inspect(r.listingId).record().recoveredBy);
            try (var files = Files.list(directory.resolve("recovery-backups"))) {
                assertEquals(previous, Files.readString(files.findFirst().orElseThrow()));
            }
        } finally { second.endSession(); }
    }
    @Test void cleanRestartDoesNotHoldCompletedCreations() throws Exception {
        var first = new TransactionJournal(directory); first.beginSession(); var r = record(); first.phase(r, "CREATED"); first.endSession();
        var second = new TransactionJournal(directory); assertEquals(0, second.beginSession());
        try { assertFalse(second.blocked(r.listingId)); } finally { second.endSession(); }
    }
    @Test void refusesUnknownFinancialStatesAndStaleReceipts() throws Exception {
        var first = new TransactionJournal(directory); first.beginSession(); var r = record(); first.phase(r, "SOLD"); first.endSession(); simulateUncleanMarker();
        var second = new TransactionJournal(directory); second.beginSession();
        try {
            assertThrows(java.io.IOException.class, () -> second.restoreCompletedCreation(second.inspect(r.listingId).record(), "admin"));
            assertTrue(second.blocked(r.listingId));
            var creation = record(); second.phase(creation, "RECOVERY_REQUIRED");
            creation.phaseBeforeRecovery = "CREATED"; second.phase(creation, "RECOVERY_REQUIRED");
            var stale = second.inspect(creation.listingId).record();
            second.phase(creation, "DELIVERY_INTENT");
            assertThrows(java.io.IOException.class, () -> second.restoreCompletedCreation(stale, "admin"));
            assertTrue(second.blocked(creation.listingId));
        } finally { second.endSession(); }
    }
    @Test void recoveredReceiptBelongsToCurrentSessionForFutureCrashProtection() throws Exception {
        var first = new TransactionJournal(directory); first.beginSession(); var r = record(); first.phase(r, "CREATED"); first.endSession(); simulateUncleanMarker();
        var second = new TransactionJournal(directory); second.beginSession(); second.restoreCompletedCreation(second.inspect(r.listingId).record(), "admin"); second.endSession();
        simulateUncleanMarker(); var third = new TransactionJournal(directory); assertEquals(1, third.beginSession());
        try { assertTrue(third.blocked(r.listingId)); } finally { third.endSession(); }
    }
    @Test void unreadableReceiptsStayBlocked() throws Exception {
        var journal = new TransactionJournal(directory); journal.beginSession(); UUID id = UUID.randomUUID();
        try {
            Files.writeString(directory.resolve(id + ".json"), "{");
            assertTrue(journal.blocked(id)); assertEquals("UNREADABLE_RECORD", journal.inspect(id).phase());
        } finally { journal.endSession(); }
    }
}
