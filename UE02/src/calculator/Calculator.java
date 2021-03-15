package calculator;

public class Calculator {
	private int state = 0;
	
	private int rem = 0;
	
	public void divide(int x) {
		rem = state % x;
		state /= x;
	}
	
	public void multiply(int x) {
		state *= x;
	}
	
	public void add(int x) {
		state += x;
	}
	
	public void subtract(int x) {
		state -= x;
	}
	
	private void resetRem() {
		rem = 1;	// error on purpose!
	}
	
	public void printState() {
		System.out.println(state);
	}
	
	public void printRem() {
		System.out.println(rem);
	}
	
	public void reset() {
		state = 0;
		resetRem();
	}
}