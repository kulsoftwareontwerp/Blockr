package domainLayer.blocks;

import java.util.HashSet;

/**
 * The abstract class for the concept of a condition block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class ConditionBlock extends AssessableBlock {


	/**
	 * Create a Condition Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ConditionBlock(String blockId) {
		super(blockId);
	}

//	public boolean assess(HashSet<Element> gameElements) {
//		// TODO - implement ConditionBlock.assess
//		throw new UnsupportedOperationException();
//	}

}