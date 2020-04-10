package domainLayer.blocks;

import java.util.HashSet;

import domainLayer.elements.ElementRepository;

/**
 * The abstract class for the concept of a condition block.
 * 
 * @version 0.1
 * @author group17
 */
public class ConditionBlock extends AssessableBlock {
	
	private String predicate;
	
	/**
	 * Create a Condition Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ConditionBlock(String blockId, String predicate) {
		super(blockId);
		setPredicate(predicate);
	}


	public String getPredicate() {
		return predicate;
	}


	private void setPredicate(String predicate) {
		this.predicate = predicate;
	}


	@Override
	public boolean assess(ElementRepository elementsRepo) {
		// TODO Auto-generated method stub
		return false;
	}



}