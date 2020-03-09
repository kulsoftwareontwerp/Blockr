package applicationLayer;

import java.util.*;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.elements.ElementRepository;
import domainLayer.gamestates.GameState;
import domainLayer.gamestates.InValidProgramState;
import events.DomainListener;
import events.GUIListener;
import events.GUISubject;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;

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
		// TODO - implement GameController.fireRobotChangeEvent
		throw new UnsupportedOperationException();
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
		// TODO - implement GameController.findNextActionBlockToBeExecuted
		throw new UnsupportedOperationException();
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
	private void fireUpdateHighlightingEvent(String highlightedBlockId) {
		// TODO - implement GameController.fireUpdateHighlightingEvent
		throw new UnsupportedOperationException();
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