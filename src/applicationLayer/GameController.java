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
		programBlockRepository=BlockRepository.getInstance();
		gameElementRepository=gameElementRepository.getInstance();
		
		guiListeners=new HashSet<GUIListener>();
		
		toState(new InValidProgramState(this));
		
	}

	public void fireRobotChangeEvent() {
		Robot robot = gameElementRepository.getRobot();
		RobotChangeEvent robotChangeEvent = new RobotChangeEvent(robot.getXCoordinate(), robot.getYCoordinate(), robot.getOrientation());
		for(GUIListener listener: guiListeners) {
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
		// TODO - implement GameController.updateState
		throw new UnsupportedOperationException();
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
		ActionBlock firstActionBlock = findNextActionBlockToBeExecuted(firstExecutableBlock);
		return firstActionBlock;
	}

	/**
	 * 
	 * @param block
	 */
	public ActionBlock findNextActionBlockToBeExecuted(ExecutableBlock block) {
		ExecutableBlock nextBlock = block.getNextBlock();
		if (nextBlock instanceof ActionBlock) {
			return (ActionBlock) nextBlock;
		}
		else if (nextBlock instanceof WhileBlock) {

			AssessableBlock condition = nextBlock.getConditionBlock();
			if (condition.assess(gameElementRepository)) {
				return findNextActionBlockToBeExecuted(nextBlock.getFirstBlockOfBody());
			} else {
				return findNextActionBlockToBeExecuted(nextBlock.getNextBlock());
			}
		}
		// nextBlock instanceof IfBlock
		else {
			// Check if this IfBlock is reached because it's beneath another block (so we need to continue with the firstBlockOfBody of this IfBlock)
			// OR it's reached because we are at the end of executing this IfBlock (so we need to continue with the nextBlock of this IfBlock)
			if (!isReachedFromEndOfBody(block.getBlockId(),nextBlock.getBlockId(),nextBlock.getFirstBlockOfBody())) {
				return findNextActionBlockToBeExecuted(nextBlock.getFirstBlockOfBody());
			} else {
				return findNextActionBlockToBeExecuted(nextBlock.getNextBlock());
			}
		}
	}
	
	// TODO: WRITE EXPLANATION
	private boolean isReachedFromEndOfBody(String endOfBodyBlockId, String ifBlockId, ExecutableBlock nextBlockToCheck) {
		if (nextBlockToCheck.getBlockId().equals(endOfBodyBlockId))	{
			return true;
		}
		else if (nextBlockToCheck.getBlockId().equals(ifBlockId)) {
			return false;
		}
		else {
			return isReachedFromEndOfBody(endOfBodyBlockId, ifBlockId, nextBlockToCheck.getNextBlock());
		}
	}
	




	/**
	 * 
	 * @param block
	 */
	public void performRobotAction(ActionBlock block) {
		switch(block.getClass().toString()) {
			case "TurnLeftBlock":
				gameElementRepository.turnRobotLeft();
			case "TurnRightBlock":
				gameElementRepository.turnRobotRight();
			case "MoveForwardBlock":
				gameElementRepository.moveRobotForward();
		}
		fireRobotChangeEvent();
	}
	
	public boolean checkIfValidProgram() {
		// TODO - implement GameController.checkIfValidProgram
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param highlightedBlockId
	 */
	public void fireUpdateHighlightingEvent(String highlightedBlockId) {
		UpdateHighlightingEvent updateHighlightingEvent = new UpdateHighlightingEvent(highlightedBlockId);
		for(GUIListener listener: guiListeners) {
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
		// TODO Auto-generated method stub
		
	}

}