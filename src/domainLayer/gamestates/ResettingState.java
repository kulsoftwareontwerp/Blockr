package domainLayer.gamestates;

import java.lang.reflect.Constructor;

import applicationLayer.*;

public class ResettingState extends GameState {

	private Class<? extends GameState> nextState;

	/**
	 * 
	 * @param game
	 */
	public ResettingState(GameController gameController) {
		super(gameController);
		setNextState(ValidProgramState.class);
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
		gameController.resetRobot();
		try {
			//GameState newState = getNextState().getDeclaredConstructor().newInstance(gameController);
			Constructor<? extends GameState> constructor = getNextState().getConstructor(GameController.class);
			GameState newState = (GameState) constructor.newInstance(gameController);
			gameController.toState(newState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * 
	 * @param state
	 */
	private void setNextState(Class<? extends GameState> state) {
		this.nextState = state;
	}

	private Class<? extends GameState> getNextState() {
		return this.nextState;
	}




}