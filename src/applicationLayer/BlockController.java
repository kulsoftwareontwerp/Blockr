package applicationLayer;

import java.util.*;
import domainLayer.*;

public class BlockController implements GUISubject, DomainSubject {

	private Collection<GUIListener> guiListeners;
	private Collection<DomainListener> domainListeners;
	private BlockRepository programBlockRepository;

	public BlockController() {
		guiListeners=new HashSet<GUIListener>();
		domainListeners=new HashSet<DomainListener>();
		programBlockRepository=BlockRepository.getInstance();
		
	}

	private void fireBlockAdded() {
		String blockId = programBlockRepository.getLastAddedBlockId();
		BlockAddedEvent event = new BlockAddedEvent(blockId);
		
		for(GUIListener listener:guiListeners) {
			listener.onBlockAdded(event);
		}
	}

	private void fireBlockRemoved() {
		// TODO - implement BlockController.fireBlockRemoved
		throw new UnsupportedOperationException();
	}

	private void fireBlockChanged() {
		// TODO - implement BlockController.fireBlockChanged
		throw new UnsupportedOperationException();
	}

	private void firePanelChangedEvent() {
		PanelChangeEvent event = new PanelChangeEvent(false);
		for(GUIListener listener:guiListeners) {
			listener.onPanelChangedEvent(event);
		}
	}

	private void fireUpdateGameState() {
		UpdateGameStateEvent event = new UpdateGameStateEvent();
		
		for(DomainListener listener:domainListeners) {
			listener.onUpdateGameStateEvent(event);
		}
		
	}

	private void fireResetExecutionEvent() {
		ResetExecutionEvent event = new ResetExecutionEvent();
		for(DomainListener listener:domainListeners) {
			listener.onResetExecutionEvent(event);
		}
	}

	/**
	 * 
	 * @param blockType
	 * @param connectedBlockId
	 * @param connection
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		if(programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			throw new MaxNbOfBlocksReachedException("The maximum number of blocks has already been reached.");
		}
		programBlockRepository.addBlock(blockType, connectedBlockId, connection);
		
		
		fireUpdateGameState();
		fireResetExecutionEvent();
		if(programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			firePanelChangedEvent();
		}
		fireBlockAdded();
	}

	public int getMaxNbOfBlocks() {
		return programBlockRepository.getMaxNbOfBlocks();
	}

	/**
	 * 
	 * @param blockId
	 */
	public void removeBlock(String blockId) {
		// TODO - implement BlockController.removeBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param movedBlockId
	 * @param connectedBeforeMoveBlockId
	 * @param connectionBeforeMove
	 * @param connectedAfterMoveBlockId
	 * @param connectionAfterMove
	 */
	public void moveBlock(String movedBlockId, String connectedBeforeMoveBlockId, ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		// TODO - implement BlockController.moveBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param blockId
	 */
	public Collection<String> getAllBlockIDsUnderneath(String blockId) {
		// TODO - implement BlockController.getAllBlockIDsUnderneath
		throw new UnsupportedOperationException();
	}


	@Override
	public void addDomainListener(DomainListener listener) {
		domainListeners.add(listener);
		
	}

	@Override
	public void removeDomainListener(DomainListener listener) {
		domainListeners.remove(listener);
		
	}

	@Override
	public void removeListener(GUIListener listener) {
		guiListeners.remove(listener);
		
	}

	@Override
	public void addListener(GUIListener listener) {
		guiListeners.add(listener);
		
	}

}