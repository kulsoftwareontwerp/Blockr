package domainLayer.blocks;

/**
 * The abstract class for the concept of a Not block.
 * 
 * @version 0.1
 * @author group17
 */
public class NotBlock extends UnaryOperatorBlock {

	/**
	 * Create a Not Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public NotBlock(String blockId) {
		super(blockId);
	}

	public boolean assess() {
		// TODO - implement NotBlock.assess
		throw new UnsupportedOperationException();
	}



}