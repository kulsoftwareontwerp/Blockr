package exceptions;

/**
 * This exception is thrown when an operation needs a connectedBlockID when the given BlockId isn't preset in the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class NoSuchConnectedBlockException extends RuntimeException {
	private static final long serialVersionUID = -7856102320458746727L;

	/**
	 * Create an NoSuchConnectedBlockException
	 * @param 		message
	 * 				The message clarifying the context in which the exception was thrown.
	 */
	public NoSuchConnectedBlockException(String message) {
		super(message);
	}
	
	

}

