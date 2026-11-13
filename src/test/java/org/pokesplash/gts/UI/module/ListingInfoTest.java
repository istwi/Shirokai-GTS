package org.pokesplash.gts.UI.module;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListingInfoTest {
    @Test void compactRemainingTimeHandlesDayHourMinuteAndSecondBoundaries() {
        assertEquals("2d 22h 59m", ListingInfo.compactDuration((2 * 86400L + 22 * 3600 + 59 * 60 + 57) * 1000));
        assertEquals("21h 58m", ListingInfo.compactDuration((21 * 3600L + 58 * 60) * 1000));
        assertEquals("1m", ListingInfo.compactDuration(60000));
        assertEquals("59s", ListingInfo.compactDuration(59000));
        assertEquals("0s", ListingInfo.compactDuration(-1000));
    }
}
