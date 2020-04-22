package commands;

import applicationLayer.BlockController;
import types.BlockSnapshot;

/**
 *  RemoveBlockCommand, The command to remove a block from the domain.
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
	 * Create a new RemoveblockCommand
	 * @param blockController	The blockController to perform the remove on
	 * @param blockIdToBeRemoved The ID of the block to be removed.
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
			BlockSnapshot newSnapshot = new BlockSnapshot(snapshot.getBlock(), snapshot.getConnectedBlockAfterSnapshot(), snapshot.getConnectedBlockBeforeSnapshot(), snapshot.getChangingBlocks());
			this.snapshot=newSnapshot;
			blockController.restoreBlockSnapshot(snapshot, true);
			snapshot = null;
		}
	}

}
