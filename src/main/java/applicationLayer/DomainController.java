package applicationLayer;

import java.awt.Graphics;
import java.util.Set;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import commands.AddBlockCommand;
import commands.BlockCommand;
import commands.CommandHandler;
import commands.MoveBlockCommand;
import commands.RemoveBlockCommand;
import domainLayer.elements.ElementType;
import events.GUIListener;
import exceptions.InvalidBlockConnectionException;
import exceptions.InvalidBlockTypeException;
import exceptions.MaxNbOfBlocksReachedException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;

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
	private GameWorld gameWorld;
	private CommandHandler commandHandler;

	/**
	 * Construct a domainController and it's dependencies. - GameController -
	 * BlockController - ElementController
	 * 
	 * @param gameWorld TODO
	 */
	public DomainController(GameWorld gameWorld) {
		CommandHandler handler = new CommandHandler();

		initializeDomainController(new GameController(gameWorld, handler), new BlockController(),
				new ElementController(), gameWorld, handler);

	}

	@SuppressWarnings("unused")
	private DomainController(GameController gameController, BlockController blockController,
			ElementController elementController, GameWorld gameWorld, CommandHandler handler) {
		initializeDomainController(gameController, blockController, elementController, gameWorld, handler);
	}

	private void initializeDomainController(GameController gameController, BlockController blockController,
			ElementController elementController, GameWorld gameWorld, CommandHandler handler) {
		this.gameController = gameController;
		this.blockController = blockController;
		this.elementController = elementController;

		this.blockController.addDomainListener(gameController);
		this.elementController.addDomainListener(gameController);

		this.gameWorld = gameWorld;
		this.commandHandler = handler;

		// fill dynamic enum with actions and predicates from GameWorldApi
		Set<Predicate> supportedPredicates = gameWorld.getType().supportedPredicates();
		Set<Action> supportedActions = gameWorld.getType().supportedActions();

		for (Predicate predicate : supportedPredicates) {
			new BlockType(predicate.toString(), BlockCategory.CONDITION, predicate);
		}

		for (Action action : supportedActions) {
			new BlockType(action.toString(), BlockCategory.ACTION, action);
		}

	}
	
	// Used for mockinjection in the tests
	private DomainController(GameWorld gw, GameController gc, BlockController bc) {
		this.gameWorld = gw;
		this.gameController = gc;
		this.blockController = bc;
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
			System.out.println("DomainController");
			BlockCommand command = new AddBlockCommand(blockController, blockType, connectedBlockId, connection);
			commandHandler.handle(command);
		}
	}

	/**
	 * Removes a block with the given blockID from the domain.
	 * 
	 * @param blockID The blockID of the block to be removed.
	 * @throws IllegalArgumentException      If the given BlockID is null or an
	 *                                       empty String
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
		if (blockID == "" || blockID == null) {
			throw new IllegalArgumentException("No blockType given.");
		} else {
			BlockCommand command = new RemoveBlockCommand(blockController, blockID);
			commandHandler.handle(command);
		}

	}

	
	public void resetGameExecution() {
		gameController.resetGameExecution();
	}
	
	/**
	 * Returns all the blockID's in the body of a given ControlBlock
	 * 
	 * @param blockID The blockID of the controlBlock of which you want to retrieve
	 *                all Blocks in the body.
	 * @throws IllegalArgumentException      Is thrown when the given blockID is
	 *                                       empty or null.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @throws InvalidBlockTypeException     Is thrown when given blockID isn't the
	 *                                       ID of a ControlBlock.
	 * @return A set containing the blockID of the blocks in the body of the given
	 *         ControlBlock.
	 * 
	 */
	public Set<String> getAllBlockIDsInBody(String blockID) {
		if (blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}

		return blockController.getAllBlockIDsInBody(blockID);

	}

	/**
	 * Move a block that has been added in the domain to change the program you are
	 * constructing. If you're program is in a Execution state this action will, if
	 * successful, reset your game state.
	 * 
	 * 
	 * 
	 * @param topOfMovedChainBlockId    The Id of block to be moved, if you move a
	 *                                  chain of blocks this will be the first block
	 *                                  in the chain, this parameter is required.
	 * @param movedBlockId              TODO
	 * @param connectedAfterMoveBlockId The Id of the block you wish to connect the
	 *                                  block you are moving to. This parameter is
	 *                                  Required. If there's no connected block
	 *                                  after the move please use an empty String,
	 *                                  "".
	 * @param connectionAfterMove       The connection of the block you wish to
	 *                                  connect the block you are moving to. This
	 *                                  parameter is Required. If there's no
	 *                                  connected block after the move please use
	 *                                  ConnectionType.NOCONNECTION.
	 * 
	 * 
	 * @throws IllegalArgumentException        This Exception when thrown will
	 *                                         result in a non execution of the
	 *                                         expected changes. This means that the
	 *                                         block you wish to move will not be
	 *                                         modified. This exception is thrown
	 *                                         when; - The Id of the "moved block"
	 *                                         is null or an empty String. - One of
	 *                                         the ConnectionTypes is null or if it
	 *                                         does not exist. - If a "connected
	 *                                         block"-id is either a null or a empty
	 *                                         String and the ConnectionType is not
	 *                                         NOCONNECTION.
	 * 
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
	 * 
	 * @event changeBlockEvent Fires an changeBlockEvent if the execution was
	 *        successful.
	 * @event UpdateGameStateEvent Fires an UpdateGameStateEvent if the execution
	 *        was successful.
	 * @event ResetExecutionEvent Fires a ResetExecutionEvent if the execution was
	 *        successful.
	 * 
	 */
	public void moveBlock(String topOfMovedChainBlockId, String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove) {
		if (topOfMovedChainBlockId == null || topOfMovedChainBlockId.equals("")) {
			throw new IllegalArgumentException("No movedBlockID given");
		}
		if (movedBlockId == null) {
			movedBlockId = "";
		}
		if (connectionAfterMove == null) {
			throw new IllegalArgumentException("Null given as connection, use ConnectionType.NOCONNECTION.");
		} else if (connectedAfterMoveBlockId.equals("") && !(connectionAfterMove == ConnectionType.NOCONNECTION)) {
			throw new IllegalArgumentException("No blockId given for connectedAfterMovedBlockID");
		} else {
			BlockCommand command = new MoveBlockCommand(blockController, topOfMovedChainBlockId, movedBlockId,
					connectedAfterMoveBlockId, connectionAfterMove);
			commandHandler.handle(command);
		}
	}

	/**
	 * Adds a GUI listener for Game, this listener will be notified about all
	 * changes for the GUI. If the given listener is already a listener for Game it
	 * will not be added another time.
	 * 
	 * @param listener The listener to be added.
	 * @throws IllegalArgumentException Is thrown when the given listener is null.
	 */
	public void addGameListener(GUIListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("No listener given.");
		}
		gameController.addListener(listener);
		blockController.addListener(listener);
		elementController.addListener(listener);
	}

	/**
	 * Removes a GUI listener for Game, this listener will no longer be notified
	 * about any changes for the GUI. If the GUI listener is no listener Game it
	 * also won't be removed.
	 * 
	 * @param listener The listener to be added.
	 * @throws IllegalArgumentException Is thrown when the given listener is null.
	 */
	public void removeGameListener(GUIListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("No listener given.");
		}
		gameController.removeListener(listener);
		elementController.removeListener(listener);
		blockController.removeListener(listener);
	}

	
	public void executeBlock() {
		gameController.executeBlock();
	}

	/**
	 * Returns all the BlockID's underneath a certain block
	 * 
	 * @param blockID The blockID of the Block of which you want to retrieve all
	 *                Blocks underneath.
	 * @throws IllegalArgumentException      Is thrown when the given blockID is
	 *                                       empty or null.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A set containing the blockID's of all connected Conditions and every
	 *         kind of block in the body of the given block or under the given
	 *         block. The ID of the block itself is also given.
	 */
	public Set<String> getAllBlockIDsUnderneath(String blockID) {
		if (blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}
		return blockController.getAllBlockIDsUnderneath(blockID);
	}
	
	/**
	 * Add an element to the domain.
	 * 
	 * @param element The type of element.
	 * @param X       The X coordinate
	 * @param Y       The Y coordinate
	 * @throws IllegalArgumentException Is thrown when element is null.
	 * @event ElementAddedEvent When the operation is successful the
	 *        ElementAddedEvent will be thrown to all the listeners.
	 * @event RobotAddedEvent When the operation is successful and elementType is
	 *        robot the RobotAddedEvent with an Orientation of UP will be thrown to
	 *        all the listeners.
	 */
	public void addElement(ElementType element, int x, int y) {
		if (element == null) {
			throw new IllegalArgumentException("No elementType given.");
		} else {
			elementController.addElement(element, x, y);
		}
	}

	/**
	 * 
	 * @param id
	 * @return The ID of the first block below the block with the given ID, returns
	 *         NULL if this block doesn't specify a block below.
	 */
	public String getFirstBlockBelow(String id) {
		if (id == null || id == "") {
			throw new IllegalArgumentException("No blockID given.");
		}
		blockController.getFirstBlockBelow(id);

		return "";
	}

	// TO BE DOCUMENTED:
	public String getEnclosingControlBlock(String id) {
		return blockController.getEnclosingControlBlock(id);
	}

	public Set<String> getAllBlockIDsBelowCertainBlock(String blockID) {
		if (blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}

		return blockController.getAllBlockIDsBelowCertainBlock(blockID);
	}

	public Set<String> getAllHeadControlBlocks() {
		return blockController.getAllHeadControlBlocks();
	}

	public Set<String> getAllHeadBlocks() {
		return blockController.getAllHeadBlocks();
	}

	/**
	 * Paint the gameWorld on a given graphics object.
	 * 
	 * @param gameAreaGraphics
	 */
	public void paint(Graphics gameWorldGraphics) {
		gameWorld.paint(gameWorldGraphics);

	}
	
	public void undo() {
		commandHandler.undo();
	}
	
	public void redo() {
		commandHandler.redo();
	}



}