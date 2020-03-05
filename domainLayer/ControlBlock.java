package domainLayer;

public abstract class ControlBlock extends ExecutableBlock {

	private ExecutableBlock firstBlockOfBody;
	private AssassebleBlock condition;

	/**
	 * 
	 * @param blockId
	 */
	public ControlBlock(String blockId) {
		// TODO - implement ControlBlock.ControlBlock
		throw new UnsupportedOperationException();
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
	public void setConditionBlock(AssassebleBlock block) {
		// TODO - implement ControlBlock.setConditionBlock
		throw new UnsupportedOperationException();
	}

}