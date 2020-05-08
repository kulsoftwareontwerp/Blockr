/**
 * 
 */
package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import types.BlockType;
import types.ConnectionType;

/**
 * /** CallFunctionBlock A block used to call upon the functionalities of a
 * given definitionBlock.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CallFunctionBlock extends Block {

	private HashSet<ConnectionType> supportedConnectionTypes;
	private BlockType type;

	/**
	 * Create a new CallFunctionBlock
	 * 
	 * @param blockID The blockID for this CallFunctionBlock
	 * @param type    The BlockType of the definitionBlock
	 */
	public CallFunctionBlock(String blockID, BlockType type) {
		super(blockID);

		this.type = type;

		this.supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
	}

	@Override
	public BlockType getBlockType() {
		return type;
	}

	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return this.getSupportedConnectionTypes();
	}

//	/**
//	 * Retrieve the ID of the DefinitionBlock that will be called by this
//	 * CallFunctionBlock.
//	 * 
//	 * @return the ID of the DefinitionBlock that will be called by this
//	 *         CallFunctionBlock.
//	 */
//	public String getDefinitionBlockID() {
//		return this.definitionBlockID;
//	}

}
