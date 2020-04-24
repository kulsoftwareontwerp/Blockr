/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import types.BlockSnapshot;
import types.ConnectionType;

/**
 * MoveBlockCommand, The command to move a block.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MoveBlockCommand implements BlockCommand {
	private String topOfMovedChainBlockId;
	private String movedBlockId;
	private String connectedAfterMoveBlockId;
	private ConnectionType connectionAfterMove;
	private BlockController blockController;
	private BlockSnapshot snapshot;

	/**
	 * 
	 * @param blockController           The blockController to perform this
	 *                                  MoveBlockCommand on.
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
	 */
	public MoveBlockCommand(BlockController blockController, String topOfMovedChainBlockId, String movedBlockId,
			String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		super();
		this.topOfMovedChainBlockId = topOfMovedChainBlockId;
		this.movedBlockId = movedBlockId;
		this.connectedAfterMoveBlockId = connectedAfterMoveBlockId;
		this.connectionAfterMove = connectionAfterMove;
		this.blockController = blockController;
		this.snapshot = null;
	}

	@Override
	public void execute() {
		snapshot = blockController.moveBlock(topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId,
				connectionAfterMove);

	}

	@Override
	public void undo() {
		if (snapshot != null) {
			snapshot = new BlockSnapshot(snapshot.getBlock(), snapshot.getConnectedBlockAfterSnapshot(),
					snapshot.getConnectedBlockBeforeSnapshot(), snapshot.getChangingBlocks());
			blockController.restoreBlockSnapshot(snapshot, true);
			snapshot = null;
		}
	}

}
