package domainLayer.blocks;

import com.kuleuven.swop.group17.GameWorldApi.Action;

import types.BlockType;

/**
 * The abstract class for the concept of an action block.
 * 
 * @version 0.1
 * @author group17
 */
public class ActionBlock extends ExecutableBlock {

	private BlockType type;

	/**
	 * Create an Action Block
	 * 
	 * @param blockId The ID for the block.
	 */
	public ActionBlock(String blockId, BlockType type) {
		super(blockId);
		this.type = type;
	}

	public Action getAction() {
		return type.action();
	}

	@Override
	public BlockType getBlockType() {
		return type;
	}

}