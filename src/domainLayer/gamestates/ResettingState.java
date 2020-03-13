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
	

	/**
	 * Update of the "Game State" after the "In Execution State".
	 * 
	 * @Result This update method of the ResettingState will, once called, check either the program is in a valid state or not.
	 * 			If the program is in a valid state the gameState will be changed to a "ValidProgramState".
	 * 			If not the program will be in a InvalidProgramState. This means that the execution of the program will not be possible.
	 * 			
	 */
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