/**
 * 
 */
package guiLayer.types;

import java.util.TimerTask;

/**
 * MaskedKeyPressed
 * This TimerTask will release a the pressing of a maskedKey 
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MaskedKeyPressed extends TimerTask {
	private MaskedKeyBag bag;
	private Boolean resetBoth;

	/**
	 * Create a new MaskedKeyPressed TimerTask
	 * @param bag a bag with the flags for the maskedKeys
	 * @param resetBoth a flag indicating if both maskedKeys should be released or if just the ctrl maskedKey should be released.
	 */
	public MaskedKeyPressed(MaskedKeyBag bag, Boolean resetBoth) {
		this.bag = bag;
		this.resetBoth = resetBoth;
	}

	@Override
	public void run() {
		if (resetBoth) {
			bag.pressShift(false);
			bag.pressCtrl(false);
		} else {
			bag.pressCtrl(false);
		}
	}

}
