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

	public void handleCommand(GameWorldCommand command) {
		this.commandHandler.handle(command);
	}

	public void resetGameExecution() {
		GameState currentState = getCurrentState();
		currentState.reset();

	}

	/**
	 * ResetGame is only allowed to be called from the resettingState class.
	 * 
	 * @return The ExecutionSnapshot describing the state before the reset.
	 */
	public ExecutionSnapshot resetGame() {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = new ExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot, getCurrentState());
		gameWorld.restoreState(initialSnapshot);
		fireUpdateHighlightingEvent(null);

		try {
			if (getCurrentState().getNextState() == null) {
				// This is not a resettingState.
				toState(new ResettingState(this));
			}
			Constructor<? extends GameState> constructor = getCurrentState().getNextState()
					.getConstructor(GameController.class);
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
		return condition.assess(gameWorld);
	}

	/**
	 * 
	 * @param block
	 */
	public ExecutionSnapshot performAction(ActionBlock block) {
		GameWorldSnapshot gameSnapshot = gameWorld.saveState();
		ActionBlock nextBlockToBeExecuted = getCurrentState().getNextActionBlockToBeExecuted();
		ExecutionSnapshot snapshot = new ExecutionSnapshot(nextBlockToBeExecuted, gameSnapshot, getCurrentState());
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
	
	/**
	 * Is it useful to perform a gameAction at the moment. An action is useful if it
	 * changes anything, otherwise it's just a waste of time and resources.
	 * 
	 * @return if it's useful to perform a gameAction at the moment.
	 */
	public boolean isGameExecutionUseful() {
		return (getCurrentState() instanceof ValidProgramState) || (getCurrentState() instanceof InExecutionState && getCurrentState().getNextActionBlockToBeExecuted()!=null) ;
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