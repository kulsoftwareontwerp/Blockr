package domainLayer;

public abstract class Block {

	private String blockId;

	/**
	 * 
	 * @param blockID
	 */
	public Block(String blockID) {
		// TODO - implement Block.Block
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setOperand(AssessebleBlock block) {
		// TODO - implement Block.setOperand
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setNextBlock(ExecutableBlock block) {
		// TODO - implement Block.setNextBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setFirstBlockOfBody(ExecutableBlock block) {
		// TODO - implement Block.setFirstBlockOfBody
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setConditionBlock(AssessebleBlock block) {
		// TODO - implement Block.setConditionBlock
		throw new UnsupportedOperationException();
	}

	public AssessebleBlock getOperand() {
		// TODO - implement Block.getOperand
		throw new UnsupportedOperationException();
	}

	public ExecutableBlock getNextBlock() {
		// TODO - implement Block.getNextBlock
		throw new UnsupportedOperationException();
	}

	public ExecutableBlock getFirstBlockOfBody() {
		// TODO - implement Block.getFirstBlockOfBody
		throw new UnsupportedOperationException();
	}

	public AssessebleBlock getConditionBlock() {
		// TODO - implement Block.getConditionBlock
		throw new UnsupportedOperationException();
	}

	public String getBlockId() {
		return this.blockId;
	}

}