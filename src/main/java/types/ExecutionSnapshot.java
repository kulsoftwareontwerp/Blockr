/**
 * 
 */
package types;

import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import domainLayer.blocks.ActionBlock;
import domainLayer.gamestates.GameState;

/**
 * ExecutionSnapshot, a snapshot describing the situation regarding executions before an action has been performed.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ExecutionSnapshot {

	private ActionBlock nextBlockToBeExecuted;
	private GameWorldSnapshot gameSnapshot;
	private GameState state;

	/**
	 * Create a new ExecutionSnapshot
	 * @param nextBlockToBeExecuted The next block to be executed
	 * @param gameSnapshot the gameWorld snapshot before the execution has been performed.
	 * @param state the saved state of an execution
	 * @throws NullPointerException when the gameSnapshot is null
	 * @throws NullPointerException when the state is null
	 */
	public ExecutionSnapshot(ActionBlock nextBlockToBeExecuted, GameWorldSnapshot gameSnapshot, GameState state) {
		super();
		if(gameSnapshot==null) {
			throw new NullPointerException("the gameSnapshot of an ExecutionSnapshot can't be null");
		}
		if(state==null) {
			throw new NullPointerException("the state of an ExecutionSnapshot can't be null");
		}
		this.nextBlockToBeExecuted = nextBlockToBeExecuted;
		this.gameSnapshot = gameSnapshot;
		this.state = state;
	}

	/**
	 * Retrieve the next ActionBlock to be executed
	 * @return the next ActionBlock to be executed
	 */
	public ActionBlock getNextActionBlockToBeExecuted() {
		return nextBlockToBeExecuted;
	}

	/**
	 * Retrieve the game snapshot
	 * @return the game snapshot
	 */
	public GameWorldSnapshot getGameSnapshot() {
		return gameSnapshot;
	}

	/**
	 * Retrieve the GameState
	 * @return the GameState
	 */
	public GameState getState() {
		return state;
	}

}
