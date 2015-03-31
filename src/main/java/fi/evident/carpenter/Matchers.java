package fi.evident.carpenter;

import fi.evident.carpenter.utils.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static fi.evident.carpenter.utils.CollectionUtils.copyWithSubListReplaced;
import static fi.evident.carpenter.utils.CollectionUtils.copyWithValueReplacedAtIndex;
import static java.util.Arrays.asList;

public final class Matchers {

    private Matchers() {
    }

    @NotNull
    public static <T> Matcher<T> any() {
        return predicate(a -> true);
    }

    @NotNull
    public static <T> Matcher<T> isInstance(@NotNull Class<? extends T> cl) {
        return predicate(cl::isInstance);
    }

    @NotNull
    public static <T> Matcher<T> predicate(@NotNull Predicate<? super T> predicate) {
        return new Matcher<T>() {
            @NotNull
            @Override
            public Match<T> match(@NotNull T value) {
                return predicate.test(value) ? Match.constant(value) : Match.failure();
            }
        };
    }

    @NotNull
    public static <T> Matcher<T> isEqual(T value) {
        return predicate(Predicate.isEqual(value));
    }

    @NotNull
    public static <T> Matcher<Optional<T>> required(@NotNull Matcher<T> matcher) {
        return new Matcher<Optional<T>>() {
            @NotNull
            @Override
            public Match<Optional<T>> match(@NotNull Optional<T> value) {
                T v = value.orElse(null);
                if (v != null)
                    return Match.from(Optional::of, matcher.match(v));
                else
                    return Match.failure();
            }
        };
    }

    @NotNull
    public static <T> Matcher<List<T>> contains(@NotNull Matcher<T> matcher) {
        return new Matcher<List<T>>() {
            @NotNull
            @Override
            public Match<List<T>> match(@NotNull List<T> value) {
                for (int i = 0, len = value.size(); i < len; i++) {
                    Match<T> m = matcher.match(value.get(i));
                    int index = i;

                    if (m.isSuccess())
                        return Match.from(v -> copyWithValueReplacedAtIndex(value, index, v), m);
                }
                return Match.failure();
            }
        };
    }

    @NotNull
    public static <T> Matcher<List<T>> listWithPrefix(@NotNull List<Matcher<T>> prefixMatchers, @NotNull Matcher<List<T>> suffixMatcher) {
        return new Matcher<List<T>>() {
            @NotNull
            @Override
            public Match<List<T>> match(@NotNull List<T> value) {
                List<Match<T>> prefixMatches = prefixMatches(value);
                if (prefixMatches == null) return Match.failure();

                List<T> suffix = value.subList(prefixMatches.size(), value.size());
                Match<List<T>> suffixMatch = suffixMatcher.match(suffix);

                return Match.fromList(CollectionUtils::concat, prefixMatches, suffixMatch);
            }

            @Nullable
            private List<Match<T>> prefixMatches(@NotNull List<T> list) {
                if (prefixMatchers.size() > list.size())
                    return null;

                List<Match<T>> matches = new ArrayList<>(prefixMatchers.size());

                int i = 0;
                for (Matcher<T> matcher : prefixMatchers) {
                    Match<T> m = matcher.match(list.get(i++));
                    if (m.isSuccess())
                        matches.add(m);
                    else
                        return null;
                }

                return matches;
            }
        };
    }

    @NotNull
    @SafeVarargs
    public static <T> Matcher<List<T>> listWithConsecutive(@NotNull Matcher<T>... matchers) {
        return listWithConsecutive(asList(matchers));
    }

    @NotNull
    public static <T> Matcher<List<T>> listWithConsecutive(@NotNull List<? extends Matcher<T>> matchers) {
        return new Matcher<List<T>>() {
            @NotNull
            @Override
            public Match<List<T>> match(@NotNull List<T> value) {
                // Pre-allocate a list for matches so that we don't have to create new for every attempt
                List<Match<T>> matches = new ArrayList<>(matchers.size());
                for (int i = 0, max = value.size() - matchers.size() + 1; i < max; i++) {
                    if (matchesAt(value, i, matches)) {
                        int index = i;
                        return Match.fromList(vs -> copyWithSubListReplaced(value, index, vs), matches);
                    }
                }
                return Match.failure();
            }

            private boolean matchesAt(@NotNull List<T> values, int index, @NotNull List<Match<T>> matches) {
                matches.clear();

                int i = index;
                for (Matcher<T> matcher : matchers) {
                    Match<T> m = matcher.match(values.get(i++));
                    if (m.isSuccess())
                        matches.add(m);
                    else
                        return false;
                }

                return Match.mergeAll(matches).isValid();
            }
        };
    }

    @SafeVarargs
    @NotNull
    public static <T> Matcher<List<T>> list(@NotNull Matcher<T>... matchers) {
        return list(asList(matchers));
    }

    @NotNull
    public static <T> Matcher<List<T>> list(@NotNull List<Matcher<T>> matchers) {
        return new Matcher<List<T>>() {
            @NotNull
            @Override
            public Match<List<T>> match(@NotNull List<T> value) {
                if (value.size() != matchers.size())
                    return Match.failure();

                Iterator<T> valuesIterator = value.iterator();
                Iterator<Matcher<T>> matchersIterator = matchers.iterator();
                List<Match<T>> matches = new ArrayList<>(matchers.size());

                while (valuesIterator.hasNext()) {
                    Match<T> m = matchersIterator.next().match(valuesIterator.next());
                    if (m.isSuccess())
                        matches.add(m);
                    else
                        return Match.failure();
                }

                return Match.fromList(matches);
            }
        };
    }
}
