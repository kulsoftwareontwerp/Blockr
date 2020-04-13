/**
 * 
 */
package types;

import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import domainLayer.blocks.ActionBlock;
import domainLayer.gamestates.GameState;

/**
/**
 * ExecutionSnapshot
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
	 * @param nextBlockToBeExecuted
	 * @param gameSnapshot
	 */
	public ExecutionSnapshot(ActionBlock nextBlockToBeExecuted, GameWorldSnapshot gameSnapshot,GameState state) {
		super();
		this.nextBlockToBeExecuted = nextBlockToBeExecuted;
		this.gameSnapshot = gameSnapshot;
		this.state = state;
	}
	public ActionBlock getNextActionBlockToBeExecuted() {
		return nextBlockToBeExecuted;
	}
	public GameWorldSnapshot getGameSnapshot() {
		return gameSnapshot;
	}
	public GameState getState() {
		return state;
	}
	
	


}
