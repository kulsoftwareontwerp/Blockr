package domainLayer;

import applicationLayer.*;

public class ResettingState extends GameState {

	private String nextState;

	/**
	 * 
	 * @param game
	 */
	public ResettingState(GameController game) {
		super(game);
		// TODO - implement ResettingState.ResettingState
		throw new UnsupportedOperationException();
	}

	public void update() {
		// TODO - implement ResettingState.update
		throw new UnsupportedOperationException();
	}

	public void reset() {
		// TODO - implement ResettingState.reset
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param state
	 */
	private void setNextState(String state) {
		this.nextState = state;
	}

	private String getNextState() {
		return this.nextState;
	}

}