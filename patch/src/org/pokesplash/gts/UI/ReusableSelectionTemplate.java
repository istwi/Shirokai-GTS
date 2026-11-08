package org.pokesplash.gts.UI;

import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;

/** A selection menu whose callbacks can reopen it after Builder.build() resets. */
public final class ReusableSelectionTemplate extends ChestTemplate.Builder {
    private ChestTemplate snapshot;

    private ReusableSelectionTemplate(int rows) {
        super(rows);
    }

    public static ChestTemplate.Builder builder(int rows) {
        return new ReusableSelectionTemplate(rows);
    }

    @Override
    public ChestTemplate build() {
        if (snapshot == null) {
            snapshot = super.build();
        }
        return snapshot.clone();
    }
}
