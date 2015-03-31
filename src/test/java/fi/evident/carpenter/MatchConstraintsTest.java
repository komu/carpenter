package fi.evident.carpenter;

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static fi.evident.carpenter.MatchConstraints.empty;
import static fi.evident.carpenter.MatchConstraints.invalid;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class MatchConstraintsTest {

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
        MatchConstraints constraints = MatchConstraints.fromCapture(var, "foo");

        assertThat(constraints, isValid());
    }

    @Test
    public void constraintsForDifferentVariablesAreValid() {
        Capture<String> var1 = new Capture<>("var1");
        Capture<String> var2 = new Capture<>("var2");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var1, "foo");
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var2, "bar");

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreValidIfValueIsEquals() {
        Capture<String> var = new Capture<>("var");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var, "foo");
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var, "foo");

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreValidIfBothValuesAreNull() {
        Capture<String> var = new Capture<>("var");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var, null);
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var, null);

        assertThat(constraints1.merge(constraints2), isValid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEquals() {
        Capture<String> var = new Capture<>("var");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var, "foo");
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var, "bar");

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEqualsForNullLhsValue() {
        Capture<String> var = new Capture<>("var");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var, null);
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var, "foo");

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @Test
    public void constraintsForSameVariableAreInvalidIfValueIsNotEqualsForNullRhsValue() {
        Capture<String> var = new Capture<>("var");
        MatchConstraints constraints1 = MatchConstraints.fromCapture(var, "foo");
        MatchConstraints constraints2 = MatchConstraints.fromCapture(var, null);

        assertThat(constraints1.merge(constraints2), isInvalid());
    }

    @NotNull
    private static Matcher<MatchConstraints> isInvalid() {
        return not(isValid());
    }

    @NotNull
    private static Matcher<MatchConstraints> isValid() {
        return new CustomTypeSafeMatcher<MatchConstraints>("valid constraints") {
            @Override
            protected boolean matchesSafely(MatchConstraints item) {
                return item.isValid();
            }
        };
    }
}
