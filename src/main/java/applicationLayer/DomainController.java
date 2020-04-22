package applicationLayer;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import commands.AddBlockCommand;
import commands.BlockCommand;
import commands.CommandHandler;
import commands.MoveBlockCommand;
import commands.RemoveBlockCommand;
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

	private GameWorld gameWorld;
	private CommandHandler commandHandler;

	private void initializeDomainController(GameController gameController, BlockController blockController,
			GameWorld gameWorld, CommandHandler handler) {
		this.gameController = gameController;
		this.blockController = blockController;

		this.blockController.addDomainListener(gameController);

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

	/**
	 * Construct a domainController and it's dependencies. 
	 * The commandHandler,  GameController and the BlockController
	 * 
	 * @param gameWorld The GameWorld to work with.
	 */
	public DomainController(GameWorld gameWorld) {
		CommandHandler handler = new CommandHandler();
		initializeDomainController(new GameController(gameWorld, handler), new BlockController(), gameWorld, handler);
	}
	
	// Used for mockinjection in the tests
	@SuppressWarnings("unused")
	private DomainController(GameWorld gw, GameController gc, BlockController bc, CommandHandler ch) {
		this.gameWorld = gw;
		this.gameController = gc;
		this.blockController = bc;
		this.commandHandler = ch;
	}	

	@SuppressWarnings("unused")
	private DomainController(GameController gameController, BlockController blockController, GameWorld gameWorld,
			CommandHandler handler) {
		initializeDomainController(gameController, blockController, gameWorld, handler);
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
	 * Move a block that has been added in the domain to change the program you are
	 * constructing. If you're program is in a Execution state this action will, if
	 * successful, reset your game state.
	 * 
	 * 
	 * 
	 * @param topOfMovedChainBlockId    The Id of block to be moved, if you move a
	 *                                  chain of blocks this will be the first block
	 *                                  in the chain, this parameter is required.
	 * @param movedBlockId              The Id of block that's actually being moved,
	 *                                  this might be the same as the
	 *                                  topOfMovedChainBlockId, if the movedBlockId
	 *                                  is empty the topOfMovedChainBlockId will be
	 *                                  used in any way.
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

	/**
	 * Executes the next block to be executed if the gamestate is in a valid state or an in execution state. 
	 * 	If this is not the case, nothing happens.
	 * 
	 * @event UpdateHighlightingEvent
	 * 		  Fires a UpdateHighlightingEvent if the program was in a valid state or an in executing state.
	 */
	public void executeBlock() {
		gameController.executeBlock();
	}

	/**
	 * Resets the game execution.
	 * 
	 * @event UpdateHighlightingEvent
	 * 		  Fires a UpdateHighlightingEvent if the program was in an executing state.
	 */
	public void resetGameExecution() {
		gameController.resetGameExecution();
	}

	/**
	 * Paint the gameWorld on a given graphics object.
	 * 
	 * @param gameWorldGraphics The graphics object to paint the gameWorld on.
	 */
	public void paint(Graphics gameWorldGraphics) {
		gameWorld.paint(gameWorldGraphics);

	}

	/**
	 * Undo the last executed domain command
	 */
	public void undo() {
		commandHandler.undo();
	}

	/**
	 * Redo the last undone domain command
	 */
	public void redo() {
		commandHandler.redo();
	}

	/**
	 * Retrieve a set containing the id's of all headBlocks
	 * 
	 * @return a set with the id's of all the headblocks
	 */
	public Set<String> getAllHeadBlocks() {
		return blockController.getAllHeadBlocks();
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
	 * Returns all the BlockID's below a certain block
	 * 
	 * @param blockID The blockID of the Block of which you want to retrieve all
	 *                Blocks below.
	 * @throws IllegalArgumentException      Is thrown when the given blockID is
	 *                                       empty or null.
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A set containing the blockID's of every kind of block under the given
	 *         block. The ID of the block itself is also given.
	 */
	public Set<String> getAllBlockIDsBelowCertainBlock(String blockID) {
		if (blockID == null || blockID == "") {
			throw new IllegalArgumentException("No blockID given.");
		}

		return blockController.getAllBlockIDsBelowCertainBlock(blockID);
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
	 * Check if the connection is open and can be used to perform a move or add on.
	 * 
	 * @param blockToCheck   The id of the block to check the connection from
	 * @param connection     The connection to check on the given block
	 * @param changingBlocks A set with the id's of all blocks that are changing at
	 *                       the moment as to keep in measure that if the
	 *                       blockToCheck is connected to one of the blocks in this
	 *                       set that connection will be removed after the operation
	 *                       and hence can be ignored. If this parameter is null an
	 *                       empty set will be used and the method won't keep in
	 *                       mind any possible changed blocks.
	 * @throws IllegalArgumentException      when the blockToCheck is null
	 * @throws IllegalArgumentException      when the Connection is null
	 * @throws NoSuchConnectedBlockException Is thrown when a blockID is given that
	 *                                       is not present in the domain.
	 * @return A flag indicating if the given connection for the given block is
	 *         open.
	 */
	public Boolean checkIfConnectionIsOpen(String blockToCheck, ConnectionType connection, Set<String> changingBlocks) {
		if (blockToCheck == null || blockToCheck == "") {
			throw new IllegalArgumentException("No BlockID to check given.");
		}
		if (connection == null) {
			throw new IllegalArgumentException("No connection to check given.");
		}
		if (changingBlocks == null) {
			changingBlocks = new HashSet<String>();
		}

		return blockController.checkIfConnectionIsOpen(blockToCheck, connection, changingBlocks);
	}

	/**
	 * Retrieve the blockType of the block associated with the given id.
	 * 
	 * @param id The id of the block to retrieve the Blocktype from.
	 * @return the blockType associated with the given block
	 */
	public BlockType getBlockType(String id) {
		if (id == null || id == "") {
			throw new IllegalArgumentException("No blockID given.");
		}
		return blockController.getBlockType(id);
	}

	/**
	 * Check if the given id is present in the domain.
	 * 
	 * @param id the id to check
	 * @return a flag indication if a block is present in the domain.
	 */
	public boolean isBlockPresent(String id) {
		if (id == null) {
			return false;
		}
		return blockController.isBlockPresent(id);
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
		blockController.removeListener(listener);
	}

	/**
	 * Is it useful to perform an execution step at the moment. An execution step is
	 * useful if it changes anything, otherwise it's just a waste of time and
	 * resources.
	 * 
	 * @return if it's useful to perform an execution step at the moment.
	 */
	public boolean isGameExecutionUseful() {
		return gameController.isGameExecutionUseful();
	}

	/**
	 * Is it useful to perform a reset of the gameWorld at the moment. A reset of
	 * the gameWorld is useful if it changes anything, otherwise it's just a waste
	 * of time and resources.
	 * 
	 * @return if it's useful to perform a reset of the gameWorld at the moment.
	 */
	public boolean isGameResetUseful() {
		return gameController.isGameResetUseful();
	}

}