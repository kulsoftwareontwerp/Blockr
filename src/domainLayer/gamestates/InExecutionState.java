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
	}

	public void reset() {
		// TODO - implement InExecutionState.reset
		throw new UnsupportedOperationException();
	}

	public void execute() {
		ActionBlock currentActionBlockToBeExecuted = getNextActionBlockToBeExecuted();
		
		// If there is no next actionBlock to be executed, the program has finished and the user needs to reset
		if (currentActionBlockToBeExecuted != null) {
			gameController.performRobotAction(currentActionBlockToBeExecuted);
			
			ActionBlock newNextActionBlockToBeExecuted = gameController.findNextActionBlockToBeExecuted(currentActionBlockToBeExecuted);
			setNextActionBlockToBeExecuted(newNextActionBlockToBeExecuted);
			
			if (newNextActionBlockToBeExecuted != null)
				gameController.fireUpdateHighlightingEvent(getNextActionBlockToBeExecuted().getBlockId());
			else
				gameController.fireUpdateHighlightingEvent(null);
		}
	}

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