package lych.soulcraft.util;

import lych.soulcraft.SoulCraft;
import net.minecraft.util.Unit;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DefaultValues {
    public static final long INFINITY = -1;
    public static final Object DUMMY = Unit.INSTANCE;
    public static final ITextComponent COMMA = new TranslationTextComponent(SoulCraft.prefixMsg("comma"));
    public static final ITextComponent SPACE = new TranslationTextComponent(SoulCraft.prefixMsg("space"));
    public static final ITextComponent TRUE_SPACE = new StringTextComponent(" ");
    private static final StringTextComponent DUMMY_TEXT_COMPONENT = new StringTextComponent("");
    private static final Consumer<?> DUMMY_CONSUMER = o -> {};
    private static final Runnable DUMMY_RUNNABLE = () -> {};
    private static final Supplier<?> DUMMY_SUPPLIER = () -> null;
    private static final Function<?, ?> DUMMY_FUNCTION = o -> null;

    private DefaultValues() {}

    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> dummyFunction() {
        return (Function<T, R>) DUMMY_FUNCTION;
    }

    public static Runnable dummyRunnable() {
        return DUMMY_RUNNABLE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> dummySupplier() {
        return (Supplier<T>) DUMMY_SUPPLIER;
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> dummyConsumer() {
        return (Consumer<T>) DUMMY_CONSUMER;
    }

    public static StringTextComponent dummyTextComponent() {
        return DUMMY_TEXT_COMPONENT;
    }
}
