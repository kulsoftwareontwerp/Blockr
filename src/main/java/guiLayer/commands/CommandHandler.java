/**
 * 
 */
package guiLayer.commands;

import java.util.Stack;

import guiLayer.CanvasWindow;
import guiLayer.shapes.Shape;

/**
 * /** CommandHandler
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CommandHandler {
	private Stack<Command> executedCommands;
	private Stack<Command> undoneCommands;

	private BlockCommand currentlyHandledBlockCommand;
	private CanvasWindow canvas;

	/**
	 * Create a CommandHandler
	 * 
	 * @param canvas The canvas object that calls this commandHandler.
	 */
	public CommandHandler(CanvasWindow canvas) {
		executedCommands = new Stack<Command>();
		undoneCommands = new Stack<Command>();
		this.canvas = canvas;
	}

	// For testing purposes
	CommandHandler(CanvasWindow canvas, Stack<Command> executedCommands,
			Stack<Command> undoneCommands, BlockCommand currentlyHandledBlockCommand) {
		this.canvas = canvas;
		this.currentlyHandledBlockCommand = currentlyHandledBlockCommand;
	}

	/**
	 * Execute a BlockCommand and put it on the stack, all undone BlockCommands will
	 * be cleared as well as all GameWorldCommands, executed and undone alike.
	 * 
	 * @param command the blockCommand to handle
	 */
	public void handle(BlockCommand command) {
		// no undo operations for gameWorld possible anymore
		if (!(command instanceof GuiMoveCommand)) {
			clearAllGameWorldCommands();
			undoneCommands.clear();
		}
		currentlyHandledBlockCommand = command;
		command.execute();
		currentlyHandledBlockCommand = null;
		executedCommands.push(command);
	}

	void clearAllGameWorldCommands() {
		executedCommands.removeIf(s -> s instanceof GameWorldCommand);
		undoneCommands.removeIf(s -> s instanceof GameWorldCommand);
	}

	/**
	 * Execute a GameWorldCommand and put it on the stack, all undone
	 * GameWorldCommands will be cleared.
	 * 
	 * @param command the GameWorldCommand to execute.
	 */
	public void handle(GameWorldCommand command) {
		undoneCommands.clear();
		command.execute();
		executedCommands.push(command);
	}

	/**
	 * Set all the unset ID's associated with the currently handled command.
	 * 
	 * @param ID The ID to set the unset ID's to.
	 */
	public void setAddedId(String ID) {
		if (currentlyHandledBlockCommand != null) {
			currentlyHandledBlockCommand.setAddedID(ID);
		}
	}

	/**
	 * Set the saved height after the action for the given ID to the given height
	 * 
	 * @param ID     The id to set the saved height for.
	 * @param height the height to set the current ID to.
	 */
	public void setHeight(String ID, int height) {
		if (currentlyHandledBlockCommand != null) {
			currentlyHandledBlockCommand.setAfterActionHeight(ID, height);
		}
	}

	/**
	 * Undo a command.
	 */
	public void undo() {
		if (executedCommands.size() != 0) {
			Command c = executedCommands.pop();
			if (c instanceof BlockCommand) {
				canvas.setUndoMode(true);
				currentlyHandledBlockCommand = (BlockCommand) c;
				if (c instanceof DomainMoveCommand) {
					clearAllGameWorldCommands();
				}
			}
			c.undo();
			
			undoneCommands.push(c);
			currentlyHandledBlockCommand = null;
			canvas.setUndoMode(false);
		}
	}

	/**
	 * Redo an undone command.
	 */
	public void redo() {
		if (undoneCommands.size() != 0) {
			Command c = undoneCommands.pop();
			if (c instanceof BlockCommand) {
				currentlyHandledBlockCommand = (BlockCommand) c;
				if (c instanceof DomainMoveCommand) {
					clearAllGameWorldCommands();
				}
			}
			c.execute();
			
			executedCommands.push(c);
			currentlyHandledBlockCommand = null;
		}
	}

	public void addShapeToBeforeSnapshot(Shape shape) {
		if (currentlyHandledBlockCommand != null) {
			currentlyHandledBlockCommand.addShapeToBeforeSnapshot(shape);
		} 
	}

}
