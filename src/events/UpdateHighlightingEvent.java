package events;

public class UpdateHighlightingEvent implements EventObject {

	private String highlightBlockId;

	/**
	 * 
	 * @param highlightBlockId
	 */
	public UpdateHighlightingEvent(String highlightBlockId) {
		// TODO - implement UpdateHighlightingEvent.UpdateHighlightingEvent
		throw new UnsupportedOperationException();
	}

	public String getHighlightBlockId() {
		return this.highlightBlockId;
	}

}