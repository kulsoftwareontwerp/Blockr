/**
 * 
 */
package guiLayer.types;

import java.util.TimerTask;

import guiLayer.CanvasWindow;

/**
/**
 * AlertTask, announce a message on a window.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class AlertTask extends TimerTask {
	private CanvasWindow window;

	
	/**
	 * Create a new AlertTask with the given window and message.
	 * @param window The window to draw the message on
	 * @param message The message to show
	 */
	public AlertTask(CanvasWindow window,String message) {
		if(window == null) {
			throw new NullPointerException("This task needs a window to anounce a message on.");
		}
		window.showAlert(message);
		this.window=window;
		
	}

	@Override
	public void run() {
		window.showAlert(null);

	}

}
