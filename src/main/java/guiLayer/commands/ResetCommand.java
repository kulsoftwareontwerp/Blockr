/**
 * 
 */
package guiLayer.commands;

import applicationLayer.DomainController;

/**
/**
 * ResetCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ResetCommand extends GameWorldCommand {
	
	private DomainController controller;
	private Boolean executed;

	/**
	 * Create a new ResetCommand
	 * @param controller The controller to perform this command on.
	 */
	public ResetCommand(DomainController controller) {
		super(controller);
		this.controller=controller;
		executed=false;
	}

	@Override
	public void execute() {
		if(executed) {
			controller.redo();
		}
		else {
			controller.resetGameExecution();
			executed =true;
		}
	}

}
