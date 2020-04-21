/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;

/**
 * AddBlockCommand, The command to add a block.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class AddBlockCommand implements BlockCommand {
	private BlockController blockController;
	private BlockType blockType;
	private String connectedBlockId;
	private ConnectionType connection;
	private BlockSnapshot snapshot;

	/**
	 * Create a new addBlockCommand
	 * 
	 * @param blockController  The blockController to perform the add on
	 * @param type             The type of the block to add
	 * @param connectedBlockId The Id of the block to connect the added block to, an
	 *                         empty string if the block won't be connected to any
	 *                         other block on add.
	 * @param connection       The connection to connect the added block to on the
	 *                         connectedBlock, NOCONNECTION if the added block won't
	 *                         be connected to any other block.
	 */
	public AddBlockCommand(BlockController blockController, BlockType type, String connectedBlockId,
			ConnectionType connection) {
		super();
		this.blockController = blockController;
		this.blockType = type;
		this.connectedBlockId = connectedBlockId;
		this.connection = connection;
	}

	@Override
	public void execute() {
		if (snapshot == null) {
			snapshot = blockController.addBlock(blockType, connectedBlockId, connection);
		} else {
			blockController.restoreBlockSnapshot(snapshot, false);
		}
	}

	@Override
	public void undo() {
		blockController.removeBlock(snapshot.getBlock().getBlockId(), false);
	}

}
