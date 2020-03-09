package domainLayer.gamestates;

import applicationLayer.*;
import domainLayer.blocks.ActionBlock;

public class InExecutionState extends GameState {

	private ActionBlock nextActionBlockToBeExecuted;

	/**
	 * 
	 * @param game
	 * @param nextBlock
	 */
	public InExecutionState(GameController game, ActionBlock nextBlock) {
		super(game);
		// TODO - implement InExecutionState.InExecutionState
		throw new UnsupportedOperationException();
	}

	public void reset() {
		// TODO - implement InExecutionState.reset
		throw new UnsupportedOperationException();
	}

	public void execute() {
		gameController.performRobotAction(getNextActionBlockToBeExecuted());
		
	}

	public void update() {
		// TODO - implement InExecutionState.update
		throw new UnsupportedOperationException();
	}

	public ActionBlock getNextActionBlockToBeExecuted() {
		return this.nextActionBlockToBeExecuted;
	}

}