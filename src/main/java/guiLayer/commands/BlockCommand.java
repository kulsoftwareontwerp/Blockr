package guiLayer.commands;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.GuiSnapshot;

public abstract class BlockCommand implements Command, Constants {
	private CanvasWindow canvas;
	private GuiSnapshot beforeSnapshot;
	private GuiSnapshot afterSnapshot;

	/**
	 * @param canvas
	 * @param beforeSnapshot
	 * @param afterSnapshot
	 */
	public BlockCommand(CanvasWindow canvas, GuiSnapshot beforeSnapshot, GuiSnapshot afterSnapshot) {
		super();
		if (canvas == null) {
			throw new IllegalArgumentException("A BlockCommand needs a CanvasWindow.");
		}
		this.canvas = canvas;
		this.beforeSnapshot = beforeSnapshot;
		this.afterSnapshot = afterSnapshot;
	}

	/**
	 * If there are unset ID's in any snapshot associated with this command these
	 * will be replaced with the given ID.
	 * 
	 * @param ID The id to set the unset ID's to.
	 */
	public void setAddedID(String ID) {
		if (beforeSnapshot != null) {
			beforeSnapshot.setID(ID);
		}

		if (afterSnapshot != null) {
			afterSnapshot.setID(ID);
		}
	}
	
	
	
	

	@Override
	public void execute() {
		canvas.setCurrentSnapshot(afterSnapshot);

	}

	@Override
	public void undo() {
		canvas.setCurrentSnapshot(beforeSnapshot);
	}

}
