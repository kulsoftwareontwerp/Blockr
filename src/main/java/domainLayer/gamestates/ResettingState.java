package domainLayer.gamestates;

import applicationLayer.GameController;
import commands.GameWorldCommand;
import commands.ResetCommand;

public class ResettingState extends GameState {

	private Class<? extends GameState> nextState;
	private Boolean updated;

	/**
	 * 
	 * @param game
	 */
	public ResettingState(GameController gameController) {
		super(gameController);
		setNextState(ValidProgramState.class);
		updated = false;
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
		updated=true;
		boolean currentState = gameController.checkIfValidProgram();
		if(!currentState) {
			setNextState(InValidProgramState.class);
		}
		else {
			setNextState(ValidProgramState.class);
		}
	}

	/**
	 * Resets the program execution.
	 * 
	 * @Result If the program is not updated, a new ResetCommand gets created and
	 * 			the gameController handles the newly created ResetCommand.
	 * 			Otherwise the resetGame method of the gameController gets called directly
	 * 			and the updated flag gets set to false. 
	 * 
	 * @event UpdateHighlightingEvent
	 * 			Fires an UpdateHighlightingEvent.
	 */
	public void reset() {
		if(!updated) {
			GameWorldCommand command = new ResetCommand(gameController);
			gameController.handleCommand(command);
		}
		else {
			gameController.resetGame();
			updated=false;
		}
	}


	/**
	 * 
	 * @param state
	 */
	private void setNextState(Class<? extends GameState> state) {
		this.nextState = state;
	}

	public Class<? extends GameState> getNextState() {
		return this.nextState;
	}
	
	// For testing purposes
	void setUpdated(Boolean updated) {
		this.updated = updated;
	}

	Boolean getUpdated() {
		return updated;
	}

}