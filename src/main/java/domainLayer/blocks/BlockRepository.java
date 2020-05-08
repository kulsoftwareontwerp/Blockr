package domainLayer.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockSnapshot;
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

	// For testing purposes
	BlockRepository(HashSet<Block> headBlocks, HashMap<String, Block> allBlocks) {
		this.headBlocks = headBlocks;
		this.allBlocks = allBlocks;
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
			if (headBlocks.stream().map(e -> e.getBlockId()).collect(Collectors.toSet()).contains(connectedBlockId)) {
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
		if (connectedBlock != null) {
			addBlockToAllBlocks(connectedBlock);
		}
		addBlockToAllBlocks(newBlock);
		return newBlock.getBlockId();

	}

	private void validateConnection(Block connectedBlock, ConnectionType connection, Block block) {
		boolean connectionOpen = checkIfConnectionIsOpen(connectedBlock, connection, block);

		if (!connectionOpen) {
			throw new InvalidBlockConnectionException("Connection at connectedBlock is already occupied.");
		}

	}

	/**
	 * Check if the connection between 2 blocks is open and can be used to perform a
	 * move or add on.
	 * 
	 * @param connectedBlock The block to check the connection from
	 * @param connection     The connection to check on the given block
	 * @param block          The block to check the connection to
	 * 
	 * @return A flag indicating if the given connection for the given block is
	 *         open.
	 */
	public boolean checkIfConnectionIsOpen(Block connectedBlock, ConnectionType connection, Block block) {
		boolean connectionOccupied = false;
		switch (connection) {
		case NOCONNECTION:
			break;
		case UP:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = !headBlocks.contains(connectedBlock);

			if (connectionOccupied && block != null) {
				if (block instanceof ExecutableBlock) {
					connectionOccupied = block.getNextBlock() != connectedBlock;
				}
			}
			break;
		case LEFT:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = !headBlocks.contains(connectedBlock);

			if (connectionOccupied && block != null) {
				if (block instanceof ControlBlock) {
					connectionOccupied = block.getConditionBlock() != connectedBlock;
				}
				if (block instanceof OperatorBlock) {
					connectionOccupied = block.getOperand() != connectedBlock;
				}
			}
			break;
		case DOWN:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getNextBlock() != null;
			if (connectionOccupied && block != null) {
				connectionOccupied = !connectedBlock.getNextBlock().getBlockId().equals(block.getBlockId());
			}
			break;
		case BODY:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getFirstBlockOfBody() != null;
			if (connectionOccupied && block != null) {
				connectionOccupied = !connectedBlock.getFirstBlockOfBody().getBlockId().equals(block.getBlockId());
			}
			break;
		case CONDITION:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getConditionBlock() != null;
			if (connectionOccupied && block != null) {
				connectionOccupied = !connectedBlock.getConditionBlock().getBlockId().equals(block.getBlockId());
			}
			break;
		case OPERAND:
			validateConnectedBlockIsInDomain(connectedBlock);
			connectionOccupied = connectedBlock.getOperand() != null;
			if (connectionOccupied && block != null) {
				connectionOccupied = !connectedBlock.getOperand().getBlockId().equals(block.getBlockId());
			}
			break;

		default:
			// It shouldn't be possible to get here.
			break;
		}
		return !connectionOccupied;
	}

	private void validateConnectedBlockIsInDomain(Block connectedBlock) {
		if (connectedBlock == null) {
			throw new NoSuchConnectedBlockException("The requested blockId does not exist in the domain.");
		}
	}

	/**
	 * Retrieve a block by its ID.
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
	 * Remove a block by its ID.
	 * 
	 * @param isChain A flag announcing if a chain of blocks has to be removed or if
	 *                only the given blockId has to be removed.
	 * @param block   The ID of the block to be removed.
	 * 
	 * @throws NoSuchConnectedBlockException If the given BlockID doesn't result in
	 *                                       a block in the domain.
	 * @return A set containing the id's of all the blocks that were removed from
	 *         the domain.
	 */
	public Set<String> removeBlock(String blockId, Boolean isChain) {
		Block b = getBlockByID(blockId);

		if(b instanceof DefinitionBlock) {
			BlockType.removeBlockType(b.getBlockId());
		}
		
		// the given exception may be thrown here.
		validateConnectedBlockIsInDomain(b);
		Set<String> blockIdsToBeRemoved = new HashSet<String>();
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		if (isChain) {
			blocksToBeRemoved = getAllBlocksConnectedToAndAfterACertainBlock(b);
		} else {
			blocksToBeRemoved.add(b);
		}

		if (headBlocks.contains(b)) {
			// If the given block was in HeadBlocks, none of the blocks connected to that
			// block are in headBlocks
			removeBlockFromHeadBlocks(b);

			if (!isChain && b.getNextBlock() != null) {
				addBlockToHeadBlocks(b.getNextBlock());
			}
			if (!isChain && b.getConditionBlock() != null) {
				addBlockToHeadBlocks(b.getConditionBlock());
			}
			if (!isChain && b.getOperand() != null) {
				addBlockToHeadBlocks(b.getOperand());
			}

		} else {
			// Search the parent of b and cut all connections
			ArrayList<String> parentIdentifiers = getConnectedParentIfExists(b.getBlockId());
			Block parent = getBlockByID(parentIdentifiers.get(1));
			switch (ConnectionType.valueOf(parentIdentifiers.get(0))) {
			case BODY:
				parent.setFirstBlockOfBody(null);
				break;
			case CONDITION:
				parent.setConditionBlock(null);
				break;
			case DOWN:
				parent.setNextBlock(null);
				break;
			case OPERAND:
				parent.setOperand(null);
				break;
			default:
				break;
			}

		}

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
	public String moveBlock(String topOfMovedChainBlockId, String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove) {

		Block movedBlock = getBlockByID(movedBlockId);
		Block topMovedBlock = getBlockByID(topOfMovedChainBlockId);
		// The id of the block that's changed
		String movedBlockID = movedBlockId;

//		Set<String> movedBlocks = getAllBlocksConnectedToAndAfterACertainBlock(movedBlock).stream().map(s->s.getBlockId()).collect(Collectors.toSet());
		Block afm = getBlockByID(connectedAfterMoveBlockId);
		ConnectionType connectionBeforeMove = ConnectionType.NOCONNECTION;
		ArrayList<String> beforeMove = new ArrayList<String>();
		Block bfm = null;

		if (movedBlock == null)
			throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

		ArrayList<String> beforeMoveTopBlock = getConnectedParentIfExists(topOfMovedChainBlockId);
//		beforeMove = getConnectedParentIfExists(topOfMovedChainBlockId);
		beforeMove = getConnectedBlockBeforeMove(movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);

		connectionBeforeMove = ConnectionType.valueOf(beforeMove.get(0));
		String bfmBlockId = beforeMove.get(1);
		bfm = getBlockByID(bfmBlockId);

		if (beforeMoveTopBlock.get(0) != "NOCONNECTION") {
			disconnectParentTopOfChain(topOfMovedChainBlockId);
		}
		if (connectionBeforeMove == ConnectionType.NOCONNECTION) {
			// indien no connection dan is er hier geen nood aan verandering
			if (afm == null)
				throw new NoSuchConnectedBlockException("The requested block doesn't exist in the domain.");

			addBlockToHeadBlocks(topMovedBlock);

			if (connectionAfterMove == ConnectionType.DOWN) {
				if (afm.getNextBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);

				afm.setNextBlock(movedBlock);

			} else if (connectionAfterMove == ConnectionType.UP) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(afm);

				if (movedBlock.getNextBlock() != null) {
					Block nextBlockInChain = movedBlock;
					while (nextBlockInChain.getNextBlock() != null) {
						nextBlockInChain = nextBlockInChain.getNextBlock();
					}
					nextBlockInChain.setNextBlock(afm);
					movedBlockID = nextBlockInChain.getBlockId();
				} else {
					movedBlock.setNextBlock(afm);
				}
			}

			else if (connectionAfterMove == ConnectionType.BODY) {
				if (afm.getFirstBlockOfBody() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);

				afm.setFirstBlockOfBody(movedBlock);
			}

			else if (connectionAfterMove == ConnectionType.CONDITION) {
				if (afm.getConditionBlock() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);
				afm.setConditionBlock(movedBlock);

			} else if (connectionAfterMove == ConnectionType.LEFT) {
				if (!headBlocks.contains(afm))
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(afm);

				if (movedBlock.getConditionBlock() != null) {
					Block nextChainBlock = movedBlock.getConditionBlock();
					while (nextChainBlock.getOperand() != null) {
						nextChainBlock = nextChainBlock.getOperand();
					}
					nextChainBlock.setOperand(afm);
					movedBlockID = nextChainBlock.getBlockId();

				} else {
					if (movedBlock.getOperand() != null) {
						Block nextChainBlock = movedBlock;
						while (nextChainBlock.getOperand() != null) {
							nextChainBlock = nextChainBlock.getOperand();
						}
						nextChainBlock.setOperand(afm);
						movedBlockID = nextChainBlock.getBlockId();
					} else {
						// If movedBlock is a controlBlock it doesn't have an operand and vice versa,
						// We don't have to worry about calling setCondition or setOperand.
						movedBlock.setConditionBlock(afm);
						movedBlock.setOperand(afm);
					}
				}

			} else if (connectionAfterMove == ConnectionType.OPERAND) {
				if (afm.getOperand() != null)
					throw new InvalidBlockConnectionException("This socket is not free");

				removeBlockFromHeadBlocks(movedBlock);
				afm.setOperand(movedBlock);
			}
		} else {
			if (bfm == null)
				throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

			if (connectionBeforeMove == ConnectionType.DOWN) {
				if (bfm.getNextBlock() != null && !bfm.getNextBlock().equals(movedBlock))
					throw new InvalidBlockConnectionException(
							"The moved block is not connected to this block or socket");

				if (connectionAfterMove == ConnectionType.NOCONNECTION) {
					bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
					addBlockToHeadBlocks(movedBlock);
				} else {
					if (afm == null)
						throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

					if (connectionAfterMove == ConnectionType.DOWN) {
						if (afm.getNextBlock() != null)
							throw new InvalidBlockConnectionException("This socket is not free");
						bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
						afm.setNextBlock(movedBlock);
					} else if (connectionAfterMove == ConnectionType.UP) {
						if (!headBlocks.contains(afm))
							throw new InvalidBlockConnectionException("This socket is not free");

						bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
						addBlockToHeadBlocks(movedBlock);// connection up is broken so there is no upper block
						removeBlockFromHeadBlocks(afm); // Remove the old headblock from headblocks
						if (movedBlock.getNextBlock() != null) // block is Head block of a blockChain
						{
							Block nextBlockInChain = movedBlock;
							while (nextBlockInChain.getNextBlock() != null) {
								nextBlockInChain = nextBlockInChain.getNextBlock();
							}
							nextBlockInChain.setNextBlock(afm);
							movedBlockID = nextBlockInChain.getBlockId();
						} else {
							movedBlock.setNextBlock(afm);
						}

					} else if (connectionAfterMove == ConnectionType.BODY) {
						if (afm.getFirstBlockOfBody() != null)
							throw new InvalidBlockConnectionException("This socket is not free");

						bfm.setNextBlock(null);// verwijderen referentie van block bij vorige verbonden block
						afm.setFirstBlockOfBody(movedBlock);
					} else if (connectionAfterMove == ConnectionType.LEFT) {
						if (!headBlocks.contains(afm))
							throw new InvalidBlockConnectionException("This socket is not free");

						//

						addBlockToHeadBlocks(movedBlock);
						removeBlockFromHeadBlocks(afm);
						if (movedBlock.getOperand() != null) {
							Block nextChainBlock = movedBlock;
							while (nextChainBlock.getOperand() != null) {
								nextChainBlock = nextChainBlock.getOperand();
							}
							nextChainBlock.setOperand(afm);
							movedBlockID = nextChainBlock.getBlockId();
						} else {
							movedBlock.setOperand(afm);
						}
					}
				}
				// conditionBlock is hier niet mogelijk aangezien we met een UP connectie zaten.
			}

			// ConnectionBeforeMove == connectionType.UP neemt nooit plaats wanneer een
			// block ge-moved wordt.
			else if (connectionBeforeMove == ConnectionType.CONDITION) {
				if (bfm.getConditionBlock() != null && !bfm.getConditionBlock().equals(movedBlock))
					throw new InvalidBlockConnectionException(
							"The moved block is not connected to this block or socket");

				if (connectionAfterMove == ConnectionType.NOCONNECTION) {
					bfm.setConditionBlock(null);
					addBlockToHeadBlocks(movedBlock);

				} else {
					if (afm == null)
						throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

					if (connectionAfterMove == ConnectionType.CONDITION) {
						if (afm.getConditionBlock() != null)
							throw new InvalidBlockConnectionException("This socket is not free");
						bfm.setConditionBlock(null);
						afm.setConditionBlock(movedBlock);
					} else if (connectionAfterMove == ConnectionType.OPERAND) {
						if (afm.getOperand() != null)
							throw new InvalidBlockConnectionException("This socket is not free");

						bfm.setConditionBlock(null);
						afm.setOperand(movedBlock);
					} else if (connectionAfterMove == ConnectionType.LEFT) {
						if (!headBlocks.contains(afm))
							throw new InvalidBlockConnectionException("This socket is not free");

						//

						addBlockToHeadBlocks(movedBlock);
						removeBlockFromHeadBlocks(afm);
						if (movedBlock.getOperand() != null) {
							Block nextChainBlock = movedBlock;
							while (nextChainBlock.getOperand() != null) {
								nextChainBlock = nextChainBlock.getOperand();
							}
							nextChainBlock.setOperand(afm);
							movedBlockID = nextChainBlock.getBlockId();
						} else {
							movedBlock.setOperand(afm);
						}
					}
				}

				// Connectie rechts van andere conditie
			} else if (connectionBeforeMove == ConnectionType.OPERAND) {
				if (bfm.getOperand() != null && !bfm.getOperand().equals(movedBlock))
					throw new InvalidBlockConnectionException(
							"The moved block is not connected to this block or socket");

				if (connectionAfterMove == ConnectionType.NOCONNECTION) {
					bfm.setOperand(null);
					addBlockToHeadBlocks(movedBlock);
				} else {
					if (afm == null)
						throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

					if (connectionAfterMove == ConnectionType.CONDITION) {
						if (afm.getConditionBlock() != null)
							throw new InvalidBlockConnectionException("This socket is not free");
						bfm.setOperand(null);
						afm.setConditionBlock(movedBlock);
					} else if (connectionAfterMove == ConnectionType.OPERAND) {
						if (afm.getOperand() != null)
							throw new InvalidBlockConnectionException("This socket is not free");

						bfm.setOperand(null);
						afm.setOperand(movedBlock);
					} else if (connectionAfterMove == ConnectionType.LEFT) {
						if (!headBlocks.contains(afm))
							throw new InvalidBlockConnectionException("This socket is not free");

						addBlockToHeadBlocks(movedBlock);
						removeBlockFromHeadBlocks(afm);

						// dit moet de gelinkte blok zijn met de TOP block en niet de blok waarbij de
						// effectieve move op gedaan wordt.
						// Er is hier dus nood aan 2 blokken
						disconnectParentTopOfChain(topOfMovedChainBlockId);
						if (movedBlock.getNextBlock() != null) {
							Block nextBlockInChain = movedBlock;
							while (nextBlockInChain.getOperand() != null) {
								nextBlockInChain = nextBlockInChain.getOperand();
							}
							nextBlockInChain.setOperand(afm);
							movedBlockID = nextBlockInChain.getBlockId();
						} else {
							movedBlock.setOperand(afm);
						}
					}
				}
			} else if (connectionBeforeMove == ConnectionType.BODY) {
				if (bfm.getFirstBlockOfBody() != null && !bfm.getFirstBlockOfBody().equals(movedBlock))
					throw new InvalidBlockConnectionException(
							"The moved block is not connected to this block or socket");

				if (connectionAfterMove == ConnectionType.LEFT) {
					if (!headBlocks.contains(afm))
						throw new InvalidBlockConnectionException("This socket is not free");

					// The before move does not change anything here.
					removeBlockFromHeadBlocks(afm);
					movedBlock.setConditionBlock(afm);
					addBlockToHeadBlocks(movedBlock);

				} else {
					bfm.setFirstBlockOfBody(null);

					if (connectionAfterMove == ConnectionType.NOCONNECTION) {
						addBlockToHeadBlocks(movedBlock);
					} else {
						if (afm == null)
							throw new NoSuchConnectedBlockException("The requested block doens't exist in the domain");

						if (connectionAfterMove == ConnectionType.DOWN) {
							if (afm.getNextBlock() != null)
								throw new InvalidBlockConnectionException("This socket is not free");

							afm.setNextBlock(movedBlock);
						} else if (connectionAfterMove == ConnectionType.UP) {
							if (!headBlocks.contains(afm))
								throw new InvalidBlockConnectionException("This socket is not free");

							addBlockToHeadBlocks(movedBlock);
							removeBlockFromHeadBlocks(afm);

							if (movedBlock.getNextBlock() != null) {
								Block nextBlockInChain = movedBlock;
								while (nextBlockInChain.getNextBlock() != null) {
									nextBlockInChain = nextBlockInChain.getNextBlock();
								}
								nextBlockInChain.setNextBlock(afm);
								movedBlockID = nextBlockInChain.getBlockId();
							} else {
								movedBlock.setNextBlock(afm);
							}
						} else if (connectionAfterMove == ConnectionType.BODY) {
							if (afm.getFirstBlockOfBody() != null)
								throw new InvalidBlockConnectionException("This socket is not free");

							afm.setFirstBlockOfBody(movedBlock);
						}
					}
				}
			}
		}

		if (afm != null) {
			addBlockToAllBlocks(afm);
		}
		if (bfm != null) {
			addBlockToAllBlocks(bfm);
		}
		return movedBlockID;
	}

	/**
	 * Disconnects the topOfChainBlock with its old parent if it has one.
	 * 
	 * @param topOfMovedChainBlockId
	 */
	private void disconnectParentTopOfChain(String topOfMovedChainBlockId) {
		ArrayList<String> parentInfo = getConnectedParentIfExists(topOfMovedChainBlockId);

		if (parentInfo.size() != 0) {
			Block parent = getBlockByID(parentInfo.get(1));
			switch (parentInfo.get(0)) {

			case "NOCONNECTION":
				break;
			case "DOWN":
				parent.setNextBlock(null);
				break;
			case "OPERAND":
				parent.setOperand(null);
				break;
			case "CONDITION":
				parent.setConditionBlock(null);
				break;
			case "BODY":
				parent.setFirstBlockOfBody(null);
				break;
			}

			addBlockToAllBlocks(parent);
			if (headBlocks.stream().anyMatch(s -> s.getBlockId().equals(parent.getBlockId()))) {
				addBlockToHeadBlocks(parent);
			}
		}

	}

	/**
	 * Returns the connected block before a move that isn't connected anymore after
	 * the move.
	 * 
	 * @param movedBlockId The id of the block to be moved
	 * @param afmId        The id of the block on which a new connection will be
	 *                     made
	 * @param cafm         The new connection
	 * @return An arrayList with on position 0 the old connection and on position 1
	 *         the old ID. If there is no connected block that isn't connected
	 *         anymore NOCONNECTION will be returned on position 0 and an empty
	 *         string on position 1.
	 */
	public ArrayList<String> getConnectedBlockBeforeMove(String movedBlockId, String afmId, ConnectionType cafm) {
		ArrayList<String> connectedBlockInfo = getConnectedParentIfExists(movedBlockId);
		ConnectionType cbfm = ConnectionType.valueOf(connectedBlockInfo.get(0));
//		cbfm == ConnectionType.BODY ||
		if ((cbfm == ConnectionType.DOWN || cbfm == ConnectionType.CONDITION || cbfm == ConnectionType.OPERAND)
				&& cafm == ConnectionType.LEFT) {
			connectedBlockInfo.set(0, ConnectionType.NOCONNECTION.toString());
			connectedBlockInfo.set(1, "");
		}

		return connectedBlockInfo;
	}

	/**
	 * Determine the connection before the remove happened.
	 * 
	 * @param movedBlockId The ID of the block that will be removed.
	 * @return A list with 2 elements, the first the ConnectionType in String form
	 *         and the second the ID of the block with wich the connection was had.
	 */
	public ArrayList<String> getConnectedBlockBeforeRemove(String removedBlockId) {
		ArrayList<String> connectedBlockInfo = getConnectedParentIfExists(removedBlockId);
		ConnectionType cbfm = ConnectionType.valueOf(connectedBlockInfo.get(0));
		if (cbfm == ConnectionType.NOCONNECTION) {
			Block block = getBlockByID(removedBlockId);
			if (block.getNextBlock() != null) {
				connectedBlockInfo.set(1, block.getNextBlock().getBlockId());
				connectedBlockInfo.set(0, ConnectionType.UP.toString());
			}
			if (block.getConditionBlock() != null) {
				connectedBlockInfo.set(1, block.getConditionBlock().getBlockId());
				connectedBlockInfo.set(0, ConnectionType.LEFT.toString());
			}
			if (block.getOperand() != null) {
				connectedBlockInfo.set(1, block.getOperand().getBlockId());
				connectedBlockInfo.set(0, ConnectionType.LEFT.toString());
			}

		}

		return connectedBlockInfo;
	}

	/**
	 * Find the connection the given block had before he is moved.
	 * 
	 * @param movedBlockId The ID of the block that will be moved.
	 * @return A list with 2 elements, the first the ConnectionType in String form
	 *         and the second the ID of the block with wich the connection was had.
	 */
	public ArrayList<String> getConnectedParentIfExists(String movedBlockId) {
		Block movedBlock = getBlockByID(movedBlockId);
		Iterator itAllBlocks = allBlocks.entrySet().iterator();
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		while (itAllBlocks.hasNext()) {
			Map.Entry element = (Entry) itAllBlocks.next();
			Block block = (Block) element.getValue();
			if (block instanceof ActionBlock) {
				if (block.getNextBlock() != null && block.getNextBlock().equals(movedBlock)) {
					connectedBlockInfo.add("DOWN");
					connectedBlockInfo.add(block.getBlockId());
				}
			} else if (block instanceof DefinitionBlock) {
				if (block.getFirstBlockOfBody() != null && block.getFirstBlockOfBody().equals(movedBlock)) {
					connectedBlockInfo.add("BODY");
					connectedBlockInfo.add(block.getBlockId());
				}
			} else if (block instanceof ControlBlock) {
				if (block.getNextBlock() != null && block.getNextBlock().equals(movedBlock)) {
					connectedBlockInfo.add("DOWN");
					connectedBlockInfo.add(block.getBlockId());
				} else if (block.getConditionBlock() != null && block.getConditionBlock().equals(movedBlock)) {
					connectedBlockInfo.add("CONDITION");
					connectedBlockInfo.add(block.getBlockId());
				} else if (block.getFirstBlockOfBody() != null && block.getFirstBlockOfBody().equals(movedBlock)) {
					connectedBlockInfo.add("BODY");
					connectedBlockInfo.add(block.getBlockId());
				}
			} else if (block instanceof OperatorBlock) {

				if (block.getOperand() != null && block.getOperand().equals(movedBlock)) {
					connectedBlockInfo.add("OPERAND");
					connectedBlockInfo.add(block.getBlockId());
				}

			}
		}
		if (connectedBlockInfo.size() == 0) {
			connectedBlockInfo.add("NOCONNECTION");
			connectedBlockInfo.add("");

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

	/**
	 * Checks if the chain starting from the given block is valid.
	 * 
	 * @param nextBlockInChain First block of the chain to check.
	 * @return A flag indicating if the chain is valid or not.
	 */
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
	 * Method used to check if ControlBlock is in a valid state.
	 * 
	 * @param block The controlblock that needs to be checked.
	 * @return A flag indicating if the controlBlock is in a valid state or not.
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
	 * @param block the block to check if it's valid
	 * @return a flag indicating if a chain of operand finishes with a
	 *         conditionBlock.
	 */
	public boolean checkIfValidStatement(Block block) {
		if (block != null) {
			if (block.getOperand() instanceof ConditionBlock)
				return true;
			return checkIfValidStatement(block.getOperand());
		}
		return false;

	}

	/**
	 * Method that returns the first block to be executed in the program. This
	 * method can only be called in a valid program state. If that is the case,
	 * there is only one block in headBlocks and that block gets returned.
	 * 
	 * @return The first block to be executed in the program.
	 */
	public ExecutableBlock findFirstBlockToBeExecuted() {
		// We find the "first" item in the HashSet, that should always be the only item
		// in the set, otherwise it would not be in an ValidState
		Iterator<Block> iter = headBlocks.iterator();
		ExecutableBlock firstExecutableBlock = (ExecutableBlock) iter.next();
		return firstExecutableBlock;
	}

	/**
	 * Adds the given block to the list of headBlocks.
	 * 
	 * @param block The block that needs to be added to the list of headBlocks.
	 */
	private void addBlockToHeadBlocks(Block block) {
		if (this.headBlocks.stream().anyMatch(b -> b.getBlockId().equals(block.getBlockId()))) {
			deepReplace(block, this.headBlocks);
		}
		Optional<Block> b = this.headBlocks.stream().filter(s -> s.getBlockId().equals(block.getBlockId())).findAny();
		if (b.isPresent()) {
			this.headBlocks.remove(b.get());
		}
		this.headBlocks.add(block);
	}

	private void addBlockToAllBlocks(Block block) {
		for (Block b : getAllBlocksConnectedToAndAfterACertainBlock(block)) {
			if (allBlocks.containsKey(b.getBlockId())) {
				deepReplace(b, this.allBlocks.values());
			}

			this.allBlocks.put(b.getBlockId(), b);
		}
	}

	private void deepReplace(Block block, Collection<Block> collection) {
		for (Block b : collection) {
			deepReplace(block, b);
		}
	}

	private void deepReplace(Block block, Block parent) {
		Optional<Block> connectedBlock = getAllBlocksConnectedToAndAfterACertainBlock(parent).stream()
				.filter(s -> (block != null && s.getBlockId().equals(block.getBlockId()) && parent != null
						&& !s.getBlockId().equals(parent.getBlockId())))
				.findAny();
		if (connectedBlock.isPresent()) {
			// index 1: ID
			// index 0: connection
			ArrayList<String> parentInfo = getConnectedParentIfExists(connectedBlock.get().getBlockId());

			Block firstParent = getBlockByID(parentInfo.get(1));
			ConnectionType connection = ConnectionType.valueOf(parentInfo.get(0));

			switch (connection) {
			case BODY:
				firstParent.setFirstBlockOfBody(block);
				break;
			case CONDITION:
				firstParent.setConditionBlock(block);
				break;
			case OPERAND:
				firstParent.setOperand(block);
				break;
			case DOWN:
				firstParent.setNextBlock(block);
				break;
			default:
				break;
			}

			deepReplace(firstParent, parent);

		}

	}

	private void removeBlockFromHeadBlocks(Block block) {
		this.headBlocks.removeIf(s -> s.getBlockId().equals(block.getBlockId()));
	}

	private void removeBlockFromAllBlocks(Block block) {
		this.allBlocks.remove(block.getBlockId());
	}

	/**
	 * Checks if the maximum number of blocks has been reached.
	 * 
	 * @return getMaxNbOfBlocks() =< totalNumberOfBlocks
	 */
	public boolean checkIfMaxNbOfBlocksReached() {
		return this.getMaxNbOfBlocks() <= this.allBlocks.size();
	}

	// TODO: how to test this?
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

	/**
	 * Returns all the blocks that are connected to the given block in every
	 * direction except for up.
	 * 
	 * @param block The block for which this method returns its connected blocks.
	 * @return A set of all the blocks that are connected to the given block in
	 *         every direction except for up.
	 */
	public Set<Block> getAllBlocksConnectedToAndAfterACertainBlock(Block block) {
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
	 * Returns all the blockID's in the body of a given BodyCavityBlock
	 * 
	 * @param BodyCavityBlock The BodyCavityBlock of which you want to retrieve all
	 *                        Blocks in the body.
	 * @return A set containing the blockID of the blocks in the body of the given
	 *         BodyCavityBlock, there won't be any ID's of assessable blocks.
	 */
	public Set<String> getAllBlockIDsInBody(BodyCavityBlock bodyCavityBlock) {
		Set<String> blockIDsInBody = new HashSet<String>();

		getAllBlocksConnectedToAndAfterACertainBlock(bodyCavityBlock.getFirstBlockOfBody()).stream()
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

	/**
	 * Finds all the controlblocks who are not in another controlBlock.
	 * 
	 * @return A set of all the controlblocks who are not in another controlBlock.
	 */
	public Set<ControlBlock> getAllHeadControlBlocks() {
		Set<ControlBlock> firstControlBlocks = new HashSet<ControlBlock>();

		for (ExecutableBlock block : headBlocks.stream().filter(b -> b instanceof ExecutableBlock)
				.map(b -> (ExecutableBlock) b).collect(Collectors.toSet())) {
			firstControlBlocks.addAll(getAllBlocksInBodyTopLevel(block).stream().filter(b -> b instanceof ControlBlock)
					.map(b -> (ControlBlock) b).collect(Collectors.toSet()));
		}

		return firstControlBlocks;
	}

	/**
	 * Finds the enclosing controlblock of the given block.
	 * 
	 * @param block The block to find the enclosing controlblock of.
	 * @return The enclosing controlblock. If there is no enclosing block, the
	 *         method returns null.
	 */
	public ControlBlock getEnclosingControlBlock(ExecutableBlock block) {
		Set<ControlBlock> chain = new HashSet<ControlBlock>();
		for (Block headBlock : headBlocks) {
			getAllBlocksConnectedToAndAfterACertainBlock(headBlock).stream()
					.filter(e -> !e.getBlockId().equals(block.getBlockId()) && (e instanceof ControlBlock))
					.forEach(p -> chain.add((ControlBlock) p));
		}
		for (ControlBlock c : chain) {
			for (ExecutableBlock topLevelBlock : getAllBlocksInBodyTopLevel(c.getFirstBlockOfBody())) {
				if (topLevelBlock.getBlockId().equals(block.getBlockId())) {
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

	/**
	 * Finds all the ID's of the blocks that are below the given block.
	 * 
	 * @param blockID The ID of the block from which we want to find all blocks
	 *                below.
	 * @return A set of blockID's of the blocks below the given block.
	 */
	public Set<String> getAllBlockIDsBelowCertainBlock(Block block) {
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		getAllBlocksAfterACertainBlock(block).stream().map(s -> s.getBlockId()).forEach(s -> blockIDsUnderNeath.add(s));
		return blockIDsUnderNeath;
	}

	/**
	 * Finds all headblocks.
	 * 
	 * @return A set of all headblocks.
	 */
	public Set<Block> getAllHeadBlocks() {
		return new HashSet<Block>(headBlocks);
	}

	public String getBlockIdToPerformMoveOn(String topOfMovedChainBlockId, String movedBlockId,
			ConnectionType connectionAfterMove) {
		String movedID = topOfMovedChainBlockId;
		if (connectionAfterMove == ConnectionType.LEFT) {
			Block movedBlock = getBlockByID(movedBlockId);
			if (movedBlock instanceof ControlBlock) {
				movedID = movedBlockId;
			} else {
				ArrayList<String> parentInfo = getConnectedParentIfExists(movedBlockId);
				ConnectionType parentConnection = ConnectionType.valueOf(parentInfo.get(0));
				while (parentConnection == ConnectionType.OPERAND) {
					parentInfo = getConnectedParentIfExists(parentInfo.get(1));
					parentConnection = ConnectionType.valueOf(parentInfo.get(0));
				}
				if (parentConnection == ConnectionType.CONDITION) {
					movedID = movedBlockId;
				}
			}

		}
		return movedID;
	}

	public Boolean restoreBlockSnapshot(BlockSnapshot snapshot) {
		Set<Block> connectedBlocks = getAllBlocksConnectedToAndAfterACertainBlock(snapshot.getBlock());

		Boolean isRemoved = false;
		for (Block b : connectedBlocks) {
			if (getBlockByID(b.getBlockId()) == null) {
				isRemoved = true;
				break;
			}
		}

		if (isRemoved) {
			// removed blocks
			if (snapshot.getConnectedBlockAfterSnapshot() != null) {
				Block cb = snapshot.getConnectedBlockAfterSnapshot();
				if (getAllBlockIDsBelowCertainBlock(snapshot.getBlock()).contains(cb.getBlockId())) {
					removeBlockFromHeadBlocks(cb);
					addBlockToHeadBlocks(snapshot.getBlock());
				} else {
					deepReplace(cb, headBlocks);
					if (getAllHeadBlocks().stream().anyMatch(s -> s.getBlockId().equals(cb.getBlockId()))) {
						addBlockToHeadBlocks(cb);
					}
				}
				addBlockToAllBlocks(cb);

			} else {
				addBlockToHeadBlocks(snapshot.getBlock());
			}

			for (Block b : connectedBlocks) {
				addBlockToAllBlocks(b);
			}
		} else {
			// blocks are still present in the domain (Move)
			Block b = snapshot.getBlock();
			if (snapshot.getConnectedBlockBeforeSnapshot() != null) {

				Block cb = snapshot.getConnectedBlockBeforeSnapshot();
				if (cb.getConditionBlock() != null && cb.getConditionBlock().equals(b)) {
					cb.setConditionBlock(null);

				}
				if (cb.getFirstBlockOfBody() != null && cb.getFirstBlockOfBody().equals(b)) {
					cb.setFirstBlockOfBody(null);
				}
				if (cb.getOperand() != null && cb.getOperand().equals(b)) {
					cb.setOperand(null);
				}
				if (cb.getNextBlock() != null && cb.getNextBlock().equals(b)) {
					cb.setNextBlock(null);
				}

				if (b.getConditionBlock() != null && b.getConditionBlock().equals(cb)) {
					b.setConditionBlock(null);
				}
				if (b.getFirstBlockOfBody() != null && b.getFirstBlockOfBody().equals(cb)) {
					b.setFirstBlockOfBody(null);
				}
				if (b.getOperand() != null && b.getOperand().equals(cb)) {
					b.setOperand(null);
				}
				if (b.getNextBlock() != null && b.getNextBlock().equals(cb)) {
					b.setNextBlock(null);
				}

				if (getAllBlockIDsUnderneath(getBlockByID(b.getBlockId())).contains(cb.getBlockId())) {
					addBlockToHeadBlocks(cb);
//					addBlockToHeadBlocks(b);					
				} else {
					addBlockToHeadBlocks(b);
				}
				addBlockToAllBlocks(cb);
				addBlockToAllBlocks(b);

			}
			if (snapshot.getConnectedBlockAfterSnapshot() != null) {
				Block ab = snapshot.getConnectedBlockAfterSnapshot();
				addBlockToAllBlocks(ab);
				if (headBlocks.stream().anyMatch(s -> s.getBlockId().equals(ab.getBlockId()))) {

					headBlocks.removeIf(s -> s.getBlockId().equals(ab.getBlockId()));
					addBlockToHeadBlocks(ab);
				} else {
					deepReplace(ab, headBlocks);
				}
				if (headBlocks.stream().anyMatch(s -> s.getBlockId().equals(b.getBlockId()))) {
					removeBlockFromHeadBlocks(b);
				} else {
					/**
					 * This part of the code looks for the top of a chain within the changed blocks
					 * of a snapshot.
					 */

//					boolean foundParent = true;
//					Block topOfMoveBlock=b;
//					String topOfMoveBlockId = b.getBlockId();
//					while (foundParent) {
//						Optional<Block> topOfMoveBlockCheck = snapshot.getChangingBlocks().stream()
//								.filter(s -> (s.getConditionBlock() != null
//										&& s.getConditionBlock().getBlockId().equals(topOfMoveBlockId))
//										|| (s.getNextBlock() != null
//												&& s.getNextBlock().getBlockId().equals(topOfMoveBlockId))
//										|| (s.getFirstBlockOfBody() != null
//												&& s.getFirstBlockOfBody().getBlockId().equals(topOfMoveBlockId))
//										|| (s.getConditionBlock() != null
//												&& s.getConditionBlock().getBlockId().equals(topOfMoveBlockId))
//										|| (s.getOperand() != null
//												&& s.getOperand().getBlockId().equals(topOfMoveBlockId)))
//								.findFirst();
//						foundParent=topOfMoveBlockCheck.isPresent();
//						if(foundParent) {
//							topOfMoveBlock=topOfMoveBlockCheck.get();
//							topOfMoveBlockId=topOfMoveBlock.getBlockId();
//						}
//					}
					Optional<Block> topOfMoveBlock = snapshot.getChangingBlocks().stream()
							.filter(s -> headBlocks.stream().anyMatch(d -> d.getBlockId().equals(s.getBlockId())))
							.findFirst();
					if (topOfMoveBlock.isPresent()) {
						removeBlockFromHeadBlocks(topOfMoveBlock.get());
					}

				}
			}
		}

		return isRemoved;
	}

	/**
	 * Finds the type of connection between 2 blocks.
	 * 
	 * @param parent The parent block.
	 * @param child  The child block.
	 * @return The type of connection between the 2 given blocks.
	 */
	public ConnectionType getConnectionType(Block parent, Block child) {
		if (parent == null) {
			return ConnectionType.NOCONNECTION;
		}
		if (parent.getConditionBlock() != null && parent.getConditionBlock().equals(child)) {
			return ConnectionType.CONDITION;
		}
		if (parent.getFirstBlockOfBody() != null && parent.getFirstBlockOfBody().equals(child)) {
			return ConnectionType.BODY;
		}
		if (parent.getNextBlock() != null && parent.getNextBlock().equals(child)) {
			return ConnectionType.DOWN;
		}
		if (parent.getOperand() != null && parent.getOperand().equals(child)) {
			return ConnectionType.OPERAND;
		}
		if ((child.getOperand() != null && child.getOperand().equals(parent))
				|| (child.getConditionBlock() != null && child.getConditionBlock().equals(parent))) {
			return ConnectionType.LEFT;
		}
		if (child.getNextBlock() != null && child.getNextBlock().equals(parent)) {
			return ConnectionType.UP;
		}
		return ConnectionType.NOCONNECTION;
	}

}
