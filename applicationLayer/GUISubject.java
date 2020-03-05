package applicationLayer;

public interface GUISubject {

	/**
	 * 
	 * @param listener
	 */
	void removeListener(GUIListener listener);

	/**
	 * 
	 * @param listener
	 */
	void addListener(GUIListener listener);

}