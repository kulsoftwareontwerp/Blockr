package events;


/**
 * The event thrown when a panelChange is requested.
 * 
 * @version 0.1
 * @author group17
 */
public class PanelChangeEvent implements EventObject {

	private boolean show;

	/**
	 * Create the PanelChangeEvent
	 * @param show the panel?
	 */
	public PanelChangeEvent(boolean show) {
		this.show=show;
	}

	/**
	 * Request if panel needs to be shown
	 * @return	show the panel?
	 */
	public boolean isShown() {
		return show;
	}

}