package domainLayer.gamestates;

import applicationLayer.*;

public class InValidProgramState extends GameState {

	/**
	 * 
	 * @param game
	 */
	public InValidProgramState(GameController game) {
		super(game);
	}

	/**
	 * Update of the "Game State".
	 * 
	 * @Result This update method of the InvalidProgramState will, once called, check either the program is in a valid state or not.
	 * 			If the program is in a valid state the gameState will be changed to a "ValidProgramState".
	 * 			
	 */
	public void update() {
		boolean currentState = gameController.checkIfValidProgram();
		if(currentState) {
			GameState newState = new ValidProgramState(gameController);
			gameController.toState(newState);
		}
	}

}