import org.junit.jupiter.api.Test;
import util.Behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class BehaviorTest {

    private int testVariable;

    @Test
    public void testBasicBehavior() {
        Behavior<TestInterface, Integer> behavior = new Behavior<TestInterface, Integer>()
                .entry(ClassOne.class, this::functionOne)
                .entry(ClassTwo.class, this::functionTwo)
                .def((t, i) -> testVariable = 3);

        behavior.apply(new ClassOne(), 123);
        assertEquals(1, testVariable);

        // Falls to default
        behavior.apply(new ClassThree(), 123);
        assertEquals(3, testVariable);
    }

    @Test
    public void testClassAlreadyExists() {
        assertThrowsExactly(IllegalArgumentException.class, () ->
                new Behavior<TestInterface, Integer>()
                        .entry(ClassOne.class, this::functionOne)
                        .entry(ClassOne.class, this::functionOne)
        );
    }

    @Test
    public void testCombineBehaviors() {
        Behavior<TestInterface, Integer> behavior1 = new Behavior<TestInterface, Integer>()
                .entry(ClassOne.class, this::functionOne);

        Behavior<TestInterface, Integer> behavior2 = new Behavior<TestInterface, Integer>()
                .entry(ClassOne.class, this::functionOneUpdated);

        behavior1.overlay(behavior2);

        behavior1.apply(new ClassOne(), 123);
        assertEquals(111, testVariable);
    }


    private void functionOne(ClassOne value, Integer val) {
        this.testVariable = 1;
    }

    private void functionOneUpdated(ClassOne value, Integer val) {
        this.testVariable = 111;
    }

    private void functionTwo(ClassTwo value, Integer val) {
        this.testVariable = 2;
    }

    interface TestInterface {}

    private static class ClassOne implements TestInterface {}

    private static class ClassTwo implements TestInterface {}

    private static class ClassThree implements TestInterface {}
}
