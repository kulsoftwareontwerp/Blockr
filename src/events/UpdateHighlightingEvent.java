package events;

/**
 * The event thrown when a new block needs to be highlighted in the GUI.
 * 
 * @version 0.1
 * @author group17
 */
public class UpdateHighlightingEvent implements EventObject {

	private String highlightBlockId;
	
	/**
	 * Create the UpdateHighlightingEvent
	 * 
	 * @param 	highlightBlockId
	 * 			The new block to be highlighted in the GUI.
	 */
	public UpdateHighlightingEvent(String highlightBlockId) {
		super();
		this.highlightBlockId = highlightBlockId;
	}


	/**
	 * Retrieve the ID of the block to be highlighted in the GUI
	 * 
	 * @return the ID of the block to be highlighted in the GUI
	 */
	public String getHighlightBlockId() {
		return this.highlightBlockId;
	}

}