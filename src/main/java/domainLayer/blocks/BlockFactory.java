package domainLayer.blocks;

import types.BlockType;

/**
 * The BlockFactory is responsible for the creation of blocks.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockFactory {

	private BlockIDGenerator blockIDGenerator;

	public BlockFactory() {
		blockIDGenerator= BlockIDGenerator.getInstance();
	}

	/**
	 * Create a block of a certain BlockType.
	 * @param 	type
	 * 			The BlockType of which a block must be added.
	 */
	public Block createBlock(BlockType type) {
		String blockId = blockIDGenerator.generateBlockID();
		switch (type) {
		case If:
			return new IfBlock(blockId);
		case MoveForward:
			return new MoveForwardBlock(blockId);
		case Not:
			return new NotBlock(blockId);
		case TurnLeft:
			return new TurnLeftBlock(blockId);
		case TurnRight:
			return new TurnRightBlock(blockId);
		case WallInFront:
			return new WallInFrontBlock(blockId);
		case While:
			return new WhileBlock(blockId);
		default:
			// This can't happen.
			break;
		}
		
		return null;
	}

}