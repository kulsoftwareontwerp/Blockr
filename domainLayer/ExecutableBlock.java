package domainLayer;

public abstract class ExecutableBlock extends Block {

	private ExecutableBlock nextBlock;

	/**
	 * 
	 * @param blockId
	 */
	public ExecutableBlock(String blockId) {
		super(blockId);
		// TODO - implement ExecutableBlock.ExecutableBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setNextBlock(ExecutableBlock block) {
		this.nextBlock = block;
	}

	public ExecutableBlock getNextBlock() {
		return this.nextBlock;
	}

}