package events;


/**
 * The event thrown when a block has been removed from the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockRemovedEvent implements EventObject {

	private String removedBlockId;

	
	/**
	 * Create the blockRemovedEvent
	 * 
	 * @param 	removedBlockId
	 * 			the ID of the block that has been removed.
	 */
	public BlockRemovedEvent(String removedBlockId) {
		this.removedBlockId = removedBlockId;
	}

	
	/**
	 * Retrieve the ID of the block that has been removed.
	 * 
	 * @return the ID of the block that has been removed.
	 */
	public String getRemovedBlockId() {
		return removedBlockId;
	}
	
	



}