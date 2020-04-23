/**
 * 
 */
package guiLayer.commands;

import guiLayer.CanvasWindow;
import guiLayer.types.GuiSnapshot;

/**
/**
 * GuiMoveCommand
 * 
 * @version 0.1
 * @author group17
 *
 */
public class GuiMoveCommand extends BlockCommand {
	private CanvasWindow canvas;
	
	/**
	 * Create a new GuiMoveCommand
	 * @param canvas the canvas to perform this command on
	 * @param beforeSnapshot The snapshot before this command was executed.
	 * @param afterSnapshot The snapshot after this command was executed.
	 */
	public GuiMoveCommand(CanvasWindow canvas, GuiSnapshot beforeSnapshot, GuiSnapshot afterSnapshot) {
		super(canvas, beforeSnapshot, afterSnapshot);
		this.canvas=canvas;
	}

	@Override
	public void execute() {
		super.execute();
		canvas.placeShapes();
		canvas.setCurrentSnapshot(null);
	}

	@Override
	public void undo() {
		super.undo();
		canvas.placeShapes();
		canvas.setCurrentSnapshot(null);
	}
	
	
	

}
