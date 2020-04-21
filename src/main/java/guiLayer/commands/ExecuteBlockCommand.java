/**
 * 
 */
package guiLayer.commands;

import applicationLayer.DomainController;

/**
 * ExecuteBlockCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ExecuteBlockCommand extends GameWorldCommand {
	private DomainController controller;
	private Boolean executed;

	/**
	 * Create a new ExecuteBlockCommand
	 * @param controller The DomainController to perform this command on.
	 */
	public ExecuteBlockCommand(DomainController controller) {
		super(controller);
		this.controller = controller;
		executed = false;
	}

	@Override
	public void execute() {
		if(executed) {
			controller.redo();
		}
		else {
			controller.executeBlock();
			executed =true;
		}
	}

}
