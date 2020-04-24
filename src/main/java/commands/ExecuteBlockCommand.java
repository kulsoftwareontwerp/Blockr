/**
 * 
 */
package commands;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;
import types.ExecutionSnapshot;

/**
 * ExecuteBlockCommand, the command to execute a block.
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
	 * Create a new ExecuteBlockCommand
	 * @param gameController The gameController to perform the execution on. 
	 * @param block the block to execute.
	 */
	public ExecuteBlockCommand(GameController gameController, ActionBlock block) {
		super();
		this.gameController = gameController;
		this.block = block;
		snapshot = null;

	}

	@Override
	public void execute() {
		snapshot = gameController.performAction(block);
	}

	@Override
	public void undo() {
		if (snapshot != null) {
			gameController.restoreExecutionSnapshot(snapshot);
			snapshot = null;
		}

	}

}
