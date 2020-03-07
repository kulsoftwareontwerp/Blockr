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
		// TODO - implement BlockController.fireBlockAdded
		throw new UnsupportedOperationException();
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
		// TODO - implement BlockController.firePanelChangedEvent
		throw new UnsupportedOperationException();
	}

	private void fireUpdateGameState() {
		// TODO - implement BlockController.fireUpdateGameState
		throw new UnsupportedOperationException();
	}

	private void fireResetExecutionEvent() {
		// TODO - implement BlockController.fireResetExecutionEvent
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param blockType
	 * @param connectedBlockId
	 * @param connection
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		// TODO - implement BlockController.addBlock
		throw new UnsupportedOperationException();
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