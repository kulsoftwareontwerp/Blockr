package domainLayer.blocks;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import types.BlockType;

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

	@Override
	public boolean assess(GameWorld gameWorld) {		
		return !getOperand().assess(gameWorld);
	}

	@Override
	public BlockType getBlockType() {
		return BlockType.NOT;
	}

}