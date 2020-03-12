package domainLayer.blocks;

/**
 * The abstract class for the concept of a unary operator block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class UnaryOperatorBlock extends OperatorBlock {

	private AssessableBlock operand;

	/**
	 * Create a Unary Operator Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public UnaryOperatorBlock(String blockId) {
		super(blockId);
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
	 * @param 	The assessableBlock to be set as operand.
	 */
	public AssessableBlock getOperand() {
		return this.operand;
	}

}