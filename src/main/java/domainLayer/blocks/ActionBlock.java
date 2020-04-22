package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import com.kuleuven.swop.group17.GameWorldApi.Action;

import types.BlockType;
import types.ConnectionType;

/**
 * The abstract class for the concept of an action block.
 * 
 * @version 0.1
 * @author group17
 */
public class ActionBlock extends ExecutableBlock {

	private BlockType type;
	private HashSet<ConnectionType> supportedConnectionTypes;

	/**
	 * Create an Action Block
	 * 
	 * @param blockId The ID for the block.
	 * @param type The BlockType of the ActionBlock
	 */
	public ActionBlock(String blockId, BlockType type) {
		super(blockId);
		this.type = type;
		
		this.supportedConnectionTypes=new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
		
	}

	/**
	 * Retrieve the action associated with this actionBlock
	 * @return the action associated with this actionBlock
	 */
	public Action getAction() {
		return type.action();
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