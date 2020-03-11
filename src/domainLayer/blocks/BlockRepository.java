package domainLayer.blocks;

import java.util.*;
import applicationLayer.*;
import domainLayer.blocks.*;
import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;


/**
 * The BlockRepository performs Create, Update, Delete and Retrieve operations for Blocks.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockRepository {

	private BlockFactory blockFactory;
	private Collection<Block> headBlocks;
	private HashMap<String, Block> allBlocks;
	private final int maxNbOfBlocks = 20;
	private static BlockRepository instance;

	private BlockRepository() {
		headBlocks = new HashSet<Block>();
		allBlocks = new HashMap<String, Block>();
		blockFactory = new BlockFactory();
	}


	/**
	 * Add a block of the given blockType to the domain and connect it with the given connectedBlockId on the given connection
	 * 
	 * @param 	blockType
	 * 			The type of block to be added, this parameter is required.
	 * @param 	connectedBlockId
	 * 			The ID of the block to connect to, can be empty.
	 * @param 	connection
	 * 			The connection of the connected block on which the new block must be connected.
	 * 			If no connectedBlockId was given, this parameter must be set to "ConnectionType.NOCONNECTION".
	 * @return TODO
	 * @throws	InvalidBlockConnectionException
	 * 			The given combination of the blockType,connectedBlockId and connection is impossible.
	 * 			- an ExecutableBlock added to an AssessableBlock or ControlBlock as condition
	 * 			- an AssessableBlock added to a block as a body or "next block"
	 * 			- a block added to another block of which the required connection is not provided.
	 * 			- a block added to a connection of a connected block to which there is already a block connected.
	 * @throws	NoSuchConnectedBlockException
	 * 			Is thrown when a connectedBlockId is given that is not present in the domain.
	 * @return	The ID of the block that has been added.
	 */
	public String addBlock(BlockType blockType, String connectedBlockId, ConnectionType connection) {
		Block newBlock = blockFactory.createBlock(blockType);
		Block connectedBlock = getBlockByID(connectedBlockId);

			validateConnection(connectedBlock, connection, newBlock);			
		switch (connection) {
		case NOCONNECTION:
			addBlockToHeadBlocks(newBlock);
			break;
		case UP:
			newBlock.setNextBlock(connectedBlock);
			break;
		case DOWN:
			connectedBlock.setNextBlock(newBlock);
			break;
		case BODY:
			connectedBlock.setFirstBlockOfBody(newBlock);
			break;
		case CONDITION:
			connectedBlock.setConditionBlock(newBlock);
			break;
		case OPERAND:
			connectedBlock.setOperand(newBlock);
			break;
		case LEFT:
			// In Block the right method for adding a condition to an operator/controlBlock will be called.
			newBlock.setConditionBlock(connectedBlock);
			break;

		default:
			// It shouldn't be possible to get here.
			break;
		}

		addBlockToAllBlocks(newBlock);
		return newBlock.getBlockId();
	
		
		

	}

	private void validateConnection(Block connectedBlock, ConnectionType connection, Block block) {
		boolean connectionOccupied=false;
		switch (connection) {
		case NOCONNECTION:
			break;
		case UP:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = block.getNextBlock()!=null  && !headBlocks.contains(connectedBlock) ; 
			break;
		case LEFT:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = (block.getOperand()!=null || block.getConditionBlock()!=null) && !headBlocks.contains(connectedBlock); 
			break;
		case DOWN:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getNextBlock()!=null;
			break;
		case BODY:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getFirstBlockOfBody()!=null;
			break;
		case CONDITION:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getConditionBlock()!=null;
			break;
		case OPERAND:
			validateConnectedBlockIsInDomain(connectedBlock);
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


	private void validateConnectedBlockIsInDomain(Block connectedBlock) {
		if(connectedBlock==null) {
			throw new NoSuchConnectedBlockException("The requested connectedBlockId does not exist in the domain.");
		}
	}

	/**
	 * Retrieve a block by its ID
	 * @param ID
	 * @return 	The block corresponding with the given ID. 
	 * 			If there is no block for the given ID, null is returned.
	 */
	public Block getBlockByID(String ID) {
		return allBlocks.get(ID);
	}

	/**
	 * Remove a block by its ID
	 * @param 	block
	 * 			The ID of the block to be removed.
	 * @return 	All the id's
	 */
	public Set<String> removeBlock(String blockId) {
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
	 * @return TODO
	 */
	public Set<String> moveBlock(String movedBlockId, String connectedBeforeMoveBlockId, ConnectionType connectionBeforeMove,
		String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		Set<String> movedBlocks = new HashSet<String>();
		Block movedBlock = getBlockByID(movedBlockId);
		Block bfm = getBlockByID(connectedBeforeMoveBlockId);
		Block afm = getBlockByID(connectedAfterMoveBlockId);
		movedBlocks.add(movedBlockId);
		if (connectionBeforeMove == ConnectionType.NOCONNECTION) {
			// indien no connection dan is er hier geen nood aan verandering
			if (connectionAfterMove == ConnectionType.DOWN) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				
				removeBlockFromHeadBlocks(movedBlock);
				afm.setNextBlock((ExecutableBlock) movedBlock);
			} 
			
			else if (connectionAfterMove == ConnectionType.UP) {
				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
						movedBlocks.add(nextBlockInChain.getBlockId());
					}
					nextBlockInChain.setNextBlock((ExecutableBlock) afm);
				} else {
					movedBlock.setNextBlock((ExecutableBlock) afm);
				}
			} 
			
			else if (connectionAfterMove == ConnectionType.BODY) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				removeBlockFromHeadBlocks(movedBlock);
				afm.setFirstBlockOfBody((ExecutableBlock) movedBlock);
			} 
			
			else if (connectionAfterMove == ConnectionType.CONDITION) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getConditionBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				
				headBlocks.remove(movedBlock);
				afm.setConditionBlock((AssessableBlock) movedBlock);
			}
			
			
			
			
		} else if (connectionBeforeMove == ConnectionType.DOWN) {
			if(bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			
			if (connectionAfterMove == ConnectionType.NOCONNECTION) {			
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				addBlockToHeadBlocks(movedBlock);
			}
			
			
			else if (connectionAfterMove == ConnectionType.DOWN) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				afm.setNextBlock((ExecutableBlock) movedBlock);
			} 
			
			else if (connectionAfterMove == ConnectionType.UP) {
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				addBlockToHeadBlocks(movedBlock);// connection up is broken so there is no upper block
				if (movedBlock.getNextBlock() != null) // block is Head block of a blockChain
				{
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
						movedBlocks.add(nextBlockInChain.getBlockId());
					}
					nextBlockInChain.setNextBlock((ExecutableBlock) afm);
				} else {
					movedBlock.setNextBlock((ExecutableBlock) afm);
				}

			} 
			
			
			else if (connectionAfterMove == ConnectionType.BODY) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
		
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				afm.setFirstBlockOfBody((ExecutableBlock) movedBlock);
			}
			// conditionBlock is hier niet mogelijk aangezien we met een UP connectie zaten.
		}
		
		
		
		// ConnectionBeforeMove == connectionType.UP neemt nooit plaats wanneer een
		// block ge-moved wordt.
		else if (connectionBeforeMove == ConnectionType.CONDITION) {
			if(bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			
			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setConditionBlock(null);
				addBlockToHeadBlocks(movedBlock);
			} else if (connectionAfterMove == ConnectionType.CONDITION) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getConditionBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				
				bfm.setConditionBlock(null);
				afm.setConditionBlock((AssessableBlock) movedBlock);
			}
			
			
			
			// Connectie rechts van andere conditie
		} else if (connectionBeforeMove == ConnectionType.BODY) {
			if(bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			
			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setFirstBlockOfBody(null);
				addBlockToHeadBlocks(movedBlock);
			} 
			
			
			
			else if (connectionAfterMove == ConnectionType.DOWN) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				
				bfm.setFirstBlockOfBody(null);
				afm.setNextBlock((ExecutableBlock) movedBlock);
			} 
			
			
			else if (connectionAfterMove == ConnectionType.UP) {
				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
						movedBlocks.add(nextBlockInChain.getBlockId());
					}
					nextBlockInChain.setNextBlock((ExecutableBlock) afm);
				} else {
					movedBlock.setNextBlock((ExecutableBlock) afm);
				}
			} 
			
			
			else if (connectionAfterMove == ConnectionType.BODY) {
				if(afm == null) 
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if(afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				
				bfm.setFirstBlockOfBody(null);
				afm.setFirstBlockOfBody((ExecutableBlock) movedBlock);
			}
		}
		return movedBlocks;
	}

	public boolean checkIfValidProgram() {
		return headBlocks.size() == 1;
	}

	public ExecutableBlock findFirsttBlockToBeExecuted() {
		// TODO - implement BlockRepository.findFirsttBlockToBeExecuted
		throw new UnsupportedOperationException();
	}


	private void addBlockToHeadBlocks(Block block) {
		this.headBlocks.add(block);
	}


	private void addBlockToAllBlocks(Block block) {
		this.allBlocks.put(block.getBlockId(), block);
	}


	private void removeBlockFromHeadBlocks(Block block) {
		this.headBlocks.remove(block);
	}


	private void removeBlockFromAllBlocks(Block block) {
		this.allBlocks.remove(block.getBlockId());
	}

	/**
	 * Checks if the maximum number of blocks has been reached.
	 * 
	 * @return getMaxNbOfBlocks() <= totalNumberOfBlocks
	 */
	public boolean checkIfMaxNbOfBlocksReached() {
		return this.getMaxNbOfBlocks() <= this.allBlocks.size();
	}

	/**
	 * Retrieve the instantiation of BlockRepository.
	 * 
	 * @return	The instantiation of BlockRepository.
	 */
	public static BlockRepository getInstance() {
		if (instance == null) {
			instance = new BlockRepository();
		}
		return instance;
	}
	
	/**
	 * Retrieve the maximum number of blocks.
	 * @return the maximum number of blocks.
	 */
	public int getMaxNbOfBlocks() {
		return maxNbOfBlocks;
	}
	
	public Set<String> getAllBlocksUnderneath(String blockID){
		Set<String> blocksUnderneath = new HashSet<String>();
		Block nextBlock = getBlockByID(blockID);
		while(nextBlock.getNextBlock() != null) {
			blocksUnderneath.add(nextBlock.getNextBlock().getBlockId());
			nextBlock = nextBlock.getNextBlock();
		}
		return blocksUnderneath;
	}

}