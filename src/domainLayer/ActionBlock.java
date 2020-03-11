package domainLayer;

public abstract class ActionBlock extends ExecutableBlock {

	/**
	 * 
	 * @param blockId
	 * @param mediator
	 */
	public ActionBlock(String blockId) {
		super(blockId);
	}

	public abstract void execute(ElementRepository elementsRepo);
}