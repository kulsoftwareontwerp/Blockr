package applicationLayer;

import java.util.*;
import domainLayer.blocks.*;
import domainLayer.elements.*;
import domainLayer.gamestates.GameState;
import domainLayer.gamestates.InValidProgramState;
import events.*;

public class GameController implements DomainListener, GUISubject {

	private Collection<GUIListener> guiListeners;
	private BlockRepository programBlockRepository;
	private GameState currentState;
	private ElementRepository gameElementRepository;

	public GameController() {
		programBlockRepository = BlockRepository.getInstance();
		gameElementRepository = ElementRepository.getInstance();

		guiListeners = new HashSet<GUIListener>();

		toState(new InValidProgramState(this));

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
			ActionBlock firstActionBlock = findNextActionBlockToBeExecuted(firstExecutableBlock);
			return firstActionBlock;
		}
		return (ActionBlock) firstExecutableBlock;
	}

	/**
	 * 
	 * @param block
	 */
	public ActionBlock findNextActionBlockToBeExecuted(ExecutableBlock currentBlock) {
		// ExecutableBlock nextBlock = block.getNextBlock();
		if (currentBlock == null) {
			ControlBlock cb = programBlockRepository.getEnclosingControlBlock(currentBlock);
			if (cb == null) {
				return null;
			} else if (cb instanceof IfBlock) {
				return findNextActionBlockToBeExecuted(cb.getNextBlock());
			} else {
				return findNextActionBlockToBeExecuted(cb);
			}
		}
		else if (currentBlock instanceof ActionBlock) {
			return (ActionBlock) currentBlock;
		} else {
			// If or while block
			AssessableBlock condition = currentBlock.getConditionBlock();
			if (condition.assess(gameElementRepository)) {
				return findNextActionBlockToBeExecuted(currentBlock.getFirstBlockOfBody());
			} else {
				return findNextActionBlockToBeExecuted(currentBlock.getNextBlock());
			}
		}
	}

	/**
	 * 
	 * @param block
	 */
	public void performRobotAction(ActionBlock block) {
		block.execute(gameElementRepository);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateGameStateEvent(UpdateGameStateEvent event) {
		updateState();
	}

}