package events;

import applicationLayer.ConnectionType;

public class BlockChangeEvent implements EventObject {

	private String changedBlockId;
	private String changedLinkedBlockId;
	private ConnectionType connectionType;

	/**
	 * 
	 * @param changedBlockId
	 * @param changedLinkedBlockId
	 * @param connectionType
	 */
	public BlockChangeEvent(String changedBlockId, String changedLinkedBlockId, ConnectionType connectionType) {
		// TODO - implement BlockChangeEvent.BlockChangeEvent
		throw new UnsupportedOperationException();
	}

}