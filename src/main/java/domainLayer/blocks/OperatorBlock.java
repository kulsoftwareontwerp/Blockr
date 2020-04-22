package domainLayer.blocks;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;




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

	abstract public boolean assess(GameWorld gameWorld);

}