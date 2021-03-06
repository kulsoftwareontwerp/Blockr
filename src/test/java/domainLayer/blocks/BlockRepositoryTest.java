/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.kuleuven.swop.group17.GameWorldApi.Action;

import applicationLayer.GameController;

import exceptions.NoSuchConnectedBlockException;

import exceptions.InvalidBlockConnectionException;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;

/**
 * BlockRepositoryTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockRepositoryTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Spy
	private HashSet<Block> headBlocks = spy(new HashSet<Block>());
	@Spy
	private HashMap<String, Block> allBlocks = spy(new HashMap<String, Block>());

	@Spy
	@InjectMocks
	private BlockRepository blockRepo;

	private ActionBlock movedActionBlock;
	private ConditionBlock movedConditionBlock;
	private ActionBlock actionBlock;
	private ControlBlock ifBlock;
	private OperatorBlock notBlock;
	private OperatorBlock notBlock2;
	private ActionBlock actionBlockNotInHeadBlocks;
	private DefinitionBlock definitionBlock;
	private CallFunctionBlock callBlock;

	@Mock
	private BlockSnapshot blockSnapshot;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		movedActionBlock = spy(new ActionBlock("movedActionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		movedConditionBlock = spy(
				new ConditionBlock("movedConditionBlockId", new BlockType("Condition", BlockCategory.CONDITION)));
		actionBlock = spy(new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		ifBlock = spy(new IfBlock("ifBlock"));
		notBlock = spy(new NotBlock("notBlockId"));
		notBlock2 = spy(new NotBlock("notBlock2Id"));

		actionBlockNotInHeadBlocks = spy(
				new ActionBlock("actionBlockNotInHeadBlocksId", new BlockType("Action", BlockCategory.ACTION)));
		definitionBlock = spy(new DefinitionBlock("definitionBlockId"));
		callBlock = spy(
				new CallFunctionBlock("callBlockId", new BlockType("Call", BlockCategory.CALL, "definitionBlockId")));

		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		allBlocks.put(ifBlock.getBlockId(), ifBlock);
		allBlocks.put(notBlock.getBlockId(), notBlock);
		allBlocks.put(movedConditionBlock.getBlockId(), movedConditionBlock);
		allBlocks.put(definitionBlock.getBlockId(), definitionBlock);
		allBlocks.put(callBlock.getBlockId(), callBlock);

		headBlocks.add(actionBlock);
		headBlocks.add(definitionBlock);

		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));

		MockitoAnnotations.initMocks(this);
		clearInvocations(headBlocks);
		clearInvocations(allBlocks);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		allBlocks.clear();
		headBlocks.clear();

		movedActionBlock = spy(new ActionBlock("movedActionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		movedConditionBlock = spy(
				new ConditionBlock("movedConditionBlockId", new BlockType("Condition", BlockCategory.CONDITION)));
		actionBlock = spy(new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION)));
		ifBlock = spy(new IfBlock("ifBlock"));
		notBlock = spy(new NotBlock("notBlockId"));
		notBlock2 = spy(new NotBlock("notBlock2Id"));
		actionBlockNotInHeadBlocks = spy(
				new ActionBlock("actionBlockNotInHeadBlocksId", new BlockType("Action", BlockCategory.ACTION)));

	}

	@Test
	public void testAddBlockPositiveNOCONNECTION() {

		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		}).when(blockRepo).checkIfConnectionIsOpen(any(), any(), any());
		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			for (BlockCategory c : BlockCategory.values()) {
				if (b.type() == "WrongType") {

				} else {
					blockRepo.addBlock((BlockType) b, null, ConnectionType.NOCONNECTION);
				}
				verify(headBlocks, atLeast(1)).add(any(Block.class));
				verify(allBlocks, atLeast(1)).put(any(String.class), any(Block.class));// 4 times in setup

			}
		}

	}

	@Test
	public void testAddBlockPositiveConnection() {
		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return true;
			}
		}).when(blockRepo).checkIfConnectionIsOpen(any(), any(), any());
		when(blockRepo.getBlockByID(notBlock2.getBlockId())).thenReturn(notBlock2);
		blockRepo.addBlock(BlockType.IF, ifBlock.getBlockId(), ConnectionType.DOWN);
		blockRepo.addBlock(BlockType.IF, actionBlock.getBlockId(), ConnectionType.UP);
		blockRepo.addBlock(BlockType.IF, ifBlock.getBlockId(), ConnectionType.BODY);
		blockRepo.addBlock(BlockType.NOT, ifBlock.getBlockId(), ConnectionType.CONDITION);
		blockRepo.addBlock(BlockType.NOT, notBlock.getBlockId(), ConnectionType.OPERAND);
		blockRepo.addBlock(BlockType.IF, notBlock2.getBlockId(), ConnectionType.LEFT);
		verify(headBlocks,atLeast(1)).add(any(Block.class));
		verify(allBlocks, atLeast(1)).put(any(String.class), any(Block.class));

		actionBlock.setNextBlock(null);
	}

	@Test
	public void testAddBlockNegativeConnection() {
		String excMessage = "Connection at connectedBlock is already occupied.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);

		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return false;
			}
		}).when(blockRepo).checkIfConnectionIsOpen(any(), any(), any());

		blockRepo.addBlock(BlockType.IF, actionBlock.getBlockId(), ConnectionType.UP);
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_NoConnection_Positive() {
		assertTrue(blockRepo.checkIfConnectionIsOpen(null, ConnectionType.NOCONNECTION, null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfConnectionIsOpen(Block, ConnectionType, Block)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_ConnectedBlockNull_NoSuchConnectedBlockException() {
		String excMessage = "The requested blockId does not exist in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);

		blockRepo.checkIfConnectionIsOpen(null, ConnectionType.UP, null);
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_HeadBlockContainsConnectedBlock_Positive() {
		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.UP, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_HeadBlockNotContainsConnectedBlock_BlockNull_Positive() {
		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.UP, null));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_HeadBlockNotContainsConnectedBlock_BlockNotExecutableBlock_Positive() {
		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.UP, notBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_HeadBlockNotContainsConnectedBlock_BlockExecutableBlock_NextBlockNotConnectedBlock_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.UP, actionBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Up_HeadBlockNotContainsConnectedBlock_BlockExecutableBlock_NextBlockConnectedBlock_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(actionBlockNotInHeadBlocks);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.UP, actionBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockContainsConnectedBlock_Positive() {
		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.LEFT, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockNull_Positive() {
		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.LEFT, null));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockNotControlOrOperatorBlock_Positive() {
		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.LEFT, actionBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockControlBlock_ConditionNotConnectedBlock_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.LEFT, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockControlBlock_ConditionIsConnectedBlock_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);

		assertTrue(blockRepo.checkIfConnectionIsOpen(notBlock, ConnectionType.LEFT, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockOperatorBlock_OperandNotConnectedBlock_Positive() {
		when(notBlock.getOperand()).thenReturn(notBlock2);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlockNotInHeadBlocks, ConnectionType.LEFT, notBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Left_HeadBlockNotContainsConnectedBlock_BlockOperatorBlock_OperandIsConnectedBlock_Positive() {
		when(notBlock.getOperand()).thenReturn(notBlock2);

		assertTrue(blockRepo.checkIfConnectionIsOpen(notBlock2, ConnectionType.LEFT, notBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Down_NextBlockNull_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(null);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.DOWN, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Down_NextBlockNotNull_BlockNotNull_NextBlockEqualsBlock_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.DOWN, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Down_NextBlockNotNull_BlockNotNull_NextBlockNotEqualsBlock_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(actionBlockNotInHeadBlocks);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.DOWN, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Down_NextBlockNotNull_BlockNull_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.DOWN, null));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Body_FirstBlockOfBodyNull_Positive() {
		when(actionBlock.getFirstBlockOfBody()).thenReturn(null);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.BODY, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Body_FirstBlockOfBodyNotNull_BlockNotNull_FirstBlockOfBodyEqualsBlock_Positive() {
		when(actionBlock.getFirstBlockOfBody()).thenReturn(ifBlock);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.BODY, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Body_FirstBlockOfBodyNotNull_BlockNotNull_FirstBlockOfBodyNotEqualsBlock_Positive() {
		when(actionBlock.getFirstBlockOfBody()).thenReturn(actionBlockNotInHeadBlocks);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.BODY, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Body_FirstBlockOfBodyNotNull_BlockNull_Positive() {
		when(actionBlock.getFirstBlockOfBody()).thenReturn(ifBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.BODY, null));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Condition_ConditionBlockNull_Positive() {
		when(actionBlock.getConditionBlock()).thenReturn(null);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.CONDITION, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Condition_ConditionBlockNotNull_BlockNotNull_ConditionBlockEqualsBlock_Positive() {
		when(actionBlock.getConditionBlock()).thenReturn(movedConditionBlock);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.CONDITION, movedConditionBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Condition_ConditionBlockNotNull_BlockNotNull_ConditionBlockNotEqualsBlock_Positive() {
		when(actionBlock.getConditionBlock()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.CONDITION, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Condition_ConditionBlockNotNull_BlockNull_Positive() {
		when(actionBlock.getConditionBlock()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.CONDITION, null));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Operand_NextBlockNull_Positive() {
		when(actionBlock.getOperand()).thenReturn(null);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.OPERAND, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Operand_NextBlockNotNull_BlockNotNull_NextBlockEqualsBlock_Positive() {
		when(actionBlock.getOperand()).thenReturn(notBlock);

		assertTrue(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.OPERAND, notBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Operand_NextBlockNotNull_BlockNotNull_NextBlockNotEqualsBlock_Positive() {
		when(actionBlock.getOperand()).thenReturn(notBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.OPERAND, ifBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Operand_NextBlockNotNull_BlockNull_Positive() {
		when(actionBlock.getOperand()).thenReturn(notBlock);

		assertFalse(blockRepo.checkIfConnectionIsOpen(actionBlock, ConnectionType.OPERAND, null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getBlockByID(java.lang.String)}.
	 */
	@Test
	public void testGetBlockByID() {
		assertEquals(actionBlock, blockRepo.getBlockByID(actionBlock.getBlockId()));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_HeadBlocksContainsBlock_GettersNull_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = false;

		Mockito.doReturn(actionBlock).when(blockRepo).getBlockByID(removedBlockId);
		when(actionBlock.getNextBlock()).thenReturn(null);
		when(actionBlock.getConditionBlock()).thenReturn(null);
		when(actionBlock.getOperand()).thenReturn(null);

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlock.getBlockId());
		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Body_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo)
				.getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);

		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("BODY");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());

		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));
		verify(ifBlock, atLeastOnce()).setFirstBlockOfBody(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Condition_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo)
				.getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);

		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("CONDITION");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());

		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));
		verify(ifBlock, atLeastOnce()).setConditionBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Down_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo)
				.getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);

		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("DOWN");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(ifBlock).when(blockRepo).getBlockByID("parentId");

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());

		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));
		verify(ifBlock, atLeastOnce()).setNextBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_HeadBlocksDoesNotContainBlock_Operand_Positive() {
		String removedBlockId = "removedBlockId";
		boolean isChain = true;
		Mockito.doReturn(actionBlockNotInHeadBlocks).when(blockRepo).getBlockByID(removedBlockId);
		Set<Block> blocksToBeRemoved = new HashSet<Block>();
		blocksToBeRemoved.add(actionBlockNotInHeadBlocks);
		Mockito.doReturn(blocksToBeRemoved).when(blockRepo)
				.getAllBlocksConnectedToAndAfterACertainBlock(actionBlockNotInHeadBlocks);

		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("OPERAND");
		parentIdentifiers.add("parentId");
		Mockito.doReturn(parentIdentifiers).when(blockRepo).getConnectedParentIfExists("actionBlockNotInHeadBlocksId");
		Mockito.doReturn(notBlock).when(blockRepo).getBlockByID("parentId");

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add(actionBlockNotInHeadBlocks.getBlockId());

		assertEquals(expectedResult, blockRepo.removeBlock(removedBlockId, isChain));
		verify(notBlock, atLeastOnce()).setOperand(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_BlockDefinitionBlock_Positive() {
		String removedBlockId = "removedBlockId";
		Mockito.doReturn(definitionBlock).when(blockRepo).getBlockByID(removedBlockId);

		blockRepo.removeBlock(removedBlockId, true);

		// BlockType.removeBlockType("definitionBlockId") should be verified, but as of
		// now,
		// Mockito does not support testing for static methods.
//		verify(BlockType,atLeastOnce()).removeBlockType("definitionBlockId");
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#moveBlock(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeRemove(String)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeRemove_NoNoConnection_Positive() {
		String removedblockIdParam = "removedActionBlockId";
		ArrayList<String> connectedBlockInfo = new ArrayList<String>();
		connectedBlockInfo.add("DOWN");
		connectedBlockInfo.add("SomeId");
		Mockito.doReturn(connectedBlockInfo).when(blockRepo).getConnectedParentIfExists(removedblockIdParam);

		assertEquals(connectedBlockInfo, blockRepo.getConnectedBlockBeforeRemove(removedblockIdParam));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeRemove(String)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeRemove_NoConnection_NextBlockNotNull_Positive() {
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeRemove(String)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeRemove_NoConnection_ConditionBlockNotNull_Positive() {
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedBlockBeforeRemove(String)}.
	 */
	@Test
	public void testGetConnectedBlockBeforeRemove_NoConnection_OperandNotNull_Positive() {
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ActionBlockDownConnection_NextBlockNotEqual_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);

		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("NOCONNECTION");
		expectedConnectedBlockInfo.add("");
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_ControlBlockOtherOptions_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(ifBlock.getNextBlock()).thenReturn(actionBlockNotInHeadBlocks);
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlockNotInHeadBlocks);

		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("NOCONNECTION");
		expectedConnectedBlockInfo.add("");
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_OperatorBlockNotEqual_Positive() {
		String blockIdParam = "movedConditionBlockId";
		Mockito.doReturn(movedConditionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(notBlock.getOperand()).thenReturn(notBlock);

		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("NOCONNECTION");
		expectedConnectedBlockInfo.add("");
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectedParentIfExists(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedParentIfExists_BlockDefinitionBlock_Positive() {
		String blockIdParam = "movedActionBlockId";
		Mockito.doReturn(movedActionBlock).when(blockRepo).getBlockByID(blockIdParam);
		when(definitionBlock.getFirstBlockOfBody()).thenReturn(movedActionBlock);

		ArrayList<String> expectedConnectedBlockInfo = new ArrayList<String>();
		expectedConnectedBlockInfo.add("BODY");
		expectedConnectedBlockInfo.add("definitionBlockId");
		assertEquals(expectedConnectedBlockInfo, blockRepo.getConnectedParentIfExists(blockIdParam));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidProgram()}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidProgram()}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram_NotValidBreak_Positive() {
		HashSet<Block> headBlocks = new HashSet<Block>(Arrays.asList(actionBlock));
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));
		Mockito.doReturn(false).when(blockRepo).checkIfChainIsValid(actionBlock);

		assertFalse(blockRepo.checkIfValidProgram());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfChainIsValid(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfChainIsValid_blockNull_Positive() {
		assertTrue(blockRepo.checkIfChainIsValid(null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfChainIsValid(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfChainIsValid_blockNotBodyCavityBlock_Positive() {
		when(actionBlock.getNextBlock()).thenReturn(null);
		assertTrue(blockRepo.checkIfChainIsValid(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#CheckIfChainIsValid(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfChainIsValid_blockControlBlock_FirstBlockOfBodyNotValid_Positive() {
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);
		Mockito.doReturn(false).when(blockRepo).checkIfChainIsValid(actionBlock);
		assertFalse(blockRepo.checkIfChainIsValid(ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#CheckIfChainIsValid(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfChainIsValid_blockControlBlock_ConditionBlockNotValid_Positive() {
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);
		Mockito.doReturn(true).when(blockRepo).checkIfChainIsValid(actionBlock);
		when(ifBlock.getConditionBlock()).thenReturn(movedConditionBlock);
		Mockito.doReturn(false).when(blockRepo).checkIfValidStatement(movedConditionBlock);

		assertFalse(blockRepo.checkIfChainIsValid(ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidStatement(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfValidStatement_BlockNull_Positive() {
		assertFalse(blockRepo.checkIfValidStatement(null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidStatement(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfValidStatement_BlockNotNull_OperandConditionBlock_Positive() {
		when(ifBlock.getOperand()).thenReturn(movedConditionBlock);
		assertTrue(blockRepo.checkIfValidStatement(ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfValidStatement(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testCheckIfValidStatement_BlockNotNull_OperandNotConditionBlock_Positive() {
		when(ifBlock.getOperand()).thenReturn(null);
		assertFalse(blockRepo.checkIfValidStatement(ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#findFirstBlockToBeExecuted()}.
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
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_NotPresent_Positive() {
		HashSet<Block> headBlocks = new HashSet<Block>();
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(actionBlock.getBlockId(), actionBlock);
		blockRepo = new BlockRepository(headBlocks, allBlocks);

		assertEquals(null, blockRepo.findFirstBlockToBeExecuted());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfMaxNbOfBlocksReached()}.
	 */
	@Test
	public void testCheckIfMaxNbOfBlocksReached_False_Positive() {
		assertFalse(blockRepo.checkIfMaxNbOfBlocksReached());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfMaxNbOfBlocksReached()}.
	 */
	@Test
	public void testCheckIfMaxNbOfBlocksReached_True_Positive() {
		for (int i = 0; i < blockRepo.getMaxNbOfBlocks(); i++) {
			allBlocks.put(Integer.toString(i) + "_CheckMaxNumberOfBlocks", actionBlock);
		}
		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));
		assertTrue(blockRepo.checkIfMaxNbOfBlocksReached());
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockRepository#getNumberOfRemainingBlocks()}.
	 */
	@Test
	public void testgetNumberOfRemainingBlocks() {
		blockRepo = spy(new BlockRepository(headBlocks, allBlocks));
		allBlocks.clear();
		for (int i = 0; i <= blockRepo.getMaxNbOfBlocks(); i++) {
			assertEquals(blockRepo.getMaxNbOfBlocks() - i, blockRepo.getNumberOfRemainingBlocks());
			allBlocks.put(Integer.toString(i) + "_CheckMaxNumberOfBlocks", actionBlock);
		}
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockRepository#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		assertTrue(BlockRepository.getInstance() instanceof BlockRepository);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlockIDsUnderneath(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneathBlock_Positive() {
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		blockIDsUnderNeath.add("BlockIdUnderneath");
		Set<Block> blocksUnderNeath = new HashSet<Block>();
		blocksUnderNeath.add(new IfBlock("BlockIdUnderneath"));
		Mockito.doReturn(blocksUnderNeath).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);

		assertEquals(blockIDsUnderNeath, blockRepo.getAllBlockIDsUnderneath(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlocksConnectedToAndAfterACertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlocksConnectedToAndAfterACertainBlock_BlockNotNull_Positive() {
		HashSet<Block> expectedResult = new HashSet<Block>();
		expectedResult.add(actionBlock);

		assertEquals(expectedResult, blockRepo.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlocksConnectedToAndAfterACertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlocksConnectedToAndAfterACertainBlock_BlockNull_Positive() {
		assertEquals(new HashSet<Block>(), blockRepo.getAllBlocksConnectedToAndAfterACertainBlock(null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlockIDsInBody(domainLayer.blocks.ControlBlock)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_Positive() {
		Set<String> blockIDsInBody = new HashSet<String>();
		blockIDsInBody.add("BlockIdInBody");
		Set<Block> connectedBlocks = new HashSet<Block>();
		connectedBlocks.add(new IfBlock("BlockIdInBody"));
		connectedBlocks.add(movedConditionBlock);
		Mockito.doReturn(connectedBlocks).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);

		assertEquals(blockIDsInBody, blockRepo.getAllBlockIDsInBody(ifBlock));

	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getMaxNbOfBlocks()}.
	 */
	@Test
	public void testGetMaxNbOfBlocks_Positive() {
		assertEquals(20, blockRepo.getMaxNbOfBlocks());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks_Positive() {
		Set<ControlBlock> firstControlBlocks = new HashSet<ControlBlock>();
		firstControlBlocks.add(ifBlock);
		when(actionBlock.getNextBlock()).thenReturn(ifBlock);

		assertEquals(firstControlBlocks, blockRepo.getAllHeadControlBlocks());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllDefinitionBlocks_Positive() {
		Set<DefinitionBlock> definitionBlocks = new HashSet<DefinitionBlock>();
		definitionBlocks.add(definitionBlock);

		assertEquals(definitionBlocks, blockRepo.getAllDefinitionBlocks());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getEnclosingBodyCavityBlock(domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_Positive() {
		Set<Block> connectedBlocks = new HashSet<Block>();
		connectedBlocks.add(ifBlock);
		connectedBlocks.add(actionBlockNotInHeadBlocks);
		connectedBlocks.add(movedActionBlock);
		Mockito.doReturn(connectedBlocks).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);
		when(actionBlock.getNextBlock()).thenReturn(movedActionBlock);

		assertEquals(ifBlock, blockRepo.getEnclosingBodyCavityBlock(movedActionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getEnclosingBodyCavityBlock(domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_NoTopLevelBlock_Positive() {
		Set<Block> connectedBlocks = new HashSet<Block>();
		connectedBlocks.add(ifBlock);
		connectedBlocks.add(actionBlockNotInHeadBlocks);
		connectedBlocks.add(movedActionBlock);
		Mockito.doReturn(connectedBlocks).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);

		assertEquals(null, blockRepo.getEnclosingBodyCavityBlock(movedActionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getEnclosingBodyCavityBlock(domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_NoControlBlocks_Positive() {
		Set<Block> connectedBlocks = new HashSet<Block>();
		connectedBlocks.add(actionBlockNotInHeadBlocks);
		connectedBlocks.add(movedActionBlock);
		Mockito.doReturn(connectedBlocks).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);

		assertEquals(null, blockRepo.getEnclosingBodyCavityBlock(movedActionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlockIDsBelowCertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_Positive() {
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		String blockId = actionBlock.getBlockId();
		blockIDsUnderNeath.add(blockId);

		assertEquals(blockIDsUnderNeath, blockRepo.getAllBlockIDsBelowCertainBlock(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllBlockIDsBelowCertainBlock(domainLayer.blocks.Block)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_BlockNull_Positive() {
		Set<String> blockIDsUnderNeath = new HashSet<String>();

		assertEquals(blockIDsUnderNeath, blockRepo.getAllBlockIDsBelowCertainBlock(null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		Set<Block> headBlocks = new HashSet<Block>();
		headBlocks.add(actionBlock);
		headBlocks.add(definitionBlock);
		assertEquals(headBlocks, blockRepo.getAllHeadBlocks());
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedTrue_NoDefinitionNotRemoved_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(actionBlock);
		Set<Block> connectedBlocks = new HashSet<Block>();
		connectedBlocks.add(actionBlock);
		connectedBlocks.add(definitionBlock);
		Mockito.doReturn(null).when(blockRepo).getBlockByID("actionBlockId");
		Mockito.doReturn(connectedBlocks).when(blockRepo).getAllBlocksConnectedToAndAfterACertainBlock(actionBlock);

		assertTrue(blockRepo.restoreBlockSnapshot(blockSnapshot));
		assertFalse(headBlocks.contains(definitionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedTrue_DefinitionAndRemoved_ContainsBlock_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(definitionBlock);
		when(blockSnapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock);
		Mockito.doReturn(null).when(blockRepo).getBlockByID("definitionBlockId");

		Set<String> blockIDsBelow = new HashSet<String>();
		blockIDsBelow.add("actionBlockId");
		Mockito.doReturn(blockIDsBelow).when(blockRepo).getAllBlockIDsBelowCertainBlock(definitionBlock);

		assertTrue(blockRepo.restoreBlockSnapshot(blockSnapshot));
		assertFalse(headBlocks.contains(actionBlock));
		assertTrue(headBlocks.contains(definitionBlock));
		assertTrue(allBlocks.containsValue(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedTrue_DefinitionAndRemoved_DoesNotContainBlock_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(definitionBlock);
		when(blockSnapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock);
		Mockito.doReturn(null).when(blockRepo).getBlockByID("definitionBlockId");

		Set<String> blockIDsBelow = new HashSet<String>();
		Mockito.doReturn(blockIDsBelow).when(blockRepo).getAllBlockIDsBelowCertainBlock(definitionBlock);

		// TODO: testing of deepReplace methods is too complex, but would be still
		// needed here.

		assertTrue(blockRepo.restoreBlockSnapshot(blockSnapshot));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeAndAfterNull_Positive() {
		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If1True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(movedConditionBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(ifBlock);
		when(ifBlock.getConditionBlock()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setConditionBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If2True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(actionBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(ifBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setFirstBlockOfBody(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If3True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(movedConditionBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(notBlock);
		when(notBlock.getOperand()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(notBlock, atLeastOnce()).setOperand(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If4True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(actionBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(ifBlock);
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setNextBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If5True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(ifBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(movedConditionBlock);
		when(ifBlock.getConditionBlock()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setConditionBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If6True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(ifBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(actionBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setFirstBlockOfBody(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If7True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(notBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(movedConditionBlock);
		when(notBlock.getOperand()).thenReturn(movedConditionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(notBlock, atLeastOnce()).setOperand(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockBeforeNotNull_If8True_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(ifBlock);
		when(blockSnapshot.getConnectedBlockBeforeSnapshot()).thenReturn(actionBlock);
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		verify(ifBlock, atLeastOnce()).setNextBlock(null);
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockAfterNotNull_AfterBlockInHeadBlocks_GetBlockInHeadBlocks_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(definitionBlock);
		when(blockSnapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		assertTrue(allBlocks.containsValue(actionBlock));
		assertTrue(headBlocks.contains(actionBlock));
		assertFalse(headBlocks.contains(definitionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#restoreBlockSnapshot(types.BlockSnapshot)}.
	 */
	@Test
	public void testrestoreBlockSnapshot_IsRemovedFalse_ConnectedBlockAfterNotNull_AfterBlockNotInHeadBlocks_GetBlockNotInHeadBlocks_Positive() {
		when(blockSnapshot.getBlock()).thenReturn(ifBlock);
		when(blockSnapshot.getConnectedBlockAfterSnapshot()).thenReturn(notBlock);
		Set<Block> changingBlocks = new HashSet<Block>();
		changingBlocks.add(actionBlock);
		when(blockSnapshot.getChangingBlocks()).thenReturn(changingBlocks);

		assertFalse(blockRepo.restoreBlockSnapshot(blockSnapshot));
		assertFalse(headBlocks.contains(actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ParentNull_Positive() {
		assertEquals(ConnectionType.NOCONNECTION, blockRepo.getConnectionType(null, null));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ParentConditionEqualsChild_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);

		assertEquals(ConnectionType.CONDITION, blockRepo.getConnectionType(ifBlock, notBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ParentFirstBlockOfBodyEqualsChild_Positive() {
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlock);

		assertEquals(ConnectionType.BODY, blockRepo.getConnectionType(ifBlock, actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ParentNextBlockEqualsChild_Positive() {
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);

		assertEquals(ConnectionType.DOWN, blockRepo.getConnectionType(ifBlock, actionBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ParentOperandEqualsChild_Positive() {
		when(ifBlock.getOperand()).thenReturn(notBlock);

		assertEquals(ConnectionType.OPERAND, blockRepo.getConnectionType(ifBlock, notBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ChildOperandEqualsParent_Positive() {
		when(ifBlock.getOperand()).thenReturn(notBlock);

		assertEquals(ConnectionType.LEFT, blockRepo.getConnectionType(notBlock, ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ChildConditionEqualsParent_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);

		assertEquals(ConnectionType.LEFT, blockRepo.getConnectionType(notBlock, ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_ChildNextBlockEqualsParent_Positive() {
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);

		assertEquals(ConnectionType.UP, blockRepo.getConnectionType(actionBlock, ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_NoConnection_Positive() {
		assertEquals(ConnectionType.NOCONNECTION, blockRepo.getConnectionType(ifBlock, ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetConnectionType_NoConnectionWithOtherOptions_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(notBlock);
		when(ifBlock.getFirstBlockOfBody()).thenReturn(actionBlockNotInHeadBlocks);
		when(ifBlock.getNextBlock()).thenReturn(actionBlockNotInHeadBlocks);
		when(ifBlock.getOperand()).thenReturn(notBlock);
		assertEquals(ConnectionType.NOCONNECTION, blockRepo.getConnectionType(ifBlock, ifBlock));
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#getConnectionType(Block, Block)}.
	 */
	@Test
	public void testGetCallerBlocksByDefinition_Positive() {
		Set<Block> callBlocks = new HashSet<Block>();
		callBlocks.add(callBlock);

		assertEquals(callBlocks, blockRepo.getCallerBlocksByDefinition("definitionBlockId"));
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfBlockIsInBody(String)}.
	 */
	@Test
	public void testCheckIfBlockIsInBody_In_Body() {
		Block b= new WhileBlock("inBody");
		allBlocks.put("inBody", b);
		ifBlock.setFirstBlockOfBody(b);

		assertEquals(true, blockRepo.checkIfBlockIsInBody("inBody"));
		
		
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#checkIfBlockIsInBody(String)}.
	 */
	@Test
	public void testCheckIfBlockIsInBody_Not_In_Body() {
		Block b= new WhileBlock("inBody");
		allBlocks.put("inBody", b);
		

		assertEquals(false, blockRepo.checkIfBlockIsInBody("inBody"));
		
		
	}
	
	
}
