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

	public void update() {
		boolean currentState = gameController.checkIfValidProgram();
		if(currentState) {
			GameState newState = new ValidProgramState(gameController);
			gameController.toState(newState);
		}
	}

}