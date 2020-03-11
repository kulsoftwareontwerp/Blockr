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
		currentState.update();
	}

	public void resetRobot() {
		// TODO - implement GameController.resetRobot
		throw new UnsupportedOperationException();
	}

	public void executeBlock() {
		// TODO - implement GameController.executeBlock
		throw new UnsupportedOperationException();
	}

	public ActionBlock findFirstBlockToBeExecuted() {
		// TODO - implement GameController.findFirstBlockToBeExecuted
		throw new UnsupportedOperationException();
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
		// TODO - implement GameController.performRobotAction
		throw new UnsupportedOperationException();
	}

	public boolean checkIfValidProgram() {
		return programBlockRepository.checkIfValidProgram();
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
		updateState();
		
	}

}