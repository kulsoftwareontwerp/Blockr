package domainLayer;

import applicationLayer.*;

public abstract class GameState {

	protected GameController gameController;

	/**
	 * 
	 * @param game
	 */
	public GameState(GameController game) {
		// TODO - implement GameState.GameState
		throw new UnsupportedOperationException();
	}

	public void reset() {
		// TODO - implement GameState.reset
		throw new UnsupportedOperationException();
	}

	public void execute() {
		// TODO - implement GameState.execute
		throw new UnsupportedOperationException();
	}

	public void update() {
		// TODO - implement GameState.update
		throw new UnsupportedOperationException();
	}

}