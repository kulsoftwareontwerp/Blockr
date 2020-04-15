/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import domainLayer.blocks.Block;
import types.BlockSnapshot;

/**
 * /** RemoveBlockCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class RemoveBlockCommand implements BlockCommand {
	private BlockController blockController;
	private String blockIdToBeRemoved;
	private BlockSnapshot snapshot;

	/**
	 * @param blockIdToBeRemoved
	 */
	public RemoveBlockCommand(BlockController blockController, String blockIdToBeRemoved) {
		super();
		this.blockController = blockController;
		this.blockIdToBeRemoved = blockIdToBeRemoved;
		snapshot = null;
	}

	@Override
	public void execute() {
		snapshot = blockController.removeBlock(blockIdToBeRemoved, true);
	}

	@Override
	public void undo() {
		if (snapshot != null) {
			BlockSnapshot newSnapshot = new BlockSnapshot(snapshot.getBlock(), snapshot.getConnectedBlockAfterSnapshot(), snapshot.getConnectedBlockBeforeSnapshot());
			this.snapshot=newSnapshot;
			blockController.restoreBlockSnapshot(snapshot, true);
			snapshot = null;
		}
	}

}
