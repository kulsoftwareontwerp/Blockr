package applicationLayer;

public class BlockAddedEvent implements EventObject {

	private String addedBlockId;

	/**
	 * 
	 * @param addedBlockId
	 */
	public BlockAddedEvent(String addedBlockId) {
		this.addedBlockId=addedBlockId;
	}

}