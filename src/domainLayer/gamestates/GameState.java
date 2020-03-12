package domainLayer.gamestates;

import applicationLayer.*;

public abstract class GameState {

	protected GameController gameController;

	/**
	 * 
	 * @param game
	 */
	public GameState(GameController game) {
		gameController=game;
	}

	public void reset() {
		// TODO - implement GameState.reset
		throw new UnsupportedOperationException();
	}

	public void execute() {
		// Nothing should happen in the default implementation of execute. 
		// (while trying to execute in inValidState or resettingState)
	}

	public void update() {
		
	}

}