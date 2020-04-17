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
		currentlyHandledBlockCommand=command;
		command.execute();
		currentlyHandledBlockCommand=null;
		executedBlockCommands.push(command);
		
	}

	public void handle(GameWorldCommand command) {
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

	public void undo() {
		if(executedGameWorldCommands.size()!=0) {
			GameWorldCommand c = executedGameWorldCommands.pop();
			c.undo();
			undoneGameWorldCommands.push(c);
		}
		else if(executedBlockCommands.size()!=0){
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
			BlockCommand c = undoneBlockCommands.pop();
			currentlyHandledBlockCommand=c;
			c.execute();
			currentlyHandledBlockCommand=null;
			executedBlockCommands.push(c);
		}
	}


}