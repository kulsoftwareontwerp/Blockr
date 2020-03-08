package domainLayer;

public abstract class ControlBlock extends ExecutableBlock {

	private ExecutableBlock firstBlockOfBody;
	private AssessebleBlock condition;

	/**
	 * 
	 * @param blockId
	 */
	public ControlBlock(String blockId) {
		super(blockId);
	}

	/**
	 * 
	 * @param block
	 */
	public void setFirstBlockOfBody(ExecutableBlock block) {
		this.firstBlockOfBody = block;
	}

	/**
	 * 
	 * @param block
	 */
	public void setConditionBlock(AssessebleBlock block) {
		// TODO - implement ControlBlock.setConditionBlock
		throw new UnsupportedOperationException();
	}

}