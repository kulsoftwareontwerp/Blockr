package domainLayer.blocks;

import java.util.*;
import applicationLayer.*;
import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;

public class BlockRepository {

	private BlockFactory blockFactory;
	private Collection<Block> headBlocks;
	private HashMap<String, Block> allBlocks;
	private String lastAddedBlockId;
	private final int maxNbOfBlocks = 20;
	private static BlockRepository instance;

	private BlockRepository() {
		headBlocks = new HashSet<Block>();
		allBlocks = new HashMap<String, Block>();
		blockFactory = new BlockFactory();
	}

	public String getLastAddedBlockId() {
		return lastAddedBlockId;
	}

	/**
	 * 
	 * @param blockType
	 * @param connectedBlockId
	 * @param connection
	 */
	public void addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		Block newBlock = blockFactory.createBlock(blockType);
		this.lastAddedBlockId = newBlock.getBlockId();
		Block connectedBlock = getBlockByID(connectedBlockId);

		switch (connection) {
		case NOCONNECTION:
			addBlockToHeadBlocks(newBlock);
			break;
		case UP:
			validateConnectedBlock(connectedBlock, connection);
			newBlock.setNextBlock(connectedBlock);
			break;
		case DOWN:
			validateConnectedBlock(connectedBlock, connection);
			connectedBlock.setNextBlock(newBlock);
			break;
		case BODY:
			validateConnectedBlock(connectedBlock, connection);
			connectedBlock.setFirstBlockOfBody(newBlock);
			break;
		case CONDITION:
			validateConnectedBlock(connectedBlock, connection);
			connectedBlock.setConditionBlock(newBlock);
			break;
		case RIGHT:
			validateConnectedBlock(connectedBlock, connection);
			connectedBlock.setOperand(newBlock);
			break;

		default:
			// It shouldn't be possible to get here.
			break;
		}

		addBlockToAllBlocks(newBlock);

	}

	private void validateConnectedBlock(Block connectedBlock, ConnectionType connection) {
		if(connectedBlock==null) {
			throw new NoSuchConnectedBlockException("The requested connectedBlockId does not exist in the domain.");
		}
		boolean connectionOccupied=false;
		switch (connection) {
		case NOCONNECTION:

			break;
		case UP:
			break;
		case DOWN:
			connectionOccupied = connectedBlock.getNextBlock()!=null;
			break;
		case BODY:
			connectionOccupied = connectedBlock.getFirstBlockOfBody()!=null;
			break;
		case CONDITION:
			connectionOccupied = connectedBlock.getConditionBlock()!=null;
			break;
		case RIGHT:
			connectionOccupied = connectedBlock.getOperand()!=null;
			break;

		default:
			// It shouldn't be possible to get here.
			break;
		}
		
		if(connectionOccupied) {
			throw new InvalidBlockConnectionException("Connection at connectedBlock is already occupied.");
		}
		
	}

	/**
	 * 
	 * @param id
	 */
	public Block getBlockByID(String id) {
		return allBlocks.get(id);
	}

	/**
	 * 
	 * @param blockId
	 */
	public void removeBlock(String blockId) {
		// TODO - implement BlockRepository.removeBlock
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
	public void moveBlock(String movedBlockId, String connectedBeforeMoveBlockId, ConnectionType connectionBeforeMove,
			String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		// TODO - implement BlockRepository.moveBlock
		throw new UnsupportedOperationException();
	}

	public boolean checkIfValidProgram() {
		// TODO - implement BlockRepository.checkIfValidProgram
		throw new UnsupportedOperationException();
	}

	public ExecutableBlock findFirsttBlockToBeExecuted() {
		// TODO - implement BlockRepository.findFirsttBlockToBeExecuted
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	private void addBlockToHeadBlocks(Block block) {
		this.headBlocks.add(block);
	}

	/**
	 * 
	 * @param block
	 */
	private void addBlockToAllBlocks(Block block) {
		this.allBlocks.put(block.getBlockId(), block);
	}

	/**
	 * 
	 * @param block
	 */
	private void removeBlockFromHeadBlocks(Block block) {
		this.headBlocks.remove(block);
	}

	/**
	 * 
	 * @param block
	 */
	private void removeBlockFromAllBlocks(Block block) {
		this.allBlocks.remove(block.getBlockId());
	}

	/**
	 * 
	 * @return True totalNumberOfBlocks >= getMaxNbOfBlocks()
	 */
	public boolean checkIfMaxNbOfBlocksReached() {
		return this.getMaxNbOfBlocks() <= this.allBlocks.size();
	}

	public static BlockRepository getInstance() {
		if (instance == null) {
			instance = new BlockRepository();
		}
		return instance;
	}

	public int getMaxNbOfBlocks() {
		return maxNbOfBlocks;
	}

}