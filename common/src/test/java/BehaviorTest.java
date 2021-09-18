import org.junit.jupiter.api.Test;
import util.Behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class BehaviorTest {

    @Test
    public void testBasicBehavior() {
        Behavior<TestInterface, Integer> behavior = new Behavior<TestInterface, Integer>()
                .entry(ClassOne.class, this::functionOne)
                .entry(ClassTwo.class, this::functionTwo)
                .def(c -> 3);

        int result = behavior.apply(new ClassOne());
        assertEquals(1, result);

        // Falls to default
        result = behavior.apply(new ClassThree());
        assertEquals(3, result);
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

        int result = behavior1.apply(new ClassOne());
        assertEquals(111, result);
    }


    private int functionOne(ClassOne value) {
        return 1;
    }

    private int functionOneUpdated(ClassOne value) {
        return 111;
    }

    private int functionTwo(ClassTwo value) {
        return 2;
    }

    interface TestInterface {}

    private static class ClassOne implements TestInterface {}

    private static class ClassTwo implements TestInterface {}

    private static class ClassThree implements TestInterface {}
}
