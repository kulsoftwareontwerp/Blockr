package domainLayer;

import java.util.*;
import applicationLayer.*;

public class BlockRepository {

	private BlockFactory blockFactory;
	private Collection<Block> headBlocks;
	private HashMap<String,Block> allBlocks;
	private String lastAddedBlockId;
	private final int maxNbOfBlocks=20;
	private static BlockRepository instance;

	private BlockRepository() {
		headBlocks=new HashSet<Block>();
		allBlocks=new HashMap<String,Block>();
		blockFactory=new BlockFactory();
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
		
		
	}

	/**
	 * 
	 * @param id
	 */
	public Block getBlockByID(String id) {
		// TODO - implement BlockRepository.getBlockByID
		throw new UnsupportedOperationException();
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
	public void moveBlock(String movedBlockId, String connectedBeforeMoveBlockId, ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
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
		// TODO - implement BlockRepository.addBlockToHeadBlocks
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	private void addBlockToAllBlocks(Block block) {
		// TODO - implement BlockRepository.addBlockToAllBlocks
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	private void removeBlockFromHeadBlocks(Block block) {
		// TODO - implement BlockRepository.removeBlockFromHeadBlocks
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param block
	 */
	private void removeBlockFromAllBlocks(Block block) {
		// TODO - implement BlockRepository.removeBlockFromAllBlocks
		throw new UnsupportedOperationException();
	}

	public boolean checkIfMaxNbOfBlocksReached() {
		// TODO - implement BlockRepository.checkIfMaxNbOfBlocksReached
		throw new UnsupportedOperationException();
	}

	public static BlockRepository getInstance() {
		if(instance==null) {
			instance = new BlockRepository();
		}
		return instance;
	}

	public int getMaxNbOfBlocks() {
		return maxNbOfBlocks;
	}

}