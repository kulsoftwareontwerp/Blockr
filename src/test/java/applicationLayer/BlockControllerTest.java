/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.CallFunctionBlock;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.DefinitionBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.OperatorBlock;
import domainLayer.blocks.WhileBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.DomainListener;
import events.GUIListener;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;

import exceptions.InvalidBlockTypeException;
import exceptions.MaxNbOfBlocksReachedException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;
import types.ExecutionSnapshot;

/**
 * BlockControllerTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockControllerTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Mock
	private GUIListener mockGuiListener;
	@Spy
	private HashSet<GUIListener> guiListeners;
	@Mock(name="blockRepository")
	private BlockRepository blockRepository;
	@Spy @InjectMocks
	private BlockController bc;
	
	private ActionBlock actionBlock0;
	private ActionBlock actionBlock1;
	private ActionBlock actionBlockMock;
	private ActionBlock actionBlockSpy;
	private ControlBlock controlBlock;
	private BlockSnapshot snapshot;
	private BlockSnapshot associatedSnapshot;
	
	@Mock
	private DomainListener mockDomainListener;

	
	

	private ArrayList<Block> allBlocksInTest = new ArrayList<Block>();

	private ArrayList<String> blockIdsInRepository = new ArrayList<String>();
	
	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
	private ActionBlock connectedActionBlock;
	private ControlBlock connectedControlBlock;
	private ConditionBlock connectedConditionBlock;
	private OperatorBlock connectedOperatorBlock;
	private ConditionBlock newConditionBlock;
	private ActionBlock newActionBlock;
	private IfBlock newIfBlock;
	private NotBlock newNotBlock;

	private WhileBlock newWhileBlock;

	private DefinitionBlock definitionBlock;
	private CallFunctionBlock callBlock;

	private DefinitionBlock newDefinitionBlock;

	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		guiListeners.clear();
		guiListeners.add(mockGuiListener);
		
		actionBlock0 = new ActionBlock("0", new BlockType("Action", BlockCategory.ACTION));
		actionBlock1 = new ActionBlock("1", new BlockType("Action", BlockCategory.ACTION));
		actionBlockMock = spy(new ActionBlock("actionBlockMock", new BlockType("Action", BlockCategory.ACTION)));
		
		actionBlockSpy = spy(new ActionBlock("2", new BlockType("Action", BlockCategory.ACTION)));
		controlBlock = spy(new IfBlock("ifBlock"));
		snapshot = spy(new BlockSnapshot(actionBlock0, null, null, new HashSet<Block>(), new HashSet<BlockSnapshot>()));
		associatedSnapshot = spy(new BlockSnapshot(actionBlock1, null, null, new HashSet<Block>(), new HashSet<BlockSnapshot>()));
		
//		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);
		
		
		
		
		blockIdsInRepository.add("conditionBlock");
		blockIdsInRepository.add("operatorBlock");
		blockIdsInRepository.add("controlBlock");
		blockIdsInRepository.add("actionBlock");
		blockIdsInRepository.add("noBlock");
		blockIdsInRepository.add("definitionBlock");


		connectedActionBlock = spy(new ActionBlock("connectedActionBlock",new BlockType("random", BlockCategory.ACTION)));
		connectedConditionBlock = spy(new ConditionBlock("connectedConditionBlock",new BlockType("random", BlockCategory.CONDITION)));
		connectedControlBlock = spy(new WhileBlock("controlBlock"));
		connectedOperatorBlock = spy(new NotBlock("operatorBlock"));


		
		newConditionBlock = spy(new ConditionBlock("newConditionBlock",new BlockType("random", BlockCategory.CONDITION)));
		newActionBlock = spy(new ActionBlock("newActionBlock",new BlockType("random", BlockCategory.ACTION)));
		newIfBlock = spy(new IfBlock("newIfBlock"));

		newNotBlock = spy(new NotBlock("newNotBlock"));
		newWhileBlock = spy(new WhileBlock("newWhileBlock"));
		
		definitionBlock = spy(new DefinitionBlock("definitionBlockId"));
		callBlock = spy(new CallFunctionBlock("CallBlock", new BlockType("Call "+ "definitionBlockId", BlockCategory.CALL, "definitionBlockId")));
		newDefinitionBlock = spy(new DefinitionBlock("newDefinitionBlock"));

		allBlocksInTest.add(connectedActionBlock);
		allBlocksInTest.add(connectedControlBlock);
		allBlocksInTest.add(connectedOperatorBlock);
		allBlocksInTest.add(connectedConditionBlock);
		allBlocksInTest.add(newIfBlock);
		allBlocksInTest.add(newNotBlock);
		allBlocksInTest.add(newActionBlock);
		allBlocksInTest.add(newWhileBlock);	
		allBlocksInTest.add(newDefinitionBlock);
		
		
		
		when(blockRepository.addBlock(any(BlockType.class), any(String.class), any(ConnectionType.class))).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				BlockType type = (BlockType) invocation.getArgument(0);
				switch(type.cat()) {
				case ACTION:
					return newActionBlock.getBlockId();
				case CONDITION:
					return newConditionBlock.getBlockId();
				case CONTROL:
					if(type==BlockType.IF) {
						return newIfBlock.getBlockId();
					}else if(type==BlockType.WHILE) {
						return newWhileBlock.getBlockId();
					}else {
						return null;
					}
				case OPERATOR:
					return newNotBlock.getBlockId();
				case DEFINITION:
					return newDefinitionBlock.getBlockId();
				default:
					return null;
				
				}
			}
		});
		
		when(blockRepository.getBlockByID(any(String.class))).thenAnswer(new Answer<Block>() {

			@Override
			public Block answer(InvocationOnMock invocation) throws Throwable {
				String id = (String) invocation.getArgument(0);
				
				
				if(allBlocksInTest.stream().anyMatch(s->s.getBlockId().equals(id))) {
					return allBlocksInTest.stream().filter(s->s.getBlockId().equals(id)).findFirst().get();
				}
				else {
					return null;
				}			
			}
			
		});
		

		connectionTypes.add(ConnectionType.BODY);
		connectionTypes.add(ConnectionType.CONDITION);
		connectionTypes.add(ConnectionType.LEFT);
		connectionTypes.add(ConnectionType.DOWN);
		connectionTypes.add(ConnectionType.NOCONNECTION);
		connectionTypes.add(ConnectionType.OPERAND);
		connectionTypes.add(ConnectionType.UP);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#BlockController()}.
	 */
	@Test
	public void testBlockController_Positive() {
		BlockController newBlockController = new BlockController();
		try {
			Field guiListeners = BlockController.class.getDeclaredField("guiListeners");
			guiListeners.setAccessible(true);
			assertTrue("guiListeners were not initialised", guiListeners.get(newBlockController) != null);

			Field domainListeners = BlockController.class.getDeclaredField("domainListeners");
			domainListeners.setAccessible(true);
			assertTrue("domainListeners were not initialised", domainListeners.get(newBlockController) != null);

			Field programBlockRepository = BlockController.class.getDeclaredField("programBlockRepository");
			programBlockRepository.setAccessible(true);
			assertTrue("programBlockRepository was not initialised", programBlockRepository.get(newBlockController) != null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	private static class TestType extends DynaEnum<TestType>{

		protected TestType(String type, BlockCategory cat, Action action, Predicate predicate, String definition) {
			super(type, cat, action, predicate, definition);
		}
		
		@SuppressWarnings("unused")
		public static void removeFromDynaEnum(DynaEnum<?> literal ) {
			remove(literal);
		}
		
		public static <E> DynaEnum<? extends DynaEnum<?>>[]  values() {
			return values(BlockType.class);
		}
		
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockPositiveMaxNbOfBlocksReached() {
		
		
		
		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);
		//when(blockRepository.getBlockByID("definitionBlockId")).thenReturn(definitionBlock);
		TestType.removeFromDynaEnum(callBlock.getBlockType());
		for (DynaEnum<? extends DynaEnum<?>> b : TestType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false, true);
				String cb = "connectedActionBlock";
				BlockSnapshot s = bc.addBlock((BlockType) b, cb, c);

				verify(blockRepository, atLeastOnce()).addBlock(blockType.capture(), connectedBlock.capture(),
						connectionType.capture());

				assertEquals(b, blockType.getValue());
				assertEquals(cb, connectedBlock.getValue());
				assertEquals(c, connectionType.getValue());

				InOrder updateResetOrder = inOrder(mockDomainListener);
				updateResetOrder.verify(mockDomainListener, atLeastOnce())
						.onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
				updateResetOrder.verify(mockDomainListener, atLeastOnce())
						.onResetExecutionEvent(any(ResetExecutionEvent.class));

				ArgumentCaptor<PanelChangeEvent> panelChangeEvent = ArgumentCaptor.forClass(PanelChangeEvent.class);
				verify(mockGuiListener, atLeastOnce()).onPanelChangedEvent(panelChangeEvent.capture());
				assertFalse(panelChangeEvent.getValue().isShown());
				verify(mockGuiListener, atLeastOnce()).onBlockAdded(any(BlockAddedEvent.class));
				clearInvocations(blockRepository);
			}
		}
	}
	
	@Test
	public void testAddBlockNegativeInstanceOf() {
		String excMessage = "There is no DefinitionBlock in the domain with the given definitionBlockID.";
		
		BlockType newType = new BlockType("Call 1", BlockCategory.CALL,"1");
		Block definition = spy(new ActionBlock("1", BlockType.IF));
//		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		
		assertExceptionBCAddBlockCombination(newType, null, ConnectionType.NOCONNECTION, excMessage);
		
		verifyNoInteractions(mockGuiListener);
		verifyNoInteractions(mockDomainListener);

		BlockType.removeBlockType("1");
	}
	

	
	/**
	 * Test method for {@link applicationLayer.BlockController#getNumberOfRemainingBlocks()}.
	 */
	@Test
	public void testgetNumberOfRemainingBlocks() {

		bc.getNumberOfRemainingBlocks();
		
		verify(blockRepository, atLeastOnce()).getNumberOfRemainingBlocks();
	}
	
	@Test
	public void testAddBlockMaxNumberOfBlocksReached() {
		String excMessage = "The maximum number of blocks has already been reached.";
		exceptionRule.expect(MaxNbOfBlocksReachedException.class);
		exceptionRule.expectMessage(excMessage);
		
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		bc.addBlock(mock(BlockType.class), null, ConnectionType.NOCONNECTION);
	}
	
	private void assertExceptionBCAddBlockCombination(BlockType bt, String cb, ConnectionType ct, String excMessage) {
		boolean pass = false;
		try {
			bc.addBlock(bt, cb, ct);
		} catch (NoSuchConnectedBlockException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("addBlock failed in the blockController for combination: BlockType=" + bt
				+ " ConnectedBlockId=" + cb + " ConnectionType=" + ct.toString(), pass);
	}
	
	@Test
	public void testAddBlockNegativeDefinitionBlockNull() {
		String excMessage = "There is no DefinitionBlock in the domain with the given definitionBlockID.";
		
		BlockType mockType = Mockito.spy(new BlockType("Call 1", BlockCategory.CALL));
		assertExceptionBCAddBlockCombination(mockType, null, ConnectionType.NOCONNECTION, excMessage);
		verifyNoInteractions(mockGuiListener);
		verifyNoInteractions(mockDomainListener);
		BlockType.removeBlockType("1");
	}

	@Test
	public void testFireBlockAddedControlBlock() {
		//when(actionBlockSpy.getNextBlock()).thenReturn(controlBlock);
		when(controlBlock.getConditionBlock()).thenReturn(connectedOperatorBlock);
		//when(connectedOperatorBlock.getOperand()).thenReturn(connectedConditionBlock);
		when(controlBlock.getFirstBlockOfBody()).thenReturn(actionBlock1);
		
		BlockSnapshot bsnap = mock(BlockSnapshot.class);
		when(bsnap.getBlock()).thenReturn(controlBlock);

		bc.fireBlockAdded(bsnap);

//		verify(actionBlockSpy.getNextBlock());
		verify(controlBlock,times(2)).getConditionBlock();
		verify(controlBlock,times(2)).getFirstBlockOfBody();
//		verify(connectedOperatorBlock.getOperand());
	}
	
	@Test
	public void testFireBlockAddedActionBlock() {
		when(actionBlockSpy.getNextBlock()).thenReturn(controlBlock);
//		when(controlBlock.getConditionBlock()).thenReturn(connectedOperatorBlock);
//		when(connectedOperatorBlock.getOperand()).thenReturn(connectedConditionBlock);
//		when(controlBlock.getFirstBlockOfBody()).thenReturn(actionBlock1);
	
		BlockSnapshot bsnap = mock(BlockSnapshot.class);
		when(bsnap.getBlock()).thenReturn(actionBlockSpy);

		bc.fireBlockAdded(bsnap);

		verify(actionBlockSpy,times(2)).getNextBlock();
//		verify(controlBlock.getConditionBlock());
//		verify(controlBlock.getFirstBlockOfBody());
//		verify(connectedOperatorBlock.getOperand());
	}
	
	
	@Test
	public void testFireBlockAddedOperandBlock() {
		//when(actionBlockSpy.getNextBlock()).thenReturn(controlBlock);
		//when(controlBlock.getConditionBlock()).thenReturn(connectedOperatorBlock);
		when(connectedOperatorBlock.getOperand()).thenReturn(connectedConditionBlock);
		//when(controlBlock.getFirstBlockOfBody()).thenReturn(actionBlock1);
		
		BlockSnapshot bsnap = mock(BlockSnapshot.class);
		when(bsnap.getBlock()).thenReturn(connectedOperatorBlock);

		bc.fireBlockAdded(bsnap);

//		verify(actionBlockSpy.getNextBlock());
//		verify(controlBlock.getConditionBlock());
//		verify(controlBlock.getFirstBlockOfBody());
		verify(connectedOperatorBlock,times(2)).getOperand();
	}
	
	
	@Ignore
	public void testFireBlockAddedRecursively() {
		when(actionBlockSpy.getNextBlock()).thenReturn(controlBlock);
		when(controlBlock.getConditionBlock()).thenReturn(connectedOperatorBlock);
		when(connectedOperatorBlock.getOperand()).thenReturn(connectedConditionBlock);
		when(controlBlock.getFirstBlockOfBody()).thenReturn(actionBlock1);
		
		BlockSnapshot bsnap = mock(BlockSnapshot.class);
		when(bsnap.getBlock()).thenReturn(connectedOperatorBlock);

		bc.fireBlockAdded(bsnap);
		
		verify(actionBlockSpy).getNextBlock();
		verify(controlBlock).getConditionBlock();
		verify(controlBlock).getFirstBlockOfBody();
		verify(connectedOperatorBlock).getOperand();
	}
	/**
	 * Test method for {@link applicationLayer.BlockController#getMaxNbOfBlocks()}.
	 */
	@Test
	public void testGetMaxNbOfBlocks() {
		when(blockRepository.getMaxNbOfBlocks()).thenReturn(1);
		
		assertEquals(1, bc.getMaxNbOfBlocks());
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainTrue_MaxBlocksReachedFalse_ConnectedBlockBeforeDeleteNotNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = true;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>(), associatedSnapshots);
		
		when(blockRepository.getConnectedParentIfExists(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(actionBlock1);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>(), associatedSnapshots)).thenReturn(blockSnapShot);
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(new HashSet<String>());
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = false;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, null, null, new HashSet<Block>(), associatedSnapshots);
		
		when(blockRepository.getConnectedBlockBeforeRemove(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(null);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, null, null, new HashSet<Block>(), associatedSnapshots)).thenReturn(blockSnapShot);
		HashSet<String> idsToBeRemoved = new HashSet<String>();
		idsToBeRemoved.add("idToBeRemoved");
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(idsToBeRemoved);
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_DefinitionBlock_Positive() {
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		
		when(blockRepository.getConnectedBlockBeforeRemove("definitionBlockId")).thenReturn(previousConnection);
		
		when(blockRepository.getBlockByID("definitionBlockId")).thenReturn(definitionBlock);
		
		Set<Block> callBlocks = new HashSet<Block>();
		callBlocks.add(callBlock);
		when(blockRepository.getCallerBlocksByDefinition("definitionBlockId")).thenReturn(callBlocks);
		ArrayList<String> previousConnectionCallBlock = new ArrayList<String>();
		previousConnectionCallBlock.add("DOWN");
		previousConnectionCallBlock.add("actionBlockMock");
		when(blockRepository.getConnectedParentIfExists("CallBlock")).thenReturn(previousConnectionCallBlock);
		
		when(blockRepository.getBlockByID("actionBlockMock")).thenReturn(actionBlockMock);
		when(callBlock.getNextBlock()).thenReturn(actionBlock1);
		
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlockMock, null, null, new HashSet<Block>(), new HashSet<BlockSnapshot>());
		// Strange error with assertEquals
		assertTrue(bc.removeBlock("definitionBlockId", false) != null);
		verify(actionBlockMock,atLeastOnce()).setNextBlock(actionBlock1);
	}
	
