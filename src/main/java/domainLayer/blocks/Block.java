package domainLayer.blocks;

import java.util.Set;

import exceptions.InvalidBlockConnectionException;
import types.BlockType;
import types.ConnectionType;

/**
 * The abstract class for the concept of a program block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class Block implements Cloneable {

	private String blockId;

	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockId == null) ? 0 : blockId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (blockId == null) {
			if (other.blockId != null)
				return false;
		} else if (!blockId.equals(other.blockId))
			return false;
		return true;
	}

	@Override
	public Block clone() {
		Block cloned;
		try {
			cloned = (Block) super.clone();
			try {
				if (cloned.getConditionBlock() != null) {
					cloned.setConditionBlock(cloned.getConditionBlock().clone());
				}
			} catch (InvalidBlockConnectionException ex) {
				// In the unlikely case that a block has a connection but doesn't allow to set
				// the connection, don't do anything with the exception and move on.
			}
			try {
				if (cloned.getFirstBlockOfBody() != null) {
					cloned.setFirstBlockOfBody(cloned.getFirstBlockOfBody().clone());
				}
			} catch (InvalidBlockConnectionException ex) {
				// In the unlikely case that a block has a connection but doesn't allow to set
				// the connection, don't do anything with the exception and move on.
			}
			try {
				if (cloned.getNextBlock() != null) {
					cloned.setNextBlock(cloned.getNextBlock().clone());
				}
			} catch (InvalidBlockConnectionException ex) {
				// In the unlikely case that a block has a connection but doesn't allow to set
				// the connection, don't do anything with the exception and move on.
			}
			try {
				if (cloned.getOperand() != null) {
					cloned.setOperand(cloned.getOperand().clone());
				}
			} catch (InvalidBlockConnectionException ex) {
				// In the unlikely case that a block has a connection but doesn't allow to set
				// the connection, don't do anything with the exception and move on.
			}			
			
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		return cloned;
	}

	/**
	 * Create a block with the given ID as ID
	 * 
	 * @param blockID The ID for the new Block.
	 */
	public Block(String blockID) {
		this.blockId = blockID;
	}

	/**
	 * Set the operand connection to the given block.
	 * 
	 * @param block The block to be added as a operand.
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - a block added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public final void setOperand(Block block) {
		parseToValidOperation(block);
	}

	/**
	 * Set the nextBlock connection to the given block.
	 * 
	 * @param block The block to be added as a nextBlock.
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - a block added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
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
	 * Set the firstBlockOfBody connection to the given block.
	 * 
	 * @param block The block to be added as the first block of the body.
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - a block added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
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
	 * Set the conditionBlock connection to the given block.
	 * 
	 * @param block The block to be added as a condition.
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - a block added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public final void setConditionBlock(Block block) {
		parseToValidOperation(block);

	}

	private void parseToValidOperation(Block block) {
		if (!(block instanceof AssessableBlock)) {
			throw new InvalidBlockConnectionException("This block is no AssessableBlock.");
		} else {
			if (this instanceof ControlBlock) {
				this.setConditionBlock((AssessableBlock) block);
			} else if (this instanceof OperatorBlock) {
				this.setOperand((AssessableBlock) block);
			} else {
				throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
			}
		}
	}

	/**
	 * Set the operand connection to the given block.
	 * 
	 * @param block The assessable block to be added as a operand.
	 * @throws InvalidBlockConnectionException When a block is added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 * 
	 */
	public void setOperand(AssessableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * Set the nextBlock connection to the given block.
	 * 
	 * @param block The executable block to be added as a nextBlock.
	 * @throws InvalidBlockConnectionException When a block is added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public void setNextBlock(ExecutableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * Set the firstBlockOfBody connection to the given block.
	 * 
	 * @param block The executable block to be added as the first block of the body.
	 * @throws InvalidBlockConnectionException When a block is added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public void setFirstBlockOfBody(ExecutableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * Set the conditionBlock connection to the given block.
	 * 
	 * @param block The assessable block to be added as a condition.
	 * @throws InvalidBlockConnectionException When a block is added to another
	 *                                         block of which the required
	 *                                         connection is not provided.
	 */
	public void setConditionBlock(AssessableBlock block) {
		throw new InvalidBlockConnectionException("The connected block doesn't have the requested connection.");
	}

	/**
	 * Retrieve the operand
	 * 
	 * @return the operand or null when there's no operand.
	 */
	public AssessableBlock getOperand() {
		return null;
	}

	/**
	 * Retrieve the next block
	 * 
	 * @return the next block or null when there's no next block.
	 */
	public ExecutableBlock getNextBlock() {
		return null;
	}

	/**
	 * Retrieve the first block of the body
	 * 
	 * @return the first block of the body or null when there's no first block of
	 *         the body.
	 */
	public ExecutableBlock getFirstBlockOfBody() {
		return null;
	}

	/**
	 * Retrieve the condition
	 * 
	 * @return the condition or null when there's no condition.
	 */
	public AssessableBlock getConditionBlock() {
		return null;
	}

	/**
	 * Retrieve the blockID corresponding to this block.
	 * 
	 * @return blockID
	 */
	public String getBlockId() {
		return this.blockId;
	}
	
	/**
	 * Retrieve the Type of this block.
	 * @return the Type of this block.
	 */
	public abstract BlockType getBlockType();
	
	
	/**
	 * Retrieve the supported connectionTypes for this block.
	 * @return the supported connectionTypes for this block.
	 */
	public abstract Set<ConnectionType> getSupportedConnectionTypes();
	

}