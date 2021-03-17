package main;

import calculator.CalculatorTest;
import jayunit.JayUnit;

import java.lang.reflect.InvocationTargetException;

public class Runner {
	public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		JayUnit.runTests(CalculatorTest.class);
	}
}
