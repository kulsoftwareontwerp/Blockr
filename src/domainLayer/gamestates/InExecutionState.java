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
		// TODO - implement InExecutionState.execute
		throw new UnsupportedOperationException();
	}

	public void update() {
			GameState ResettingStateFollowingUpdate = new ResettingState(gameController);
			ResettingStateFollowingUpdate.update();
			gameController.toState(ResettingStateFollowingUpdate);
			
	}

	public ActionBlock getNextActionBlockToBeExecuted() {
		return this.nextActionBlockToBeExecuted;
	}

}