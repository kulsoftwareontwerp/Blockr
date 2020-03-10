package events;

public interface DomainListener {

	/**
	 * 
	 * @param event
	 */
	void onResetExecutionEvent(ResetExecutionEvent event);

	/**
	 * 
	 * @param event
	 */
	void onUpdateGameStateEvent(UpdateGameStateEvent event);

}