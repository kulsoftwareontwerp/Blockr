package domainLayer.blocks;

import exceptions.InvalidBlockConnectionException;

public abstract class Block {

	private String blockId;

	/**
	 * 
	 * @param blockID
	 */
	public Block(String blockID) {
		this.blockId = blockID;
	}

	/**
	 * 
	 * @param block
	 */
	public final void setOperand(Block block) {
		if (!(block instanceof AssessableBlock)) {
			throw new InvalidBlockConnectionException("This block is no AssessableBlock.");
		} else {

			this.setOperand((AssessableBlock) block);

		}
	}

	/**
	 * 
	 * @param block
	 */
	public final void setNextBlock(Block block) {
		if (!(block instanceof ExecutableBlock)) {
			throw new InvalidBlockConnectionException(
					"The new block and/or the connected block is no ExecutableBlock.");
		} else {
			this.setNextBlock((ExecutableBlock) block);
		}
	}

	/**
	 * 
	 * @param block
	 */
	public final void setFirstBlockOfBody(Block block) {
		if (!(block instanceof ExecutableBlock)) {
			throw new InvalidBlockConnectionException(
					"The new block and/or the connected block is no ExecutableBlock.");
		} else {
			this.setFirstBlockOfBody((ExecutableBlock) block);
		}
	}

	/**
	 * 
	 * @param block
	 */
	public final void setConditionBlock(Block block) {
		if (!(block instanceof AssessableBlock)) {
			throw new InvalidBlockConnectionException("This block is no AssessableBlock.");
		} else {
			this.setConditionBlock((AssessableBlock) block);
		}

	}

	/**
	 * 
	 * @param block
	 */
	public void setOperand(AssessableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * 
	 * @param block
	 */
	public void setNextBlock(ExecutableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * 
	 * @param block
	 */
	public void setFirstBlockOfBody(ExecutableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * 
	 * @param block
	 */
	public void setConditionBlock(AssessableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	public AssessableBlock getOperand() {
		return null;
	}

	public ExecutableBlock getNextBlock() {
		return null;
	}

	public ExecutableBlock getFirstBlockOfBody() {
		return null;
	}

	public AssessableBlock getConditionBlock() {
		return null;
	}

	public String getBlockId() {
		return this.blockId;
	}

}