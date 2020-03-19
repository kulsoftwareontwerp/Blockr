package domainLayer.blocks;

/**
 * The abstract class for the concept of an Executable block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class ExecutableBlock extends Block {

	private ExecutableBlock nextBlock;


	/**
	 * Create an Executable Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ExecutableBlock(String blockId) {
		super(blockId);
	}

	@Override
	public void setNextBlock(ExecutableBlock block) {
		this.nextBlock = block;
	}

	
	@Override
	public ExecutableBlock getNextBlock() {
		return this.nextBlock;
	}

}