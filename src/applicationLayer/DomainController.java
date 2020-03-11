package applicationLayer;

import java.util.Collection;
import java.util.Set;

import domainLayer.blocks.BlockType;
import events.GUIListener;

/**
 * The DomainController performs initial checks on the parameters and forwards
 * the requests to the relevant controller.
 * 
 * @version 0.1
 * @author group17
 */
public class DomainController {

	private GameController gameController;
	private BlockController blockController;
	private ElementController elementController;

	/**
	 * Construct a domainController and it's dependencies. - GameController -
	 * BlockController - ElementController
	 */
	public DomainController() {
		gameController = new GameController();
		blockController = new BlockController();
		elementController = new ElementController();

		blockController.addDomainListener(gameController);
		elementController.addDomainListener(gameController);
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
	 * @throws IllegalArgumentException        There can't be a block added with the
	 *                                         given parameters because either: -
	 *                                         the given blockType or the given
	 *                                         connection are null - the given
	 *                                         connectedBlockId is empty and the
	 *                                         connection isn't equal to
	 *                                         ConnectionType.NOCONNECTION - the
	 *                                         given connectedBlockId is not empty
	 *                                         and the connection is equal to
	 *                                         ConnectionType.NOCONNECTION
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
	 * @event PanelChangeEvent Fires a PanelChangeEvent if the maximum number of
	 *        block has been reached after adding a block.
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		if (blockType == null) {
			throw new IllegalArgumentException("No blockType given.");
		} else if (connection == null) {
			throw new IllegalArgumentException("Null given as connection, use ConnectionType.NOCONNECTION.");
		} else if ((connectedBlockId == null || connectedBlockId.equals(""))
				&& connection != ConnectionType.NOCONNECTION) {
			throw new IllegalArgumentException("No connected block given with connection.");
		} else if ((connectedBlockId != null && !connectedBlockId.equals(""))
				&& connection == ConnectionType.NOCONNECTION) {
			throw new IllegalArgumentException("No connection given for connected block.");
		} else {
			blockController.addBlock(blockType, connectedBlockId, connection);
		}
	}

	/**
	 * 
	 * @param blockId
	 */
	public void removeBlock(String blockId) {
		// TODO - implement DomainController.removeBlock
		throw new UnsupportedOperationException();
	}

	public void resetGameExecution() {
		// TODO - implement DomainController.resetGameExecution
		throw new UnsupportedOperationException();
	}

	public Set<String> getAllBlockIdsInBody(String blockId) {

		return null;
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
		if(movedBlockId == null || movedBlockId.equals("")) {
			throw new IllegalArgumentException("No movedBlockID given");
		}
		else if(connectionBeforeMove == null || connectionAfterMove == null) {
			throw new IllegalArgumentException("Null given as connection, use ConnectionType.NOCONNECTION.");
		}
		else if(connectedBeforeMoveBlockId.equals("")  && !(connectionBeforeMove == ConnectionType.NOCONNECTION)) {
			throw new IllegalArgumentException("No blockId given for connectedBeforeMovedBlockID");
			}
		else if(connectedAfterMoveBlockId.equals("") && !(connectionAfterMove == ConnectionType.NOCONNECTION)) {
			throw new IllegalArgumentException("No blockId given for connectedAfterMovedBlockID");
		}
			else {
			blockController.moveBlock(movedBlockId, connectedBeforeMoveBlockId, connectionBeforeMove, connectedAfterMoveBlockId, connectionAfterMove);
		}
	}

	/**
	 * 
	 * @param listener
	 */
	public void addGameListener(GUIListener listener) {
		// TODO - implement DomainController.addGameListener
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeGameListener(GUIListener listener) {
		// TODO - implement DomainController.removeGameListener
		throw new UnsupportedOperationException();
	}

	public void executeBlock() {
		// TODO - implement DomainController.executeBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param blockId
	 */
	public Collection<String> getAllBlockIDsUnderneath(String blockId) {
		// TODO - implement DomainController.getAllBlockIDsUnderneath
		throw new UnsupportedOperationException();
	}

}