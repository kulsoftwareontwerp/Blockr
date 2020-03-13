package domainLayer.gamestates;

import applicationLayer.*;
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

	public void reset() {
		ResettingState resettingState = new ResettingState(gameController);
		gameController.toState(resettingState);
		resettingState.reset();
	}

	public void execute() {
		ActionBlock currentActionBlockToBeExecuted = getNextActionBlockToBeExecuted();
		
		// If there is no next actionBlock to be executed, the program has finished and the user needs to reset
		if (currentActionBlockToBeExecuted != null) {
			gameController.performRobotAction(currentActionBlockToBeExecuted);
			
			ActionBlock newNextActionBlockToBeExecuted = gameController.findNextActionBlockToBeExecuted(currentActionBlockToBeExecuted, currentActionBlockToBeExecuted.getNextBlock());
			setNextActionBlockToBeExecuted(newNextActionBlockToBeExecuted);
			
			if (newNextActionBlockToBeExecuted != null)
				gameController.fireUpdateHighlightingEvent(getNextActionBlockToBeExecuted().getBlockId());
			else
				gameController.fireUpdateHighlightingEvent(null);
		}
	}

	/**
	 * The update method of the InExecutionState will put the game in a ResettingState.
	 * The ResettingState will then evaluate in which state the program Area is after triggering of this method.
	 * The results of this method are then either a ValidProgramState or an InValidProgramState.
	 */
	public void update() {
			GameState ResettingStateFollowingUpdate = new ResettingState(gameController);
			ResettingStateFollowingUpdate.update();
			gameController.toState(ResettingStateFollowingUpdate);
			
	}

	public ActionBlock getNextActionBlockToBeExecuted() {
		return this.nextActionBlockToBeExecuted;
	}
	
	public void setNextActionBlockToBeExecuted(ActionBlock nextActionBlockToBeExecuted) {
		this.nextActionBlockToBeExecuted = nextActionBlockToBeExecuted;
	}

}