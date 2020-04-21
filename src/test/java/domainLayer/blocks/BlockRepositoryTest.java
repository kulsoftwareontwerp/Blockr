/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
import types.ConnectionType;

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
	private ActionBlock actionBlockNotInHeadBlocks;
	
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
		actionBlockNotInHeadBlocks = spy(new ActionBlock("actionBlockNotInHeadBlocksId", new BlockType("Action", BlockCategory.ACTION)));
		
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		allBlocks.put(ifBlock.getBlockId(), ifBlock);
		allBlocks.put(notBlock.getBlockId(), notBlock);
		
		headBlocks.add(actionBlock);
		
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
		assertEquals(actionBlock, blockRepo.getBlockByID(actionBlock.getBlockId()));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainTrue_HeadBlocksContainsBlock_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlock).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlock);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlock.getBlockId());
		
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_HeadBlocksContainsBlock_GettersNotNull_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = false;
		
		Mockito.doReturn(actionBlock).when(blockRepo).getBlockByID(removedBlockId);
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);
		when(actionBlock.getConditionBlock()).thenReturn(notBlock);
		when(actionBlock.getOperand()).thenReturn(notBlock);
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlock.getBlockId());
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Body_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);
		
		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("BODY");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");	
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());
		
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
		verify(ifBlock,atLeastOnce()).setFirstBlockOfBody(null);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Condition_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);
		
		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("CONDITION");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");	
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());
		
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
		verify(ifBlock,atLeastOnce()).setConditionBlock(null);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Down_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);
		
		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("DOWN");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");	
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());
		
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
		verify(ifBlock,atLeastOnce()).setNextBlock(null);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Operand_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);
		
		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("OPERAND");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(notBlock).when(blockRepo).getBlockByID("parentId");	
		
		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());
		
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));		
		verify(notBlock,atLeastOnce()).setOperand(null);
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
	public void testGetConnectedBlockBeforeMove_NoNoConnection_Positive() {
		String removedblockIdParam = "removedActionBlockId";
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		connectedBlockInfo.add("DOWN");
		connectedBlockInfo.add("SomeId");
		Mockito.doReturn(connectedBlockInfo).when(blockRepo).getConnectedParentIfExists(removedblockIdParam);
		
		assertEquals(connectedBlockInfo, blockRepo.getConnectedBlockBeforeRemove(removedblockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeMove(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeMove_NoConnection_NextBlockNotNull_Positive() {
		String removedblockIdParam = "removedActionBlockId";
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		connectedBlockInfo.add("NOCONNECTION");
		connectedBlockInfo.add("");
		Mockito.doReturn(connectedBlockInfo).when(blockRepo).getConnectedParentIfExists(removedblockIdParam);
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(removedblockIdParam);
		when(movedActionBlock.getNextBlock()).thenReturn(actionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("UP");
		expectedConnectedBlockInfo.add(actionBlock.getBlockId());
		
		assertEquals(connectedBlockInfo, blockRepo.getConnectedBlockBeforeRemove(removedblockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeMove(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeMove_NoConnection_ConditionBlockNotNull_Positive() {
		String removedblockIdParam = "ifBlock";
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		connectedBlockInfo.add("NOCONNECTION");
		connectedBlockInfo.add("");
		Mockito.doReturn(connectedBlockInfo).when(blockRepo).getConnectedParentIfExists(removedblockIdParam);
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID(removedblockIdParam);
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("LEFT");
		expectedConnectedBlockInfo.add(notBlock.getBlockId());
		
		assertEquals(connectedBlockInfo, blockRepo.getConnectedBlockBeforeRemove(removedblockIdParam));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeMove(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeMove_NoConnection_OperandNotNull_Positive() {
		String removedblockIdParam = "notBlock";
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		connectedBlockInfo.add("NOCONNECTION");
		connectedBlockInfo.add("");
		Mockito.doReturn(connectedBlockInfo).when(blockRepo).getConnectedParentIfExists(removedblockIdParam);
		Mockito.doReturn(notBlock).when(blockRepo).getBlockByID(removedblockIdParam);
		when(notBlock.getOperand()).thenReturn(movedConditionBlock);
		
		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("LEFT");
		expectedConnectedBlockInfo.add(movedConditionBlock.getBlockId());
		
		assertEquals(connectedBlockInfo, blockRepo.getConnectedBlockBeforeRemove(removedblockIdParam));
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
	public void testCheckIfMaxNbOfBlocksReached_False_Positive() {
		assertFalse(blockRepo.checkIfMaxNbOfBlocksReached());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#checkIfMaxNbOfBlocksReached()}.
	 */
	@Test
	public void testCheckIfMaxNbOfBlocksReached_True_Positive() {
		for (int i = 0; i < blockRepo.getMaxNbOfBlocks(); i++) {
			allBlocks.put(Integer.toString(i)+"_CheckMaxNumberOfBlocks", actionBlock);
		}
		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));
		assertTrue(blockRepo.checkIfMaxNbOfBlocksReached());
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
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlocksConnectedToAndAfterACertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlocksConnectedToAndAfterACertainBlock_BlockNotNull_Positive() {
		HashSet<Block> expectedResult = new HashSet<Block>();
		expectedResult.add(actionBlock);
		
		assertEquals(expectedResult, blockRepo.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getAllBlocksConnectedToAndAfterACertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlocksConnectedToAndAfterACertainBlock_BlockNull_Positive() {
		assertEquals(new HashSet<Block>(), blockRepo.getAllBlocksConnectedToAndAfterACertainBlock(null));
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
