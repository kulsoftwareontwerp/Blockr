package events;

/**
 * A GUIlistener resides in the GUI layer and updates the GUI according to events emerging from the domain.
 * 
 * @version 0.1
 * @author group17
 */
public interface GUIListener {

	/**
	 * Perform the needed actions in the GUI after a block has been added to the domain.
	 * @param 	event
	 * 			A BlockAddedEvent
	 */
	void onBlockAdded(BlockAddedEvent event);

	/**
	 * Perform the needed actions in the GUI after a block has been removed from the domain.
	 * @param 	event
	 * 			A BlockRemovedEvent
	 */
	void onBlockRemoved(BlockRemovedEvent event);

	/**
	 * Perform the needed actions in the GUI when a panelChange is requested.
	 * @param 	event
	 * 			A PanelChangeEvent
	 */
	void onPanelChangedEvent(PanelChangeEvent event);

	/**
	 * Perform the needed actions in the GUI after a block has been changed in the domain.
	 * @param 	event
	 * 			A BlockChangeEvent
	 */
	void onBlockChangeEvent(BlockChangeEvent event);

	/**
	 * Perform the needed actions in the GUI after a block has been marked to be highlighted in the domain.
	 * @param 	event
	 * 			A UpdateHighlightingEvent
	 */
	void onUpdateHighlightingEvent(UpdateHighlightingEvent event);

}