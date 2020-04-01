package domainLayer.blocks;
import java.util.HashSet;


/**
 * The concrete class for the concept of an operator block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class OperatorBlock extends AssessableBlock {


	/**
	 * Create an Operator Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public OperatorBlock(String blockId) {
		super(blockId);
	}

//	public boolean assess(HashSet<Element> gameElements) {
//		// TODO - implement OperatorBlock.assess
//		throw new UnsupportedOperationException();
//	}

}