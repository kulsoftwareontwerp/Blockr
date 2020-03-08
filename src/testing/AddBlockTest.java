/**
 * 
 */
package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.*;
import domainLayer.*;

/**
 * @author arnel
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AddBlockTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private DomainListener mockDomainListener;

	private ArrayList<BlockType> executableBlockTypes = new ArrayList<BlockType>();
	private ArrayList<BlockType> assessableBlockTypes = new ArrayList<BlockType>();
	private ArrayList<Block> allBlocks = new ArrayList<Block>();

	private ArrayList<String> blockIdsInRepository = new ArrayList<String>();

	@Spy
	private ActionBlock connectedActionBlock;
	@Spy
	private ControlBlock connectedControlBlock;
	@Spy
	private ConditionBlock connectedConditionBlock;
	@Spy
	private OperatorBlock connectedOperatorBlock;
	@Spy
	private IfBlock newIfBlock;
	@Spy
	private NotBlock newNotBlock;
	@Spy
	private MoveForwardBlock newMoveForwardBlock;
	@Spy
	private TurnLeftBlock newTurnLeftBlock;
	@Spy
	private TurnRightBlock newTurnRightBlock;
	@Spy
	private WallInFrontBlock newWallInFrontBlock;
	@Spy
	private WhileBlock newWhileBlock;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		executableBlockTypes.add(BlockType.MoveForward);
		executableBlockTypes.add(BlockType.TurnLeft);
		executableBlockTypes.add(BlockType.TurnRight);
		executableBlockTypes.add(BlockType.While);
		executableBlockTypes.add(BlockType.If);

		assessableBlockTypes.add(BlockType.Not);
		assessableBlockTypes.add(BlockType.WallInFront);

		blockIdsInRepository.add("conditionBlock");
		blockIdsInRepository.add("operatorBlock");
		blockIdsInRepository.add("controlBlock");
		blockIdsInRepository.add("actionBlock");
		blockIdsInRepository.add("nonExisting");

		connectedActionBlock = new MoveForwardBlock("actionBlock");
		connectedControlBlock = new WhileBlock("controlBlock");
		connectedOperatorBlock = new NotBlock("operatorBlock");
		connectedConditionBlock = new WallInFrontBlock("conditionBlock");

		newIfBlock = new IfBlock("newBlock");
		newMoveForwardBlock = new MoveForwardBlock("newBlock");
		newNotBlock = new NotBlock("newBlock");
		newTurnLeftBlock = new TurnLeftBlock("newBlock");
		newTurnRightBlock = new TurnRightBlock("newBlock");
		newWallInFrontBlock = new WallInFrontBlock("newBlock");
		newWhileBlock = new WhileBlock("newBlock");

		allBlocks.add(connectedActionBlock);
		allBlocks.add(connectedControlBlock);
		allBlocks.add(connectedOperatorBlock);
		allBlocks.add(connectedConditionBlock);
		allBlocks.add(newIfBlock);
		allBlocks.add(newNotBlock);
		allBlocks.add(newTurnLeftBlock);
		allBlocks.add(newTurnRightBlock);
		allBlocks.add(newWallInFrontBlock);
		allBlocks.add(newWhileBlock);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	@Mock
	BlockController mockBlockController;
	@InjectMocks
	DomainController dc;

	private void assertExceptionDCAddBlockCombination(BlockType bt, String cb, ConnectionType ct, String excMessage) {
		boolean pass = false;
		try {
			dc.addBlock(bt, cb, ct);
		} catch (IllegalArgumentException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("addBlock failed in the domainController for combination: BlockType=" + bt.toString()
				+ " ConnectedBlockId=" + cb + " ConnectionType=" + ct.toString(), pass);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockDCNegativeNoBlockType() {
		String excMessage = "No blockType given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		dc = new DomainController();

		for (ConnectionType c : ConnectionType.values()) {
			dc.addBlock(null, "", c);
			assertExceptionDCAddBlockCombination(null, "", c, excMessage);
			verifyNoInteractions(mockBlockController);
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockDCNegativeConnectedBlockNoConnection() {
		String excMessage = "No connection given for connected block.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		dc = new DomainController();

		for (BlockType b : BlockType.values()) {
			dc.addBlock(b, anyString(), ConnectionType.NOCONNECTION);
			assertExceptionDCAddBlockCombination(b, anyString(), ConnectionType.NOCONNECTION, excMessage);
			verifyNoInteractions(mockBlockController);
		}

	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockDCNegativeConnectionTypeNull() {
		String excMessage = "Null given as connection, use ConnectionType.NOCONNECTION.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		dc = new DomainController();

		dc.addBlock(any(BlockType.class), anyString(), null);
		verifyNoInteractions(mockBlockController);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockDCNegativeConnectionNoConnectedBlock() {
		String excMessage = "No connected block given with connection.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		dc = new DomainController();

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				dc.addBlock(b, null, c);
				assertExceptionDCAddBlockCombination(b, null, c, excMessage);
				verifyNoInteractions(mockBlockController);
			}
		}

	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockDCPositiveNoConnectedBlock() {
		dc = new DomainController();
		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);

		for (BlockType b : BlockType.values()) {
			dc.addBlock(b, null, ConnectionType.NOCONNECTION);
			verify(mockBlockController).addBlock(blockType.capture(), any(), ConnectionType.NOCONNECTION);
			assertEquals(b, blockType.getValue());
		}

	}

	@Mock
	private BlockRepository mockBlockRepository;
	@Spy
	@InjectMocks
	private BlockController bc;

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBCPositiveMaxNbOfBlocksReached() {
		bc = new BlockController();
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);

		when(mockBlockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				String cb = anyString();
				bc.addBlock(b, cb, c);

				verify(mockBlockRepository).addBlock(blockType.capture(), connectedBlock.capture(),
						connectionType.capture());

				assertEquals(b, blockType.getValue());
				assertEquals(cb, connectedBlock.getValue());
				assertEquals(c, connectionType.getValue());

				InOrder updateResetOrder = inOrder(mockDomainListener);
				updateResetOrder.verify(mockDomainListener).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
				updateResetOrder.verify(mockDomainListener).onResetExecutionEvent(any(ResetExecutionEvent.class));

				ArgumentCaptor<PanelChangeEvent> panelChangeEvent = ArgumentCaptor.forClass(PanelChangeEvent.class);
				verify(mockGuiListener).onPanelChangedEvent(panelChangeEvent.capture());
				assertFalse(panelChangeEvent.getValue().isShown());
				verify(mockGuiListener).onBlockAdded(any(BlockAddedEvent.class));
			}
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBCPositiveMaxNbOfBlocksNotReached() {
		bc = new BlockController();
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);

		when(mockBlockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				String cb = anyString();
				bc.addBlock(b, cb, c);

				verify(mockBlockRepository).addBlock(blockType.capture(), connectedBlock.capture(),
						connectionType.capture());

				assertEquals(b, blockType.getValue());
				assertEquals(cb, connectedBlock.getValue());
				assertEquals(c, connectionType.getValue());

				InOrder updateResetOrder = inOrder(mockDomainListener);
				updateResetOrder.verify(mockDomainListener).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
				updateResetOrder.verify(mockDomainListener).onResetExecutionEvent(any(ResetExecutionEvent.class));

				verify(mockGuiListener, never()).onPanelChangedEvent(any(PanelChangeEvent.class));

				verify(mockGuiListener).onBlockAdded(any(BlockAddedEvent.class));
			}
		}
	}

	@Mock
	private BlockFactory mockBlockFactory;
	@Spy
	@InjectMocks
	private BlockRepository blockRepo;

	private void testAddBlockBRMain() {
		when(mockBlockFactory.createBlock(BlockType.If)).thenReturn(newIfBlock);
		when(mockBlockFactory.createBlock(BlockType.MoveForward)).thenReturn(newMoveForwardBlock);
		when(mockBlockFactory.createBlock(BlockType.Not)).thenReturn(newNotBlock);
		when(mockBlockFactory.createBlock(BlockType.TurnLeft)).thenReturn(newTurnLeftBlock);
		when(mockBlockFactory.createBlock(BlockType.TurnRight)).thenReturn(newTurnRightBlock);
		when(mockBlockFactory.createBlock(BlockType.WallInFront)).thenReturn(newWallInFrontBlock);
		when(mockBlockFactory.createBlock(BlockType.While)).thenReturn(newWhileBlock);

		blockRepo = BlockRepository.getInstance();

		doReturn(connectedConditionBlock).when(blockRepo.getBlockByID("conditionBlock"));
		doReturn(connectedOperatorBlock).when(blockRepo.getBlockByID("operatorBlock"));
		doReturn(connectedControlBlock).when(blockRepo.getBlockByID("controlBlock"));
		doReturn(connectedActionBlock).when(blockRepo.getBlockByID("actionBlock"));
		doCallRealMethod().when(blockRepo.getBlockByID("newBlock"));
		doReturn(null).when(blockRepo.getBlockByID("nonExisting"));

	}

	private void resetBlocks() {
		for (Block block : allBlocks) {
			if (block.getConditionBlock() != null)
				block.setConditionBlock(null);
			if (block.getFirstBlockOfBody() != null)
				block.setFirstBlockOfBody(null);
			if (block.getNextBlock() != null)
				block.setNextBlock(null);
			if (block.getOperand() != null)
				block.setOperand(null);
			reset(block);
		}
		// Todo: Vind een manier om headBlocks en allblocks te legen zonder remove functies op te roepen.
		reset(mockBlockFactory);

	}

	private void assertExceptionBRInvalidBlockConnection(BlockType bt, String cb, ConnectionType ct,
			String excMessage) {
		boolean pass = false;
		try {
			blockRepo.addBlock(bt, cb, ct);
		} catch (InvalidBlockConnectionException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("addBlock failed in the blockRepository for combination: BlockType=" + bt.toString()
				+ " ConnectedBlockId=" + cb + " ConnectionType=" + ct.toString(), pass);
	}

	// Todo: Vind een manier om headBlocks en allblocks te controleren.
	/**
	 * Test method for
	 * {@link applicationLayer.BlockRepository#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBRPositive() {

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);
		testAddBlockBRMain();

		for (BlockType b : executableBlockTypes) {

			blockRepo.addBlock(b, "controlBlock", ConnectionType.UP);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("controlBlock", mockBlockFactory.createBlock(b).getNextBlock().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();

			blockRepo.addBlock(b, "controlBlock", ConnectionType.DOWN);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("newBlock", blockRepo.getBlockByID("controlBlock").getNextBlock().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();

			blockRepo.addBlock(b, "controlBlock", ConnectionType.BODY);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("newBlock", blockRepo.getBlockByID("controlBlock").getFirstBlockOfBody().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();

			blockRepo.addBlock(b, "actionBlock", ConnectionType.UP);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("actionBlock", mockBlockFactory.createBlock(b).getNextBlock().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();

			blockRepo.addBlock(b, "actionBlock", ConnectionType.DOWN);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("newBlock", blockRepo.getBlockByID("actionBlock").getNextBlock().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();
		}
		for (BlockType b : assessableBlockTypes) {

			blockRepo.addBlock(b, "operatorBlock", ConnectionType.CONDITION);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("operatorBlock", mockBlockFactory.createBlock(b).getOperand().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();

			blockRepo.addBlock(b, "controlBlock", ConnectionType.CONDITION);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("newBlock", blockRepo.getBlockByID("controlBlock").getConditionBlock().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();
		}
		for (BlockType b : BlockType.values()) {
			blockRepo.addBlock(b, null, ConnectionType.NOCONNECTION);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("operatorBlock", mockBlockFactory.createBlock(b).getOperand().getBlockId());
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockRepository#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBRNegativeAddExecutableBlockAsCondition() {
		String excMessage = "This block can't be added as a condition for another block.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);

		testAddBlockBRMain();
		for (BlockType b : executableBlockTypes) {
			blockRepo.addBlock(b, "controlBlock", ConnectionType.CONDITION);
			assertExceptionBRInvalidBlockConnection(b, "controlBlock", ConnectionType.CONDITION, excMessage);
			blockRepo.addBlock(b, "operatorBlock", ConnectionType.CONDITION);
			assertExceptionBRInvalidBlockConnection(b, "operatorBlock", ConnectionType.CONDITION, excMessage);
			verifyNoInteractions(mockBlockFactory);
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockRepository#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBRNegativeAddBlockConnectedBlockNoCompatibleCavity() {
		String excMessage = "The connected block doesn't have the requested connection.";

		testAddBlockBRMain();
		for (BlockType nb : BlockType.values()) {
			for (BlockType cb : BlockType.values()) {
				Block connectedBlock = mockBlockFactory.createBlock(cb);
				reset(mockBlockFactory);
				for (ConnectionType ct : ConnectionType.values()) {
					if (connectedBlock instanceof ConditionBlock) {
						assertExceptionBRInvalidBlockConnection(nb, "conditionBlock", ct, excMessage);
					}
					if (connectedBlock instanceof OperatorBlock) {
						if (ct != ConnectionType.CONDITION)
							assertExceptionBRInvalidBlockConnection(nb, "operatorBlock", ct, excMessage);
					}
					if (connectedBlock instanceof ActionBlock) {
						if (ct != ConnectionType.UP && ct != ConnectionType.DOWN)
							assertExceptionBRInvalidBlockConnection(nb, "actionBlock", ct, excMessage);
					}
					verifyNoInteractions(mockBlockFactory);
				}
			}

		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockRepository#addBlock(domainLayer.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testAddBlockBRNegativeAddAssessableBlockNoCompatible() {
		String excMessage = "The connected block doesn't have the requested connection.";

		testAddBlockBRMain();
		for (BlockType nb : assessableBlockTypes) {
			Block newblock = mockBlockFactory.createBlock(nb);

			for (BlockType cb : executableBlockTypes) {
				Block connectedBlock = mockBlockFactory.createBlock(cb);
				reset(mockBlockFactory);
				for (ConnectionType ct : ConnectionType.values()) {
					if (ct != ConnectionType.CONDITION) {
						assertExceptionBRInvalidBlockConnection(nb, "executableBlock", ct, excMessage);
						verifyNoInteractions(mockBlockFactory);
					}
				}
			}

		}
	}

	@Mock
	private BlockIDGenerator mockBlockIDGenerator;

	@Spy
	@InjectMocks
	private BlockFactory blockFactory;

	/**
	 * Test method for
	 * {@link domainLayer.BlockFactory#addBlock(domainLayer.BlockType)}.
	 */
	@Test
	public void testAddBlockBFPositive() {
		when(mockBlockIDGenerator.getBlockID()).thenReturn("newBlock");
		blockFactory=new BlockFactory();
		Block block;
		block = blockFactory.createBlock(BlockType.If);
		assertEquals(IfBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.While);
		assertEquals(WhileBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.Not);
		assertEquals(NotBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.WallInFront);
		assertEquals(WallInFrontBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.MoveForward);
		assertEquals(MoveForwardBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.TurnLeft);
		assertEquals(TurnLeftBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());

		block = blockFactory.createBlock(BlockType.TurnRight);
		assertEquals(TurnRightBlock.class, block.getClass());
		assertEquals("newBlock", block.getBlockId());
		
		verify(mockBlockIDGenerator,times(7)).getBlockID();
	}
	
	
	private BlockIDGenerator idGenerator;
	
	/**
	 * Test method for
	 * {@link domainLayer.BlockIDGenerator#getBlockID()}.
	 */
	@Test
	public void testAddBlockBIDGPositive() {
		idGenerator=BlockIDGenerator.getInstance();
		
		String id1 = idGenerator.getBlockID();
		
		String id2 = idGenerator.getBlockID();
		
		idGenerator=BlockIDGenerator.getInstance();
		
		String id3 = idGenerator.getBlockID();
		
		String id4 = idGenerator.getBlockID();
		
		assertNotEquals(id1, id2);
		assertNotEquals(id1, id3);
		assertNotEquals(id1, id4);
		assertNotEquals(id2, id3);
		assertNotEquals(id2, id4);
		assertNotEquals(id3, id4);
	}
	
	

}
