package applicationLayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.DomainListener;
import events.DomainSubject;
import events.GUIListener;
import events.GUISubject;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import exceptions.InvalidBlockConnectionException;
import exceptions.InvalidBlockTypeException;
import exceptions.MaxNbOfBlocksReachedException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockType;
import types.ConnectionType;

/**
 * The BlockController orchestrates Create, Update, Delete and Retrieve
 * operations for Blocks.
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
		guiListeners = new HashSet<GUIListener>();
		domainListeners = new HashSet<DomainListener>();
		programBlockRepository = BlockRepository.getInstance();

	}

	private void fireBlockAdded(String newBlockId) {
		BlockAddedEvent event = new BlockAddedEvent(newBlockId);

		for (GUIListener listener : guiListeners) {
			listener.onBlockAdded(event);
		}
	}

	private void fireBlockRemoved(Set<String> idsToBeRemoved, String connectedBlock, ConnectionType connectionType) {
		for (String id : idsToBeRemoved) {
			BlockRemovedEvent event = new BlockRemovedEvent(id, connectedBlock, connectionType);
			connectedBlock = "";
			connectionType = ConnectionType.NOCONNECTION;
			for (GUIListener listener : guiListeners) {
				listener.onBlockRemoved(event);
			}

		}
	}

	private void fireBlockChanged(String changedBlockId, String changedLinkedBlockId, ConnectionType connectionType,
			String beforeMoveBlockId, ConnectionType beforeMoveConnectionType) {
		BlockChangeEvent event = new BlockChangeEvent(changedBlockId, changedLinkedBlockId, connectionType,
				beforeMoveBlockId, beforeMoveConnectionType);

		for (GUIListener listener : guiListeners) {
			listener.onBlockChangeEvent(event);
		}
	}

	private void firePanelChangedEvent(Boolean show) {
		PanelChangeEvent event = new PanelChangeEvent(show);
		for (GUIListener listener : guiListeners) {
			listener.onPanelChangedEvent(event);
		}
	}

	private void fireUpdateGameState() {
		UpdateGameStateEvent event = new UpdateGameStateEvent();

		for (DomainListener listener : domainListeners) {
			listener.onUpdateGameStateEvent(event);
		}
	}

	private void fireResetExecutionEvent() {
		ResetExecutionEvent event = new ResetExecutionEvent();
		for (DomainListener listener : domainListeners) {
			listener.onResetExecutionEvent(event);
		}
	}

	/**
	 * Add a block of the given blockType to the domain and connect it with the
	 * given connectedBlockId on the given connection
	 * 
	 * @param blockType        The type of block to be added, this parameter is
	 *                         required.
	 * @param connectedBlockId The ID of the block to connect to, can be empty.
	 * @param connection       The connection of the connected block on which the
	 *                         new block must be connected. If no connectedBlockId
	 *                         was given, this parameter must be set to
	 *                         "ConnectionType.NOCONNECTION".
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - an AssessableBlock added
	 *                                         to a block as a body or "next block"
	 *                                         - a block added to another block of
	 *                                         which the required connection is not
	 *                                         provided. - a block added to a
	 *                                         connection of a connected block to
	 *                                         which there is already a block
	 *                                         connected.
	 * @throws NoSuchConnectedBlockException   Is thrown when a connectedBlockId is
	 *                                         given that is not present in the
	 *                                         domain.
	 * @throws MaxNbOfBlocksReachedException   The maximum number of blocks in the
	 *                                         domain is reached, no extra blocks
	 *                                         can be added.
	 * @event AddBlockEvent Fires an AddBlockEvent if the execution was successful.
	 * @event UpdateGameStateEvent Fires an UpdateGameStateEvent if the execution
	 *        was successful.
	 * @event ResetExecutionEvent Fires a ResetExecutionEvent if the execution was
	 *        successful.
	 * @event PanelChangeEvent Fires a PanelChangeEvent if the maximum number of
	 *        block has been reached after adding a block.
	 * 
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		if (programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			throw new MaxNbOfBlocksReachedException("The maximum number of blocks has already been reached.");
		}
		String newBlockId = programBlockRepository.addBlock(blockType, connectedBlockId, connection);

		fireUpdateGameState();
		fireResetExecutionEvent();
		if (programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			firePanelChangedEvent(false);
		}
		fireBlockAdded(newBlockId);
	}

	/**
	 * Retrieve the maximum number of blocks in the domain.
	 * 
	 * @return the maximum number of blocks.
	 */
	public int getMaxNbOfBlocks() {
		return programBlockRepository.getMaxNbOfBlocks();
	}

	/**
	 * Removes a block with the given blockID from the domain.
	 * 
	 * @param blockID The blockID of the block to be removed.
	 * @throws NoSuchConnectedBlockException If the given BlockID doesn't result in
	 *                                       a block in the domain.
	 * @event RemoveBlockEvent Fires an RemoveBlockEvent if the execution was
	 *        successful.
	 * @event UpdateGameStateEvent Fires an UpdateGameStateEvent if the execution
	 *        was successful.
	 * @event ResetExecutionEvent Fires a ResetExecutionEvent if the execution was
	 *        successful.
	 * @event PanelChangeEvent Fires a PanelChangeEvent if the maximum number of
	 *        block was reached before removing the block.
	 */
	public void removeBlock(String blockID) {
		ArrayList<String> previousConnection = programBlockRepository.getConnectedParentIfExists(blockID);
		Boolean maxBlocksReachedBeforeRemove = programBlockRepository.checkIfMaxNbOfBlocksReached();
		Set<String> idsToBeRemoved = programBlockRepository.removeBlock(blockID);
		fireUpdateGameState();
		fireResetExecutionEvent();
		if (maxBlocksReachedBeforeRemove) {
			firePanelChangedEvent(true);
		}
		fireBlockRemoved(idsToBeRemoved, previousConnection.get(1), ConnectionType.valueOf(previousConnection.get(0)));
	}

	/**
	 * Add a block of the given blockType to the domain and connect it with the
	 * given connectedBlockId on the given connection
	 * 
	 * @param movedBlockId        TODO
	 * @param connectionAfterMove The connection of the connected block on which the
	 *                            new block must be connected. If no
	 *                            connectedBlockId was given, this parameter must be
	 *                            set to "ConnectionType.NOCONNECTION".
	 * @param blockType           The type of block to be added, this parameter is
	 *                            required.
	 * @param connectedBlockId    The ID of the block to connect to, can be empty.
	 * 
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - an AssessableBlock added
	 *                                         to a block as a body or "next block"
	 *                                         - a block added to another block of
	 *                                         which the required connection is not
	 *                                         provided. - a block added to a
	 *                                         connection of a connected block to
	 *                                         which there is already a block
	 *                                         connected.
	 * @throws NoSuchConnectedBlockException   Is thrown when a connectedBlockId is
	 *                                         given that is not present in the
	 *                                         domain.
	 * 
	 * @event changeBlockEvent Fires an changeBlockEvent if the execution was
	 *        successful.
	 * @event UpdateGameStateEvent Fires an UpdateGameStateEvent if the execution
	 *        was successful.
	 * @event ResetExecutionEvent Fires a ResetExecutionEvent if the execution was
	 *        successful.
	 */
	public void moveBlock(String topOfMovedChainBlockId, String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove) {

		String movedID = programBlockRepository.getBlockIdToPerformMoveOn(topOfMovedChainBlockId, movedBlockId, connectionAfterMove);
		ArrayList<String> previousConnection = programBlockRepository.getConnectedBlockBeforeMove(movedID,
				connectedAfterMoveBlockId, connectionAfterMove);

		String movedBlockID = programBlockRepository.moveBlock(movedID, connectedAfterMoveBlockId, connectionAfterMove);

		fireUpdateGameState();
		fireResetExecutionEvent();

		fireBlockChanged(movedBlockID, connectedAfterMoveBlockId, connectionAfterMove, previousConnection.get(1),
				ConnectionType.valueOf(previousConnection.get(0)));

	}

	/**
	 * Returns all the BlockID's underneath a certain block
	 * 
	 * @param blockID The blockID of the Block of which you want to retrieve all
	 *                Blocks underneath.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A set containing the blockID's of all connected Conditions and every
	 *         kind of block in the body of the given block or under the given
	 *         block. The ID of the block itself is also given.
	 */
	public Set<String> getAllBlockIDsUnderneath(String blockID) {
		Block block = programBlockRepository.getBlockByID(blockID);
		Set<String> blockIDsUnderNeath = new HashSet<String>();

		if (block == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		} else {
			blockIDsUnderNeath = programBlockRepository.getAllBlockIDsUnderneath(block);
		}

		return blockIDsUnderNeath;
	}

	/**
	 * Returns all the blockID's in the body of a given ControlBlock
	 * 
	 * @param blockID The blockID of the controlBlock of which you want to retrieve
	 *                all Blocks in the body.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @throws InvalidBlockTypeException     Is thrown when given blockID isn't the
	 *                                       ID of a ControlBlock.
	 * @return A set containing the blockID of the blocks in the body of the given
	 *         ControlBlock.
	 */
	public Set<String> getAllBlockIDsInBody(String blockID) {
		Block block = programBlockRepository.getBlockByID(blockID);
		Set<String> blockIDsInBody = new HashSet<String>();

		if (block == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		} else if (!(block instanceof ControlBlock)) {
			throw new InvalidBlockTypeException(ControlBlock.class, block.getClass());
		} else {
			blockIDsInBody = programBlockRepository.getAllBlockIDsInBody((ControlBlock) block);
		}

		return blockIDsInBody;
	}

	// TO BE DOCUMENTED:

	// TODO THROW EXECPTIONS!!!!!

	public String getEnclosingControlBlock(String id) {

		Block givenBlock = programBlockRepository.getBlockByID(id);
		ControlBlock block = null;
		if (givenBlock instanceof ExecutableBlock) {

			block = programBlockRepository.getEnclosingControlBlock((ExecutableBlock) givenBlock);
		}

		if (block == null)
			return null;

		return block.getBlockId();
	}

	public Set<String> getAllBlockIDsBelowCertainBlock(String blockID) {
		Block block = programBlockRepository.getBlockByID(blockID);
		Set<String> blockIDsUnderNeath = new HashSet<String>();

		if (block == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		} else {
			blockIDsUnderNeath = programBlockRepository.getAllBlockIDsBelowCertainBlock(block);
		}

		return blockIDsUnderNeath;
	}

	public Set<String> getAllHeadControlBlocks() {
		return programBlockRepository.getAllHeadControlBlocks().stream().map(e -> e.getBlockId())
				.collect(Collectors.toSet());
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

	public String getFirstBlockBelow(String id) {
		Block block = programBlockRepository.getBlockByID(id);

		if (block == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		}

		if (block.getNextBlock() != null) {
			return block.getBlockId();
		} else {
			return null;
		}

	}

	public Set<String> getAllHeadBlocks() {
		return programBlockRepository.getAllHeadBlocks().stream().map(e -> e.getBlockId()).collect(Collectors.toSet());
	}

}