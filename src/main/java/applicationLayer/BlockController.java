package applicationLayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.BodyCavityBlock;
import domainLayer.blocks.DefinitionBlock;
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
import types.BlockCategory;
import types.BlockSnapshot;
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

	@SuppressWarnings("unused")
	private BlockController(BlockRepository programBlockRepository, Collection<GUIListener> guiListeners) {
		this.guiListeners = guiListeners;
		this.domainListeners = new HashSet<DomainListener>();
		this.programBlockRepository = programBlockRepository;
	}

	private void fireBlockAdded(String newBlockId, String linkedBlockId, ConnectionType linkedConnection,
			BlockType type, Set<String> changedBlocks, boolean moreRelatedEventsComing) {
		BlockAddedEvent event = new BlockAddedEvent(newBlockId, linkedBlockId, linkedConnection, type, changedBlocks, moreRelatedEventsComing);

		for (GUIListener listener : guiListeners) {
			listener.onBlockAdded(event);
		}
	}

	private void fireBlockRemoved(Set<String> idsToBeRemoved, String connectedBlock, ConnectionType connectionType, boolean areMoreRelatedEventsComing) {
		for (String id : idsToBeRemoved) {
			BlockRemovedEvent event = new BlockRemovedEvent(id, connectedBlock, connectionType, idsToBeRemoved, areMoreRelatedEventsComing);
			connectedBlock = "";
			connectionType = ConnectionType.NOCONNECTION;
			for (GUIListener listener : guiListeners) {
				listener.onBlockRemoved(event);
			}
		}
	}

	private void fireBlockChanged(String changedBlockId, String topOfMovedChainId, String beforeMoveBlockId,
			ConnectionType beforeMoveConnectionType, String changedLinkedBlockId, ConnectionType connectionType,
			Set<String> changedBlocks) {
		BlockChangeEvent event = new BlockChangeEvent(changedBlockId, topOfMovedChainId, changedLinkedBlockId,
				connectionType, beforeMoveBlockId, beforeMoveConnectionType, changedBlocks);

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
	 * @return The id of the block that has been added.
	 */
	public BlockSnapshot addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		Block definitionBlock = programBlockRepository.getBlockByID(blockType.definition());
		if (blockType.cat() == BlockCategory.CALL
				&& (definitionBlock == null || !(definitionBlock instanceof DefinitionBlock))) {
			throw new NoSuchConnectedBlockException(
					"There is no DefinitionBlock in the domain with the given definitionBlockID.");
		}

		if (programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			throw new MaxNbOfBlocksReachedException("The maximum number of blocks has already been reached.");
		}
		String newBlockId = programBlockRepository.addBlock(blockType, connectedBlockId, connection);

		Block newBlock = programBlockRepository.getBlockByID(newBlockId);
		Block connectedBlock = programBlockRepository.getBlockByID(connectedBlockId);

		HashSet<Block> addedBlocks = new HashSet<Block>();
		addedBlocks.add(programBlockRepository.getBlockByID(newBlockId).clone());
		BlockSnapshot snapshot = new BlockSnapshot(newBlock, null, connectedBlock, addedBlocks, null);

		fireUpdateGameState();
		fireResetExecutionEvent();
		if (programBlockRepository.checkIfMaxNbOfBlocksReached()) {
			firePanelChangedEvent(false);
		}

		ConnectionType after = programBlockRepository.getConnectionType(snapshot.getConnectedBlockAfterSnapshot(),
				snapshot.getBlock());

		String caID = "";
		if (snapshot.getConnectedBlockAfterSnapshot() != null) {
			caID = snapshot.getConnectedBlockAfterSnapshot().getBlockId();
		}
		fireBlockAdded(newBlockId, caID, after, blockType,
				addedBlocks.stream().map(s -> s.getBlockId()).collect(Collectors.toSet()), false);

		return snapshot;
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
	 * @param isChain A flag announcing if a chain of blocks has to be removed or if
	 *                only the given blockId has to be removed.
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
	 * @return a snapshot containing all the information regarding the removed
	 *         block.
	 */
	public BlockSnapshot removeBlock(String blockID, Boolean isChain) {
		ArrayList<String> previousConnection;
		if (isChain) {
			previousConnection = programBlockRepository.getConnectedParentIfExists(blockID);
		} else {
			previousConnection = programBlockRepository.getConnectedBlockBeforeRemove(blockID);
		}
		Boolean maxBlocksReachedBeforeRemove = programBlockRepository.checkIfMaxNbOfBlocksReached();
		Block deletedBlock = programBlockRepository.getBlockByID(blockID).clone();
		Block connectedBlockBeforeDelete = programBlockRepository.getBlockByID(previousConnection.get(1)) != null
				? programBlockRepository.getBlockByID(previousConnection.get(1)).clone()
				: null;

		Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
		if (deletedBlock instanceof DefinitionBlock) {
			final Set<Block> tempCallBlocks = programBlockRepository
					.getCallerBlocksByDefinition(deletedBlock.getBlockId());

			// Filter out the callBlocks that are lower in the chain of another callBlock or
			// that are in their own definitionBlock

			Set<Block> callBlocks = tempCallBlocks.stream()
					.filter(s -> !(tempCallBlocks.stream().anyMatch(
							j -> s != j && programBlockRepository.getAllBlockIDsUnderneath(j).contains(s.getBlockId()))
							|| programBlockRepository.getAllBlockIDsInBody((BodyCavityBlock) deletedBlock)
									.contains(s.getBlockId())))
					.collect(Collectors.toSet());

			callBlocks = callBlocks.stream().map(s -> s.clone()).collect(Collectors.toSet());

			ArrayList<String> previousConnectionCallBlock;
			for (Block callBlock : callBlocks) {
				previousConnectionCallBlock = programBlockRepository.getConnectedParentIfExists(callBlock.getBlockId());

				Block connectedBlockBeforeDeleteCallBlock = programBlockRepository
						.getBlockByID(previousConnectionCallBlock.get(1)) != null
								? programBlockRepository.getBlockByID(previousConnectionCallBlock.get(1)).clone()
								: null;

				Set<Block> blocksUnderneathCallBlock = programBlockRepository
						.getAllBlocksConnectedToAndAfterACertainBlock(callBlock);

				associatedSnapshots.add(createNewBlockSnapshot(callBlock, connectedBlockBeforeDeleteCallBlock, null,
						blocksUnderneathCallBlock, null));

			}

			callBlocks = tempCallBlocks.stream().filter(s -> !(programBlockRepository
					.getAllBlockIDsInBody((BodyCavityBlock) deletedBlock).contains(s.getBlockId())))
					.collect(Collectors.toSet());
			for (Block callBlock : callBlocks) {
				previousConnectionCallBlock = programBlockRepository.getConnectedParentIfExists(callBlock.getBlockId());

				Block callerParent = programBlockRepository.getBlockByID(previousConnectionCallBlock.get(1));
				switch (ConnectionType.valueOf(previousConnectionCallBlock.get(0))) {
				case BODY:
					callerParent.setFirstBlockOfBody(callBlock.getNextBlock());
					break;
				case DOWN:
					callerParent.setNextBlock(callBlock.getNextBlock());
					break;
				default:
					break;
				}

				Set<String> callIdsToBeRemoved = programBlockRepository.removeBlock(callBlock.getBlockId(), false);				
				
				fireBlockRemoved(callIdsToBeRemoved, callerParent != null ? callerParent.getBlockId() : "",
						ConnectionType.valueOf(previousConnectionCallBlock.get(0)), true);
			}
		}

		BlockSnapshot snapshot = createNewBlockSnapshot(deletedBlock, connectedBlockBeforeDelete, null,
				programBlockRepository.getAllBlocksConnectedToAndAfterACertainBlock(deletedBlock), associatedSnapshots);

		Set<String> idsToBeRemoved = programBlockRepository.removeBlock(blockID, isChain);

		fireUpdateGameState();
		fireResetExecutionEvent();
		if (maxBlocksReachedBeforeRemove) {
			firePanelChangedEvent(true);
		}
		fireBlockRemoved(idsToBeRemoved, previousConnection.get(1), ConnectionType.valueOf(previousConnection.get(0)), false);

		return snapshot;
	}

	// For testing purposes
	BlockSnapshot createNewBlockSnapshot(Block block, Block connectedBlockBeforeSnapshot,
			Block connectedBlockAfterSnapshot, Set<Block> changingBlocks, Set<BlockSnapshot> associatedSnapshots) {
		return new BlockSnapshot(block, connectedBlockBeforeSnapshot, connectedBlockAfterSnapshot, changingBlocks,
				associatedSnapshots);
	}

	/**
	 * Restore a given BlockSnapshot and send the needed events according to the
	 * type of restore.
	 * 
	 * @param snapshot the snapshot to restore
	 * @param isChain  a flag indicating if a chain of blocks need to be restored
	 */

	public void restoreBlockSnapshot(BlockSnapshot snapshot, boolean isChain) {
		if (snapshot == null) {
			throw new NullPointerException("No snapshot given");
		}

		ConnectionType before;

		if (programBlockRepository.getBlockByID(snapshot.getBlock().getBlockId()) != null) {
			before = programBlockRepository.getConnectionType(snapshot.getConnectedBlockBeforeSnapshot(),
					programBlockRepository.getBlockByID(snapshot.getBlock().getBlockId()));

		} else {
			before = programBlockRepository.getConnectionType(snapshot.getConnectedBlockBeforeSnapshot(),
					snapshot.getBlock());
		}

		for (BlockSnapshot associatedSnapshot : snapshot.getAssociatedSnapshots()) {
			programBlockRepository.restoreBlockSnapshot(associatedSnapshot);
			Set<Block> allCallers = programBlockRepository
					.getAllBlocksConnectedToAndAfterACertainBlock(associatedSnapshot.getBlock()).stream()
					.filter(s -> s.getBlockType().definition()
							.equals(associatedSnapshot.getBlock().getBlockType().definition()))
					.collect(Collectors.toSet());

			for (Block caller : allCallers) {

				ArrayList<String> parentIdentifiers = programBlockRepository
						.getConnectedParentIfExists(caller.getBlockId());

				ConnectionType after = ConnectionType.valueOf(parentIdentifiers.get(0));
				String caID = "";
				if (parentIdentifiers.get(1) != null) {
					caID = parentIdentifiers.get(1);
				}
				fireBlockAdded(caller.getBlockId(), caID, after, caller.getBlockType(), null, true);
			}

		}

		Boolean removed = programBlockRepository.restoreBlockSnapshot(snapshot);

		if (removed) {
			Boolean maxBlocksReached = programBlockRepository.checkIfMaxNbOfBlocksReached();
			if (maxBlocksReached) {
				firePanelChangedEvent(false);
			}
			if (isChain) {
				fireBlockAdded(snapshot);
			} else {
				ConnectionType after = programBlockRepository
						.getConnectionType(snapshot.getConnectedBlockAfterSnapshot(), snapshot.getBlock());
				String caID = "";
				if (snapshot.getConnectedBlockAfterSnapshot() != null) {
					caID = snapshot.getConnectedBlockAfterSnapshot().getBlockId();
				}
				fireBlockAdded(snapshot.getBlock().getBlockId(), caID, after, snapshot.getBlock().getBlockType(), null, false);
			}
		} else {

			ConnectionType after = programBlockRepository.getConnectionType(snapshot.getConnectedBlockAfterSnapshot(),
					snapshot.getBlock());

			String cbID = "";
			String caID = "";
			if (snapshot.getConnectedBlockBeforeSnapshot() != null) {
				cbID = snapshot.getConnectedBlockBeforeSnapshot().getBlockId();
			}
			if (snapshot.getConnectedBlockAfterSnapshot() != null) {
				caID = snapshot.getConnectedBlockAfterSnapshot().getBlockId();
			}

			fireBlockChanged(snapshot.getBlock().getBlockId(), snapshot.getBlock().getBlockId(), cbID, before, caID,
					after, snapshot.getChangingBlocks().stream().map(s -> s.getBlockId()).collect(Collectors.toSet()));
		}

		fireUpdateGameState();
		fireResetExecutionEvent();
	}

	 void fireBlockAdded(BlockSnapshot snapshot) {

		ConnectionType after = programBlockRepository.getConnectionType(snapshot.getConnectedBlockAfterSnapshot(),
				snapshot.getBlock());
		String caID = "";
		if (snapshot.getConnectedBlockAfterSnapshot() != null) {
			caID = snapshot.getConnectedBlockAfterSnapshot().getBlockId();
		}
		fireBlockAdded(snapshot.getBlock().getBlockId(), caID, after, snapshot.getBlock().getBlockType(),
				programBlockRepository.getAllBlockIDsUnderneath(snapshot.getBlock()), false);

		Block toAdd = snapshot.getBlock();

		// TODO: How best to test this?
		// See
		// testRestoreBlockSnapshot_RemovedTrue_MaxBlocksReachedTrue_IsChainTrue_AllOptionsInFireBlockAdded_Positive
		// in BlockControllerTest
		if (toAdd.getConditionBlock() != null) {
			BlockSnapshot s = new BlockSnapshot(toAdd.getConditionBlock(), null, toAdd, null, null);
			fireBlockAdded(s);
		}
		if (toAdd.getFirstBlockOfBody() != null) {
			BlockSnapshot s = new BlockSnapshot(toAdd.getFirstBlockOfBody(), null, toAdd, null, null);
			fireBlockAdded(s);
		}
		if (toAdd.getNextBlock() != null) {
			BlockSnapshot s = new BlockSnapshot(toAdd.getNextBlock(), null, toAdd, null, null);
			fireBlockAdded(s);
		}
		if (toAdd.getOperand() != null) {
			BlockSnapshot s = new BlockSnapshot(toAdd.getOperand(), null, toAdd, null, null);
			fireBlockAdded(s);
		}
	}

	/**
	 * Add a block of the given blockType to the domain and connect it with the
	 * given connectedBlockId on the given connection
	 * 
	 * @param topOfMovedChainBlockId    The Id of block to be moved, if you move a
	 *                                  chain of blocks this will be the first block
	 *                                  in the chain, this parameter is required.
	 * @param movedBlockId              The Id of block that's actually being moved,
	 *                                  this might be the same as the
	 *                                  topOfMovedChainBlockId, if the movedBlockId
	 *                                  is empty the topOfMovedChainBlockId will be
	 *                                  used in any way.
	 * @param connectionAfterMove       The connection of the connected block on
	 *                                  which the new block must be connected. If no
	 *                                  connectedBlockId was given, this parameter
	 *                                  must be set to
	 *                                  "ConnectionType.NOCONNECTION".
	 * @param connectedAfterMoveBlockId The ID of the block to connect to, can be
	 *                                  empty.
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
	 * @return the blockSnapshot representing the state and state changes affected
	 *         by this move.
	 */
	public BlockSnapshot moveBlock(String topOfMovedChainBlockId, String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove) {
		String movedID = programBlockRepository.getBlockIdToPerformMoveOn(topOfMovedChainBlockId, movedBlockId,
				connectionAfterMove);
		Set<Block> movedBlocks = programBlockRepository
				.getAllBlocksConnectedToAndAfterACertainBlock(
						programBlockRepository.getBlockByID(topOfMovedChainBlockId))
				.stream().map(b -> b.clone()).collect(Collectors.toSet());

		ArrayList<String> previousConnection = programBlockRepository
				.getConnectedParentIfExists(topOfMovedChainBlockId);

		Block movedBlock = programBlockRepository.getBlockByID(movedID) != null
				? programBlockRepository.getBlockByID(movedID).clone()
				: null;
		Block connectedBlockBeforeMove = programBlockRepository.getBlockByID(previousConnection.get(1)) != null
				? programBlockRepository.getBlockByID(previousConnection.get(1)).clone()
				: null;

		String movedBlockID = programBlockRepository.moveBlock(topOfMovedChainBlockId, movedID,
				connectedAfterMoveBlockId, connectionAfterMove);

		Block connectedBlockAfterMove = programBlockRepository.getBlockByID(connectedAfterMoveBlockId);
		BlockSnapshot snapshot = new BlockSnapshot(movedBlock, connectedBlockBeforeMove, connectedBlockAfterMove,
				movedBlocks, null);

		fireUpdateGameState();
		fireResetExecutionEvent();

		fireBlockChanged(movedBlockID, topOfMovedChainBlockId, previousConnection.get(1),
				ConnectionType.valueOf(previousConnection.get(0)), connectedAfterMoveBlockId, connectionAfterMove,
				snapshot.getChangingBlocks().stream().map(s -> s.getBlockId()).collect(Collectors.toSet()));

		return snapshot;
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
	 * Returns all the blockID's in the body of a given BodyCavityBlock
	 * 
	 * @param blockID The blockID of the BodyCavityBlock of which you want to
	 *                retrieve all Blocks in the body.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @throws InvalidBlockTypeException     Is thrown when given blockID isn't the
	 *                                       ID of a BodyCavityBlock.
	 * @return A set containing the blockID of the blocks in the body of the given
	 *         BodyCavityBlock.
	 */
	public Set<String> getAllBlockIDsInBody(String blockID) {
		Block block = programBlockRepository.getBlockByID(blockID);
		Set<String> blockIDsInBody = new HashSet<String>();

		if (block == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		} else if (!(block instanceof BodyCavityBlock)) {
			throw new InvalidBlockTypeException(BodyCavityBlock.class, block.getClass());
		} else {
			blockIDsInBody = programBlockRepository.getAllBlockIDsInBody((BodyCavityBlock) block);
		}

		return blockIDsInBody;
	}

	/**
	 * Finds the id of the enclosing controlblock of the given block.
	 * 
	 * @param id The ID of the block to find the enclosing controlblock of.
	 * @return The id of the enclosing controlblock. If there is no enclosing block,
	 *         the method returns null.
	 */
	public String getEnclosingBodyCavityBlock(String id) {
		Block givenBlock = programBlockRepository.getBlockByID(id);
		BodyCavityBlock block = null;
		if (givenBlock == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		} else if (!(givenBlock instanceof ExecutableBlock)) {
			throw new InvalidBlockTypeException(ExecutableBlock.class, givenBlock.getClass());
		} else {
			block = programBlockRepository.getEnclosingBodyCavityBlock((ExecutableBlock) givenBlock);
		}

		if (block == null)
			return null;

		return ((Block) block).getBlockId();
	}

	/**
	 * Finds all the ID's of the blocks that are below the given block.
	 * 
	 * @param blockID The ID of the block from which we want to find all blocks
	 *                below.
	 * @return A set of blockID's of the blocks below the given block.
	 */
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

	/**
	 * Finds the ID's of all the controlblocks who are not in another controlBlock.
	 * 
	 * @return A set of the ID's of all the controlblocks who are not in another
	 *         controlBlock.
	 */
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

	/**
	 * Finds all the current headblocks in the program.
	 * 
	 * @return A set of all the ID's of the current headblocks of the program.
	 */
	public Set<String> getAllHeadBlocks() {
		return programBlockRepository.getAllHeadBlocks().stream().map(e -> e.getBlockId()).collect(Collectors.toSet());
	}

	/**
	 * Check if the block associated with the given id is present in the domain.
	 * 
	 * @param id the id to check
	 * @return a boolean indicating if the block is present.
	 */
	public boolean isBlockPresent(String id) {
		return programBlockRepository.getBlockByID(id) != null;
	}

	/**
	 * Retrieve the blockType of the block associated with the given id;
	 * 
	 * @param id The id of the block to retrieve the Blocktype from.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return the blockType associated with the given block
	 */
	public BlockType getBlockType(String id) {
		Block b = programBlockRepository.getBlockByID(id);
		if (b == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		}
		return b.getBlockType();
	}

	/**
	 * Check if the connection is open and can be used to perform a move or add on.
	 * 
	 * @param blockIdToCheck The id of the block to check the connection from
	 * @param connection     The connection to check on the given block
	 * @param changingBlocks A set with the id's of all blocks that are changing at
	 *                       the moment as to keep in measure that if the
	 *                       blockToCheck is connected to one of the blocks in this
	 *                       set that connection will be removed after the operation
	 *                       and hence can be ignored. If this parameter is null an
	 *                       empty set will be used and the method won't keep in
	 *                       mind any possible changed blocks.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A flag indicating if the given connection for the given block is
	 *         open.
	 */
	public Boolean checkIfConnectionIsOpen(String blockIdToCheck, ConnectionType connection,
			Set<String> changingBlocks) {
		Block blockToCheck = programBlockRepository.getBlockByID(blockIdToCheck);
		if (blockToCheck == null) {
			throw new NoSuchConnectedBlockException("The given blockID is not present in the domain.");
		}
		if (!blockToCheck.getSupportedConnectionTypes().contains(connection)) {
			return false;
		}
		if (changingBlocks.size() == 0) {
			return programBlockRepository.checkIfConnectionIsOpen(blockToCheck, connection, null);
		}

		for (String id : changingBlocks) {
			Block changeBlock = programBlockRepository.getBlockByID(id);
			if (programBlockRepository.checkIfConnectionIsOpen(blockToCheck, connection, changeBlock)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieve the number of blocks that are still able to be added.
	 * @return the number of blocks that are still able to be added.
	 */
	public int getNumberOfRemainingBlocks() {
		return programBlockRepository.getNumberOfRemainingBlocks();
	}

}