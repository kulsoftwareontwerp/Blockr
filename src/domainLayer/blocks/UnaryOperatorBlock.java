package domainLayer.blocks;

public abstract class UnaryOperatorBlock extends OperatorBlock {

	private AssessableBlock operand;

	/**
	 * 
	 * @param blockId
	 */
	public UnaryOperatorBlock(String blockId) {
		super(blockId);
	}

	public boolean assess() {
		// TODO - implement UnaryOperatorBlock.assess
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	public void setOperand(AssessableBlock block) {
		this.operand=block;
	}
	
	public AssessableBlock getOperand() {
		return this.operand;
	}

}