package domainLayer.blocks;

import domainLayer.elements.ElementRepository;
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
		switch (type.cat()) {
		case CONTROL:
			switch (type.type()) {
			case "If":
				return new IfBlock(blockId);
			case "While":
				return new WhileBlock(blockId);
			default:
				throw new IllegalArgumentException("Unexpected value: " + type.type());
			}
		case OPERATOR:
			return new NotBlock(blockId);
		case ACTION:
			return new ActionBlock(blockId, type.action());
		case CONDITION:
			return new ConditionBlock(blockId, type.predicate());
		default:
			return null;
		}
	}

}