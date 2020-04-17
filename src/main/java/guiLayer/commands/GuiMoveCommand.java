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
	 * @param canvas
	 * @param beforeSnapshot
	 * @param afterSnapshot
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
