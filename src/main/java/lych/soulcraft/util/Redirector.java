package lych.soulcraft.util;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ArrayUtils.add;

public class Redirector<T extends A, A> implements Predicate<A> {
    private final T value;
    private final A[] aliases;

    @SafeVarargs
    public Redirector(T value, A... aliases) {
        Objects.requireNonNull(aliases, "Aliases should be non-null");
        this.value = value;
        this.aliases = add(aliases, value);
        Preconditions.checkArgument(this.aliases.length > 0, "Aliases should not be empty");
    }

    public T redirect(A a, Function<? super A, ? extends T> function) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(function);
        for (A alias : aliases) {
            if (isEqual(a, alias)) {
                return value;
            }
        }
        return function.apply(a);
    }

    protected boolean isEqual(A a, A alias) {
        return Objects.equals(a, alias);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean test(A a) {
//      Returns null if not redirected, so suppressed.
        T t = redirect(a, al -> null);
        return t != null;
    }
}
