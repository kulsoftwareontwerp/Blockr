package domainLayer.blocks;

import types.BlockType;

/**
 * The concrete class for the concept of a while block.
 * 
 * @version 0.1
 * @author group17
 */
public class WhileBlock extends ControlBlock {

	/**
	 * Create a While Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public WhileBlock(String blockId) {
		super(blockId);
	}
	
	@Override
	public BlockType getBlockType() {
		return BlockType.WHILE;
	}

}