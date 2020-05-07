package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import types.ConnectionType;

/**
 * The abstract class for the concept of a control block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class ControlBlock extends ExecutableBlock implements BodyCavityBlock {

	private ExecutableBlock firstBlockOfBody;
	private AssessableBlock conditionBlock;
	private HashSet<ConnectionType> supportedConnectionTypes;


	/**
	 * Create a Control Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ControlBlock(String blockId) {
		super(blockId);
		this.supportedConnectionTypes=new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
		supportedConnectionTypes.add(ConnectionType.BODY);
		supportedConnectionTypes.add(ConnectionType.CONDITION);
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
	
	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return new HashSet<ConnectionType>(supportedConnectionTypes);
	}


}