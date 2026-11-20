package org.pokesplash.gts.economy;

/** A stale quote is a harmless rejection, before any transaction is started. */
public final class ListingChangedException extends IllegalStateException {
    public ListingChangedException(String message) {
        super(message);
    }
}
