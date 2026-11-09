package org.pokesplash.gts.UI;

import static org.junit.jupiter.api.Assertions.*;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import java.util.function.Consumer;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.text.Text;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.pokesplash.gts.config.layout.LayoutConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReusableSelectionTemplateTest {
    private static final class Marker implements Button {
        public ItemStack getDisplay() { return null; }
        public void subscribe(Object observer, Consumer<Button> consumer) {}
        public void unsubscribe(Object observer) {}
    }

    @BeforeAll static void initializeMinecraftTypes() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test void originalBuilderLosesButtonsButFixedBuilderSurvivesRepeatedReturns() {
        Button dollars = new Marker(), tokens = new Marker(), cancel = new Marker();
        var original = ChestTemplate.builder(3).set(10, dollars);
        original.build();
        assertTrue(original.build().getSlot(10).getButton().isEmpty());

        var fixed = ReusableSelectionTemplate.builder(3).set(10, dollars).set(11, tokens).set(22, cancel);
        ChestTemplate initial = fixed.build();
        for (int i = 0; i < 10; i++) {
            ChestTemplate reopened = fixed.build();
            assertNotSame(initial, reopened);
            assertEquals(27, reopened.getSize());
            assertSame(dollars, reopened.getSlot(10).getButton().orElseThrow());
            assertSame(tokens, reopened.getSlot(11).getButton().orElseThrow());
            assertSame(cancel, reopened.getSlot(22).getButton().orElseThrow());
            reopened.clear();
        }
        assertSame(dollars, initial.getSlot(10).getButton().orElseThrow());
    }

    @Test void changingOnlyAnIconKeepsLabelsLoreAndTheAction() {
        Text label = Text.literal("Confirmar");
        LoreComponent lore = new LoreComponent(List.of(Text.literal("Preço: 100")));
        AtomicInteger clicks = new AtomicInteger();
        Button source = GooeyButton.builder().display(new ItemStack(Items.STONE))
                .with(DataComponentTypes.CUSTOM_NAME, label).with(DataComponentTypes.LORE, lore)
                .onClick(action -> clicks.incrementAndGet()).build();
        var visual = new LayoutConfig.Visual();
        visual.item = "minecraft:emerald";
        Button result = Layouts.appearance(source, visual);
        assertSame(Items.EMERALD, result.getDisplay().getItem());
        assertEquals(label, result.getDisplay().get(DataComponentTypes.CUSTOM_NAME));
        assertEquals(lore, result.getDisplay().get(DataComponentTypes.LORE));
        assertSame(Items.STONE, source.getDisplay().getItem());
        result.onClick(null);
        assertEquals(1, clicks.get());
    }

    @Test void glassControlsBecomeGrayWithoutChangingLabelsOrCallbacks() {
        AtomicInteger clicks = new AtomicInteger();
        Text label = Text.literal("Confirmar");
        Button original = GooeyButton.builder().display(new ItemStack(Items.GREEN_STAINED_GLASS_PANE))
                .with(DataComponentTypes.CUSTOM_NAME, label).onClick(a -> clicks.incrementAndGet()).build();
        Button changed = Layouts.appearance(original, new LayoutConfig.Visual());
        assertSame(Items.GRAY_STAINED_GLASS_PANE, changed.getDisplay().getItem());
        assertEquals(label, changed.getDisplay().get(DataComponentTypes.CUSTOM_NAME));
        changed.onClick(null);
        assertEquals(1, clicks.get());
        assertSame(Items.GREEN_STAINED_GLASS_PANE, original.getDisplay().getItem());
    }

    @Test void actualGlassItemsBeingTradedKeepTheirColor() {
        Button product = GooeyButton.builder().display(new ItemStack(Items.BLUE_STAINED_GLASS_PANE, 32)).build();
        Button preview = Layouts.appearance(product, new LayoutConfig.Visual(), false);
        assertSame(Items.BLUE_STAINED_GLASS_PANE, preview.getDisplay().getItem());
        assertEquals(32, preview.getDisplay().getCount());
    }
}
