package domainLayer.blocks;

import java.security.DrbgParameters.NextBytes;
import java.util.*;

import org.mockito.internal.matchers.InstanceOf;

import applicationLayer.*;
import domainLayer.blocks.*;

import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;

/**
 * The BlockRepository performs Create, Update, Delete and Retrieve operations
 * for Blocks.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockRepository {

	private BlockFactory blockFactory;
	private HashSet<Block> headBlocks;
	private HashMap<String, Block> allBlocks;
	private final int maxNbOfBlocks = 20;
	private static BlockRepository instance;

	private BlockRepository() {
		headBlocks = new HashSet<Block>();
		allBlocks = new HashMap<String, Block>();
		blockFactory = new BlockFactory();
	}

	/**
	 * Add a block of the given blockType to the domain and connect it with the
	 * given connectedBlockId on the given connection
	 * 
	 * @param blockType        The type of block to be added, this parameter is
	 *                         required.
	 * @param connectedBlockId The ID of the block to connect to, can be empty.
	 * @param connection       The connection of the connected block on which the
	 *                         new block must be connected. If no connectedBlockId
	 *                         was given, this parameter must be set to
	 *                         "ConnectionType.NOCONNECTION".
	 * @return TODO
	 * @throws InvalidBlockConnectionException The given combination of the
	 *                                         blockType,connectedBlockId and
	 *                                         connection is impossible. - an
	 *                                         ExecutableBlock added to an
	 *                                         AssessableBlock or ControlBlock as
	 *                                         condition - an AssessableBlock added
	 *                                         to a block as a body or "next block"
	 *                                         - a block added to another block of
	 *                                         which the required connection is not
	 *                                         provided. - a block added to a
	 *                                         connection of a connected block to
	 *                                         which there is already a block
	 *                                         connected.
	 * @throws NoSuchConnectedBlockException   Is thrown when a connectedBlockId is
	 *                                         given that is not present in the
	 *                                         domain.
	 * @return The ID of the block that has been added.
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
			// In Block the right method for adding a condition to an operator/controlBlock
			// will be called.
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
		boolean connectionOccupied = false;
		switch (connection) {
		case NOCONNECTION:
			break;
		case UP:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = block.getNextBlock() != null && !headBlocks.contains(connectedBlock);
			break;
		case LEFT:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = (block.getOperand() != null || block.getConditionBlock() != null)
					&& !headBlocks.contains(connectedBlock);
			break;
		case DOWN:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getNextBlock() != null;
			break;
		case BODY:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getFirstBlockOfBody() != null;
			break;
		case CONDITION:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getConditionBlock() != null;
			break;
		case OPERAND:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getOperand() != null;
			break;

		default:
			// It shouldn't be possible to get here.
			break;
		}

		if (connectionOccupied) {
			throw new InvalidBlockConnectionException("Connection at connectedBlock is already occupied.");
		}

	}

	private void validateConnectedBlockIsInDomain(Block connectedBlock) {
		if (connectedBlock == null) {
			throw new NoSuchConnectedBlockException("The requested connectedBlockId does not exist in the domain.");
		}
	}

	/**
	 * Retrieve a block by its ID
	 * 
	 * @param ID
	 * @return The block corresponding with the given ID. If there is no block for
	 *         the given ID, null is returned.
	 */
	public Block getBlockByID(String ID) {
		return allBlocks.get(ID);
	}

	/**
	 * Remove a block by its ID
	 * 
	 * @param block The ID of the block to be removed.
	 * @return All the id's
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
	public Set<String> moveBlock(String movedBlockId, String connectedBeforeMoveBlockId,
		ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove) {
		Set<String> movedBlocks = new HashSet<String>();
		Block movedBlock = getBlockByID(movedBlockId);
		Block bfm = getBlockByID(connectedBeforeMoveBlockId);
		Block afm = getBlockByID(connectedAfterMoveBlockId);
		
		if(movedBlock == null)
			throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
		
		movedBlocks.add(movedBlockId);

		if (connectionBeforeMove == ConnectionType.NOCONNECTION) {
			// indien no connection dan is er hier geen nood aan verandering
			if (connectionAfterMove == ConnectionType.DOWN) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);
				afm.setNextBlock( movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.UP) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");
				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
					}
					nextBlockInChain.setNextBlock(afm);
					movedBlocks.add(nextBlockInChain.getBlockId());
				} else {
					movedBlock.setNextBlock(afm);
				}
			}

			else if (connectionAfterMove == ConnectionType.BODY) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				removeBlockFromHeadBlocks(movedBlock);
				afm.setFirstBlockOfBody(movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.CONDITION) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getConditionBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				headBlocks.remove(movedBlock);
				afm.setConditionBlock(movedBlock);
			} else if (connectionAfterMove == ConnectionType.LEFT) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");
				headBlocks.add(movedBlock);
				if (movedBlock.getOperand() != null) {
					Block nextChainBlock = movedBlock;
					while (nextChainBlock.getOperand() != null) {
						nextChainBlock = nextChainBlock.getOperand();
					}
					nextChainBlock.setOperand(afm);
					movedBlocks.add(nextChainBlock.getBlockId());
				} else {
					movedBlock.setOperand(afm);
				}

			} else if (connectionAfterMove == ConnectionType.OPERAND) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getOperand() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				afm.setOperand( movedBlock);
			}

		} else if (connectionBeforeMove == ConnectionType.DOWN) {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if(bfm.getNextBlock() != null && !bfm.getNextBlock().equals(movedBlock))
				throw new InvalidBlockConnectionException("The moved block is not connected to this block or socket");
			
			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				addBlockToHeadBlocks(movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.DOWN) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				afm.setNextBlock(movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.UP) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");
				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				addBlockToHeadBlocks(movedBlock);// connection up is broken so there is no upper block
				if (movedBlock.getNextBlock() != null) // block is Head block of a blockChain
				{
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
					}
					nextBlockInChain.setNextBlock(afm);
					movedBlocks.add(nextBlockInChain.getBlockId());
				} else {
					movedBlock.setNextBlock( afm);
				}

			} else if (connectionAfterMove == ConnectionType.BODY) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
				afm.setFirstBlockOfBody(movedBlock);
			}
			// conditionBlock is hier niet mogelijk aangezien we met een UP connectie zaten.
		}

		// ConnectionBeforeMove == connectionType.UP neemt nooit plaats wanneer een
		// block ge-moved wordt.
		else if (connectionBeforeMove == ConnectionType.CONDITION) {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if(bfm.getConditionBlock() != null && !bfm.getConditionBlock().equals(movedBlock))
				throw new InvalidBlockConnectionException("The moved block is not connected to this block or socket");

			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setConditionBlock(null);
				addBlockToHeadBlocks(movedBlock);
			} else if (connectionAfterMove == ConnectionType.CONDITION) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getConditionBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");
				bfm.setConditionBlock(null);
				afm.setConditionBlock(movedBlock);
			} else if (connectionAfterMove == ConnectionType.LEFT) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");

				if (movedBlock.getOperand() != null) {
					Block nextChainBlock = movedBlock;
					while (nextChainBlock.getOperand() != null) {
						nextChainBlock = nextChainBlock.getOperand();
					}
					nextChainBlock.setOperand(afm);
					movedBlocks.add(nextChainBlock.getBlockId());
				} else {
					movedBlock.setOperand(afm);
				}
			}

			// Connectie rechts van andere conditie
		} else if (connectionBeforeMove == ConnectionType.OPERAND) {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if(bfm.getOperand() != null && !bfm.getOperand().equals(movedBlock))
				throw new InvalidBlockConnectionException("The moved block is not connected to this block or socket");
			
			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setOperand(null);
				headBlocks.add(movedBlock);
			} else if (connectionAfterMove == ConnectionType.CONDITION) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				bfm.setOperand(null);
				afm.setConditionBlock(movedBlock);
			} else if (connectionAfterMove == ConnectionType.LEFT) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");
				headBlocks.add(movedBlock);
				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getOperand() != null) {
						nextBlockInChain = nextBlockInChain.getOperand();
					}
					nextBlockInChain.setOperand( afm);
					movedBlocks.add(nextBlockInChain.getBlockId());
				} else {
					movedBlock.setOperand(afm);
				}
			}
		} else if (connectionBeforeMove == ConnectionType.BODY) {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if(bfm.getFirstBlockOfBody() != null && !bfm.getFirstBlockOfBody().equals(movedBlock))
				throw new InvalidBlockConnectionException("The moved block is not connected to this block or socket");

			if (connectionAfterMove == ConnectionType.NOCONNECTION) {
				bfm.setFirstBlockOfBody(null);
				addBlockToHeadBlocks(movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.DOWN) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				bfm.setFirstBlockOfBody(null);
				afm.setNextBlock( movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.UP) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");
				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
					}
					nextBlockInChain.setNextBlock(afm);
					movedBlocks.add(nextBlockInChain.getBlockId());
				} else {
					movedBlock.setNextBlock( afm);
				}
			}

			else if (connectionAfterMove == ConnectionType.BODY) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				bfm.setFirstBlockOfBody(null);
				afm.setFirstBlockOfBody( movedBlock);
			}
		}
		return movedBlocks;
	}

	public boolean checkIfValidProgram() {
		if(headBlocks.size() != 1)
			return false;
		Block headBlock = null;
		for(Block block: headBlocks) {
			headBlock = allBlocks.get(block.getBlockId());
		}
		Block nextBlockInChain = headBlock;
		while(nextBlockInChain != null) {
			if(nextBlockInChain instanceof ControlBlock)
				if(!checkIfValidControlBlock((ControlBlock) nextBlockInChain))
					return false;
			nextBlockInChain = nextBlockInChain.getNextBlock();
		}
		return true;
		
	}
	
	public boolean checkIfValidControlBlock(ControlBlock block) {
		if(block.getConditionBlock() == null)
			return false;
		if(block.getConditionBlock() instanceof OperatorBlock) {
			checkIfValidStatement(block.getConditionBlock());
		}
		return true;
	}
	
	public boolean checkIfValidStatement(Block block) {
		if(block != null) {
			if(block.getOperand() instanceof ConditionBlock)
				return true;
			checkIfValidStatement(block.getOperand());
		}
		return false;
	}

	public ExecutableBlock findFirstBlockToBeExecuted() {
		// We find the "first" item in the HashSet, that should always be the only item
		// in the set, otherwise it would not be in an ValidState
		Iterator iter = headBlocks.iterator();
		ExecutableBlock firstExecutableBlock = (ExecutableBlock) iter.next();
		return firstExecutableBlock;
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
	 * @return The instantiation of BlockRepository.
	 */
	public static BlockRepository getInstance() {
		if (instance == null) {
			instance = new BlockRepository();
		}
		return instance;
	}

	/**
	 * Retrieve the maximum number of blocks.
	 * 
	 * @return the maximum number of blocks.
	 */
	public int getMaxNbOfBlocks() {
		return maxNbOfBlocks;
	}

	public Set<String> getAllBlockIDsUnderneath(String blockID) {
		Set<String> blocksUnderneath = new HashSet<String>();
		Block nextChainBlock = getBlockByID(blockID);
		while (nextChainBlock.getNextBlock() != null) {
			blocksUnderneath.add(nextChainBlock.getNextBlock().getBlockId());
			nextChainBlock = nextChainBlock.getNextBlock();
		}
		return blocksUnderneath;
	}

}