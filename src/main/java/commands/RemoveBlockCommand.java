/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import domainLayer.blocks.Block;
import types.BlockSnapshot;

/**
/**
 * RemoveBlockCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class RemoveBlockCommand implements BlockCommand {
	private BlockController blockController;
	private String blockIdToBeRemoved;
	private BlockSnapshot deletedBlockSnapshot;
	


	/**
	 * @param blockIdToBeRemoved
	 */
	public RemoveBlockCommand(BlockController blockController, String blockIdToBeRemoved) {
		super();
		this.blockController = blockController;
		this.blockIdToBeRemoved = blockIdToBeRemoved;
		deletedBlockSnapshot= null;
	}

	@Override
	public void execute() {
		if(deletedBlockSnapshot==null) {
			deletedBlockSnapshot = blockController.removeBlock(blockIdToBeRemoved, true);
		}
		

	}

	@Override
	public void undo() {
		

	}

}
