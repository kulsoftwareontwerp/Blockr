package events;

import applicationLayer.ConnectionType;

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
	
	
	/**
	 * Create the BlockChangeEvent
	 * 
	 * @param 	changedBlockId
	 * 			The ID of the block that has been changed.
	 * @param 	changedLinkedBlockId
	 * 			The new connected block ID
	 * @param 	changedConnection
	 * 			The new connection
	 */
	public BlockChangeEvent(String changedBlockId, String changedLinkedBlockId, ConnectionType changedConnection) {
		super();
		this.changedBlockId = changedBlockId;
		this.changedLinkedBlockId = changedLinkedBlockId;
		this.changedConnection = changedConnection;
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
}