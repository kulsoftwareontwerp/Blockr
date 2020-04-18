package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import types.BlockType;
import types.ConnectionType;

/**
 * The abstract class for the concept of a condition block.
 * 
 * @version 0.1
 * @author group17
 */
public class ConditionBlock extends AssessableBlock {
		private BlockType type;
		private HashSet<ConnectionType> supportedConnectionTypes;

	/**
	 * Create a Condition Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ConditionBlock(String blockId, BlockType type) {
		super(blockId);
		this.type=type;
		this.supportedConnectionTypes=new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.LEFT);
	
	}


	public Predicate getPredicate() {
		return type.predicate();
	}





	@Override
	public boolean assess(GameWorld gameWorld) {
		return gameWorld.evaluate(getPredicate());
	}


	@Override
	public BlockType getBlockType() {
		return type;
	}


	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return new HashSet<ConnectionType>(supportedConnectionTypes);
	}



}