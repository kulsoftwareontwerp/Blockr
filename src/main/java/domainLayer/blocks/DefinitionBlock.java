/**
 * 
 */
package domainLayer.blocks;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import types.BlockType;
import types.ConnectionType;

/**
 * DefinitionBlock A definitionBlock contains a set of instructions to be called
 * upon by a CallFunctionBlock
 * 
 * @version 0.1
 * @author group17
 *
 */
public class DefinitionBlock extends Block implements BodyCavityBlock {
	private ExecutableBlock firstBlockOfBody;
	private HashSet<ConnectionType> supportedConnectionTypes;
	private Stack<String> functionBlockCallStack;

	/**
	 * Create a new DefinitionBlock with the given blockID
	 * 
	 * @param blockID The ID to give to the block.
	 */
	public DefinitionBlock(String blockID) {
		super(blockID);
		this.supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.BODY);
		this.functionBlockCallStack = new Stack<String>();
//		setCallStack(null);
	}

	@Override
	public BlockType getBlockType() {
		return BlockType.DEFINITION;
	}

	@Override
	public Set<ConnectionType> getSupportedConnectionTypes() {
		return new HashSet<ConnectionType>(supportedConnectionTypes);
	}

	@Override
	public void setFirstBlockOfBody(ExecutableBlock block) {
		this.firstBlockOfBody = block;
	}

	@Override
	public ExecutableBlock getFirstBlockOfBody() {
		return this.firstBlockOfBody;
	}

	/**
	 * Retrieve the callStack
	 * 
	 * @return the callStack
	 */
	public Stack<String> getCallStack() {
		return copyCallStack(functionBlockCallStack);
	}

	private Stack<String> copyCallStack(Stack<String> callStack) {
		Stack<String> tempCallStack = new Stack<String>();

		for (int i = 0; i < callStack.size(); i++) {
			tempCallStack.add(i, callStack.get(i));
		}
		return tempCallStack;
	}

	/**
	 * Set the callStack of this definitionBlock to the given CallStack
	 * 
	 * @param callStack The callStack to give to this block.
	 */
	public void setCallStack(Stack<String> callStack) {
		if (callStack != null && callStack.size()!=0 ) {
//			this.functionBlockCallStack = new Stack<String>();
//		} else {
			this.functionBlockCallStack = copyCallStack(callStack);
		}
	}

	/**
	 * Pushes the given CallFunctionBlock on the callStack of this DefinitionBlock.
	 * 
	 * @param caller the caller that calls this definitionBlock.
	 */
	public void pushToCallStack(CallFunctionBlock caller) {
		if (caller != null && caller.getBlockType().definition().equals(this.getBlockId())) {
			this.functionBlockCallStack.push(caller.getBlockId());
		}
	}

	/**
	 * Pop the latest caller from the callStack and return its ID
	 * 
	 * @return The ID of the block that was the last to call this definition block,
	 *         NULL if no blocks are left in the callStack.
	 */
	public String popFromCallStack() {
		if (!this.functionBlockCallStack.isEmpty()) {
			return this.functionBlockCallStack.pop();
		} else {
			return null;
		}
	}

	/**
	 * Clear the callStack of this DefinitionBlock.
	 */
	public void clearCallStack() {
		this.functionBlockCallStack.clear();
	}

}
