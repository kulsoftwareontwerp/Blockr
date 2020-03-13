package events;

/**
 * A GUISubject maintains it's list of GUIListeners and fires the DomainEvents.
 * 
 * @version 0.1
 * @author group17
 */
public interface GUISubject {

	/**
	 * Add a GUIListener
	 * @param 	listener
	 * 			The listener to be added.
	 */
	void removeListener(GUIListener listener);

	/**
	 * Remove a GUIListener
	 * @param 	listener
	 * 			The listener to be removed.
	 */
	void addListener(GUIListener listener);

}