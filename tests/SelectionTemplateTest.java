import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import java.util.function.Consumer;
import org.pokesplash.gts.UI.ReusableSelectionTemplate;

public final class SelectionTemplateTest {
    private static final class Marker implements Button {
        public net.minecraft.class_1799 getDisplay() { return null; }
        public void subscribe(Object observer, Consumer<Button> consumer) {}
        public void unsubscribe(Object observer) {}
    }

    public static void main(String[] args) {
        Button first = new Marker(), second = new Marker(), cancel = new Marker();
        ChestTemplate.Builder original = ChestTemplate.builder(3).set(10, first);
        original.build();
        check(original.build().getSlot(10).getButton().isEmpty(), "original bug must reproduce");

        ChestTemplate.Builder fixed = ReusableSelectionTemplate.builder(3)
                .set(10, first).set(11, second).set(22, cancel);
        ChestTemplate initial = fixed.build();
        for (int i = 0; i < 10; i++) {
            ChestTemplate reopened = fixed.build();
            check(reopened != initial, "each opening needs independent slots");
            check(reopened.getSize() == 27, "wrong size");
            check(reopened.getSlot(10).getButton().orElseThrow() == first, "first currency lost");
            check(reopened.getSlot(11).getButton().orElseThrow() == second, "second currency lost");
            check(reopened.getSlot(22).getButton().orElseThrow() == cancel, "cancel lost");
            reopened.clear();
        }
        check(initial.getSlot(10).getButton().orElseThrow() == first, "shared mutable slots");
        System.out.println("PASS: original reset reproduced; 10 reopenings preserve currencies and cancel");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
