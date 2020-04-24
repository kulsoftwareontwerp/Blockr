package events;

import java.util.HashSet;
import java.util.Set;

import types.ConnectionType;

/**
 * The event thrown when a block has been removed from the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockRemovedEvent implements EventObject {

	private String removedBlockId;
	private String beforeRemoveBlockId;
	private ConnectionType beforeRemoveConnection;
	private Set<String> changedBlocks;

	/**
	 * Create the blockRemovedEvent
	 * 
	 * @param removedBlockId         the ID of the block that has been removed.
	 * @param beforeRemoveBlockId    The block on which the removed block was
	 *                               connected before it was removed.
	 * @param beforeRemoveConnection The connection on which the removed block was
	 *                               connected before it was removed.
	 * @param changedBlocks 		 The id's of all blocks associated with the remove operation.
	 */
	public BlockRemovedEvent(String removedBlockId, String beforeRemoveBlockId, ConnectionType beforeRemoveConnection, Set<String> changedBlocks) {
		this.removedBlockId = removedBlockId;
		this.beforeRemoveBlockId=beforeRemoveBlockId;
		this.beforeRemoveConnection=beforeRemoveConnection;
		if(changedBlocks==null) {
			changedBlocks=new HashSet<String>();
		}
		this.changedBlocks=changedBlocks;
	}
	
	/**
	 * Retrieve the id's of all blocks associated with the remove operation
	 * @return a set with the id's of all blocks associated with the remove operation.
	 */
	public Set<String> getChangedBlocks() {
		return changedBlocks;
	}

	/**
	 * Retrieve the ID of the block that has been removed.
	 * 
	 * @return the ID of the block that has been removed.
	 */
	public String getRemovedBlockId() {
		return removedBlockId;
	}

	/**
	 * Retrieve the id of the previously connected block, returns an empty string if
	 * there was no connected block.
	 * 
	 * @return the id of the previously connected block, returns an empty string if
	 *         there was no connected block.
	 */
	public String getBeforeRemoveBlockId() {
		return beforeRemoveBlockId;
	}

	/**
	 * Retrieve the connection on which the removed block was connected to the
	 * previously connected block. Returns NOCONNECTION if there was no connected
	 * block.
	 * 
	 * @return the connection on which the removed block was connected to the
	 *         previously connected block. Returns NOCONNECTION if there was no
	 *         connected block.
	 */
	public ConnectionType getBeforeRemoveConnection() {
		return beforeRemoveConnection;
	}

}