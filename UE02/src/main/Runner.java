package main;

import calculator.CalculatorTest;
import jayunit.JayUnit;

public class Runner {
	public static void main(String[] args) {
		JayUnit.runTests(CalculatorTest.class);
	}
}
