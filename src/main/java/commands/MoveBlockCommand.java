/**
 * 
 */
package commands;

import applicationLayer.BlockController;
import types.BlockSnapshot;
import types.ConnectionType;

/**
 * /** MoveBlockCommand
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
	 * @param topOfMovedChainBlockId
	 * @param movedBlockId
	 * @param connectedAfterMoveBlockId
	 * @param connectionAfterMove
	 */
	public MoveBlockCommand(BlockController blockController, String topOfMovedChainBlockId, String movedBlockId,
			String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		super();
		this.topOfMovedChainBlockId = topOfMovedChainBlockId;
		this.movedBlockId = movedBlockId;
		this.connectedAfterMoveBlockId = connectedAfterMoveBlockId;
		this.connectionAfterMove = connectionAfterMove;
		this.blockController = blockController;
		this.snapshot =null;
	}

	@Override
	public void execute() {
		snapshot = blockController.moveBlock(topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);

	}

	@Override
	public void undo() {
		if(snapshot!=null) {
			blockController.restoreBlockSnapshot(snapshot);
			snapshot = null;
		}
	}

}
