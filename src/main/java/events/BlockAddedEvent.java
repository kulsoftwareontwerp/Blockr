package events;

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

	/**
	 * Create the blockAddedEvent
	 * 
	 * @param addedBlockID the ID of the block that has been added.
	 * @param linkedBlock TODO
	 * @param linkedType TODO
	 * @param type TODO
	 */
	public BlockAddedEvent(String addedBlockID, String linkedBlock, ConnectionType linkedType, BlockType type) {
		this.addedBlockID = addedBlockID;
		this.linkedBlockID=linkedBlock;
		this.linkedType=linkedType;
		this.type=type;
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
}