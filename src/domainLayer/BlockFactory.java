package domainLayer;


public class BlockFactory {

	private BlockIDGenerator blockIDGenerator;

	public BlockFactory() {
		blockIDGenerator= BlockIDGenerator.getInstance();
	}

	/**
	 * 
	 * @param type
	 */
	public Block createBlock(BlockType type) {
		String blockId = blockIDGenerator.getBlockID();
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