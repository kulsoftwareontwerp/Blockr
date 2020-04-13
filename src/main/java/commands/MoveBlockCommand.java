/**
 * 
 */
package commands;

import applicationLayer.BlockController;
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
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
