package applicationLayer;

import java.util.Collection;

import domainLayer.BlockType;

public class DomainController {

	private GameController gameController;
	private BlockController blockController;
	private ElementController elementController;

	
	public DomainController() {
		gameController=new GameController();
		blockController=new BlockController();
		elementController=new ElementController();
		
		blockController.addDomainListener(gameController);
		elementController.addDomainListener(gameController);
	}

	
	/**
	 * 
	 * @param blockType
	 * @param connectedBlockId
	 * @param connection
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		if(blockType == null) {
			throw new IllegalArgumentException("No blockType given.");
		}
		else if(connection == null) {
			throw new IllegalArgumentException("Null given as connection, use ConnectionType.NOCONNECTION.");
		}
		else if((connectedBlockId==null ||connectedBlockId.equals("")) && connection != ConnectionType.NOCONNECTION){
			throw new IllegalArgumentException("No connected block given with connection.");
		}
		else if((connectedBlockId!=null && !connectedBlockId.equals("")) && connection == ConnectionType.NOCONNECTION){
			throw new IllegalArgumentException("No connection given for connected block.");
		}
		else {
			blockController.addBlock(blockType, connectedBlockId, connection);
		}
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


}