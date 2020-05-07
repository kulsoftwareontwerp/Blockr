/**
 * 
 */
package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;

import types.BlockType;
import types.ConnectionType;

/**
/**
 * CallFunctionBlock
 * A block used to call upon the functionalities of a given definitionBlock.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CallFunctionBlock extends Block {

	private HashSet<ConnectionType> supportedConnectionTypes;
	private String definitionBlockID;
	
	/**
	 * Create a new CallFunctionBlock
	 * 
	 * @param blockID The blockID for this CallFunctionBlock
	 * @param definitionBlockID The ID of the definitionBlock to call upon.
	 */
	public CallFunctionBlock(String blockID,String definitionBlockID) {
		super(blockID);
		if(definitionBlockID==null) {
			definitionBlockID="";
		}
		this.definitionBlockID=definitionBlockID;
		
		this.supportedConnectionTypes=new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
	}

	@Override
	public BlockType getBlockType() {
		return BlockType.FUNCTIONCALL;
	}

	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return this.getSupportedConnectionTypes();
	}
	
	
	/**
	 * Retrieve the ID of the DefinitionBlock that will be called by this CallFunctionBlock.
	 * @return the ID of the DefinitionBlock that will be called by this CallFunctionBlock.
	 */
	public String getDefinitionBlockID() {
		return this.definitionBlockID;
	}
	

}
