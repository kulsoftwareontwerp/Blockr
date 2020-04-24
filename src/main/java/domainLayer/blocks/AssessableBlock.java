package domainLayer.blocks;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

/**
 * The abstract class for the concept of an assessable block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class AssessableBlock extends Block {


	/**
	 * Create an Assessable Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public AssessableBlock(String blockId) {
		super(blockId);
	}
	
	public abstract boolean assess(GameWorld gameWorld);

}