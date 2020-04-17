package domainLayer.blocks;

import com.kuleuven.swop.group17.GameWorldApi.Action;

/**
 * The abstract class for the concept of an action block.
 * 
 * @version 0.1
 * @author group17
 */
public class ActionBlock extends ExecutableBlock {
	
	private Action action;

	/**
	 * Create an Action Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ActionBlock(String blockId, Action action) {
		super(blockId);
		setAction(action);
		
	}

	public Action getAction() {
		return action;
	}

	private void setAction(Action action) {
		this.action = action;
	}

}