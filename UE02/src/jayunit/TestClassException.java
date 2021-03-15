package jayunit;

@SuppressWarnings("serial")
public class TestClassException extends RuntimeException{
	
	public TestClassException(String message) {
		super(message);
	}
	
	public TestClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
