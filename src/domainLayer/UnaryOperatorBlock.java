package domainLayer;

public abstract class UnaryOperatorBlock extends OperatorBlock {

	private AssessebleBlock operand;

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
	public void setOperand(AssessebleBlock block) {
		// TODO - implement UnaryOperatorBlock.setOperand
		throw new UnsupportedOperationException();
	}

}