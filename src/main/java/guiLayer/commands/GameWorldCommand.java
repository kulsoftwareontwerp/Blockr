/**
 * 
 */
package guiLayer.commands;

import applicationLayer.DomainController;

/**
/**
 * GameWorldCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public abstract class GameWorldCommand implements Command {
	private DomainController controller;

	/**
	 * Create a GameWorldCommand to tell the domain that an operation in the gameWorld needs to be done or undone.
	 * @param controller The controller to perform the command on.
	 */
	public GameWorldCommand(DomainController controller) {
		super();
		if(controller==null) {
			throw new IllegalArgumentException("A GameWorldCommand needs a DomainController.");
		}
		this.controller = controller;
		
	}

	@Override
	public abstract void execute();

	@Override
	public void undo() {
		controller.undo();
	}

}
