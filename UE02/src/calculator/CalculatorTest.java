package calculator;

import jayunit.BeforeTests;
import jayunit.ExpectException;
import jayunit.MyTest;
import jayunit.*;

import java.lang.reflect.*;

public class CalculatorTest {

    private Calculator sut;    // system under test

    @BeforeTests
    public void init() {
        sut = new Calculator();
    }

    @MyTest
    @ExpectException(exception = ArithmeticException.class)
    public void testDivideZero() {
        sut.add(1);
        sut.divide(0);
    }

    @MyTest
    public void testNegativeAdd() throws NoSuchFieldException, IllegalAccessException, TestFailedException {
        // add -1, check if state == -1 afterwards
        sut.add(-1);

        Field state = sut.getClass().getDeclaredField("state");
        state.setAccessible(true);
        if (!state.get(sut).equals(-1)) {
            throw new TestFailedException();
        }

    }

    @MyTest
    public void testResetRem() throws TestFailedException, NoSuchFieldException, IllegalAccessException {
        // test if rem == 0 after reset
        sut.add(2);
        sut.divide(1);
        sut.reset();

        Field f;

        f = sut.getClass().getDeclaredField("rem");
        f.setAccessible(true);
        if (!f.get(sut).equals(0)) {
            throw new TestFailedException();
        }
    }

    @MyTest(ignore = true)
    public void dummyTest() {
        sut.divide(2);
    }
}
