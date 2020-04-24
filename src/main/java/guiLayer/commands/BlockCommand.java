package guiLayer.commands;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.GuiSnapshot;

public abstract class BlockCommand implements Command, Constants {
	private CanvasWindow canvas;
	private GuiSnapshot beforeSnapshot;
	private GuiSnapshot afterSnapshot;

	/**
	 * Create a new BlockCommand
	 * @param canvas the canvas to perform this command on
	 * @param beforeSnapshot The snapshot before this command was executed.
	 * @param afterSnapshot The snapshot after this command was executed.
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
	
	
	/**
	 * Set the saved height after the action for the given ID to the given height
	 * @param ID The id to set the saved height for.
	 * @param height the height to set the current ID to.
	 */
	public void setAfterActionHeight(String ID, int height) {
		if(afterSnapshot!=null) {
			afterSnapshot.setHeight(ID, height);
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
