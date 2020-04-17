/**
 * 
 */
package commands;

import applicationLayer.GameController;
import types.ExecutionSnapshot;

/**
/**
 * ResetCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ResetCommand implements GameWorldCommand {
	private GameController gameController;
	private ExecutionSnapshot snapshot;



	/**
	 * @param gameController
	 * @param snapshot
	 */
	public ResetCommand(GameController gameController) {
		super();
		this.gameController = gameController;
		this.snapshot = null;
	}

	@Override
	public void execute() {
		snapshot = gameController.resetGame();
	}

	@Override
	public void undo() {
		gameController.restoreExecutionSnapshot(snapshot);

	}

}
