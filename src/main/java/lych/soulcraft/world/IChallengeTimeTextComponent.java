package lych.soulcraft.world;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.BooleanSupplier;

public interface IChallengeTimeTextComponent extends IFormattableTextComponent {
    static IFormattableTextComponent of(BooleanSupplier supplier, IFormattableTextComponent component) {
        class Impl extends StringTextComponent implements IChallengeTimeTextComponent {
            private Impl() {
                super("");
            }
        }
        return supplier.getAsBoolean() ? new Impl().append(component) : component;
    }
}
