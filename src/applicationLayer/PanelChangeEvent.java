package applicationLayer;

public class PanelChangeEvent implements EventObject {

	private boolean show;

	/**
	 * 
	 * @param show
	 */
	public PanelChangeEvent(boolean show) {
		this.show=show;
	}

	public boolean isShown() {
		return show;
	}

}