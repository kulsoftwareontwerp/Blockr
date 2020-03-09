package domainLayer.blocks;

/**
 * The abstract class for the concept of a control block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class ControlBlock extends ExecutableBlock {

	private ExecutableBlock firstBlockOfBody;
	private AssessableBlock conditionBlock;


	/**
	 * Create a Control Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ControlBlock(String blockId) {
		super(blockId);
	}

	@Override
	public void setFirstBlockOfBody(ExecutableBlock block) {
		this.firstBlockOfBody = block;
	}
	
	

	@Override
	public ExecutableBlock getFirstBlockOfBody() {
		return this.firstBlockOfBody;
	}

	@Override
	public void setConditionBlock(AssessableBlock block) {
		this.conditionBlock=block;
	}

	@Override
	public AssessableBlock getConditionBlock() {
		return this.conditionBlock;
	}
	



}