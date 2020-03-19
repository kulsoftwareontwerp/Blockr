package events;
/**
 * A DomainListener is a listener that's able to update the domain
 * and implements actions corresponding to events emerging from the domain.
 * 
 * @version 0.1
 * @author group17
 */
public interface DomainListener {

	/**
	 * Performs the actions required when a reset is requested from within the domain.
	 * @param 	event
	 * 			The ResetExecutionEvent.
	 *			
	 */
	void onResetExecutionEvent(ResetExecutionEvent event);

	/**
	 * Performs the actions required when a reset is requested from within the domain.
	 * @param 	event
	 * 			The UpdateGameStateEvent.
	 */
	void onUpdateGameStateEvent(UpdateGameStateEvent event);

}