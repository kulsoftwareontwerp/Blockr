/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import types.BlockType;
import types.ConnectionType;

/**
/**
 * AddBlockCommand
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
	private String addedBlockId;
	

	
	
	/**
	 * @param blockController
	 * @param type
	 * @param connectedBlockId
	 * @param connection
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
		addedBlockId = blockController.addBlock(blockType, connectedBlockId, connection);
	}

	@Override
	public void undo() {
		blockController.removeBlock(addedBlockId, false);
	}

}
