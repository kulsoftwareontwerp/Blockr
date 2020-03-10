package events;

public interface GUIListener {

	/**
	 * 
	 * @param event
	 */
	void onBlockAdded(BlockAddedEvent event);

	/**
	 * 
	 * @param event
	 */
	void onBlockRemoved(BlockRemovedEvent event);

	/**
	 * 
	 * @param event
	 */
	void onPanelChangedEvent(PanelChangeEvent event);

	/**
	 * 
	 * @param event
	 */
	void onBlockChangeEvent(BlockChangeEvent event);

	/**
	 * 
	 * @param event
	 */
	void onUpdateHighlightingEvent(UpdateHighlightingEvent event);

	/**
	 * 
	 * @param event
	 */
	void onRobotChangeEvent(RobotChangeEvent event);

	/**
	 * 
	 * @param event
	 */
	void onRobotAddedEvent(RobotAddedEvent event);

	/**
	 * 
	 * @param event
	 */
	void onElementAddedEvent(ElementAddedEvent event);

}