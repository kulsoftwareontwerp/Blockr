package events;

import java.util.Set;

import types.ConnectionType;

/**
 * The event thrown when a block has been changed in the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockChangeEvent implements EventObject {

	private String changedBlockId;
	private String changedLinkedBlockId;
	private ConnectionType changedConnection;
	private String beforeMoveBlockId;
	private ConnectionType beforeMoveConnection;

	
	



	/**
	 * Create the BlockChangeEvent
	 * 
	 * @param 	changedBlockId
	 * 			The ID of the block that has been changed.
	 * @param 	changedLinkedBlockId
	 * 			The new connected block ID
	 * @param 	changedConnection
	 * 			The new connection
	 * @param beforeMoveBlockId TODO
	 * @param beforeMoveConnection TODO
	 */
	public BlockChangeEvent(String changedBlockId, String changedLinkedBlockId, ConnectionType changedConnection, String beforeMoveBlockId, ConnectionType beforeMoveConnection) {
		super();
		this.changedBlockId = changedBlockId;
		this.changedLinkedBlockId = changedLinkedBlockId;
		this.changedConnection = changedConnection;
		this.beforeMoveBlockId = beforeMoveBlockId;
		this.beforeMoveConnection = beforeMoveConnection;
	}


	/**
	 * Retrieve the changed block ID
	 * @return The ID of the block that has been changed.
	 */
	public String getChangedBlockId() {
		return changedBlockId;
	}

	/**
	 * Retrieve the ID of the changed linked block
	 * @return The ID of the new linked block.
	 */
	public String getChangedLinkedBlockId() {
		return changedLinkedBlockId;
	}

	/**
	 * Retrieve the changed connection.
	 * @return	The changed connection.
	 */
	public ConnectionType getConnectionType() {
		return changedConnection;
	}
	/**
	 * Retrieve the previous connected block ID. If no blockId was previously connected returns an empty string.
	 * @return the previous connected block ID. If no blockId was previously connected returns an empty string.
	 */
	public String getBeforeMoveBlockId() {
		return beforeMoveBlockId;
	}

	/**
	 * Retrieve the previous connectionType. If no previous connection exists, NOCONNECTION will be returned.
	 * @return the previous connectionType. If no previous connection exists, NOCONNECTION will be returned.
	 */
	public ConnectionType getBeforeMoveConnection() {
		return beforeMoveConnection;
	}

}