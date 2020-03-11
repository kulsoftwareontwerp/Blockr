/**
 * 
 */
package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockFactory;
import domainLayer.blocks.BlockIDGenerator;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.BlockType;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.MoveForwardBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.OperatorBlock;
import domainLayer.blocks.TurnLeftBlock;
import domainLayer.blocks.TurnRightBlock;
import domainLayer.blocks.WallInFrontBlock;
import domainLayer.blocks.WhileBlock;
import events.BlockAddedEvent;
import events.DomainListener;
import events.GUIListener;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import exceptions.InvalidBlockConnectionException;
import exceptions.MaxNbOfBlocksReachedException;
import exceptions.NoSuchConnectedBlockException;

/**
 * 
 * @author group17
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

	private ActionBlock connectedActionBlock;

	private ControlBlock connectedControlBlock;

	private ConditionBlock connectedConditionBlock;

	private OperatorBlock connectedOperatorBlock;

	private IfBlock newIfBlock;

	private NotBlock newNotBlock;

	private MoveForwardBlock newMoveForwardBlock;

	private TurnLeftBlock newTurnLeftBlock;

	private TurnRightBlock newTurnRightBlock;

	private WallInFrontBlock newWallInFrontBlock;

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
		blockIdsInRepository.add("noBlock");

		connectedActionBlock = spy(new MoveForwardBlock("actionBlock"));
		connectedControlBlock = spy(new WhileBlock("controlBlock"));
		connectedOperatorBlock = spy(new NotBlock("operatorBlock"));
		connectedConditionBlock = spy(new WallInFrontBlock("conditionBlock"));

		newIfBlock = spy(new IfBlock("newBlock"));
		newMoveForwardBlock = spy(new MoveForwardBlock("newBlock"));
		newNotBlock = spy(new NotBlock("newBlock"));
		newTurnLeftBlock = spy(new TurnLeftBlock("newBlock"));
		newTurnRightBlock = spy(new TurnRightBlock("newBlock"));
		newWallInFrontBlock = spy(new WallInFrontBlock("newBlock"));
		newWhileBlock = spy(new WhileBlock("newBlock"));

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

	@Mock(name = "blockController")
	private BlockController mockBlockController;
	@Spy
	@InjectMocks
	private DomainController dc;

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
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testDCAddBlockNegativeNoBlockType() {
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
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testDCAddBlockNegativeConnectedBlockNoConnection() {
		String excMessage = "No connection given for connected block.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		dc = new DomainController();

		for (BlockType b : BlockType.values()) {
			dc.addBlock(b, "connectedBlockId", ConnectionType.NOCONNECTION);
			assertExceptionDCAddBlockCombination(b, "connectedBlockId", ConnectionType.NOCONNECTION, excMessage);
			verifyNoInteractions(mockBlockController);
		}

	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testDCAddBlockNegativeConnectionTypeNull() {
		String excMessage = "Null given as connection, use ConnectionType.NOCONNECTION.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		dc.addBlock(BlockType.If, "connectedBlockId", null);
		verifyNoInteractions(mockBlockController);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testDCAddBlockNegativeConnectionNoConnectedBlock() {
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
	 * {@link applicationLayer.DomainController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testDCAddBlockPositiveNoConnectedBlock() {

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		for (BlockType b : BlockType.values()) {
			dc.addBlock(b, "", ConnectionType.NOCONNECTION);
			verify(mockBlockController, atLeastOnce()).addBlock(blockType.capture(), anyString(),
					connectionType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals(ConnectionType.NOCONNECTION, connectionType.getValue());
		}

	}

	@Mock(name = "programBlockRepository")
	private BlockRepository mockBlockRepository;
	@Spy
	@InjectMocks
	private BlockController bc;

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBCAddBlockPositiveMaxNbOfBlocksReached() {
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				when(mockBlockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false, true);
				String cb = "connectedBlockId";
				bc.addBlock(b, cb, c);

				verify(mockBlockRepository, atLeastOnce()).addBlock(blockType.capture(), connectedBlock.capture(),
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
				reset(mockBlockRepository);
			}
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBCAddBlockPositiveMaxNbOfBlocksNotReached() {
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);

		when(mockBlockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				String cb = "connectedBlockId";
				bc.addBlock(b, cb, c);

				verify(mockBlockRepository, atLeastOnce()).addBlock(blockType.capture(), connectedBlock.capture(),
						connectionType.capture());

				assertEquals(b, blockType.getValue());
				assertEquals(cb, connectedBlock.getValue());
				assertEquals(c, connectionType.getValue());

				InOrder updateResetOrder = inOrder(mockDomainListener);
				updateResetOrder.verify(mockDomainListener).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
				updateResetOrder.verify(mockDomainListener).onResetExecutionEvent(any(ResetExecutionEvent.class));

				verify(mockGuiListener, never()).onPanelChangedEvent(any(PanelChangeEvent.class));

				verify(mockGuiListener, atLeastOnce()).onBlockAdded(any(BlockAddedEvent.class));
			}
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBCAddBlockNegativeMaxNbOfBlocksAlreadyReached() {
		String excMessage = "The maximum number of blocks has already been reached.";
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);

		when(mockBlockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (BlockType b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				String cb = "connectedBlockId";
				boolean pass = false;
				try {
					bc.addBlock(b, cb, c);
				} catch (MaxNbOfBlocksReachedException e) {
					pass = e.getMessage().equals(excMessage);
				}
				assertTrue("addBlock failed in the blockController for combination: BlockType=" + b.toString()
						+ " ConnectedBlockId=" + cb + " ConnectionType=" + c.toString(), pass);
			}
		}
		verify(mockBlockRepository, never()).addBlock(blockType.capture(), connectedBlock.capture(),
				connectionType.capture());
	}

	@Mock(name = "blockFactory")
	private BlockFactory mockBlockFactory;
	@Spy
	@InjectMocks
	private BlockRepository blockRepo;

	private void testAddBlockBRMain() {
		lenient().when(mockBlockFactory.createBlock(BlockType.If)).thenReturn(newIfBlock);
		when(mockBlockFactory.createBlock(BlockType.MoveForward)).thenReturn(newMoveForwardBlock);
		when(mockBlockFactory.createBlock(BlockType.Not)).thenReturn(newNotBlock);
		lenient().when(mockBlockFactory.createBlock(BlockType.TurnLeft)).thenReturn(newTurnLeftBlock);
		lenient().when(mockBlockFactory.createBlock(BlockType.TurnRight)).thenReturn(newTurnRightBlock);
		when(mockBlockFactory.createBlock(BlockType.WallInFront)).thenReturn(newWallInFrontBlock);
		lenient().when(mockBlockFactory.createBlock(BlockType.While)).thenReturn(newWhileBlock);

		when(blockRepo.getBlockByID("conditionBlock")).thenReturn(connectedConditionBlock);
		lenient().when(blockRepo.getBlockByID("operatorBlock")).thenReturn(connectedOperatorBlock);
		when(blockRepo.getBlockByID("controlBlock")).thenReturn(connectedControlBlock);
		when(blockRepo.getBlockByID("actionBlock")).thenReturn(connectedActionBlock);
		when(blockRepo.getBlockByID("noBlock")).thenReturn(null);
		when(blockRepo.getBlockByID("newBlock")).thenCallRealMethod();

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
		// TODO: Vind een manier om headBlocks en allblocks te legen zonder remove
		// functies op te roepen.
		reset(mockBlockFactory);
		testAddBlockBRMain();

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

	// TODO: Vind een manier om headBlocks en allblocks te controleren.
	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockPositive() {

		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
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

			blockRepo.addBlock(b, "operatorBlock", ConnectionType.RIGHT);
			verify(mockBlockFactory).createBlock(blockType.capture());
			assertEquals(b, blockType.getValue());
			assertEquals("newBlock", blockRepo.getBlockByID("operatorBlock").getOperand().getBlockId());
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
			assertEquals(blockRepo.getBlockByID("newBlock"), mockBlockFactory.createBlock(b));
			resetBlocks();
		}
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockNegativeAddExecutableBlockAsCondition() {
		String excMessage = "This block is no AssessableBlock.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);

		testAddBlockBRMain();
		for (BlockType b : executableBlockTypes) {
			blockRepo.addBlock(b, "controlBlock", ConnectionType.CONDITION);
			assertExceptionBRInvalidBlockConnection(b, "controlBlock", ConnectionType.CONDITION, excMessage);
			blockRepo.addBlock(b, "operatorBlock", ConnectionType.RIGHT);
			assertExceptionBRInvalidBlockConnection(b, "operatorBlock", ConnectionType.CONDITION, excMessage);
			verifyNoInteractions(mockBlockFactory);
		}
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockNegativeAddBlockConnectedBlockNoCompatibleCavity() {
		String excMessage = "The connected block doesn't have the requested connection.";

		testAddBlockBRMain();
		for (BlockType nb : BlockType.values()) {
			Block newBlock = mockBlockFactory.createBlock(nb);
			for (BlockType cb : BlockType.values()) {
				Block connectedBlock = mockBlockFactory.createBlock(cb);
				resetBlocks();

				for (ConnectionType ct : ConnectionType.values()) {
					if (connectedBlock instanceof ConditionBlock) {
						if (ct != ConnectionType.NOCONNECTION && ct != ConnectionType.UP) {
							if (ct == ConnectionType.CONDITION || ct == ConnectionType.RIGHT) {
								if (newBlock instanceof AssessableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "conditionBlock", ct, excMessage);
								}
							} else {
								if (newBlock instanceof ExecutableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "conditionBlock", ct, excMessage);
								}
							}
						}
					}
					if (connectedBlock instanceof OperatorBlock) {
						if (ct != ConnectionType.RIGHT && ct != ConnectionType.NOCONNECTION
								&& ct != ConnectionType.UP) {
							if (ct == ConnectionType.CONDITION) {
								if (newBlock instanceof AssessableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "operatorBlock", ct, excMessage);
								}
							} else {
								if (newBlock instanceof ExecutableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "operatorBlock", ct, excMessage);
								}
							}
						}
					}
					if (connectedBlock instanceof ActionBlock) {
						if (ct != ConnectionType.UP && ct != ConnectionType.DOWN && ct != ConnectionType.NOCONNECTION) {
							if ((ct == ConnectionType.RIGHT || ct == ConnectionType.CONDITION)) {
								if (newBlock instanceof AssessableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "actionBlock", ct, excMessage);
								}
							} else {
								if (newBlock instanceof ExecutableBlock) {
									assertExceptionBRInvalidBlockConnection(nb, "actionBlock", ct, excMessage);
								}
							}
						}
					}
					if (connectedBlock instanceof ControlBlock) {
						if (ct == ConnectionType.RIGHT && newBlock instanceof AssessableBlock)
							assertExceptionBRInvalidBlockConnection(nb, "controlBlock", ct, excMessage);
					}

					if (ct == ConnectionType.UP) {
						if (!(newBlock instanceof ExecutableBlock)) {
							assertExceptionBRInvalidBlockConnection(nb, "actionBlock", ct, excMessage);
						}

					}

					assertEquals(null, blockRepo.getBlockByID("newBlock"));
				}
			}

		}
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockNegativeAddBlockNoSuchConnectedBlock() {
		String excMessage = "The requested connectedBlockId does not exist in the domain.";
		testAddBlockBRMain();
		for (BlockType nb : BlockType.values()) {

			for (ConnectionType ct : ConnectionType.values()) {
				if (ct != ConnectionType.NOCONNECTION) {
					boolean pass = false;
					try {
						blockRepo.addBlock(nb, "noBlock", ct);
					} catch (NoSuchConnectedBlockException e) {
						pass = e.getMessage().equals(excMessage);
					}
					assertTrue("addBlock failed in the blockRepository for combination: BlockType=" + nb.toString()
							+ " ConnectedBlockId= noBlock ConnectionType=" + ct.toString(), pass);

				}
			}

		}
	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockNegativeAddBlockConnectionAlreadyOccupied() {
		String excMessage = "Connection at connectedBlock is already occupied.";
		testAddBlockBRMain();
		
		doReturn(connectedOperatorBlock).when(connectedControlBlock).getConditionBlock();
		doReturn(connectedConditionBlock).when(connectedOperatorBlock).getOperand();
		doReturn(connectedActionBlock).when(connectedControlBlock).getFirstBlockOfBody();
		doReturn(connectedActionBlock).when(connectedActionBlock).getNextBlock();

		
		lenient().when(blockRepo.getBlockByID("operatorBlock")).thenReturn(connectedOperatorBlock);
		when(blockRepo.getBlockByID("controlBlock")).thenReturn(connectedControlBlock);
		when(blockRepo.getBlockByID("actionBlock")).thenReturn(connectedActionBlock);
		
		boolean pass = false;
		for (String c : blockIdsInRepository) {
			
			if (c != "noBlock" && c!="conditionBlock") {
				for (BlockType bt : BlockType.values()) {
					for (ConnectionType ct : ConnectionType.values()) {
						if (ct != ConnectionType.NOCONNECTION && ct!=ConnectionType.UP) {

							try {
								blockRepo.addBlock(bt, c, ct);
							} catch (InvalidBlockConnectionException e) {
								if(pass==false) {
								pass = e.getMessage().equals(excMessage);
								}
							} catch (Exception e) {
								// Not the purpose of this test.
							}

						}
					}
				}
			}
			
		}
		assertTrue("addBlock failed in the blockRepository for alreadyOccupied", pass);

	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#addBlock(domainLayer.blocks.BlockType, java.lang.String, applicationLayer.ConnectionType)}.
	 */
	@Test
	public void testBRAddBlockNegativeAddAssessableBlockNoCompatible() {
		String excMessage = "The new block and/or the connected block is no ExecutableBlock.";
		testAddBlockBRMain();
		for (BlockType nb : assessableBlockTypes) {

			for (ConnectionType ct : ConnectionType.values()) {
				if (ct == ConnectionType.DOWN || ct == ConnectionType.BODY) {
					assertExceptionBRInvalidBlockConnection(nb, "actionBlock", ct, excMessage);

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
	 * {@link domainLayer.blocks.BlockFactory#addBlock(domainLayer.blocks.BlockType)}.
	 */
	@Test
	public void testBFAddBlockPositive() {
		when(mockBlockIDGenerator.generateBlockID()).thenReturn("newBlock");
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

		verify(mockBlockIDGenerator, times(7)).generateBlockID();
	}

	private BlockIDGenerator idGenerator;

	/**
	 * Test method for {@link domainLayer.blocks.BlockIDGenerator#generateBlockID()}.
	 */
	@Test
	public void testBIDAddBlockGPositive() {
		idGenerator = BlockIDGenerator.getInstance();

		String id1 = idGenerator.generateBlockID();

		String id2 = idGenerator.generateBlockID();

		idGenerator = BlockIDGenerator.getInstance();

		String id3 = idGenerator.generateBlockID();

		String id4 = idGenerator.generateBlockID();

		assertNotEquals(id1, id2);
		assertNotEquals(id1, id3);
		assertNotEquals(id1, id4);
		assertNotEquals(id2, id3);
		assertNotEquals(id2, id4);
		assertNotEquals(id3, id4);
	}

}
