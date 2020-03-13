package exceptions;

import domainLayer.blocks.Block;

/**
 * This exception is thrown when an operation took place that needed a block of a certain type but retrieved a block of another type.
 * 
 * @version 0.1
 * @author group17
 */
public class InvalidBlockTypeException extends RuntimeException {
	private static final long serialVersionUID = -1610735208954128656L;
	private Class<? extends Block> expected;
	private Class<? extends Block> actual;

	/**
	 * Create an InvalidBlockConnectionException
	 * @param 		expected
	 * 				The expected blockType
	 * @param 		actual
	 * 				The actual blockType
	 */
	public InvalidBlockTypeException(Class<? extends Block> expected,Class<? extends Block> actual) {
		super();

		
		this.expected=expected;
		this.actual=actual;
		
	}

	/**
	 * Retrieve the expected class
	 * 
	 * @return The expected class
	 */
	public Class<? extends Block> getExpected() {
		return expected;
	}

	
	/**
	 * Retrieve the actual class
	 * 
	 * @return The actual class
	 */
	public Class<? extends Block> getActual() {
		return actual;
	}

	@Override
	public String getMessage() {
		StringBuilder b = new StringBuilder();
		b.append("Expected to see a block extending the class ");
		b.append(expected.getSimpleName());
		b.append(" but got a block extending ");
		b.append(actual.getSimpleName());
		b.append(" instead.");
		return b.toString();
	}
	
	
	
	

	
	
}