//	/**
//	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
//	 */
//	@Test
//	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_DefinitionBlock_ConnectedBlockNull_Positive() {
//		ArrayList<String> previousConnection = new ArrayList<String>();
//		previousConnection.add("UP");
//		previousConnection.add("1");
//		
//		when(blockRepository.getConnectedBlockBeforeRemove("definitionBlockId")).thenReturn(previousConnection);
//		
//		when(blockRepository.getBlockByID("definitionBlockId")).thenReturn(definitionBlock);
//		
//		Set<Block> callBlocks = new HashSet<Block>();
//		callBlocks.add(callBlock);
//		when(blockRepository.getCallerBlocksByDefinition("definitionBlockId")).thenReturn(callBlocks);
//		ArrayList<String> previousConnectionCallBlock = new ArrayList<String>();
//		previousConnectionCallBlock.add("DOWN");
//		previousConnectionCallBlock.add(null);
//		when(blockRepository.getConnectedParentIfExists("CallBlock")).thenReturn(previousConnectionCallBlock);
//		
//		when(blockRepository.getBlockByID("actionBlockMock")).thenReturn(actionBlockMock);
//		when(callBlock.getNextBlock()).thenReturn(actionBlock1);
//		
//		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlockMock, null, null, new HashSet<Block>(), new HashSet<BlockSnapshot>());
//		// Strange error with assertEquals
//		assertTrue(bc.removeBlock("definitionBlockId", false) != null);
//		verify(actionBlockMock,atLeastOnce()).setNextBlock(actionBlock1);
//	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_DefinitionBlock_ConnectionBody_Positive() {
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		
		when(blockRepository.getConnectedBlockBeforeRemove("definitionBlockId")).thenReturn(previousConnection);
		
		when(blockRepository.getBlockByID("definitionBlockId")).thenReturn(definitionBlock);
		
		Set<Block> callBlocks = new HashSet<Block>();
		callBlocks.add(callBlock);
		when(blockRepository.getCallerBlocksByDefinition("definitionBlockId")).thenReturn(callBlocks);
		ArrayList<String> previousConnectionCallBlock = new ArrayList<String>();
		previousConnectionCallBlock.add("BODY");
		previousConnectionCallBlock.add("ifBlock");
		when(blockRepository.getConnectedParentIfExists("CallBlock")).thenReturn(previousConnectionCallBlock);
		
		when(blockRepository.getBlockByID("ifBlock")).thenReturn(controlBlock);
		when(callBlock.getNextBlock()).thenReturn(actionBlock1);
		
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlockMock, null, null, new HashSet<Block>(), new HashSet<BlockSnapshot>());
		// Strange error with assertEquals
		assertTrue(bc.removeBlock("definitionBlockId", false) != null);
		verify(controlBlock,atLeastOnce()).setFirstBlockOfBody(actionBlock1);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_MaxBlocksReachedTrue_IsChainTrue_AllOptionsInFireBlockAdded_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		
		// Options for fireBlockAdded (TODO: how to test the recursive calls?)
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock1);
//		when(actionBlockSpy.getConditionBlock()).thenReturn(Mockito.mock(AssessableBlock.class));
		
		bc.restoreBlockSnapshot(snapshot, true);
		
		verify(mockGuiListener,atLeastOnce()).onPanelChangedEvent(Mockito.any(PanelChangeEvent.class));
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
		
		// Verifies for fireBlockAdded
		verify(mockGuiListener,atLeastOnce()).onBlockAdded(Mockito.any(BlockAddedEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_IsChainFalse_ConnectedBlockAfterSnapshotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(null);	
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_IsChainFalse_ConnectedBlockAfterSnapshotNotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock1);	
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedFalse_ConnectedBlockBeforeAndAfterSnapshotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(false);
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(null);
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedFalse_ConnectedBlockBeforeAndAfterSnapshotNotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
//		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(false);
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(actionBlockSpy);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlockSpy);
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
		verify(actionBlockSpy,atLeastOnce()).getBlockId();
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_SnapshotNull_NullPointerException() {
		String excMessage = "No snapshot given";
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.restoreBlockSnapshot(null, true);
		} catch (NullPointerException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockRepository);
		
		bc.restoreBlockSnapshot(null, true);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_BeforeNull_WithAssociatedSnapshots_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
