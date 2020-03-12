package applicationLayer;

import java.util.*;

import domainLayer.blocks.BlockIDGenerator;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.BlockType;
import events.BlockAddedEvent;
import events.DomainListener;
import events.DomainSubject;
import events.GUIListener;
import events.GUISubject;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import exceptions.MaxNbOfBlocksReachedException;

/**
 * The BlockController orchestrates Create, Update, Delete and Retrieve operations for Blocks.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockController implements GUISubject, DomainSubject {

	private Collection<GUIListener> guiListeners;
	private Collection<DomainListener> domainListeners;
	private BlockRepository programBlockRepository;

	/**
	 * Construct a BlockController and retrieve an instance of it's BlockRepository.
	 */
	public BlockController() {
		guiListeners=new HashSet<GUIListener>();
		domainListeners=new HashSet<DomainListener>();
		programBlockRepository=BlockRepository.getInstance();
		
	}

	private void fireBlockAdded(String newBlockId) {
		BlockAddedEvent event = new BlockAddedEvent(newBlockId);
		
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
	 * Add a block of the given blockType to the domain and connect it with the given connectedBlockId on the given connection
	 * 
	 * @param 	blockType
	 * 			The type of block to be added, this parameter is required.
	 * @param 	connectedBlockId
	 * 			The ID of the block to connect to, can be empty.
	 * @param 	connection
	 * 			The connection of the connected block on which the new block must be connected.
	 * 			If no connectedBlockId was given, this parameter must be set to "ConnectionType.NOCONNECTION".
	 * @throws	InvalidBlockConnectionException
	 * 			The given combination of the blockType,connectedBlockId and connection is impossible.
	 * 			- an ExecutableBlock added to an AssessableBlock or ControlBlock as condition
	 * 			- an AssessableBlock added to a block as a body or "next block"
	 * 			- a block added to another block of which the required connection is not provided.
	 * 			- a block added to a connection of a connected block to which there is already a block connected.
	 * @throws	NoSuchConnectedBlockException
	 * 			Is thrown when a connectedBlockId is given that is not present in the domain.
	 * @throws	MaxNbOfBlocksReachedException
	 * 			The maximum number of blocks in the domain is reached, no extra blocks can be added.
	 * @event	AddBlockEvent
	 * 			Fires an AddBlockEvent if the execution was successful.
	 * @event	UpdateGameStateEvent
	 * 			Fires an UpdateGameStateEvent if the execution was successful.
	 * @event	ResetExecutionEvent
	 * 			Fires a ResetExecutionEvent if the execution was successful.
	 * @event	PanelChangeEvent
	 * 			Fires a PanelChangeEvent if the maximum number of block has been reached after adding a block.
	 * 
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		if(programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			throw new MaxNbOfBlocksReachedException("The maximum number of blocks has already been reached.");
		}
		String newBlockId= programBlockRepository.addBlock(blockType, connectedBlockId, connection);
		
		
		fireUpdateGameState();
		fireResetExecutionEvent();
		if(programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			firePanelChangedEvent();
		}
		fireBlockAdded(newBlockId);
	}

	/**
	 * Retrieve the maximum number of blocks in the domain.
	 * @return the maximum number of blocks.
	 */
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