package calculator;

import jayunit.*;
import java.lang.reflect.*;

public class CalculatorTest {

	private Calculator sut;	// system under test
	
	public void init() {
		// TODO init sut
	}
	
	public void testDivideZero() {
		// TODO divide by 0, expect ArithmeticException
	}
	
	public void testNegativeAdd() {
		// TODO add -1, check if state == -1 afterwards
	}
	
	public void testResetRem() {
		// TODO test if rem == 0 after reset
	}
	
	public void dummyTest() {
		// TODO empty test, should be ignored by JayUnit
	}
}
