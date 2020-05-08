package commands;

import java.util.HashSet;
import java.util.Set;

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
			Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
			for(BlockSnapshot s:snapshot.getAssociatedSnapshots()) {
				associatedSnapshots.add(new BlockSnapshot(s.getBlock(), s.getConnectedBlockAfterSnapshot(), s.getConnectedBlockBeforeSnapshot(), s.getChangingBlocks(), s.getAssociatedSnapshots()));
			}
			
			BlockSnapshot newSnapshot = new BlockSnapshot(snapshot.getBlock(), snapshot.getConnectedBlockAfterSnapshot(), snapshot.getConnectedBlockBeforeSnapshot(), snapshot.getChangingBlocks(), associatedSnapshots);
			this.snapshot=newSnapshot;
			blockController.restoreBlockSnapshot(snapshot, true);
			snapshot = null;
		}
	}

}
