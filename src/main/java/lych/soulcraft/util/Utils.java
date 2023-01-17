package lych.soulcraft.util;

import com.google.common.base.Preconditions;
import lych.soulcraft.world.CommandData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Utils {
    public static final long INFINITY = -1;
    private static final StringTextComponent DUMMY_TEXT_COMPONENT = new StringTextComponent("");
    private static final Consumer<?> DUMMY_CONSUMER = o -> {};
    private static final Runnable DUMMY_RUNNABLE = () -> {};
    private static final Supplier<?> DUMMY_SUPPLIER = () -> null;
    private static final Function<?, ?> DUMMY_FUNCTION = o -> null;

    private Utils() {}

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

    public static long clamp(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static boolean allowsCommands(ServerWorld world) {
        return world.getServer().getWorldData().getAllowCommands() || world.getServer().getPlayerList().isAllowCheatsForAllPlayers();
    }

    public static boolean allowedCommands(ServerWorld world) {
        return CommandData.get(world.getServer()).isAllowedCommands();
    }

    public static ResourceLocation getRegistryName(IForgeRegistryEntry<?> entry) {
        return Objects.requireNonNull(entry.getRegistryName(), "If you call this method, you must ensure that registry name is not null");
    }

    public static <T> Collector<T, ?, TreeSet<T>> toTreeSet() {
        return Collectors.toCollection(TreeSet::new);
    }

    public static <T> Collector<T, ?, TreeSet<T>> toTreeSet(Comparator<T> comparator) {
        return Collectors.toCollection(() -> new TreeSet<>(comparator));
    }

    public static float fade(float x) {
        x = MathHelper.clamp(x, 0, 1);
        return (6 * x * x - 15 * x + 10) * x * x * x;
    }

    public static float round(float value, int scale) {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    public static float round(float value, int scale, RoundingMode mode) {
        BigDecimal d = new BigDecimal(Float.toString(value)).setScale(scale, mode);
        return d.floatValue();
    }

    public static double round(double value, int scale) {
        return round(value, scale, RoundingMode.HALF_UP);
    }

    public static double round(double value, int scale, RoundingMode mode) {
        BigDecimal d = new BigDecimal(Double.toString(value)).setScale(scale, mode);
        return d.doubleValue();
    }

    public static <T> T getOrDefault(@Nullable T obj, T defaultValue) {
//      Equivalent to obj == null ? defaultValue : obj
        return getOrDefault(obj, defaultValue, Function.identity());
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static <T, U> U applyIfNonnull(@Nullable T obj, Function<? super T, ? extends U> ifNonNull) {
        return getOrDefault(obj, null, ifNonNull);
    }

    public static <T, U> U getOrDefault(@Nullable T obj, U defaultValue, Function<? super T, ? extends U> ifNonNull) {
        return obj == null ? defaultValue : ifNonNull.apply(obj);
    }

    public static String snakeToCamel(String s) {
        String[] arr = s.split("_");
        if (Arrays.stream(arr).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Malformed string " + s);
        }
        StringBuilder builder = new StringBuilder();
        Arrays.stream(arr).forEach(ss -> {
            builder.append(ss.substring(0, 1).toUpperCase());
            builder.append(ss.substring(1));
        });
        return builder.toString();
    }
}
