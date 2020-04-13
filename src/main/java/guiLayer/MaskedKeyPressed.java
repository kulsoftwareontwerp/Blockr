/**
 * 
 */
package guiLayer;

import java.util.TimerTask;

/**
 * /** MaskedKeyPressed
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MaskedKeyPressed extends TimerTask {
	private MaskedKeyBag bag;
	private Boolean resetBoth;
	

	/**
	 * 
	 */
	public MaskedKeyPressed(MaskedKeyBag bag,Boolean resetBoth) {
		this.bag=bag;
		this.resetBoth = resetBoth;
	}

	@Override
	public void run() {
		if(resetBoth) {
		bag.setShift(false);
		bag.setCtrl(false);
		}
		else {
			bag.setCtrl(false);
		}
	}

}
