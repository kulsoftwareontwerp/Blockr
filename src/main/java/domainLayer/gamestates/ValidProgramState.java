package domainLayer.gamestates;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;

public class ValidProgramState extends GameState {

	/**
	 * 
	 * @param game
	 */
	public ValidProgramState(GameController game) {
		super(game);
	}

	public void execute() {
		ActionBlock firstActionBlockToBeExecuted = gameController.findFirstBlockToBeExecuted();
		InExecutionState inExecutionState = new InExecutionState(gameController, firstActionBlockToBeExecuted);
		gameController.toState(inExecutionState);
		inExecutionState.execute();
	}
	/**
	 * Update of the "Game State".
	 * 
	 * @Result This update method of the ValidProgramState will, once called, check either the program is in a valid state or not.
	 * 			If the program is not in a valid state the gameState will be changed to a "InValidProgramState".
	 * 			
	 */
	public void update() {
		boolean currentState = gameController.checkIfValidProgram();
		if(!currentState) {
			GameState newState = new InValidProgramState(gameController);
			gameController.toState(newState);
		}
	}

}