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
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.gamestates.GameState;
import domainLayer.gamestates.InExecutionState;
import domainLayer.gamestates.InValidProgramState;
import domainLayer.gamestates.ResettingState;
import domainLayer.gamestates.ValidProgramState;
import events.DomainListener;
import events.GUIListener;
import events.GUISubject;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import events.UpdateHighlightingEvent;
import types.ExecutionSnapshot;

public class GameController implements DomainListener, GUISubject {

	private Collection<GUIListener> guiListeners;
	private BlockRepository programBlockRepository;
	private GameState currentState;
	private GameWorld gameWorld;
	private GameWorldSnapshot initialSnapshot;
	private CommandHandler commandHandler;

	public GameController(GameWorld gameWorld, CommandHandler commandHandler) {
		this.gameWorld = gameWorld;
		programBlockRepository = BlockRepository.getInstance();
		guiListeners = new HashSet<GUIListener>();

		this.commandHandler = commandHandler;
		initialSnapshot = gameWorld.saveState();

		toState(new InValidProgramState(this));
	}
	
	@SuppressWarnings("unused")
	private GameController(BlockRepository programBlockRepository, GameWorld gameWorld, CommandHandler commandHandler) {
		this.programBlockRepository = programBlockRepository;
		this.gameWorld = gameWorld;
		this.commandHandler = commandHandler;
		this.guiListeners = new HashSet<GUIListener>();
	}

	public void handleCommand(GameWorldCommand command) {
		this.commandHandler.handle(command);
	}
	
	/**
	 * Resets the game execution. 
	 * 
	 * @event UpdateHighlightingEvent
	 * 		  Fires an UpdateHighlightingEvent if the program was in an executing state.
	 */
	public void resetGameExecution() {
		GameState currentState = getCurrentState();
		currentState.reset();
	}

	/**
	 * Restores the gameworld back to its initial state, changes the current state of the program to its correct nextState and
	 *  returns the state of the program before the reset. ResetGame is only allowed to be called from the resettingState class.
	 * 
	 * @event fireUpdateHighlightingEvent Fires an UpdateHighlightingEvent.
	 * @return The ExecutionSnapshot describing the state before the reset.
	 */
	public ExecutionSnapshot resetGame() {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = createNewExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot, getCurrentState());
		gameWorld.restoreState(initialSnapshot);
		fireUpdateHighlightingEvent(null);
		
		try {
			if (getCurrentState().getNextState() == null) {
				// This is not a resettingState.
				toState(new ResettingState(this));
			} else {
				// TODO: How to test this?
				Constructor<? extends GameState> constructor = getCurrentState().getNextState().getConstructor(GameController.class);
				GameState newState = (GameState) constructor.newInstance(this);
				toState(newState);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return snapshot;
	}
	
	// For testing purposes
	ExecutionSnapshot createNewExecutionSnapshot(ActionBlock actionBlock, GameWorldSnapshot snapshot, GameState state) {
		return new ExecutionSnapshot(actionBlock, snapshot, state);
	}
	
	/**
	 * Returns the current state of the program.
	 * 
	 * @return The current state of the program.
	 */
	public GameState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Sets the current state of the program to the given state.
	 * 
	 * @param state		The new state of the program.
	 */
	public void toState(GameState state) {
		this.currentState = state;
	}

	/**
	 * Updates the current state of the program.
	 */
	public void updateState() {
		currentState.update();
	}

	/**
	 * Executes the next block to be executed if the gamestate is in a valid state or an in execution state. 
	 * 	If this is not the case, nothing happens.
	 * 
	 * @event UpdateHighlightingEvent
	 * 		  Fires an UpdateHighlightingEvent if the program was in a valid state or an in executing state.
	 */
	public void executeBlock() {
		GameState currentState = getCurrentState();
		currentState.execute();
	}

	/**
	 * Find the first action block that needs to be executed next.
	 * 
	 * @return The next actionBlock to be executed.
	 */
	public ActionBlock findFirstBlockToBeExecuted() {
		ExecutableBlock firstExecutableBlock = programBlockRepository.findFirstBlockToBeExecuted();
		if (!(firstExecutableBlock instanceof ActionBlock)) {
			ActionBlock firstActionBlock = findNextActionBlockToBeExecuted(null, firstExecutableBlock);
			return firstActionBlock;
		}
		return (ActionBlock) firstExecutableBlock;
	}

	/**
	 * Given a block to check (currentBlock) and a block that was checked before the currentBlock (previousBlock),
	 * 	return the first actionBlock to be executed following the rules of control- and assessable blocks by means of recursion.
	 * 
	 * @param previousBlock		The block that was the currentBlock in the previous recursive call of this method.
	 * @param currentBlock		The newest possible block that needs to be checked.
	 * 
	 * @return The next actionBlock in the program.
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

			if (condition.assess(gameWorld)) {
				return findNextActionBlockToBeExecuted(currentBlock, currentBlock.getFirstBlockOfBody());
			} else {
				return findNextActionBlockToBeExecuted(currentBlock, currentBlock.getNextBlock());
			}
		}
	}
	
	/**
	 * Asks the gameWorld to perform the action of the given block and find the next block to be executed and
	 *  set it in the current execution state.
	 * 
	 * @param block The block which action needs to be performed.
	 * @event fireUpdateHighlightingEvent Fires an UpdateHighlightingEvent.
	 * @return a snapshot containing all the information regarding the state of the program before the action was performed.
	 */
	public ExecutionSnapshot performAction(ActionBlock block) {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = createNewExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot, getCurrentState());
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

	/**
	 * Checks if the current program is valid.
	 * 
	 * @return true if the current program is valid.
	 */
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
	
	/**
	 * Is it useful to perform a gameAction at the moment. An action is useful if it
	 * changes anything, otherwise it's just a waste of time and resources.
	 * 
	 * @return if it's useful to perform a gameAction at the moment.
	 */
	public boolean isGameExecutionUseful() {
		return (getCurrentState() instanceof ValidProgramState) || 
				(getCurrentState() instanceof InExecutionState && 
						getCurrentState().getNextActionBlockToBeExecuted()!=null) ;
	}

	/**
	 * Is it useful to perform a reset of the gameWorld at the moment. A reset of the gameWorld is useful if it
	 * changes anything, otherwise it's just a waste of time and resources.
	 * 
	 * @return if it's useful to perform a reset of the gameWorld at the moment.
	 */
	public boolean isGameResetUseful() {
		return getCurrentState() instanceof InExecutionState  ;

	}

}