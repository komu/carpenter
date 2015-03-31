package fi.evident.carpenter.utils;

import org.junit.Test;

import static fi.evident.carpenter.utils.CollectionUtils.concat;
import static fi.evident.carpenter.utils.CollectionUtils.map;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CollectionUtilsTest {

    @Test
    public void concatenation() {
        assertThat(concat(emptyList(), emptyList()), is(emptyList()));
        assertThat(concat(asList("foo", "bar", "baz"), emptyList()), is(asList("foo", "bar", "baz")));
        assertThat(concat(emptyList(), asList("foo", "bar", "baz")), is(asList("foo", "bar", "baz")));
        assertThat(concat(asList("foo", "bar", "baz"), asList("quux", "xyzzy")), is(asList("foo", "bar", "baz", "quux", "xyzzy")));
    }

    @Test
    public void mapping() {
        assertThat(map(asList("foo", "xyzzy", "quux"), String::length), is(asList(3, 5, 4)));
    }
}
