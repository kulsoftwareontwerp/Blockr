/**
 * 
 */
package guiLayer.commands;

import java.util.Stack;

/**
 * /** CommandHandler
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CommandHandler {
	private Stack<BlockCommand> executedBlockCommands;
	private Stack<BlockCommand> undoneBlockCommands;
	private Stack<GameWorldCommand> executedGameWorldCommands;
	private Stack<GameWorldCommand> undoneGameWorldCommands;
	private BlockCommand currentlyHandledBlockCommand;

	public CommandHandler() {
		executedBlockCommands=new Stack<BlockCommand>();
		undoneBlockCommands=new Stack<BlockCommand>();
		executedGameWorldCommands=new Stack<GameWorldCommand>();
		undoneGameWorldCommands=new Stack<GameWorldCommand>();
	}

	public void handle(BlockCommand command) {
		//no undo operations for gameWorld possible anymore
		executedGameWorldCommands.clear();
		undoneGameWorldCommands.clear();
		undoneBlockCommands.clear();
		currentlyHandledBlockCommand=command;
		command.execute();
		currentlyHandledBlockCommand=null;
		executedBlockCommands.push(command);
		
	}

	public void handle(GameWorldCommand command) {
		undoneGameWorldCommands.clear();
		command.execute();
		executedGameWorldCommands.push(command);
	}
	
	/**
	 * Set all the unset ID's associated with the currently handled command.
	 * @param ID The ID to set the unset ID's to.
	 */
	public void setAddedId(String ID) {
		if(currentlyHandledBlockCommand!=null) {
			currentlyHandledBlockCommand.setAddedID(ID);
		}
	}
	
	/**
	 * Set the saved height after the action for the given ID to the given height
	 * @param ID The id to set the saved height for.
	 * @param height the height to set the current ID to.
	 */
	public void setHeight(String ID, int height) {
		if(currentlyHandledBlockCommand!=null) {
			currentlyHandledBlockCommand.setAfterActionHeight(ID, height);
		}
	}
	
	

	public void undo() {
		if(executedGameWorldCommands.size()!=0) {
			GameWorldCommand c = executedGameWorldCommands.pop();
			c.undo();
			undoneGameWorldCommands.push(c);
		}
		else if(executedBlockCommands.size()!=0){
			executedGameWorldCommands.clear();
			undoneGameWorldCommands.clear();
			BlockCommand c = executedBlockCommands.pop();
			currentlyHandledBlockCommand=c;
			c.undo();
			currentlyHandledBlockCommand=null;
			undoneBlockCommands.push(c);
		}
	}

	public void redo() {
		if(undoneGameWorldCommands.size()!=0) {
			GameWorldCommand c = undoneGameWorldCommands.pop();
			c.execute();
			executedGameWorldCommands.push(c);
		}
		else if(undoneBlockCommands.size()!=0){
			executedGameWorldCommands.clear();
			undoneGameWorldCommands.clear();
			BlockCommand c = undoneBlockCommands.pop();
			currentlyHandledBlockCommand=c;
			c.execute();
			currentlyHandledBlockCommand=null;
			executedBlockCommands.push(c);
		}
	}


}
