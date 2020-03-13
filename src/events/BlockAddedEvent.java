package events;

/**
 * The event thrown when a block has been added to the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockAddedEvent implements EventObject {

	private String addedBlockID;

	/**
	 * Create the blockAddedEvent
	 * 
	 * @param 	addedBlockID
	 * 			the ID of the block that has been added.
	 */
	public BlockAddedEvent(String addedBlockID) {
		this.addedBlockID=addedBlockID;
	}

	
	/**
	 * Retrieve the ID of the block that has been added.
	 * 
	 * @return the ID of the block that has been added.
	 */
	public String getAddedBlockID() {
		return addedBlockID;
	}
	
	

}