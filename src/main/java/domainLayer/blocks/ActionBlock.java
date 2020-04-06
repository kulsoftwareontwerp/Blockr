package domainLayer.blocks;

import domainLayer.elements.ElementRepository;

/**
 * The abstract class for the concept of an action block.
 * 
 * @version 0.1
 * @author group17
 */
public abstract class ActionBlock extends ExecutableBlock {

	/**
	 * Create an Action Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public ActionBlock(String blockId) {
		super(blockId);
	}

	public abstract void execute(ElementRepository elementsRepo);
}