package applicationLayer;

import java.util.Collection;
import java.util.Set;

import domainLayer.blocks.BlockType;
import events.GUIListener;
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
	 * Removes a block with the given blockID from the domain.
	 * 
	 * @param 	blockID
	 * 			The blockID of the block to be removed.
	 * @throws 	IllegalArgumentException
	 * 			If the given BlockID is null or an empty String
	 * @throws	NoSuchConnectedBlockException
	 * 			If the given BlockID doesn't result in a block in the domain.	
	 * @event 	RemoveBlockEvent 	
	 * 			Fires an RemoveBlockEvent if the execution was successful.
	 * @event 	UpdateGameStateEvent 
	 * 			Fires an UpdateGameStateEvent if the execution was successful.
	 * @event 	ResetExecutionEvent
	 * 			Fires a ResetExecutionEvent if the execution was successful.
	 * @event 	PanelChangeEvent Fires a PanelChangeEvent if the maximum number of
	 *        	block was reached before removing the block.
	 */
	public void removeBlock(String blockID) {
		if (blockID == "" || blockID==null) {
			throw new IllegalArgumentException("No blockType given.");
		}
		else {
			blockController.removeBlock(blockID);
		}
			
	}

	public void resetGameExecution() {
		gameController.resetGameExecution();
	}

	
	
	
	/**
	 * Returns all the blockID's in the body of a given ControlBlock
	 * @param 	blockID
	 * 			The blockID of the controlBlock of which you want to retrieve all Blocks in the body.
	 * @throws 	IllegalArgumentException 
	 * 			Is thrown when the given blockID is empty or null.
	 * @throws	NoSuchConnectedBlockException
	 * 			Is thrown when a blockID is given that is not present in the domain.
	 * @throws 	InvalidBlockTypeException 
	 * 			Is thrown when given blockID isn't the ID of a ControlBlock.
	 * @return	A set containing the blockID of the blocks in the body of the given ControlBlock.
	 * 
	 */
	public Set<String> getAllBlockIDsInBody(String blockID){
		if(blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}
		
		 return blockController.getAllBlockIDsInBody(blockID);
		
		
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
		else if(movedBlockId.equals(connectedBeforeMoveBlockId) || movedBlockId.equals(connectedAfterMoveBlockId))
			throw new IllegalArgumentException("You can't connect a block to itself.");
		else {
			blockController.moveBlock(movedBlockId, connectedBeforeMoveBlockId, connectionBeforeMove, connectedAfterMoveBlockId, connectionAfterMove);
		}
	}

	/**
	 * Adds a GUI listener for Game, this listener will be notified about all changes for the GUI.
	 * If the given listener is already a listener for Game it will not be added another time.
	 * @param 	listener
	 * 			The listener to be added.
	 * @throws 	IllegalArgumentException 
	 * 			Is thrown when the given listener is null.
	 */
	public void addGameListener(GUIListener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("No listener given.");
		}
		gameController.addListener(listener);
	}

	/**
	 * Removes a GUI listener for Game, this listener will no longer be notified about any changes for the GUI.
	 * If the GUI listener is no listener Game it also won't be removed.
	 * @param 	listener
	 * 			The listener to be added.
	 * @throws 	IllegalArgumentException 
	 * 			Is thrown when the given listener is null.
	 */
	public void removeGameListener(GUIListener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("No listener given.");
		}
		gameController.removeListener(listener);
	}

	public void executeBlock() {
		gameController.executeBlock();
	}

	/**
	 * Returns all the BlockID's underneath a certain block
	 * 
	 * @param blockID The blockID of the Block of which you want to retrieve all
	 *                Blocks underneath.
	 * @throws 	IllegalArgumentException 
	 * 			Is thrown when the given blockID is empty or null.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A set containing the blockID's of  all connected Conditions and every
	 *         kind of block in the body of the given block or under the given
	 *         block. The ID of the block itself is also given.
	 */

	public Set<String> getAllBlockIDsUnderneath(String blockID) {
		if(blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}
		
		 return blockController.getAllBlockIDsUnderneath(blockID);
	}

}