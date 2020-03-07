package domainLayer.gamestates;

import applicationLayer.*;

public class ValidProgramState extends GameState {

	/**
	 * 
	 * @param game
	 */
	public ValidProgramState(GameController game) {
		super(game);
	}

	public void execute() {
		// TODO - implement ValidProgramState.execute
		throw new UnsupportedOperationException();
	}

	public void update() {
		boolean currentState = gameController.checkIfValidProgram();
		if(!currentState) {
			GameState newState = new InValidProgramState(gameController);
			gameController.toState(newState);
		}
	}

}