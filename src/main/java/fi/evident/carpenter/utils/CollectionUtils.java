package fi.evident.carpenter.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Returns the first element of the list.
     *
     * @throws IllegalArgumentException if the list is empty
     */
    @NotNull
    public static <T> T head(@NotNull List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("can't take head of empty list");

        return list.get(0);
    }

    /**
     * Returns the last element of the list.
     *
     * @throws IllegalArgumentException if the list is empty
     */
    @NotNull
    public static <T> T last(@NotNull List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("can't take last of empty list");

        return list.get(list.size() - 1);
    }

    /**
     * Returns sub-list containing all but the first element of the list.
     *
     * @throws IllegalArgumentException if the list is empty
     */
    @NotNull
    public static <T> List<T> tail(@NotNull List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("can't take tail of empty list");

        return list.subList(1, list.size());
    }

    /**
     * Returns sub-list containing all but the last element of the list.
     *
     * @throws IllegalArgumentException if the list is empty
     */
    @NotNull
    public static <T> List<T> init(@NotNull List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("can't take init of empty list");

        return list.subList(0, list.size() - 1);
    }

    @NotNull
    public static <A,B> List<B> map(@NotNull Collection<? extends A> list, @NotNull Function<? super A, ? extends B> function) {
        return list.stream().map(function).collect(toList());
    }

    @NotNull
    public static <A,B> List<B> map(@NotNull A[] array, @NotNull Function<? super A, ? extends B> function) {
        return Stream.of(array).map(function).collect(toList());
    }

    @NotNull
    public static <K,B,A> Map<K,B> mapValues(@NotNull Map<K,A> map, @NotNull Function<? super A, ? extends B> function) {
        Map<K,B> result = new LinkedHashMap<>(map.size());

        for (Map.Entry<K, A> entry : map.entrySet())
            result.put(entry.getKey(), function.apply(entry.getValue()));

        return result;
    }

    @NotNull
    public static <T> List<T> filter(@NotNull List<? extends T> list, @NotNull Predicate<? super T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.<T>toList());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Optional<T> findFirst(@NotNull List<? extends T> list, @NotNull Predicate<? super T> predicate) {
        return (Optional<T>) list.stream().filter(predicate).findFirst();
    }

    @NotNull
    public static <T> List<T> cons(@NotNull T x, @NotNull List<T> xs) {
        List<T> result = new ArrayList<>(1 + xs.size());
        result.add(x);
        result.addAll(xs);
        return result;
    }

    public static int sum(@NotNull Collection<Integer> values) {
        return values.stream().mapToInt(x -> x).sum();
    }

    @NotNull
    public static <A,B> List<Pair<A,B>> pairs(@NotNull List<? extends A> as, @NotNull List<? extends B> bs) {
        List<Pair<A,B>> result = new ArrayList<>(min(as.size(), bs.size()));

        Iterator<? extends A> itA = as.iterator();
        Iterator<? extends B> itB = bs.iterator();
        while (itA.hasNext() && itB.hasNext())
            result.add(Pair.of(itA.next(), itB.next()));

        return result;
    }

    @NotNull
    public static <T> List<T> concat(@NotNull Collection<T> xs, @NotNull Collection<T> ys) {
        List<T> result = new ArrayList<>(xs.size() + ys.size());
        result.addAll(xs);
        result.addAll(ys);
        return result;
    }

    @NotNull
    public static <T> List<T> copyWithValueReplacedAtIndex(@NotNull List<T> list, int index, T v) {
        List<T> result = new ArrayList<>(list);
        result.set(index, v);
        return result;
    }

    @NotNull
    public static <T> List<T> copyWithSubListReplaced(List<T> list, int index, List<T> replacement) {
        List<T> result = new ArrayList<>(list);

        int i = index;
        for (T v : replacement)
            result.set(i++, v);

        return result;
    }

    @NotNull
    public static String join(@NotNull String separator, @NotNull Collection<?> items) {
        return items.stream().map(String::valueOf).collect(joining(separator));
    }
}
