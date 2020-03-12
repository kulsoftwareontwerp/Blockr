package applicationLayer;

import java.util.Collection;
import java.util.Set;

import domainLayer.blocks.BlockType;
import events.GUIListener;
import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;

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
	 * Move a block that has been added in the domain to change the program you are constructing.
	 * If you're program is in a Execution state this action will, if successful, reset your game state.
	 * 
	 * 
	 * 
	 * @param movedBlockId					The Id of block to be moved, this parameter is required.
	 * 
	 * @param connectedBeforeMoveBlockId	The Id which is connected to our block before moving the block, 
	 * 										this parameter is required. If there's no block connected to the 
	 * 										block you wish to move then use an empty String "".
	 * 
	 * @param connectionBeforeMove			The connection of the block which is connected to our block before moving the block, 
	 * 										this parameter is required. If there's no block connected to the 
	 * 										block you wish to move then use an ConnectionType.NOCONNECTION.
	 * 										For the ConnectionTypes, those are related to the block that is 
	 * 										connected to the block you wish to move e.g. 
	 * 										ConnectionType.DOWN means that the block you wish 
	 * 										to move is underneath the connected block.
	 * 
	 * @param connectedAfterMoveBlockId		The Id of the block you wish to connect the block you are moving to. This parameter is Required.
	 * 										If there's no connected block after the move please use an empty String, "".
	 * @param connectionAfterMove			The connection of the block you wish to connect the block you are moving to. This parameter is Required.
	 * 										If there's no connected block after the move please use ConnectionType.NOCONNECTION.
	 * 
	 * @throws IllegalArgumentException		This Exception when thrown will result in a non execution of the expected changes.
	 * 										This means that the block you wish to move will not be modified.
	 * 										This exception is thrown when;
	 * 										- The Id of the "moved block" is null or an empty String.
	 * 										- One of the ConnectionTypes is null or if it does not exist.
	 * 										- If a "connected block"-id is either a null or a empty String and the ConnectionType is not NOCONNECTION.
	 * 
	 * 
	 *@throws InvalidBlockConnectionException The given combination of the
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
	 *                                         
	 * @event	changeBlockEvent
	 * 			Fires an changeBlockEvent if the execution was successful.
	 * @event	UpdateGameStateEvent
	 * 			Fires an UpdateGameStateEvent if the execution was successful.
	 * @event	ResetExecutionEvent
	 * 			Fires a ResetExecutionEvent if the execution was successful.
	 * 
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
		else if(movedBlockId.equals(connectedBeforeMoveBlockId) || movedBlockId.equals(connectedAfterMoveBlockId))
			throw new IllegalArgumentException("You can't connect a block to itself.");
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
		gameController.executeBlock();
	}

	/**
	 * 
	 * @param blockId
	 */
	public Set<String> getAllBlockIDsUnderneath(String blockId) {
		return blockController.getAllBlockIDsUnderneath(blockId);
	}

}