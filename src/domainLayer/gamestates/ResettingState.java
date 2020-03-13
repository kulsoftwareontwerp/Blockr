package domainLayer.gamestates;

import applicationLayer.*;

public class ResettingState extends GameState {

	private Class<? extends GameState> nextState;

	/**
	 * 
	 * @param game
	 */
	public ResettingState(GameController gameController) {
		super(gameController);
	}
	


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
			GameState newState = getNextState().getDeclaredConstructor().newInstance(gameController);
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