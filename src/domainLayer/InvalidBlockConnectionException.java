package domainLayer;

public class InvalidBlockConnectionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//TODO: add the relevant block to the exception. 
	public InvalidBlockConnectionException(String message) {
		super(message);
	}

}
