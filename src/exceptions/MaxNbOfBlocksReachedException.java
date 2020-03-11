package exceptions;

/**
 * This exception is thrown when an addBlock operation is tried when the maxNbOfBlocks is already met.
 * 
 * @version 0.1
 * @author group17
 */
public class MaxNbOfBlocksReachedException extends RuntimeException {
	private static final long serialVersionUID = 2208218934015147446L;

	
	/**
	 * Create a MaxNbOfBlocksReachedException
	 * @param 		message
	 * 				The message clarifying the context in which the exception was thrown.
	 */
	public MaxNbOfBlocksReachedException(String message) {
		super(message);
	}

}
