package events;

import java.util.HashSet;
import java.util.Set;

import types.BlockType;
import types.ConnectionType;

/**
 * The event thrown when a block has been added to the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockAddedEvent implements EventObject {

	private String addedBlockID;
	private String linkedBlockID;
	private BlockType type;
	private ConnectionType linkedType;
	private Set<String> changedBlocks;
	private boolean moreRelatedEventsComing;

	/**
	 * Create the blockAddedEvent
	 * 
	 * @param addedBlockID the ID of the block that has been added.
	 * @param linkedBlock The ID of the block that's linked to the addedBlock after the add
	 * @param linkedType The connection on which the added block is connected to the linkedBlock.
	 * @param type The BlockType of the added Block
	 * @param changedBlocks a set with the ID's of all blocks that were involved in this add. 
	 * @param moreRelatedEventsComing signifies that there are still related events comming.
	 */
	public BlockAddedEvent(String addedBlockID, String linkedBlock, ConnectionType linkedType, BlockType type, Set<String> changedBlocks, boolean moreRelatedEventsComing) {
		this.addedBlockID = addedBlockID;
		this.linkedBlockID=linkedBlock;
		this.linkedType=linkedType;
		this.type=type;
		if(changedBlocks==null) {
			changedBlocks=new HashSet<String>();
		}
		this.changedBlocks=changedBlocks;
		this.moreRelatedEventsComing=moreRelatedEventsComing;
	}

	
	
	
	
	
	/**
	 * Retrieve the id's of all blocks that were added during the operation.
	 * @return the changedBlocks a set with all id's of the added blocks
	 */
	public Set<String> getChangedBlocks() {
		return changedBlocks;
	}






	/**
	 * Retrieve the ID of the block that has been added.
	 * 
	 * @return the ID of the block that has been added.
	 */
	public String getAddedBlockID() {
		return addedBlockID;
	}

	/**
	 * Retrieve connectionType linkedType
	 * @return return ConnectionType the linkedType
	 */
	public ConnectionType getLinkedType() {
		return linkedType;
	}
	
	/**
	 * Retrieve the linked blockID
	 * @return return the linkedType
	 */
	public String getLinkedBlockID() {
		return linkedBlockID;
	}

	/**
	 * Retrieve the BlockType of the added block.
	 * @return the BlockType of the added block.
	 */
	public BlockType getAddedBlockType() {
		return type;
	}





	/**
	 * Retrieve if there are more related events coming.
	 * @return if there are more related events coming.
	 */
	public boolean areMoreRelatedEventsComing() {
		return moreRelatedEventsComing;
	}
}