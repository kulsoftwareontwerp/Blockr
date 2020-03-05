package applicationLayer;

public class DomainController {

	private GameController gameController;
	private BlockController blockController;
	private ElementController elementController;

	/**
	 * 
	 * @param blockType
	 * @param connectedBlockId
	 * @param connection
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		// TODO - implement DomainController.addBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param blockId
	 */
	public void removeBlock(String blockId) {
		// TODO - implement DomainController.removeBlock
		throw new UnsupportedOperationException();
	}

	public void resetGameExecution() {
		// TODO - implement DomainController.resetGameExecution
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param movedBlockId
	 * @param connectedBeforeMoveBlockId
	 * @param connectionBeforeMove
	 * @param connectedAfterMoveBlockId
	 * @param connectionAfterMove
	 */
	public void moveBlock(String movedBlockId, String connectedBeforeMoveBlockId, ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		// TODO - implement DomainController.moveBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param listener
	 */
	public void addGameListener(GUIListener listener) {
		// TODO - implement DomainController.addGameListener
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeGameListener(GUIListener listener) {
		// TODO - implement DomainController.removeGameListener
		throw new UnsupportedOperationException();
	}

	public void executeBlock() {
		// TODO - implement DomainController.executeBlock
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param blockId
	 */
	public Collection<String> getAllBlockIDsUnderneath(String blockId) {
		// TODO - implement DomainController.getAllBlockIDsUnderneath
		throw new UnsupportedOperationException();
	}

	public DomainController() {
		// TODO - implement DomainController.DomainController
		throw new UnsupportedOperationException();
	}

}