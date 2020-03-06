package domainLayer;


public class BlockFactory {

	@SuppressWarnings("unused")
	private BlockIDGenerator blockIDGenerator;

	public BlockFactory() {
		blockIDGenerator= BlockIDGenerator.getInstance();
	}

	/**
	 * 
	 * @param type
	 */
	public Block createBlock(BlockType type) {
		// TODO - implement BlockFactory.createBlock
		throw new UnsupportedOperationException();
	}

}