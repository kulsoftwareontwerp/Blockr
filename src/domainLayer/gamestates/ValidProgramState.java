package domainLayer.gamestates;

import applicationLayer.*;
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

	public void update() {
		// TODO - implement ValidProgramState.update
		throw new UnsupportedOperationException();
	}

}