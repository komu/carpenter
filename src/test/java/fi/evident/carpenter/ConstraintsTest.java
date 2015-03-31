package fi.evident.carpenter;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static fi.evident.carpenter.Constraints.empty;
import static fi.evident.carpenter.Constraints.invalid;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ConstraintsTest {

    @Test
    public void emptyConstraintsIsValid() {
        assertThat(empty(), isValid());
    }

    @Test
    public void mergingEmptyConstraintsIsValid() {
        assertThat(empty().merge(empty()), isValid());
    }

    @Test
    public void invalidConstraints() {
        assertThat(invalid(), isInvalid());
    }

    @Test
    public void mergingInvalidConstraints() {
        assertThat(invalid().merge(empty()), isInvalid());
        assertThat(empty().merge(invalid()), isInvalid());
    }

    @Test
    public void singleConstraintIsValid() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints = Constraints.forValue(var, "foo");

        assertThat(constraints, isValid());
    }

    @Test
    public void constraintsForDifferentVariablesAreValid() {
        Capture<String> var1 = new Capture<>("var1");
        Capture<String> var2 = new Capture<>("var2");
        Constraints constraints1 = Constraints.forValue(var1, "foo");
        Constraints constraints2 = Constraints.forValue(var2, "bar");

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreValidIfValueIsEquals() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints1 = Constraints.forValue(var, "foo");
        Constraints constraints2 = Constraints.forValue(var, "foo");

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreValidIfBothValuesAreNull() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints1 = Constraints.forValue(var, null);
        Constraints constraints2 = Constraints.forValue(var, null);

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEquals() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints1 = Constraints.forValue(var, "foo");
        Constraints constraints2 = Constraints.forValue(var, "bar");

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEqualsForNullLhsValue() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints1 = Constraints.forValue(var, null);
        Constraints constraints2 = Constraints.forValue(var, "foo");

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEqualsForNullRhsValue() {
        Capture<String> var = new Capture<>("var");
        Constraints constraints1 = Constraints.forValue(var, "foo");
        Constraints constraints2 = Constraints.forValue(var, null);

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @Test
    public void getExistingValue() {
        Capture<String> var = new Capture<>("var");
        assertThat(Constraints.forValue(var, "foo").getValue(var), is("foo"));
    }

    @Test
    public void getExistingNullValue() {
        Capture<String> var = new Capture<>("var");
        assertThat(Constraints.forValue(var, null).getValue(var), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistingNullValue() {
        Constraints.empty().getValue(new Capture<>("var"));
    }

    @Test
    public void mergeMultipleSuccessfully() {
        Capture<String> var1 = new Capture<>("var1");
        Capture<String> var2 = new Capture<>("var2");
        Capture<String> var3 = new Capture<>("var3");

        Constraints result = Constraints.mergeAll(asList(
                Constraints.forValue(var1, "foo"),
                Constraints.forValue(var2, "bar"),
                Constraints.forValue(var3, "baz"),
                Constraints.forValue(var3, "baz")));

        assertThat(result, isValid());
        assertThat(result.getValue(var1), is("foo"));
        assertThat(result.getValue(var2), is("bar"));
        assertThat(result.getValue(var3), is("baz"));
    }

    @Test
    public void mergeAllEmpty() {
        Constraints result = Constraints.mergeAll(emptyList());

        assertThat(result, isValid());
        assertThat(result, isEmpty());
    }

    @NotNull
    private static Matcher<Constraints> isInvalid() {
        return not(isValid());
    }

    @NotNull
    private static Matcher<Constraints> isValid() {
        return new CustomTypeSafeMatcher<Constraints>("valid constraints") {
            @Override
            protected boolean matchesSafely(Constraints item) {
                return item.isValid();
            }
        };
    }

    @NotNull
    private static Matcher<Constraints> isEmpty() {
        return new CustomTypeSafeMatcher<Constraints>("empty constraints") {
            @Override
            protected boolean matchesSafely(Constraints item) {
                return item.isEmpty();
            }
        };
    }
}
