package domainLayer.gamestates;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;

public abstract class GameState {

	protected GameController gameController;

	/**
	 * 
	 * @param game
	 */
	public GameState(GameController game) {
		gameController = game;
	}

	public ActionBlock getNextActionBlockToBeExecuted() {
		return null;
	}
	
	public Class<? extends GameState> getNextState(){
		return null;
	}
	
	public void setNextActionBlockToBeExecuted(ActionBlock nextActionBlockToBeExecuted) {
		// Nothing should happen in the default implementation of setNextActionBlockToBeExecuted.
		// (while trying to setNextActionBlockToBeExecuted in InValidState, ValidState or resettingState)
	}

	public void reset() {
		// Nothing should happen in the default implementation of reset.
		// (while trying to reset in InValidState or ValidState)
	}

	public void execute() {
		// Nothing should happen in the default implementation of execute.
		// (while trying to execute in inValidState or resettingState)
	}

	public void update() {

	}

}