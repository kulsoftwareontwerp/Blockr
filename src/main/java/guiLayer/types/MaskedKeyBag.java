/**
 * 
 */
package guiLayer.types;

/**
/**
 * MaskedKeyBag
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MaskedKeyBag {
	private boolean shift;
	private boolean ctrl;

	/**
	 * @param shift
	 * @param ctrl
	 */
	public MaskedKeyBag(Boolean shift, Boolean ctrl) {
		super();
		this.shift = shift;
		this.ctrl = ctrl;
	}
	public synchronized boolean getShift() {
		return shift;
	}
	public synchronized boolean getCtrl() {
		return ctrl;
	}
	
	public synchronized void setShift(boolean shift) {
		this.shift = shift;
	}
	public synchronized void setCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}
}