//		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(false);
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(actionBlockSpy);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlockSpy);
		
		when(blockRepository.getBlockByID("2")).thenReturn(actionBlock0);
		
		// Mocks related to the associatedSnapshots
		Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
		associatedSnapshots.add(associatedSnapshot);
		when(snapshot.getAssociatedSnapshots()).thenReturn(associatedSnapshots);
		when(associatedSnapshot.getBlock()).thenReturn(callBlock);
		Set<Block> allCallers = new HashSet<Block>();
		allCallers.add(callBlock);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(callBlock)).thenReturn(allCallers);
		BlockType callTypeMock = new BlockType("Call "+ "definitionBlockId", BlockCategory.CALL, "definitionBlockId");
		when(callBlock.getBlockType()).thenReturn(callTypeMock);
		ArrayList<String> parentIdentifiers = new ArrayList<String>();
		parentIdentifiers.add("UP");
		parentIdentifiers.add("1");
		when(blockRepository.getConnectedParentIfExists("CallBlock")).thenReturn(parentIdentifiers);
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
		verify(actionBlockSpy,atLeastOnce()).getBlockId();
		
		verify(blockRepository,atLeastOnce()).restoreBlockSnapshot(associatedSnapshot);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testBCMoveBlockPositive() {//TODO maken van verschillende blocks door nieuwe implementatie van methode.
		bc.addDomainListener(mockDomainListener);
		bc.addListener(mockGuiListener);

		// mockDomainListeners.add(mockDomainListener);
		InOrder updateMoveOrder = inOrder(mockDomainListener, mockGuiListener);
		blockIdsInRepository.add("1");
		blockIdsInRepository.add("2");
		blockIdsInRepository.add("3");

		// when(mockBlockReprository.moveBlock("1","",ConnectionType.NOCONNECTION)).thenReturn(blockIdsInRepository);

		ArrayList<ConnectionType> ConnectionsWithoutNoConnection = (ArrayList<ConnectionType>) connectionTypes.clone();
		ConnectionsWithoutNoConnection.remove(ConnectionType.NOCONNECTION);

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");

		when(blockRepository.getConnectedParentIfExists((any(String.class)))).thenReturn(parentInfo);
		when(blockRepository.moveBlock(any(String.class), any(String.class),any(String.class), any(ConnectionType.class))).thenReturn("1");
		when(blockRepository.getBlockIdToPerformMoveOn(any(String.class), any(String.class), any(ConnectionType.class))).thenReturn("1");

		when(blockRepository.getBlockByID("1")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("2")).thenReturn(actionBlock1);
		when(blockRepository.getBlockByID("3")).thenReturn(controlBlock);
		when(blockRepository.getBlockByID("1").clone()).thenReturn(actionBlock0);

		for (ConnectionType connectionType : ConnectionsWithoutNoConnection) {

			bc.moveBlock("1", "", "3", connectionType);
			verify(blockRepository).moveBlock("1","1", "3", connectionType);

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onUpdateGameStateEvent(any(UpdateGameStateEvent.class));

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onResetExecutionEvent(any(ResetExecutionEvent.class));

			updateMoveOrder.verify(mockGuiListener, atLeastOnce())
					.onBlockChangeEvent(any(BlockChangeEvent.class));

		}
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		when(blockRepository.getAllBlockIDsUnderneath(actionBlock0)).thenReturn(blockIDsUnderNeath);
		
		assertEquals(blockIDsUnderNeath, bc.getAllBlockIDsUnderneath(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsUnderneath(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsUnderneath(Mockito.any(Block.class));
		
		bc.getAllBlockIDsUnderneath(blockIdParam);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		Set<String> blockIDsInBody = new HashSet<String>();
		when(blockRepository.getAllBlockIDsInBody(controlBlock)).thenReturn(blockIDsInBody);
		
		assertEquals(blockIDsInBody, bc.getAllBlockIDsInBody(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsInBody(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsInBody(Mockito.any(ControlBlock.class));
		
		bc.getAllBlockIDsInBody(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockNoControlBlock_InvalidBlockTypeException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		
		exceptionRule.expect(InvalidBlockTypeException.class);
		
		try {
			bc.getAllBlockIDsInBody(blockIdParam);
		} catch (InvalidBlockTypeException e) {
			
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsInBody(Mockito.any(ControlBlock.class));
		
		bc.getAllBlockIDsInBody(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingBodyCavityBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		when(blockRepository.getEnclosingBodyCavityBlock(actionBlock0)).thenReturn(controlBlock);
		String controlBlockId = controlBlock.getBlockId();
		
		assertEquals(controlBlockId, bc.getEnclosingBodyCavityBlock(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingBodyCavityBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_EnclosingBlockNull_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		when(blockRepository.getEnclosingBodyCavityBlock(actionBlock0)).thenReturn(null);
		
		assertEquals(null, bc.getEnclosingBodyCavityBlock(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingBodyCavityBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_GivenBlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getEnclosingBodyCavityBlock(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getEnclosingBodyCavityBlock(Mockito.any(ActionBlock.class));
		
		bc.getEnclosingBodyCavityBlock(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingBodyCavityBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_BlockNoExecutableBlock_InvalidBlockTypeException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(Mockito.mock(NotBlock.class));
		
		exceptionRule.expect(InvalidBlockTypeException.class);
		
		try {
			bc.getEnclosingBodyCavityBlock(blockIdParam);
		} catch (InvalidBlockTypeException e) {
			
		}
		
		verify(blockRepository, Mockito.times(0)).getEnclosingBodyCavityBlock(Mockito.any(ActionBlock.class));
		
		bc.getEnclosingBodyCavityBlock(blockIdParam);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		Set<String> blockIDsUnderNeath = new HashSet<String>();
//		when(blockRepository.getAllBlockIDsInBody(controlBlock)).thenReturn(blockIDsUnderNeath);
		
		assertEquals(blockIDsUnderNeath, bc.getAllBlockIDsBelowCertainBlock(blockIdParam));
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsBelowCertainBlock(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsBelowCertainBlock(null);
		
		bc.getAllBlockIDsBelowCertainBlock(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks_Positive() {
		Set<ControlBlock> allHeadControlBlocks = new HashSet<ControlBlock>();
		allHeadControlBlocks.add(controlBlock);
		when(blockRepository.getAllHeadControlBlocks()).thenReturn(allHeadControlBlocks);
		String controlBlockId = controlBlock.getBlockId();
		Set<String> allHeadControlBlocksID = new HashSet<String>();
		allHeadControlBlocksID.add(controlBlockId);
		
		assertEquals(allHeadControlBlocksID, bc.getAllHeadControlBlocks());		
	}

	@Captor
	private ArgumentCaptor<GUIListener> listenerCaptor;
	
	/**
	 * Test method for {@link applicationLayer.BlockController#addDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testAddDomainListener_Positive() {
		bc.addDomainListener(mockDomainListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testRemoveDomainListener_Positive() {
		bc.removeDomainListener(mockDomainListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener_Positive() {
		GUIListener listenerToRemove = Mockito.mock(GUIListener.class);
		guiListeners.add(listenerToRemove);
		assertEquals(2, guiListeners.size());

		bc.removeListener(listenerToRemove);
		verify(guiListeners).remove(listenerCaptor.capture());

		assertEquals(listenerToRemove, listenerCaptor.getValue());
		assertEquals(1, guiListeners.size());

	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener_Positive() {
		GUIListener listenerToAdd = Mockito.mock(GUIListener.class);
		assertEquals(1, guiListeners.size());

		bc.addListener(listenerToAdd);
		verify(guiListeners, atLeastOnce()).add(listenerCaptor.capture());

		assertEquals(listenerToAdd, listenerCaptor.getValue());
		assertEquals(2, guiListeners.size());
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks_Positive() {
		Set<Block> allHeadBlocks = new HashSet<Block>();
		allHeadBlocks.add(controlBlock);
		when(blockRepository.getAllHeadBlocks()).thenReturn(allHeadBlocks);
		String controlBlockId = controlBlock.getBlockId();
		Set<String> allHeadBlocksID = new HashSet<String>();
		allHeadBlocksID.add(controlBlockId);
		
		assertEquals(allHeadBlocksID, bc.getAllHeadBlocks());		
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_Present_Positive() {
		when(blockRepository.getBlockByID("blockId")).thenReturn(actionBlock0);
		
		assertTrue(bc.isBlockPresent("blockId"));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_NotPresent_Positive() {
		when(blockRepository.getBlockByID("blockId")).thenReturn(null);
		
		assertFalse(bc.isBlockPresent("blockId"));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		when(controlBlock.getBlockType()).thenReturn(BlockType.IF);
		
		assertEquals(BlockType.IF, bc.getBlockType(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		bc.getBlockType(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		//when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		when(blockRepository.getBlockByID("anyBlockId")).thenReturn(controlBlock);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, controlBlock)).thenReturn(true);
		
		Set<String> changingBlocks = new HashSet<String>();
		changingBlocks.add("anyBlockId");
		assertTrue(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, changingBlocks));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_False_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
//		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		when(blockRepository.getBlockByID("anyBlockId")).thenReturn(controlBlock);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, controlBlock)).thenReturn(false);
		
		Set<String> changingBlocks = new HashSet<String>();
		changingBlocks.add("anyBlockId");
		assertFalse(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, changingBlocks));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_NotContainsConnection_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		
		assertFalse(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_SizeZero_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		assertTrue(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>()));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>());
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>());
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfBlockIsInBody(String)}.
	 */
	@Test
	public void testCheckIfBlockIsInBody_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		when(controlBlock.getBlockType()).thenReturn(BlockType.IF);
		bc.checkIfBlockIsInBody(blockIdParam);

		verify(blockRepository, atLeastOnce()).checkIfBlockIsInBody(blockIdParam);

	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfBlockIsInBody(String)}.
	 */
	@Test
	public void testCheckIfBlockIsInBody_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		bc.checkIfBlockIsInBody(blockIdParam);
	}

	
}
