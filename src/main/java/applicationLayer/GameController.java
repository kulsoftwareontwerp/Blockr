package applicationLayer;

import java.util.*;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.elements.ElementRepository;
import domainLayer.elements.Robot;
import domainLayer.gamestates.GameState;
import domainLayer.gamestates.InValidProgramState;
import events.DomainListener;
import events.GUIListener;
import events.GUISubject;
import events.ResetExecutionEvent;
import events.RobotChangeEvent;
import events.UpdateGameStateEvent;
import events.UpdateHighlightingEvent;

public class GameController implements DomainListener, GUISubject {

	private Collection<GUIListener> guiListeners;
	private BlockRepository programBlockRepository;
	private GameState currentState;
	private ElementRepository gameElementRepository;
	private GameWorld gameWorld;

	public GameController(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
		programBlockRepository = BlockRepository.getInstance();
		gameElementRepository = ElementRepository.getInstance();

		guiListeners = new HashSet<GUIListener>();

		toState(new InValidProgramState(this));

	}
	
	// Used for mockinjection in the tests
	private GameController(BlockRepository br) {
		this.programBlockRepository = br;
	}


	public void fireRobotChangeEvent() {
		Robot robot = gameElementRepository.getRobot();
		RobotChangeEvent robotChangeEvent = new RobotChangeEvent(robot.getXCoordinate(), robot.getYCoordinate(),
				robot.getOrientation());
		for (GUIListener listener : guiListeners) {
			listener.onRobotChangeEvent(robotChangeEvent);
		}
	}

	private boolean isMaxNbOfBlocksReached() {
		// TODO - implement GameController.isMaxNbOfBlocksReached
		throw new UnsupportedOperationException();
	}

	public void resetGameExecution() {
		GameState currentState = getCurrentState();
		currentState.reset();
		fireUpdateHighlightingEvent(null);
		fireRobotChangeEvent();
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

	public void resetRobot() {
		gameElementRepository.removeRobot();
		gameElementRepository.initializeRobot();
		fireRobotChangeEvent();
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
		}
		else if (currentBlock instanceof ActionBlock) {
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
	
	private boolean evaluateCondition(AssessableBlock condition){
		if(condition instanceof ConditionBlock){
			return gameWorld.evaluate(((ConditionBlock) condition).getPredicate());
		}else {
			return !evaluateCondition(condition.getOperand());
		}
	}

	/**
	 * 
	 * @param block
	 */
	public void performRobotAction(ActionBlock block) {
		gameWorld.performAction(block.getAction());
		fireRobotChangeEvent();
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