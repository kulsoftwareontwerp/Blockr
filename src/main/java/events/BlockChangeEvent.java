package events;

import java.util.HashSet;
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
	private String topOfMovedChainId;

	private String changedLinkedBlockId;
	private ConnectionType changedConnection;

	private String beforeMoveBlockId;
	private ConnectionType beforeMoveConnection;

	private Set<String> changedBlocks;

	/**
	 * Create the BlockChangeEvent
	 * 
	 * @param changedBlockId       The ID of the block that has been changed.
	 * @param topOfMovedChainId	   The ID of the block on the top of the chain
	 * @param changedLinkedBlockId The new connected block ID
	 * @param changedConnection    The new connection
	 * @param beforeMoveBlockId    The ID of the block to which the changedBlock was connected before the move, "" if the block was not connected to any other block.
	 * @param beforeMoveConnection The connection of the block before the move, NOCONNECTION if there was no connection before the move.
	 * @param changedBlocks 		A set with the id's of all the changed blocks.
	 */
	public BlockChangeEvent(String changedBlockId, String topOfMovedChainId, String changedLinkedBlockId,
			ConnectionType changedConnection, String beforeMoveBlockId, ConnectionType beforeMoveConnection,
			Set<String> changedBlocks) {
		super();
		this.changedBlockId = changedBlockId;
		this.changedLinkedBlockId = changedLinkedBlockId;
		this.changedConnection = changedConnection;
		this.beforeMoveBlockId = beforeMoveBlockId;
		this.beforeMoveConnection = beforeMoveConnection;
		this.topOfMovedChainId = topOfMovedChainId;

		if (changedBlocks == null) {
			changedBlocks = new HashSet<String>();
		}
		this.changedBlocks = changedBlocks;
	}

	/**
	 * Retrieve the id's of all the changed blocks
	 * @return a set containing the id's of all the changed blocks
	 */
	public Set<String> getChangedBlocks() {
		return changedBlocks;
	}

	/**
	 * Retrieve the changed block ID
	 * 
	 * @return The ID of the block that has been changed.
	 */
	public String getChangedBlockId() {
		return changedBlockId;
	}

	/**
	 * Retrieve the ID of the changed linked block
	 * 
	 * @return The ID of the new linked block.
	 */
	public String getChangedLinkedBlockId() {
		return changedLinkedBlockId;
	}

	/**
	 * Retrieve the changed connection.
	 * 
	 * @return The changed connection.
	 */
	public ConnectionType getConnectionType() {
		return changedConnection;
	}

	/**
	 * Retrieve the previous connected block ID. If no blockId was previously
	 * connected returns an empty string.
	 * 
	 * @return the previous connected block ID. If no blockId was previously
	 *         connected returns an empty string.
	 */
	public String getBeforeMoveBlockId() {
		return beforeMoveBlockId;
	}

	/**
	 * Retrieve the previous connectionType. If no previous connection exists,
	 * NOCONNECTION will be returned.
	 * 
	 * @return the previous connectionType. If no previous connection exists,
	 *         NOCONNECTION will be returned.
	 */
	public ConnectionType getBeforeMoveConnection() {
		return beforeMoveConnection;
	}

	/**
	 * Retrieve the ID of the block on the top of the chain.
	 * 
	 * @return the ID of the block on the top of the chain
	 */
	public String getTopOfMovedChainId() {
		return topOfMovedChainId;
	}

}