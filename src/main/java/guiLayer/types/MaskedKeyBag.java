/**
 * 
 */
package guiLayer.types;

/**
/**
 * MaskedKeyBag, a dataBag containing booleans regarding state of a maskedKey being pressed or not.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MaskedKeyBag {
	private boolean shift;
	private boolean ctrl;

	/**
	 * Create a new MaskedKeyDataBag
	 * @param shift the initial value indicating if shift is pressed
	 * @param ctrl the initial value indicating if control is pressed
	 */
	public MaskedKeyBag(Boolean shift, Boolean ctrl) {
		super();
		this.shift = shift;
		this.ctrl = ctrl;
	}
	/**
	 * Is the shift key pressed
	 * @return a flag indicating if shift is pressed
	 */
	public synchronized boolean isShiftPressed() {
		return shift;
	}
	
	/**
	 * Is the control key pressed
	 * @return a flag indicating if control is pressed
	 */
	public synchronized boolean isCtrlPressed() {
		return ctrl;
	}
	/**
	 * Set the value if shift is pressed or not
	 * @param shift if true shift is pressed, if false shift is released
	 */
	public synchronized void pressShift(boolean shift) {
		this.shift = shift;
	}
	/**
	 * Set the value if control is pressed or not
	 * @param ctrl if true control is pressed, if false control is released
	 */
	public synchronized void pressCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}
}
