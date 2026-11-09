package org.pokesplash.gts.browser;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SaleInputTest {
    @Test void parsesCommaAndOptionalQuantity() {
        assertEquals("10.5", SaleInput.parse("10,50 3", true, 8).price().toPlainString());
        assertEquals(3, SaleInput.parse("10,50 3", true, 8).amount());
        assertEquals(1, SaleInput.parse("25", true, 8).amount());
    }
    @Test void rejectsInvalidAmountsAndPrices() {
        for (String value : new String[] {"NaN", "Infinity", "-1", "1.234", "10 0", "10 -1", "10 9", "10 999999999999", "10 1 extra", ""}) {
            assertThrows(IllegalArgumentException.class, () -> SaleInput.parse(value, true, 8), value);
        }
    }
    @Test void pokemonPriceDoesNotAcceptItemQuantity() {
        assertThrows(IllegalArgumentException.class, () -> SaleInput.parse("10 2", false, 1));
    }
}
