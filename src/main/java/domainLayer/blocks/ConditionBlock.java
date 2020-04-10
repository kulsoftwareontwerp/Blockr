package domainLayer.blocks;

import java.util.HashSet;

import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import domainLayer.elements.ElementRepository;

/**
 * The abstract class for the concept of a condition block.
 * 
 * @version 0.1
 * @author group17
 */
public class ConditionBlock extends AssessableBlock {
	
	private Predicate predicate;
	
	/**
	 * Create a Condition Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ConditionBlock(String blockId, Predicate predicate) {
		super(blockId);
		setPredicate(predicate);
	}


	public Predicate getPredicate() {
		return predicate;
	}


	private void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}


//	@Override
//	public boolean assess(ElementRepository elementsRepo) {
//		// TODO Auto-generated method stub
//		return false;
//	}



}