/**
 * 
 */
package commands;

/**
 * /** Command
 * 
 * @version 0.1
 * @author group17
 *
 */
public interface Command {
	/**
	 * Execute a command
	 */
	public void execute();

	/**
	 * Undo a command
	 */
	public void undo();
}
