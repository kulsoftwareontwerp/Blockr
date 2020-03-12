package domainLayer.gamestates;

import applicationLayer.*;

public class ResettingState extends GameState {

	private Class nextState;

	/**
	 * 
	 * @param game
	 */
	public ResettingState(GameController game) {
		super(game);
	}
	


	public void update() {
		boolean currentState = gameController.checkIfValidProgram();
		if(!currentState) {
			setNextState(InValidProgramState.class);
		}
		else {
			setNextState(ValidProgramState.class);
		}
	}


	public void reset() {
		// TODO - implement ResettingState.reset
		throw new UnsupportedOperationException();
	}
	
	private void setNextState(Class state) {
		this.nextState = state;
	}

	private Class getNextState() {
		return this.nextState;
	}




}