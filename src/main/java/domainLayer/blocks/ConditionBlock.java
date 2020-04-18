package domainLayer.blocks;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import types.BlockType;

/**
 * The abstract class for the concept of a condition block.
 * 
 * @version 0.1
 * @author group17
 */
public class ConditionBlock extends AssessableBlock {
		private BlockType type;
	
	/**
	 * Create a Condition Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ConditionBlock(String blockId, BlockType type) {
		super(blockId);
		this.type=type;
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






}