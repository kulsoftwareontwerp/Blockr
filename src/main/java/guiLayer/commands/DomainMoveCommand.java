/**
 * 
 */
package guiLayer.commands;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;
import guiLayer.types.GuiSnapshot;

/**
 * DomainMoveCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class DomainMoveCommand extends BlockCommand {
	private CanvasWindow canvas;
	private DomainController controller;
	private Boolean executed;

	/**
	 * Create a new DomainMoveCommand
	 * @param controller The domainController to perform the command on.
	 * @param canvas The canvasWindow associated with this command
	 * @param beforeSnapshot The snapshot before this command was executed
	 * @param afterSnapshot The snapshot after this command was executed.
	 */
	public DomainMoveCommand(DomainController controller, CanvasWindow canvas, GuiSnapshot beforeSnapshot,
			GuiSnapshot afterSnapshot) {
		super(canvas, beforeSnapshot, afterSnapshot);
		if (controller == null) {
			throw new IllegalArgumentException("A DomainMoveCommand needs a DomainController.");
		}
		this.controller = controller;
		this.canvas = canvas;
		executed = false;
	}

	@Override
	public void execute() {
		super.execute();
		if (executed) {
			controller.redo();
		} else {
			executed = true;
		}
	}

	@Override
	public void undo() {
		super.undo();
		controller.undo();
		canvas.setCurrentSnapshot(null);
	}

}
