/**
 * 
 */
package types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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

	private Map<String,Stack<String>> callStacks;
	private ActionBlock nextBlockToBeExecuted;
	private GameWorldSnapshot gameSnapshot;
	private GameState state;

	/**
	 * Create a new ExecutionSnapshot
	 * @param nextBlockToBeExecuted The next block to be executed
	 * @param gameSnapshot the gameWorld snapshot before the execution has been performed.
	 * @param state the saved state of an execution
	 * @param callStacks The callStacks of all the definitionBlocks.
	 * @throws NullPointerException when the gameSnapshot is null
	 * @throws NullPointerException when the state is null
	 */
	public ExecutionSnapshot(ActionBlock nextBlockToBeExecuted, GameWorldSnapshot gameSnapshot, GameState state, Map<String, Stack<String>> callStacks) {
		super();
		if(gameSnapshot==null) {
			throw new NullPointerException("the gameSnapshot of an ExecutionSnapshot can't be null");
		}
		if(state==null) {
			throw new NullPointerException("the state of an ExecutionSnapshot can't be null");
		}
		if(callStacks==null) {
			throw new NullPointerException("the callStacks of an ExecutionSnapshot can't be null");
		}
		this.nextBlockToBeExecuted = nextBlockToBeExecuted;
		this.gameSnapshot = gameSnapshot;
		this.state = state;
		this.callStacks=callStacks;
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
	
	
	/**
	 * Retrieve the callStacks from this snapshot.
	 * @return the callStacks from this snapshot.
	 */
	public Map<String, Stack<String>> getCallStacks(){
		HashMap<String, Stack<String>> copy = new HashMap<String, Stack<String>>();
	    for (Map.Entry<String, Stack<String>> entry : this.callStacks.entrySet())
	    {
	    	Stack<String> temp=new Stack<String>();
	    	temp.addAll(entry.getValue());
	    	copy.put(entry.getKey(),temp);
	    }
		return copy;
	}
	

}
