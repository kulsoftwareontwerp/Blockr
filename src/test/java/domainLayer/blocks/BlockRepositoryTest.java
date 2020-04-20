/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.Action;

import applicationLayer.GameController;
import types.BlockCategory;
import types.BlockType;

/**
 * BlockRepositoryTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockRepositoryTest {

	@Spy
	private BlockRepository blockRepo;
	
	private ActionBlock actionBlock;
	private ControlBlock ifBlock;	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		actionBlock = new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION));
		ifBlock = new IfBlock("IfBlock");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getBlockByID(java.lang.String)}.
	 */
	@Test
	public void testGetBlockByID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#moveBlock(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeMove(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeMove() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram_OneHeadBlockTrue_Positive() {
		HashSet<Block> headBlocks = new HashSet<Block>(Arrays.asList(actionBlock));
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		blockRepo = new BlockRepository(headBlocks, allBlocks);
		
		assertTrue(blockRepo.checkIfValidProgram());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram_TwoHeadBlocksFalse_Positive() {
		HashSet<Block> headBlocks = new HashSet<Block>(Arrays.asList(actionBlock, ifBlock));
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		allBlocks.put(ifBlock.getBlockId(), ifBlock);
		
		blockRepo = new BlockRepository(headBlocks, allBlocks);
		
		assertFalse(blockRepo.checkIfValidProgram());
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#CheckIfChainIsValid(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfChainIsValid() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfValidControlBlock(domainLayer.blocks.ControlBlock)}.
	 */
	@Test
	public void testCheckIfValidControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfValidStatement(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfValidStatement() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_Positive() {
		HashSet<Block> headBlocks = new HashSet<Block>(Arrays.asList(actionBlock));
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		blockRepo = new BlockRepository(headBlocks, allBlocks);
		
		assertEquals(actionBlock, blockRepo.findFirstBlockToBeExecuted());
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfMaxNbOfBlocksReached()}.
	 */
	@Test
	public void testCheckIfMaxNbOfBlocksReached() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlockIDsUnderneath(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneathBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlockIDsInBody(domainLayer.blocks.ControlBlock)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getMaxNbOfBlocks()}.
	 */
	@Test
	public void testGetMaxNbOfBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneathString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getEnclosingControlBlock(domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testGetEnclosingControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlockIDsBelowCertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getBlockIdToPerformMoveOn(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testGetBlockIdToPerformMoveOn() {
		fail("Not yet implemented");
	}

}
