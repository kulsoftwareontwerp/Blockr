package events;

/**
 * A DomainSubject maintains it's list of DomainListeners and fires the DomainEvents.
 * 
 * @version 0.1
 * @author group17
 */
public interface DomainSubject {

	/**
	 * Add a DomainListener
	 * @param 	listener
	 * 			The listener to be added.
	 */
	void addDomainListener(DomainListener listener);

	/**
	 * Remove a DomainListener.
	 * @param 	listener
	 * 			The listener to be removed.
	 */
	void removeDomainListener(DomainListener listener);

}