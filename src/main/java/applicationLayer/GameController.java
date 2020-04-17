package applicationLayer;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import commands.CommandHandler;
import commands.GameWorldCommand;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.elements.ElementRepository;
import domainLayer.elements.Robot;
import domainLayer.gamestates.GameState;
import domainLayer.gamestates.InValidProgramState;
import domainLayer.gamestates.ResettingState;
import events.DomainListener;
import events.GUIListener;
import events.GUISubject;
import events.ResetExecutionEvent;
import events.RobotChangeEvent;
import events.UpdateGameStateEvent;
import events.UpdateHighlightingEvent;
import types.ExecutionSnapshot;

public class GameController implements DomainListener, GUISubject {

	private Collection<GUIListener> guiListeners;
	private BlockRepository programBlockRepository;
	private GameState currentState;
	private ElementRepository gameElementRepository;
	private GameWorld gameWorld;
	private GameWorldSnapshot initialSnapshot;
	private CommandHandler commandHandler;

	public GameController(GameWorld gameWorld, CommandHandler commandHandler) {
		this.gameWorld = gameWorld;
		programBlockRepository = BlockRepository.getInstance();
		gameElementRepository = ElementRepository.getInstance();

		guiListeners = new HashSet<GUIListener>();
		
		this.commandHandler=commandHandler;
		initialSnapshot = gameWorld.saveState();
		

		toState(new InValidProgramState(this));

	}
	
	// Used for mockinjection in the tests
	private GameController(BlockRepository br) {
		this.programBlockRepository = br;
	}

	public void handleCommand(GameWorldCommand command) {
		this.commandHandler.handle(command);
	}
	
	public void resetGameExecution() {
		GameState currentState = getCurrentState();
		currentState.reset();
	}
	
	
	/**
	 * ResetGame is only allowed to be called from the resettingState class.
	 * @return
	 */
	public ExecutionSnapshot resetGame() {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = new ExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot,getCurrentState());
		gameWorld.restoreState(initialSnapshot);
		fireUpdateHighlightingEvent(null);
		
		
		try {
			if(getCurrentState().getNextState()==null) {
				//This is not a resettingState.
				toState(new ResettingState(this));
			}
			Constructor<? extends GameState> constructor = getCurrentState().getNextState().getConstructor(GameController.class);
			GameState newState = (GameState) constructor.newInstance(this);
			toState(newState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return snapshot;
	}
	
	
	
	public GameState getCurrentState() {
		return this.currentState;
	}

	/**
	 * 
	 * @param state
	 */
	public void toState(GameState state) {
		this.currentState = state;
	}

	public void updateState() {
		currentState.update();
	}



	public void executeBlock() {
		GameState currentState = getCurrentState();
		currentState.execute();
	}

	public ActionBlock findFirstBlockToBeExecuted() {
		ExecutableBlock firstExecutableBlock = programBlockRepository.findFirstBlockToBeExecuted();
		if (!(firstExecutableBlock instanceof ActionBlock)) {
			ActionBlock firstActionBlock = findNextActionBlockToBeExecuted(null, firstExecutableBlock);
			return firstActionBlock;
		}
		return (ActionBlock) firstExecutableBlock;
	}

	/**
	 * 
	 * @param block
	 */
	public ActionBlock findNextActionBlockToBeExecuted(ExecutableBlock previousBlock, ExecutableBlock currentBlock) {
		// ExecutableBlock nextBlock = block.getNextBlock();
		if (currentBlock == null) {
			ControlBlock cb = programBlockRepository.getEnclosingControlBlock(previousBlock);
			if (cb == null) {
				return null;
			} else if (cb instanceof IfBlock) {
				return findNextActionBlockToBeExecuted(cb, cb.getNextBlock());
			} else {
				return findNextActionBlockToBeExecuted(currentBlock, cb);
			}
		} else if (currentBlock instanceof ActionBlock) {
			return (ActionBlock) currentBlock;
		} else {
			// If or while block
			AssessableBlock condition = currentBlock.getConditionBlock();

			if (evaluateCondition(condition)) {
				return findNextActionBlockToBeExecuted(currentBlock, currentBlock.getFirstBlockOfBody());
			} else {
				return findNextActionBlockToBeExecuted(currentBlock, currentBlock.getNextBlock());
			}
		}
	}

	private boolean evaluateCondition(AssessableBlock condition) {
		if (condition instanceof ConditionBlock) {
			return gameWorld.evaluate(((ConditionBlock) condition).getPredicate());
		} else {
			return !evaluateCondition(condition.getOperand());
		}
	}

	/**
	 * 
	 * @param block
	 */
	public ExecutionSnapshot performAction(ActionBlock block) {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = new ExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot,getCurrentState());
		gameWorld.performAction(block.getAction());
		
		ActionBlock newNextActionBlockToBeExecuted = findNextActionBlockToBeExecuted(block, block.getNextBlock());
		getCurrentState().setNextActionBlockToBeExecuted(newNextActionBlockToBeExecuted);
		if (getCurrentState().getNextActionBlockToBeExecuted() != null) {
			fireUpdateHighlightingEvent(getCurrentState().getNextActionBlockToBeExecuted().getBlockId());
		} else {
			fireUpdateHighlightingEvent(null);
		}
		return snapshot;
	}
	
	public void restoreExecutionSnapshot(ExecutionSnapshot snapshot) {
		getCurrentState().setNextActionBlockToBeExecuted(snapshot.getNextActionBlockToBeExecuted());
		gameWorld.restoreState(snapshot.getGameSnapshot());
		toState(snapshot.getState());
		if (getCurrentState().getNextActionBlockToBeExecuted() != null) {
			fireUpdateHighlightingEvent(getCurrentState().getNextActionBlockToBeExecuted().getBlockId());
		} else {
			fireUpdateHighlightingEvent(null);
		}
	}

	public boolean checkIfValidProgram() {
		return programBlockRepository.checkIfValidProgram();
	}

	/**
	 * 
	 * @param highlightedBlockId
	 */
	public void fireUpdateHighlightingEvent(String highlightedBlockId) {
		UpdateHighlightingEvent updateHighlightingEvent = new UpdateHighlightingEvent(highlightedBlockId);
		for (GUIListener listener : guiListeners) {
			listener.onUpdateHighlightingEvent(updateHighlightingEvent);
		}

	}

	private HashMap<String, Integer> findNextPosition() {
		// TODO - implement GameController.findNextPosition
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeListener(GUIListener listener) {
		this.guiListeners.remove(listener);
	}

	@Override
	public void addListener(GUIListener listener) {
		this.guiListeners.add(listener);

	}

	@Override
	public void onResetExecutionEvent(ResetExecutionEvent event) {
		resetGameExecution();
	}

	@Override
	public void onUpdateGameStateEvent(UpdateGameStateEvent event) {
		updateState();
	}

}