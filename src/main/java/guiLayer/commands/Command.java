/**
 * 
 */
package guiLayer.commands;

/**
 * /** Command
 * 
 * @version 0.1
 * @author group17
 *
 */
public interface Command {
	/**
	 * Execute this command.
	 */
	public void execute();

	/**
	 * Undo this command.
	 */
	public void undo();
}
