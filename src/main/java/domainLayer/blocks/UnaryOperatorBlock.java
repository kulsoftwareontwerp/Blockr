package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import types.ConnectionType;

/**
 * The abstract class for the concept of a unary operator block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class UnaryOperatorBlock extends OperatorBlock {

	private AssessableBlock operand;
	private HashSet<ConnectionType> supportedConnectionTypes;

	/**
	 * Create a Unary Operator Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public UnaryOperatorBlock(String blockId) {
		super(blockId);
		this.supportedConnectionTypes=new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.LEFT);
		supportedConnectionTypes.add(ConnectionType.OPERAND);
	}

	/**
	 * Sets the operand.
	 * @param 	block
	 * 			The assessableBlock to be set as operand.
	 */
	public void setOperand(AssessableBlock block) {
		this.operand=block;
	}
	
	/**
	 * Retrieve the operand.
	 */
	public AssessableBlock getOperand() {
		return this.operand;
	}
	
	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return new HashSet<ConnectionType>(supportedConnectionTypes);
	}

}