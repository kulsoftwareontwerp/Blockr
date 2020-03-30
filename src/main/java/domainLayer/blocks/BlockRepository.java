package domainLayer.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockType;
import types.ConnectionType;

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
			if(headBlocks.stream().map(e-> e.getBlockId()).collect(Collectors.toSet()).contains(connectedBlockId)){
				removeBlockFromHeadBlocks(connectedBlock);
				addBlockToHeadBlocks(newBlock);
			}
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
			throw new NoSuchConnectedBlockException("The requested blockId does not exist in the domain.");
		}
	}

	/**
	 * Retrieve a block by its ID
	 * 
	 * @param ID
	 * 
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
	 * @throws NoSuchConnectedBlockException If the given BlockID doesn't result in
	 *                                       a block in the domain.
	 * @return A set containing the id's of all the blocks that were removed from
	 *         the domain.
	 */
	public Set<String> removeBlock(String blockId) {
		Block b = getBlockByID(blockId);

		// the given exception may be thrown here.
		validateConnectedBlockIsInDomain(b);

		Set<Block> blocksToBeRemoved = getAllBlocksConnectedToAndAfterACertainBlock(b);
		Set<String> blockIdsToBeRemoved = new HashSet<String>();

		// If the given block was in HeadBlocks, none of the blocks connected to that
		// block are in headBlocks
		removeBlockFromHeadBlocks(b);

		for (Block blockToBeRemoved : blocksToBeRemoved) {
			removeBlockFromAllBlocks(blockToBeRemoved);
			blockIdsToBeRemoved.add(blockToBeRemoved.getBlockId());
		}
		return blockIdsToBeRemoved;
	}

	/**
	 * 
	 * Move a block given by it's ID in the domain, disconnecting it from an
	 * eventual connected block to connect it to another block.
	 * 
	 * 
	 * 
	 * @param movedBlockId              The Id of block to be moved, this parameter
	 *                                  is required.
	 * 
	 * 
	 * 
	 * @param connectedAfterMoveBlockId The Id of the block you wish to connect the
	 *                                  block you are moving to. This parameter is
	 *                                  Required. If there's no connected block
	 *                                  after the move please use an empty String,
	 *                                  "".
	 * @param connectionAfterMove       The connection of the block you wish to
	 *                                  connect the block you are moving to. This
	 *                                  parameter is Required. If there's no
	 *                                  connected block after the move please use
	 *                                  ConnectionType.NOCONNECTION.
	 * 
	 * @throws NoSuchConnectedBlockException   If This Exception is thrown all
	 *                                         modifications will not occur. By this
	 *                                         principle if you wished to move a
	 *                                         block, consider this block still
	 *                                         being at it's "old" place/connection.
	 * 
	 *                                         This exception will be thrown in
	 *                                         following cases; - If the Id of the
	 *                                         moved block does not exist in the
	 *                                         domain. - If one of the connected
	 *                                         block id's is not existing in the
	 *                                         domain.
	 * 
	 * @throws InvalidBlockConnectionException If this Exception is thrown all
	 *                                         modifications will not occur. By this
	 *                                         principle if you wished to move a
	 *                                         block, consider this block still
	 *                                         being at it's "old" place/connection.
	 * 
	 *                                         This exception will be thrown in
	 *                                         following cases; For simplicity we
	 *                                         will use the examples of BlockA and
	 *                                         BlockB. - If you wish to disconnect
	 *                                         BlockA of BlockB by moving BlockA but
	 *                                         BlockB has no connection with BlockA.
	 *                                         - If you wish to disconnect BlockA of
	 *                                         BlockB given an certain
	 *                                         ConnectionType, but BlockB has no
	 *                                         connection with BlockA on this
	 *                                         particular ConnectionType. - If a
	 *                                         BlockA is dropped on the
	 *                                         ConnectionType UP of BlockB but the
	 *                                         socket of BlockB is not aviable.
	 * 
	 * @return This method will return an Set of Strings that corresponds to the
	 *         block that have been modified while moving a block. If you move a
	 *         block that has no connected block underneath the only block being
	 *         modified is the block you've moved. In this case you'll receive a set
	 *         with only the Id of the block you've moved. Otherwise if the block
	 *         you are moving has blocks underneath and thus forms a chain of block.
	 *         Moving this chain of block upon on other block will have as result
	 *         that the last block of the chain will be connected to the block you
	 *         wished to connect your chain of blocks to. This method will then
	 *         return a set with the head of the chain, the actual block you
	 *         selected to move, and the last block of the chain, due to
	 *         modification towards the block underneath it.
	 * 
	 * 
	 */
	public Set<String> moveBlock(String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove) {
		
		//TODO: Fix wrong movedblocks, only returns one movedblock while there are supposed to be multiple, which should trigger multiple events.
		Set<String> movedBlocks = new HashSet<String>();
		Block movedBlock = getBlockByID(movedBlockId);
		Block afm = getBlockByID(connectedAfterMoveBlockId);
		ConnectionType connectionBeforeMove = ConnectionType.NOCONNECTION;
		ArrayList<String> beforeMove = new ArrayList<String>();
		Block bfm = null;

		if (movedBlock == null)
			throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

		movedBlocks.add(movedBlockId);

		beforeMove = getConnectedBlockBeforeMoveIfExists(movedBlock);
		if (beforeMove.size() != 0) {
			connectionBeforeMove = ConnectionType.valueOf(beforeMove.get(0));
			String bfmBlockId = beforeMove.get(1);
			bfm = getBlockByID(bfmBlockId);
		}

		if (connectionBeforeMove == ConnectionType.NOCONNECTION) {
			// indien no connection dan is er hier geen nood aan verandering
			if (connectionAfterMove == ConnectionType.DOWN) {
				if (afm == null)
					throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
				if (afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);
				afm.setNextBlock(movedBlock);
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
				afm.setOperand(movedBlock);
			}

		} else if (connectionBeforeMove == ConnectionType.DOWN) {

			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if (bfm.getNextBlock() != null && !bfm.getNextBlock().equals(movedBlock))
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
					movedBlock.setNextBlock(afm);
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
			if (bfm.getConditionBlock() != null && !bfm.getConditionBlock().equals(movedBlock))
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
			if (bfm.getOperand() != null && !bfm.getOperand().equals(movedBlock))
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
					nextBlockInChain.setOperand(afm);
					movedBlocks.add(nextBlockInChain.getBlockId());
				} else {
					movedBlock.setOperand(afm);
				}
			}
		} else if (connectionBeforeMove == ConnectionType.BODY) {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");
			if (bfm.getFirstBlockOfBody() != null && !bfm.getFirstBlockOfBody().equals(movedBlock))
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
				afm.setNextBlock(movedBlock);
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

				bfm.setFirstBlockOfBody(null);
				afm.setFirstBlockOfBody(movedBlock);
			}
		}
		return movedBlocks;
	}

	public ArrayList<String> getConnectedBlockBeforeMoveIfExists(Block movedBlock) {
		Iterator itAllBlocks = allBlocks.entrySet().iterator();
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		while (itAllBlocks.hasNext()) {
			Map.Entry element = (Entry) itAllBlocks.next();
			Block block = (Block) element.getValue();
			if (block instanceof ActionBlock) {
				if (block.getNextBlock() != null) {
					if (block.getNextBlock().equals(movedBlock)) {
						connectedBlockInfo.add("DOWN");
						connectedBlockInfo.add(block.getBlockId());
					}
				}
			} else if (block instanceof ControlBlock) {
				if (block.getNextBlock() != null) {
					if (block.getNextBlock().equals(movedBlock)) {
						connectedBlockInfo.add("DOWN");
						connectedBlockInfo.add(block.getBlockId());
					} else if (block.getConditionBlock().equals(movedBlock)) {
						connectedBlockInfo.add("CONDITION");
						connectedBlockInfo.add(block.getBlockId());
					} else if (block.getFirstBlockOfBody().equals(movedBlock)) {
						connectedBlockInfo.add("BODY");
						connectedBlockInfo.add(block.getBlockId());
					}
				}
			} else if (block instanceof OperatorBlock) {
				if (block.getNextBlock() != null) {
					if (block.getOperand().equals(movedBlock)) {
						connectedBlockInfo.add("OPERAND");
						connectedBlockInfo.add(block.getBlockId());
					}
				}

			}
		}
		return connectedBlockInfo;
	}

	/**
	 * Checks whether a the programArea is in a valid state or not.
	 * 
	 * @return True if following points are respected; - All blocks in program area
	 *         are connected together. This means that their is only one "Head
	 *         Block". - If all "Control Block"-types are in a valid state, which
	 *         implies that they have a condition and at least one block connected
	 *         to the BODY ConnectionType. - If a "Control Block" has a operand or
	 *         chain of operands, this block/chain must be connected with a
	 *         "Condition Block"
	 */
	public boolean checkIfValidProgram() {

		if (headBlocks.size() != 1)
			return false;
		Block headBlock = null;
		for (Block block : headBlocks) {
			headBlock = allBlocks.get(block.getBlockId());
		}
		return CheckIfChainIsValid(headBlock);

	}

	public boolean CheckIfChainIsValid(Block nextBlockInChain) {
		while (nextBlockInChain != null) {
			if (nextBlockInChain instanceof ControlBlock)
				if (!checkIfValidControlBlock((ControlBlock) nextBlockInChain))
					return false;
			nextBlockInChain = nextBlockInChain.getNextBlock();
		}
		return true;
	}

	/**
	 * method used to check if ControlBlock is in a valid state
	 */
	public boolean checkIfValidControlBlock(ControlBlock block) {
		if (block.getConditionBlock() == null)
			return false;
		if (block.getConditionBlock() instanceof OperatorBlock)
			return checkIfValidStatement(block.getConditionBlock());
		if (block.getFirstBlockOfBody() != null) {
			return CheckIfChainIsValid(block.getFirstBlockOfBody());
		}
		return true;
	}

	/**
	 * method used to check if a chain of operand finishes with a conditionBlock.
	 * 
	 * @param block
	 * @return
	 */
	public boolean checkIfValidStatement(Block block) {
		if (block != null) {
			if (block.getOperand() instanceof ConditionBlock)
				return true;
			return checkIfValidStatement(block.getOperand());
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

	/**
	 * 
	 * @param block
	 */
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
	 * Returns all the BlockID's underneath a certain block
	 * 
	 * @param block The blockID of the Block of which you want to retrieve all
	 *              Blocks underneath.
	 * @return A set containing the blockID's of all connected Conditions and every
	 *         kind of block in the body of the given block or under the given
	 *         block. The ID of the block itself is also given.
	 */
	public Set<String> getAllBlockIDsUnderneath(Block block) {
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		getAllBlocksConnectedToAndAfterACertainBlock(block).stream().map(s -> s.getBlockId())
				.forEach(s -> blockIDsUnderNeath.add(s));
		return blockIDsUnderNeath;
	}

	private Set<Block> getAllBlocksConnectedToAndAfterACertainBlock(Block block) {
		Set<Block> allBlocksInBody = new HashSet<Block>();

		if (block != null) {
			allBlocksInBody.add(block);
			allBlocksInBody.addAll(getAllBlocksConnectedToAndAfterACertainBlock(block.getNextBlock()));
			allBlocksInBody.addAll(getAllBlocksConnectedToAndAfterACertainBlock(block.getOperand()));
			allBlocksInBody.addAll(getAllBlocksConnectedToAndAfterACertainBlock(block.getFirstBlockOfBody()));
			allBlocksInBody.addAll(getAllBlocksConnectedToAndAfterACertainBlock(block.getConditionBlock()));
		}

		return allBlocksInBody;
	}
	
	private Set<Block> getAllBlocksAfterACertainBlock(Block block) {
		Set<Block> allBlocksInBody = new HashSet<Block>();

		if (block != null) {
			allBlocksInBody.add(block);
			allBlocksInBody.addAll(getAllBlocksConnectedToAndAfterACertainBlock(block.getNextBlock()));
		}

		return allBlocksInBody;
	}

	/**
	 * Returns all the blockID's in the body of a given ControlBlock
	 * 
	 * @param controlBlock The controlBlock of which you want to retrieve all Blocks
	 *                     in the body.
	 * @return A set containing the blockID of the blocks in the body of the given
	 *         ControlBlock, there won't be any ID's of assessable blocks.
	 */
	public Set<String> getAllBlockIDsInBody(ControlBlock controlBlock) {
		Set<String> blockIDsInBody = new HashSet<String>();

		getAllBlocksConnectedToAndAfterACertainBlock(controlBlock.getFirstBlockOfBody()).stream()
				.filter(s -> !(s instanceof AssessableBlock)).map(s -> s.getBlockId())
				.forEach(s -> blockIDsInBody.add(s));

		return blockIDsInBody;
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

//	public ControlBlock getEnclosingControlBlock(ExecutableBlock block) {
//		// ExecutableBlock headBlock = findFirstBlockToBeExecuted();
//		for (Block headBlock : headBlocks) {
//			Set<ExecutableBlock> chain = new HashSet<ExecutableBlock>();
//			if (headBlock instanceof ControlBlock) {
//				chain = getAllBlocksInBodyTopLevel((ControlBlock) headBlock.getFirstBlockOfBody());
//			}
//			Set<ControlBlock> allControlBlocks = new HashSet<ControlBlock>();
//			chain.stream().filter(s -> s instanceof ControlBlock).forEach(s -> allControlBlocks.add((ControlBlock) s));
//			for (ControlBlock c : allControlBlocks) {
//				if (getAllBlockIDsInBody(c).contains(block.getBlockId())) {
//					return c;
//				}
//			}
//		}
//
//		return null;
//	}
	
	
	public Set<ControlBlock> getAllHeadControlBlocks(){
		return this.headBlocks.stream().filter(e-> e instanceof ControlBlock).map(e-> (ControlBlock)e).collect(Collectors.toSet());
	}

	public ControlBlock getEnclosingControlBlock(ExecutableBlock block) {
		Set<ControlBlock> chain = new HashSet<ControlBlock>();
		for (Block headBlock : headBlocks) {
			getAllBlocksConnectedToAndAfterACertainBlock(headBlock).stream().filter(e -> !e.getBlockId().equals(block.getBlockId()) && (e instanceof ControlBlock))
					.forEach(p -> chain.add((ControlBlock) p));
		}
		for (ControlBlock c : chain) {			
			for (ExecutableBlock topLevelBlock : getAllBlocksInBodyTopLevel(c.getFirstBlockOfBody())) {
				if(topLevelBlock.getBlockId().equals(block.getBlockId())) {
					return c;
				}
			}
		}
		return null;
	}

	private Set<ExecutableBlock> getAllBlocksInBodyTopLevel(ExecutableBlock block) {

		Set<ExecutableBlock> allBlocksInBody = new HashSet<ExecutableBlock>();

		if (block != null) {
			allBlocksInBody.add(block);
			allBlocksInBody.addAll(getAllBlocksInBodyTopLevel(block.getNextBlock()));
		}

		return allBlocksInBody;
	}
	
	public Set<String> getAllBlockIDsBelowCertainBlock(Block block) {
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		getAllBlocksAfterACertainBlock(block).stream().map(s -> s.getBlockId())
				.forEach(s -> blockIDsUnderNeath.add(s));
		return blockIDsUnderNeath;
	}

}