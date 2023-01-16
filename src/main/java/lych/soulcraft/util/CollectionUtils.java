package lych.soulcraft.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.NonNullList;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList() {
        return Collectors.toCollection(NonNullList::create);
    }

    @SafeVarargs
    public static <T> Collector<T, ?, NonNullList<T>> toNonNullList(T defaultValue, T... values) {
        return Collectors.toCollection(() -> createMutableNonNullList(defaultValue, values));
    }

    public static <T> Collector<T, ?, NonNullList<T>> toSizedNonNullList(T defaultValue, int size) {
        return Collectors.toCollection(() -> createMutableNonNullListWithSize(size, defaultValue));
    }

    @SafeVarargs
    public static <T> NonNullList<T> createMutableNonNullList(T defaultValue, T... elements) {
        return new NonNullList<T>(new ArrayList<>(Arrays.asList(elements)), defaultValue) {};
    }

    public static <T> NonNullList<T> createMutableNonNullListWithSize(int size, T defaultValue) {
        return new NonNullList<T>(new ArrayList<>(size), defaultValue) {};
    }

    public static <T> T getNonnullRandom(Collection<? extends T> collection) {
        return Objects.requireNonNull(getRandom(collection, new Random()));
    }

    public static <T> T getNonnullRandom(Collection<? extends T> collection, Random random) {
        return Objects.requireNonNull(getRandomIn(new ArrayList<>(collection), random));
    }

    @Nullable
    public static <T> T getRandom(Collection<? extends T> collection) {
        return getRandom(collection, new Random());
    }

    @Nullable
    public static <T> T getRandom(Collection<? extends T> collection, Random random) {
        return getRandomIn(new ArrayList<>(collection), random);
    }

    @Nullable
    private static <T> T getRandomIn(List<? extends T> list, Random random) {
        if (isNullOrEmpty(list)) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Returns a copy of a list that contains random elements of the list.
     * @param list The list
     * @param count The size of the new list
     * @param unmodifiable If true, the output list is unmodifiable
     * @return The new list
     */
    public static <T> List<T> getRandom(List<T> list, int count, boolean unmodifiable) {
        return getRandom(list, new Random(), count, unmodifiable);
    }

    public static <T> void refill(Collection<? super T> oldCollection, Collection<? extends T> newCollection) {
        oldCollection.clear();
        oldCollection.addAll(newCollection);
    }

    public static <K, V> void refill(Map<? super K, ? super V> oldMap, Map<? extends K, ? extends V> newMap) {
        oldMap.clear();
        oldMap.putAll(newMap);
    }

    public static <K, V> Stream<? extends Map.Entry<? extends K, ? extends V>> stream(Map<? extends K, ? extends V> map) {
        return map.entrySet().stream();
    }

    /**
     * Returns a copy of a list that contains random elements of the list.
     * @param list The list
     * @param random The random used to shuffle list
     * @param count The size of the new list
     * @param unmodifiable If true, the output list is unmodifiable
     * @return The new list
     */
    public static <T> List<T> getRandom(List<T> list, Random random, int count, boolean unmodifiable) {
        return getRandomIn(list, random, count, true, unmodifiable);
    }

    /**
     * Shuffles a list and get it.
     * @param list The list
     * @return The shuffled list
     */
    public static <T> List<T> shuffleAndGet(List<T> list) {
        return shuffleAndGet(list, new Random());
    }

    /**
     * Shuffles a list and get it.
     * @param list The list
     * @param random The random used to shuffle list
     * @return The shuffled list
     */
    public static <T> List<T> shuffleAndGet(List<T> list, Random random) {
        return shuffleAndGet(list, random, list.size());
    }

    /**
     * Returns a new list that contains random elements of the inputted list.
     * @param list The inputted list
     * @param random The random used to shuffle list
     * @param count The size of the shuffled list
     * @return The shuffled list
     */
    public static <T> List<T> shuffleAndGet(List<T> list, Random random, int count) {
        return getRandomIn(list, random, count, false, true);
    }

    /**
     * Returns a new list that contains random elements of the inputted list.
     * @param list The inputted list
     * @param random The random used to shuffle list
     * @param count The size of the new list
     * @param copy If true, return a copy of the list, otherwise modify the original list returns the sublist of the original list
     * @param unmodifiable If true, the output list is unmodifiable
     * @return The new list
     */
    private static <T> List<T> getRandomIn(List<T> list, Random random, int count, boolean copy, boolean unmodifiable) {
        checkList(list);
        Preconditions.checkElementIndex(count - 1, list.size(), "Illegal count");
        List<T> nonCopy = unmodifiable ? Collections.unmodifiableList(list) : list;
        if (count == list.size()) {
            if (copy) {
                return unmodifiable ? ImmutableList.copyOf(list) : new ArrayList<>(list);
            }
            return nonCopy;
        }
        if (count == 0) {
            if (copy) {
                return unmodifiable ? Collections.emptyList() : new ArrayList<>();
            }
            return nonCopy;
        }
        List<T> copyOfList = new ArrayList<>(list);
        Collections.shuffle(copyOfList, random);
        List<T> sublist;
        if (copy) {
            sublist = copyOfList.subList(0, count);
            return unmodifiable ? Collections.unmodifiableList(sublist) : sublist;
        }
        Collections.shuffle(list, random);
        sublist = list.subList(0, count);
        return unmodifiable ? Collections.unmodifiableList(sublist) : sublist;
    }

    private static <T> void checkList(List<T> list) {
        Preconditions.checkArgument(!isNullOrEmpty(list), "Invalid list: " + list);
    }

    public static boolean isNullOrEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> Iterable<T> iterable(Iterator<T> itr) {
        return () -> itr;
    }

    public static boolean isSingleton(@Nullable Collection<?> collection) {
        return collection != null && collection.size() == 1;
    }
}
