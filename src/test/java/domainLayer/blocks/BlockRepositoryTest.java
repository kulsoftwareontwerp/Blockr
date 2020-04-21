/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

	private HashSet<Block> headBlocks = new HashSet<Block>();
	private HashMap<String, Block> allBlocks = new HashMap<String, Block>();
	
	@Spy
	private BlockRepository blockRepo;
	
	private ActionBlock movedActionBlock;
	private ConditionBlock movedConditionBlock;
	private ActionBlock actionBlock;
	private ControlBlock ifBlock;	
	private OperatorBlock notBlock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		movedActionBlock = spy(new ActionBlock("movedActionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		movedConditionBlock = spy(new ConditionBlock("movedConditionBlockId", new BlockType("Condition", BlockCategory.CONDITION)));
		actionBlock = spy(new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		ifBlock = spy(new IfBlock("ifBlock"));
		notBlock = spy(new NotBlock("notBlockId"));
		
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		allBlocks.put(ifBlock.getBlockId(), ifBlock);
		allBlocks.put(notBlock.getBlockId(), notBlock);
		
		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));
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
	public void testGetConnectedParentIfExists_NoConnection_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("NOCONNECTION");
		expectedConnectedBlockInfo.add("");
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ActionBlockDownConnection_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(actionBlock.getNextBlock()).thenReturn(movedActionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("DOWN");
		expectedConnectedBlockInfo.add(actionBlock.getBlockId());
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ControlBlockDownConnection_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(ifBlock.getNextBlock()).thenReturn(movedActionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("DOWN");
		expectedConnectedBlockInfo.add(ifBlock.getBlockId());
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ControlBlockConditionConnection_Positive() {
		String blockIdParam = "movedConditionBlockId";
		Mockito.doReturn(movedConditionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(ifBlock.getConditionBlock()).thenReturn(movedConditionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("CONDITION");
		expectedConnectedBlockInfo.add(ifBlock.getBlockId());
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ControlBlockBodyConnection_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(movedActionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("BODY");
		expectedConnectedBlockInfo.add(ifBlock.getBlockId());
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_OperatorBlockOperandConnection_Positive() {
		String blockIdParam = "movedConditionBlockId";
		Mockito.doReturn(movedConditionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(notBlock.getOperand()).thenReturn(movedConditionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("OPERAND");
		expectedConnectedBlockInfo.add(notBlock.getBlockId());
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
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
