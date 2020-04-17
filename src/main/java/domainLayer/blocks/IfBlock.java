package domainLayer.blocks;

import types.BlockType;

/**
 * The concrete class for the concept of an if block.
 * 
 * @version 0.1
 * @author group17
 */
public class IfBlock extends ControlBlock {

	/**
	 * Create an If Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public IfBlock(String blockId) {
		super(blockId);
	}
	@Override
	public BlockType getBlockType() {
		return BlockType.IF;
	}
}