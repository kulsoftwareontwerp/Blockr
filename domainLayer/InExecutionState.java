package domainLayer;

import applicationLayer.*;

public class InExecutionState extends GameState {

	private ActionBlock nextActionBlockToBeExecuted;

	/**
	 * 
	 * @param game
	 * @param nextBlock
	 */
	public InExecutionState(GameController game, ActionBlock nextBlock) {
		super(game);
		// TODO - implement InExecutionState.InExecutionState
		throw new UnsupportedOperationException();
	}

	public void reset() {
		// TODO - implement InExecutionState.reset
		throw new UnsupportedOperationException();
	}

	public void execute() {
		// TODO - implement InExecutionState.execute
		throw new UnsupportedOperationException();
	}

	public void update() {
		// TODO - implement InExecutionState.update
		throw new UnsupportedOperationException();
	}

	public ActionBlock getNextActionBlockToBeExecuted() {
		return this.nextActionBlockToBeExecuted;
	}

}