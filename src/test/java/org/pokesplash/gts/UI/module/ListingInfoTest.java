package org.pokesplash.gts.UI.module;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListingInfoTest {
    @Test void priceLinePreservesDollarAndTokenColors() {
        for (var color : new net.minecraft.util.Formatting[]{net.minecraft.util.Formatting.GREEN, net.minecraft.util.Formatting.LIGHT_PURPLE}) {
            var price = net.minecraft.text.Text.empty().append(net.minecraft.text.Text.literal("10").formatted(color));
            var result = ListingInfo.priceLine(net.minecraft.text.Text.literal("Preço: "), price);
            assertEquals("Preço: 10", result.getString());
            var value = result.getSiblings().getFirst().getSiblings().getFirst();
            assertEquals(color.getColorValue(), value.getStyle().getColor().getRgb());
            assertEquals(net.minecraft.util.Formatting.GRAY.getColorValue(), result.getStyle().getColor().getRgb());
            assertFalse(result.getStyle().isItalic());
        }
    }

    @Test void compactRemainingTimeHandlesDayHourMinuteAndSecondBoundaries() {
        assertEquals("2d 22h 59m", ListingInfo.compactDuration((2 * 86400L + 22 * 3600 + 59 * 60 + 57) * 1000));
        assertEquals("21h 58m", ListingInfo.compactDuration((21 * 3600L + 58 * 60) * 1000));
        assertEquals("1m", ListingInfo.compactDuration(60000));
        assertEquals("59s", ListingInfo.compactDuration(59000));
        assertEquals("0s", ListingInfo.compactDuration(-1000));
    }
}
