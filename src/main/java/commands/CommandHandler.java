/**
 * 
 */
package commands;

import java.util.Stack;

/**
 * /** CommandHandler
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CommandHandler {
	private Stack<Command> executedBlockCommands;
	private Stack<Command> undoneBlockCommands;
	private Stack<Command> executedGameWorldCommands;
	private Stack<Command> undoneGameWorldCommands;

	public CommandHandler() {
		executedBlockCommands=new Stack<Command>();
		undoneBlockCommands=new Stack<Command>();
		executedGameWorldCommands=new Stack<Command>();
		undoneGameWorldCommands=new Stack<Command>();
	}

	public void handle(BlockCommand command) {
		//no undo operations for gameWorld possible anymore
		executedGameWorldCommands.clear();
		undoneGameWorldCommands.clear();
		
		command.execute();
		
		executedBlockCommands.push(command);
		
	}

	public void handle(GameWorldCommand command) {
		command.execute();
		executedGameWorldCommands.push(command);
	}

	public void undo() {
		if(executedGameWorldCommands.size()!=0) {
			Command c = executedGameWorldCommands.pop();
			c.undo();
			undoneGameWorldCommands.push(c);
		}
		else if(executedBlockCommands.size()!=0){
			Command c = executedBlockCommands.pop();
			c.undo();
			undoneBlockCommands.push(c);
		}
	}

	public void redo() {
		if(undoneGameWorldCommands.size()!=0) {
			Command c = undoneGameWorldCommands.pop();
			c.execute();
			executedGameWorldCommands.push(c);
		}
		else if(undoneBlockCommands.size()!=0){
			Command c = undoneBlockCommands.pop();
			c.execute();
			executedBlockCommands.push(c);
		}
	}


}
