/**
 * 
 */
package commands;

import java.util.Stack;

/**
 * CommandHandler, a handler for all commands in the domain
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

	/**
	 * Create a new CommandHandler
	 */
	public CommandHandler() {
		executedBlockCommands = new Stack<Command>();
		undoneBlockCommands = new Stack<Command>();
		executedGameWorldCommands = new Stack<Command>();
		undoneGameWorldCommands = new Stack<Command>();
	}

	/**
	 * Execute a BlockCommand and put it on the stack, all undone BlockCommands will
	 * be cleared as well as all GameWorldCommands, executed and undone alike.
	 * 
	 * @param command the blockCommand to handle
	 */
	public void handle(BlockCommand command) {
		// no undo operations for gameWorld possible anymore
		executedGameWorldCommands.clear();
		undoneGameWorldCommands.clear();
		undoneBlockCommands.clear();

		command.execute();

		executedBlockCommands.push(command);

	}

	/**
	 * Execute a GameWorldCommand and put it on the stack, all undone
	 * GameWorldCommands will be cleared.
	 * 
	 * @param command the GameWorldCommand to execute.
	 */
	public void handle(GameWorldCommand command) {
		command.execute();
		undoneGameWorldCommands.clear();
		executedGameWorldCommands.push(command);
	}

	/**
	 * Undo the last command, if there are no gameWorld Commands left block commands
	 * will be undone and all executed and undone GameWorld Commands will be cleared.
	 */
	public void undo() {
		if (executedGameWorldCommands.size() != 0) {
			Command c = executedGameWorldCommands.pop();
			c.undo();
			undoneGameWorldCommands.push(c);
		} else if (executedBlockCommands.size() != 0) {
			executedGameWorldCommands.clear();
			undoneGameWorldCommands.clear();
			Command c = executedBlockCommands.pop();
			c.undo();
			undoneBlockCommands.push(c);
		}
	}

	/**
	 * Redo the last undone operation
	 */
	public void redo() {
		if (undoneGameWorldCommands.size() != 0) {
			Command c = undoneGameWorldCommands.pop();
			c.execute();
			executedGameWorldCommands.push(c);
		} else if (undoneBlockCommands.size() != 0) {
			executedGameWorldCommands.clear();
			undoneGameWorldCommands.clear();
			Command c = undoneBlockCommands.pop();
			c.execute();
			executedBlockCommands.push(c);
		}
	}

}
