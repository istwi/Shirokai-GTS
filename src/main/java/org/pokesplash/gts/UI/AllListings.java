package org.pokesplash.gts.UI;
import ca.landonjw.gooeylibs2.api.page.Page;
import net.minecraft.server.network.ServerPlayerEntity;
/** Compatibility entry point for integrations using the original class. */
public class AllListings {
    public Page getPage() { return ListingBrowser.page(null); }
    public Page getPage(ServerPlayerEntity viewer) { return ListingBrowser.page(viewer); }
}
