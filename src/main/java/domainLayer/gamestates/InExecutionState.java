package domainLayer.gamestates;

import applicationLayer.GameController;
import commands.ExecuteBlockCommand;
import commands.GameWorldCommand;
import domainLayer.blocks.ActionBlock;

public class InExecutionState extends GameState {

	private ActionBlock nextActionBlockToBeExecuted;

	/**
	 * 
	 * @param game
	 * @param nextBlock
	 */
	public InExecutionState(GameController game, ActionBlock nextBlock) {
		super(game);
		setNextActionBlockToBeExecuted(nextBlock);
	}

	/**
	 * Resets the program execution.
	 * 
	 * @Result The gameState becomes a resettingState on which the reset method gets called.
	 * 
	 * @event UpdateHighlightingEvent
	 * 			Fires an UpdateHighlightingEvent.
	 */
	@Override
	public void reset() {
		ResettingState resettingState = new ResettingState(gameController, nextActionBlockToBeExecuted);
		gameController.toState(resettingState);
		resettingState.reset();
	}

	/**
	 * Executes the next block to be executed in the program.
	 * 
	 * @Result If there is a nextActionBlockToBeExecuted, a new ExecuteBlockCommand gets created with that actionBlock and
	 * 			the gameController handles the newly created  ExecuteBlockCommand.
	 *
	 * @event UpdateHighlightingEvent
	 * 			Fires an UpdateHighlightingEvent.
	 */
	@Override
	public void execute() {
		ActionBlock currentActionBlockToBeExecuted = getNextActionBlockToBeExecuted();
		
		// If there is no next actionBlock to be executed, the program has finished and the user needs to reset
		if (currentActionBlockToBeExecuted != null) {
			GameWorldCommand command = new ExecuteBlockCommand(gameController, currentActionBlockToBeExecuted);
			gameController.handleCommand(command);
		}
	}

	/**
	 * The update method of the InExecutionState will put the game in a ResettingState.
	 * The ResettingState will then evaluate in which state the program Area is after triggering of this method.
	 * The results of this method are then either a ValidProgramState or an InValidProgramState.
	 */
	@Override
	public void update() {
			GameState ResettingStateFollowingUpdate = new ResettingState(gameController, null);
			ResettingStateFollowingUpdate.update();
			gameController.toState(ResettingStateFollowingUpdate);
			
	}

	@Override
	public ActionBlock getNextActionBlockToBeExecuted() {
		return this.nextActionBlockToBeExecuted;
	}
	
	@Override
	public void setNextActionBlockToBeExecuted(ActionBlock nextActionBlockToBeExecuted) {
		this.nextActionBlockToBeExecuted = nextActionBlockToBeExecuted;
	}

}