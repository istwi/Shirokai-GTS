package org.pokesplash.gts.economy;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaleCoordinatorTest {
    @TempDir Path directory;
    private TransactionJournal.Record receipt(UUID id) {
        UUID owner = UUID.randomUUID();
        return new TransactionJournal.Record(id, owner, owner, "impactor:dollars", BigDecimal.TEN, BigDecimal.TEN, "{\"asset\":\"kept\"}", "test");
    }
    private static class Port implements SaleCoordinator.Port {
        boolean listed = true;
        boolean allowed = true;
        int debits, credits, deliveries;
        public void validate() { if (!allowed) throw new ListingChangedException("changed"); }
        public void removeListing() { listed = false; }
        public void restoreListing() { listed = true; }
        public boolean debit() { debits++; return true; }
        public boolean credit() { credits++; return true; }
        public boolean refund() { return true; }
        public void deliver() { deliveries++; }
        public void history() {}
    }
    @Test void rejectedValidationDoesNotOverwriteCreationOrLockAsset() throws Exception {
        var journal = new TransactionJournal(directory);
        UUID id = UUID.randomUUID();
        journal.phase(receipt(id), "CREATED");
        String previous = Files.readString(directory.resolve(id + ".json"));
        var port = new Port(); port.allowed = false;
        var sales = new SaleCoordinator(journal);
        assertThrows(ListingChangedException.class, () -> sales.execute(receipt(id), port));
        assertEquals(previous, Files.readString(directory.resolve(id + ".json")));
        assertFalse(journal.blocked(id));
        assertTrue(port.listed);
        assertEquals(0, port.debits + port.credits + port.deliveries);
        port.allowed = true;
        assertTrue(sales.execute(receipt(id), port));
        assertEquals(1, port.debits);
        assertEquals(1, port.deliveries);
        assertThrows(IllegalStateException.class, () -> sales.execute(receipt(id), port));
        assertEquals(1, port.debits);
    }
    @Test void failedDebitRestoresListingWithoutHoldingItOrDelivering() throws Exception {
        var journal = new TransactionJournal(directory);
        var r = receipt(UUID.randomUUID());
        var port = new Port() { @Override public boolean debit() { debits++; return false; } };
        assertFalse(new SaleCoordinator(journal).execute(r, port));
        assertTrue(port.listed);
        assertFalse(journal.blocked(r.listingId));
        assertEquals(0, port.credits + port.deliveries);
    }
    @Test void uncertainDeliveryKeepsRecoverableSnapshotAndCannotRunAgain() throws Exception {
        var journal = new TransactionJournal(directory);
        var r = receipt(UUID.randomUUID());
        var port = new Port() { @Override public void deliver() { throw new IllegalStateException("injected failure"); } };
        var sales = new SaleCoordinator(journal);
        assertThrows(IllegalStateException.class, () -> sales.execute(r, port));
        assertTrue(journal.blocked(r.listingId));
        assertEquals("DELIVERY_INTENT", journal.inspect(r.listingId).phase());
        assertEquals(r.listingJson, journal.inspect(r.listingId).record().listingJson);
        assertThrows(IllegalStateException.class, () -> sales.execute(receipt(r.listingId), port));
        assertEquals(1, port.debits);
    }
}
