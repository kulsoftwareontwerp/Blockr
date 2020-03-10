package domainLayer.blocks;

public abstract class ControlBlock extends ExecutableBlock {

	private ExecutableBlock firstBlockOfBody;
	private AssessableBlock condition;

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
	
	

	@Override
	public ExecutableBlock getFirstBlockOfBody() {
		return this.firstBlockOfBody;
	}

	/**
	 * 
	 * @param block
	 */
	public void setConditionBlock(AssessableBlock block) {
		this.condition=block;
	}

	@Override
	public AssessableBlock getConditionBlock() {
		return this.condition;
	}
	
	

}