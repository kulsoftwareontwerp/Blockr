/**
 * 
 */
package domainLayer.blocks;

import exceptions.InvalidBlockConnectionException;

/**
 * BodyCavityBlock an interface for all blocks with a cavity in their body.
 * 
 * @version 0.1
 * @author group17
 *
 */
public interface BodyCavityBlock {

	/**
	 * Retrieve the first block of the body
	 * 
	 * @return the first block of the body or null when there's no first block of
	 *         the body.
	 */
	public ExecutableBlock getFirstBlockOfBody();

	/**
	 * Set the firstBlockOfBody connection to the given block.
	 * 
	 * @param block The executable block to be added as the first block of the body.
	 * @throws InvalidBlockConnectionException When a block is added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public void setFirstBlockOfBody(ExecutableBlock block);
}
