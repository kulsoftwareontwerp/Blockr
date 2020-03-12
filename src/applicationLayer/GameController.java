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
		gameElementRepository = gameElementRepository.getInstance();

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
		// TODO - implement GameController.resetGameExecution
		throw new UnsupportedOperationException();
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
		// TODO - implement GameController.resetRobot
		throw new UnsupportedOperationException();
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
	public ActionBlock findNextActionBlockToBeExecuted(ExecutableBlock block) {
		ExecutableBlock nextBlock = block.getNextBlock();
		if (nextBlock == null) {
			return null;
		}
		if (nextBlock instanceof ActionBlock) {
			return (ActionBlock) nextBlock;
		} else {
			// If or while block
			AssessableBlock condition = nextBlock.getConditionBlock();
			if (condition.assess(gameElementRepository)) {
				// If the condition is true, we want to continue with the first block in the
				// body of the controlBlock
				// except for when we just came out of the body of an ifBlock. Then we continue
				// under it.
				if (nextBlock instanceof IfBlock && isReachedFromEndOfBody(block.getBlockId(), nextBlock.getBlockId(),
						nextBlock.getFirstBlockOfBody())) {
					return findNextActionBlockToBeExecuted(nextBlock.getNextBlock());
				} else {
					return findNextActionBlockToBeExecuted(nextBlock.getFirstBlockOfBody());
				}
			} else {
				// If the condition is false, we continue with the block under the controlBlock
				return findNextActionBlockToBeExecuted(nextBlock.getNextBlock());
			}
		}
	}

	private boolean isReachedFromEndOfBody(String endOfBodyBlockId, String ifBlockId,
			ExecutableBlock nextBlockToCheck) {
		if (nextBlockToCheck.getBlockId().equals(endOfBodyBlockId)) {
			return true;
		} else if (nextBlockToCheck.getBlockId().equals(ifBlockId)) {
			return false;
		} else {
			return isReachedFromEndOfBody(endOfBodyBlockId, ifBlockId, nextBlockToCheck.getNextBlock());
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