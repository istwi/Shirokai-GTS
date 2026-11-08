package org.pokesplash.gts.economy;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class CurrencySelectionSessionTest {
    @Test void goingBackInvalidatesTheOldConfirmationAndAllowsChangingCurrency() {
        var session = new CurrencySelectionSession(id -> new MinimumPricePolicy.Quote(false, 0));
        AtomicReference<String> published = new AtomicReference<>();
        session.select("impactor:dollars");
        session.clearSelection();
        assertFalse(session.confirm("impactor:dollars", published::set));
        assertNotNull(session.select("impactor:tokens"));
        assertTrue(session.confirm("impactor:tokens", published::set));
        assertEquals("impactor:tokens", published.get());
    }

    @Test void repeatedConfirmationPublishesOnlyOnce() {
        var session = new CurrencySelectionSession(id -> new MinimumPricePolicy.Quote(true, 10));
        AtomicInteger count = new AtomicInteger();
        session.select("impactor:dollars");
        assertTrue(session.confirm("impactor:dollars", id -> count.incrementAndGet()));
        assertFalse(session.confirm("impactor:dollars", id -> count.incrementAndGet()));
        assertEquals(1, count.get());
    }

    @Test void cancellingPreventsPublishingAndReselection() {
        var session = new CurrencySelectionSession(id -> new MinimumPricePolicy.Quote(false, 0));
        session.select("impactor:dollars");
        session.cancel();
        assertNull(session.select("impactor:tokens"));
        assertFalse(session.confirm("impactor:dollars", id -> fail("cancelled sale published")));
    }

    @Test void confirmationRevalidatesCurrencyBeforePublishing() {
        AtomicInteger checks = new AtomicInteger();
        var session = new CurrencySelectionSession(id -> checks.incrementAndGet() == 1 ? new MinimumPricePolicy.Quote(false, 0) : null);
        session.select("impactor:dollars");
        assertFalse(session.confirm("impactor:dollars", id -> fail("invalid sale published")));
        assertFalse(session.isConsumed());
    }
}
