package domainLayer.blocks;

import java.util.HashSet;

import domainLayer.elements.ElementRepository;

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



}