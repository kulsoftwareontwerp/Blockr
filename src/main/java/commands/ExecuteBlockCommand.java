/**
 * 
 */
package commands;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;
import types.ExecutionSnapshot;

/**
/**
 * ExecuteBlockCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ExecuteBlockCommand implements GameWorldCommand {
	private GameController gameController;
	private ExecutionSnapshot snapshot;
	private ActionBlock block;

	/**
	 * @param gameController
	 */
	public ExecuteBlockCommand(GameController gameController,ActionBlock block) {
		super();
		this.gameController = gameController;
		this.block=block;
		snapshot=null;
		
	}

	@Override
	public void execute() {
	snapshot = gameController.performAction(block);
	}

	@Override
	public void undo() {
		if(snapshot!=null) {
			gameController.restoreExecutionSnapshot(snapshot);
			snapshot = null;
		}

	}

}
