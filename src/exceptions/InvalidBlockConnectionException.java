package exceptions;


/**
 * This exception is thrown when an operation took place that caused 2 blocks to try to connect while that isn't possible.
 * 
 * @version 0.1
 * @author group17
 */
public class InvalidBlockConnectionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Create an InvalidBlockConnectionException
	 * @param 		message
	 * 				The message clarifying the context in which the exception was thrown.
	 */
	public InvalidBlockConnectionException(String message) {
		super(message);
	}

}

