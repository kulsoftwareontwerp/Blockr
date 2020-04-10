package domainLayer.blocks;

import domainLayer.elements.ElementRepository;
import types.BlockCategory;

/**
 * The abstract class for the concept of an action block.
 * 
 * @version 0.1
 * @author group17
 */
public class ActionBlock extends ExecutableBlock {
	
	private String action;

	/**
	 * Create an Action Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ActionBlock(String blockId, String action) {
		super(blockId);
		setAction(action);
		
	}

	public String getAction() {
		return action;
	}

	private void setAction(String action) {
		this.action = action;
	}

}