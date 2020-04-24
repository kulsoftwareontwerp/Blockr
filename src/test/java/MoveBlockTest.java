//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.security.InvalidParameterException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InOrder;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.internal.invocation.mockref.MockReference;
//import org.mockito.internal.util.reflection.FieldSetter;
//import org.mockito.internal.verification.NoInteractions;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import applicationLayer.BlockController;
//import applicationLayer.DomainController;
//import domainLayer.blocks.*;
//import events.*;
//import exceptions.*;
//import types.BlockType;
//import types.ConnectionType;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class MoveBlockTest {
//
//	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
//	private ArrayList<BlockType> executableBlockTypes = new ArrayList<BlockType>();
//	private ArrayList<BlockType> assessableBlockTypes = new ArrayList<BlockType>();
//	private Set<String> blockIdsInRepository = new HashSet<String>();
//	
//	@Rule
//	public ExpectedException exceptionRule = ExpectedException.none();
//
//
//
//	
//	@Mock(name = "blockController")
//	private BlockController mockBlockController;
//	@Spy
//	@InjectMocks
//	private DomainController dc;
//	
//	
//	@Mock
//	private DomainListener mockDomainListener;
//	@Mock
//	private GUIListener mockGUIListener;
//	
//	
//	@Mock(name = "programBlockRepository")
//	private BlockRepository mockBlockReprository;
//	@Spy
//	@InjectMocks
//	private BlockController bc;
//	
//	@Mock
//	private Set<Block> headBlocks;
//	@Spy
//	@InjectMocks
//	private BlockRepository blockRepository;
//	
//	@Spy
//	private BlockRepository blockRepositoryValidBeforeMove;
//	
//	
//	
//	@Spy
//	private Set<Block> allBlocks;
//	
//	
//	
//	private ActionBlock connectedActionBlockA;
//	private ActionBlock connectedActionBlockB;
//	private ActionBlock movedActionBlock;
//
//	private ControlBlock connectedControlBlockA;
//	private ControlBlock connectedControlBlockB;
//	private ControlBlock movedControlBlock;
//
//	private ConditionBlock connectedConditionBlockA;
//	private ConditionBlock connectedConditionBlockB;
//	private ConditionBlock movedConditionBlock;
//
//	private OperatorBlock connectedOperatorBlockA;
//	private OperatorBlock connectedOperatorBlockB;
//	private OperatorBlock movedOperatorBlock;
//
//	private IfBlock connectedIfBlockA;
//	private IfBlock connectedIfBlockB;
//	private IfBlock movedIfBlock;
//
//	private NotBlock connectedNotBlockA;
//	private NotBlock connectedNotBlockB;
//	private NotBlock movedNotBlock;
//
//	private MoveForwardBlock connectedMoveForwardBlockA;
//	private MoveForwardBlock connectedMoveForwardBlockB;
//	private MoveForwardBlock movedMoveForwardBlock;
//
//	private TurnLeftBlock connectedTurnLeftBlockA;
//	private TurnLeftBlock connectedTurnLeftBlockkB;
//	private TurnLeftBlock movedTurnLeftBlock;
//
//	private TurnRightBlock TurnRightBlocA;
//	private TurnRightBlock TurnRightBlocB;
//	private TurnRightBlock movedTurnRightBloc;
//
//	private WallInFrontBlock WallInFrontBlockA;
//	private WallInFrontBlock WallInFrontBlockB;
//	private WallInFrontBlock movedWallInFrontBlock;
//
//	private WhileBlock connectedWhileBlockA;
//	private WhileBlock connectedWhileBlockB;
//	private WhileBlock movedWhileBlock;
//
//	
//	
//	
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		
//		bc.addDomainListener(mockDomainListener);
//		bc.addListener(mockGUIListener);
//		
//		/*executableBlockTypes.add(BlockType.MoveForward);
//		executableBlockTypes.add(BlockType.TurnLeft);
//		executableBlockTypes.add(BlockType.TurnRight);
//		executableBlockTypes.add(BlockType.While);
//		executableBlockTypes.add(BlockType.If);
//
//		assessableBlockTypes.add(BlockType.Not);
//		assessableBlockTypes.add(BlockType.WallInFront);*/
//		
//		connectionTypes.add(ConnectionType.BODY);
//		connectionTypes.add(ConnectionType.CONDITION);
//		connectionTypes.add(ConnectionType.LEFT);
//		connectionTypes.add(ConnectionType.DOWN);
//		connectionTypes.add(ConnectionType.NOCONNECTION);
//		connectionTypes.add(ConnectionType.OPERAND);
//		connectionTypes.add(ConnectionType.UP);
//		
//		
//		// Mulptiple head block reprository
//		movedActionBlock = spy(new MoveForwardBlock("1"));
//		connectedMoveForwardBlockA = spy(new MoveForwardBlock("2"));
//		connectedMoveForwardBlockB = spy(new MoveForwardBlock("3"));
//		
//		
//		connectedIfBlockA = spy(new IfBlock("4"));
//		connectedIfBlockB = spy(new IfBlock("5"));
//		movedIfBlock = spy(new IfBlock("7"));
//		
//		connectedWhileBlockA = spy(new WhileBlock("8"));
//		connectedWhileBlockB = spy(new WhileBlock("9"));
//		movedWhileBlock = spy(new WhileBlock("10"));
//		
//		WallInFrontBlockA = spy(new WallInFrontBlock("11"));
//		WallInFrontBlockB = spy(new WallInFrontBlock("12"));
//		movedWallInFrontBlock = spy(new WallInFrontBlock("13"));
//		
//		connectedOperatorBlockA = spy(new NotBlock("14"));
//		
//		when(blockRepository.getBlockByID("1")).thenReturn(movedActionBlock);
//		when(blockRepository.getBlockByID("2")).thenReturn(connectedMoveForwardBlockA);
//		when(blockRepository.getBlockByID("3")).thenReturn(connectedMoveForwardBlockB);
//		when(blockRepository.getBlockByID("4")).thenReturn(connectedIfBlockA);
//
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}	
//	
//	
//	
//	@Test
//	public void testMoveBlockRepository() {
//		// verify(mockBlockController).moveBlock("1", null , ConnectionType.NOCONNECTION
//		// , "2" , ConnectionType.DOWN );
//	}
//	// POSITIVE TESTS
//	
//	@Test
//	public void testDCMoveBlockNegativeConnectionTypeNull() {
//		exceptionRule.expect(IllegalArgumentException.class);
//		
//		dc.moveBlock("", "","", null);
//		verifyNoInteractions(mockBlockController);
//		
//		dc.moveBlock("1", "","", null);
//		verifyNoInteractions(mockBlockController);
//		
//		dc.moveBlock("1", "","", null);
//		verifyNoInteractions(mockBlockController);
//		
//		dc.moveBlock("1", "","2", null);
//		verifyNoInteractions(mockBlockController);
//		
//		dc.moveBlock("1", "","3", null);
//		verifyNoInteractions(mockBlockController);
//
//	}
//	
//	public void testDCMovePositive() {
//		
//		dc.moveBlock("1", "","", ConnectionType.NOCONNECTION);
//		verify(mockBlockController).moveBlock("1",  "", "", ConnectionType.NOCONNECTION);
//		
//		for(ConnectionType connectionType: connectionTypes ) {
//		
//		dc.moveBlock("1",  "","", ConnectionType.NOCONNECTION);
//		verify(mockBlockController).moveBlock("1",  "", "", ConnectionType.NOCONNECTION);
//		
//		dc.moveBlock("1",  "","3", connectionType);
//		verify(mockBlockController).moveBlock("1", "","3", connectionType);
//		
//		
//		dc.moveBlock("1",  "", "3", connectionType);
//		verify(mockBlockController).moveBlock("1", "", "3", connectionType);
//		
//		}
//		
//	}
//	
//	
//	public void assertExceptionDCMoveBlock(String movedBlockId, String connectedBeforeMoveBlockId,
//			ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove,String excMessage) {
//		boolean pass = false;
//		
//		try {
//			dc.moveBlock(movedBlockId, "",connectedAfterMoveBlockId, connectionAfterMove );
//		} catch (IllegalArgumentException e) {
//			pass = e.getMessage().equals(excMessage);
//		}
//		assertTrue("moveBlock failed in the domainController for combination="+movedBlockId+" "+connectedBeforeMoveBlockId+" "+connectionBeforeMove+" "+connectedAfterMoveBlockId+" "+connectionAfterMove,true);
//	}
//	
//	@Test
//	public void testDCMoveBlockNegative() {
//			connectionTypes.remove(ConnectionType.NOCONNECTION);
//			for(ConnectionType connectionType: connectionTypes ) {
//					assertExceptionDCMoveBlock("","",connectionType,"",connectionType,"No movedBlockID given");
//					verifyNoInteractions(mockBlockController);
//
//					assertExceptionDCMoveBlock("1","",connectionType,"",null,"Null given as connection, use ConnectionType.NOCONNECTION.");
//					verifyNoInteractions(mockBlockController);
//					
//					assertExceptionDCMoveBlock("1","",null,"",connectionType,"Null given as connection, use ConnectionType.NOCONNECTION.");
//					verifyNoInteractions(mockBlockController);
//					
//					assertExceptionDCMoveBlock("1","2",connectionType,"",connectionType,"No blockId given for connectedAfterMovedBlockID");
//					verifyNoInteractions(mockBlockController);	
//		}
//	}
//	
//	
//	
//	@Test 
//	public void testBCMoveBlockNegativeNoMovedBlock() {
//		
//	}
//	
//	@Test
//	public void testBCMoveBlockNegativeNoConnectedBlockWithInvalidConnectionType() {
//		
//	}
//	
//	@Test
//	public void testBCMoveBlockNegativeConnectionTypeNull() {
//		exceptionRule.expect(IllegalArgumentException.class);
//		
//		InOrder updateResetOrder = inOrder(mockDomainListener);
//		dc.moveBlock("", "","", null);
//		verifyNoInteractions(mockDomainListener);
//		verifyNoInteractions(mockGUIListener);
//		
//		dc.moveBlock("1",  "","", null);
//		verifyNoInteractions(mockDomainListener);
//		verifyNoInteractions(mockGUIListener);
//		
//		dc.moveBlock("1", "","", null);
//		verifyNoInteractions(mockDomainListener);
//		verifyNoInteractions(mockGUIListener);
//		
//		dc.moveBlock("1",  "","2", null);
//		verifyNoInteractions(mockDomainListener);
//		verifyNoInteractions(mockGUIListener);
//		
//		dc.moveBlock("1", "","3", null);
//		verifyNoInteractions(mockDomainListener);
//		verifyNoInteractions(mockGUIListener);
//	}
//	
//	@Test
//	public void testBCMoveBlockPositiveNoConnectedBlockWitchValidConnectionType() {
//		
//	}
//	
//	@Test
//	public void testBCMoveBlockPositive() {
//		//mockDomainListeners.add(mockDomainListener);
//		InOrder updateResetOrder = inOrder(mockDomainListener);
//		blockIdsInRepository.add("1");
//		blockIdsInRepository.add("2");
//		blockIdsInRepository.add("3");
//		
//		//when(mockBlockReprository.moveBlock("1","",ConnectionType.NOCONNECTION)).thenReturn(blockIdsInRepository);
//		
//		
// //case filtred by domaincontroller
//		for(ConnectionType connectionType: connectionTypes) {
//			//TODO:fix this... pls
////			when(mockBlockReprository.moveBlock("1","",connectionType)).thenReturn(blockIdsInRepository);
////			when(mockBlockReprository.moveBlock("1","3",connectionType)).thenReturn(blockIdsInRepository);
//			
//			bc.moveBlock("1", "", "", connectionType);
//			verify(mockBlockReprository).moveBlock("1","", connectionType);
//			updateResetOrder.verify(mockDomainListener, atLeastOnce()).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
//			updateResetOrder.verify(mockDomainListener, atLeastOnce()).onResetExecutionEvent(any(ResetExecutionEvent.class));
//			verify(mockGUIListener,atLeastOnce()).onBlockChangeEvent(any(BlockChangeEvent.class));
//			
//			bc.moveBlock("1",  "", "3", connectionType);
//			verify(mockBlockReprository).moveBlock("1", "3", connectionType);
//			updateResetOrder.verify(mockDomainListener, atLeastOnce()).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
//			updateResetOrder.verify(mockDomainListener, atLeastOnce()).onResetExecutionEvent(any(ResetExecutionEvent.class));
//			verify(mockGUIListener,atLeastOnce()).onBlockChangeEvent(any(BlockChangeEvent.class));
//		}
//	}
//	
//	public void assertExceptionBRMoveBlock(String movedBlockId, String connectedBeforeMoveBlockId,
//			ConnectionType connectionBeforeMove, String connectedAfterMoveBlockId, ConnectionType connectionAfterMove,String excMessage) {
//		boolean pass = false;
//		
//		try {
//			blockRepository.moveBlock(movedBlockId, connectedAfterMoveBlockId,connectionAfterMove );
//		} catch (NoSuchConnectedBlockException e) {
//			pass = e.getMessage().equals(excMessage);
//		}
//		catch (InvalidBlockConnectionException e)
//		{
//			pass = e.getMessage().equals(excMessage);
//		}
//		assertTrue("moveBlock failed in the domainController for combination="+movedBlockId+" "+connectedBeforeMoveBlockId+" "+connectionBeforeMove+" "+connectedAfterMoveBlockId+" "+connectionAfterMove,pass);
//	}
//	
////Cannot be tested because private fields cannot be mocked, for this test there's a need to 
////Simulate a full program inside the program Area
////	@Test
////	public void testBRMoveBlockNegativeBlocksNotInDomain() {//Specifieker volgens case, forloop dus niet gebruiken
////		
////		ArrayList<ConnectionType> connectionTypesA;
////		ArrayList<ConnectionType> connectionTypesB;
////		
////		String excMessage = "The requested block doens't exist in the domain";
////		
////		//to Avoid other exceptions that are tested in ohter test case
////		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);
////		connectedIfBlockA.setConditionBlock(movedOperatorBlock);
////		connectedOperatorBlockA.setOperand(movedWallInFrontBlock);
////		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);
////		
////		
////		//Test for movedBlock == null
////		
////		connectionTypes = new ArrayList<ConnectionType>();
////		connectionTypes.add(ConnectionType.BODY);
////		connectionTypes.add(ConnectionType.DOWN);
////		connectionTypes.add(ConnectionType.LEFT);
////		connectionTypes.add(ConnectionType.OPERAND);
////		connectionTypes.add(ConnectionType.UP);
////		for(ConnectionType connectionTypeA : connectionTypes) {
////			for(ConnectionType connectionTypeB : connectionTypes) {
////			assertExceptionBRMoveBlock("40", "", connectionTypeA, "",connectionTypeB, excMessage );
////			}
////		}
////		
////		
////		connectionTypesA = new ArrayList<ConnectionType>();
////		connectionTypesB = new ArrayList<ConnectionType>();
////		
////		connectionTypesA.add(ConnectionType.BODY);
////		connectionTypesA.add(ConnectionType.DOWN);
////		connectionTypesA.add(ConnectionType.OPERAND);
////		connectionTypesA.add(ConnectionType.CONDITION);
////		
////		connectionTypesB.add(ConnectionType.BODY);
////		connectionTypesB.add(ConnectionType.CONDITION);
////		connectionTypesB.add(ConnectionType.LEFT);
////		connectionTypesB.add(ConnectionType.DOWN);
////		connectionTypesB.add(ConnectionType.NOCONNECTION);
////		connectionTypesB.add(ConnectionType.OPERAND);
////		connectionTypesB.add(ConnectionType.UP);
////		
////		for(ConnectionType connectionTypeA : connectionTypesA) {
////			for(ConnectionType connectionTypeB : connectionTypesB) {
////			assertExceptionBRMoveBlock("1", "30", connectionTypeA, "",connectionTypeB, excMessage );
////			}
////		}
////		
////		connectionTypes = new ArrayList<ConnectionType>();
////		connectionTypes.add(ConnectionType.BODY);
////		connectionTypes.add(ConnectionType.DOWN);
////		connectionTypes.add(ConnectionType.LEFT);
////		connectionTypes.add(ConnectionType.OPERAND);
////		//connectionTypes.add(ConnectionType.UP); cannot be tested due to HeadBlocks
////		for(ConnectionType connectionType : connectionTypes) {
////			assertExceptionBRMoveBlock("1", "", ConnectionType.NOCONNECTION, "", connectionType, excMessage );
////		}
////	}
//	
//	@Test
//	public void testBRMoveBlockNegativeConnectionNotPossible() {
//		
//	}
////-------------------------------------------------------------------------------------------------------------
//	
//	// TESTS CONNECTIONTYPE.NOCONNECTION
//	@Test
//	public void testBRMoveBlockPositiveWithNOCONNETION() {
//		
//		headBlocks.add(connectedIfBlockA);
//		headBlocks.add(connectedMoveForwardBlockB);
//		
//		//when(headBlocks.contains(connectedMoveForwardBlockB)).thenReturn(true);
//		
//		// TESTS CONNECTIONTYPE.NOCONNECTION - NOCONNECTION
//		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION);
//
//		// TESTS CONNECTIONTYPE.NOCONNECTION - DOWN
//		blockRepository.moveBlock("1",  "2", ConnectionType.DOWN);
//		assertEquals( movedActionBlock, connectedMoveForwardBlockA.getNextBlock());
//		
//		// TESTS CONNECTIONTYPE.NOCONNECTION - UP
//		//Cannot be tested due to HeadBlocks !!!!!
//		//blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "3", ConnectionType.UP);
//		//assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);
//
//		// TESTS CONNECTIONTYPE.NOCONNECTION - BODY
//		blockRepository.moveBlock("1", "4", ConnectionType.BODY);
//		assertEquals( movedActionBlock ,connectedIfBlockA.getFirstBlockOfBody());
//
//		//TESTS CONNECTIONTYPE.NOCONNECTION - CONDITION ( SEE NEGATIVE TEST CASES)
//	}
//
//	// TESTS CONNECTIONTYPE.DOWN
//	@Test
//	public void testBRMoveBlockPositiveWithDOWN() {
//
//		
//		//TESTS CONNECTIONTYPE.DOWN - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)
//
//		// TESTS CONNECTIONTYPE.DOWN - DOWN
//		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment
//
//		assertEquals( movedActionBlock, connectedMoveForwardBlockA.getNextBlock());// Initial state
//		blockRepository.moveBlock("1", "3", ConnectionType.DOWN);
//		//assertEquals(null, connectedMoveForwardBlockA.getNextBlock() );
//		assertEquals(movedActionBlock , connectedMoveForwardBlockB.getNextBlock() );
//
//
//		// TESTS CONNECTIONTYPE.DOWN - UP
//		// Test with no nextBlock for the MovedActionBlock
//		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment
//
//		//assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
//		//blockRepository.moveBlock("1", "3", ConnectionType.UP);
//		//assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
//		//assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);
//
//		// TESTS CONNECTIONTYPE.DOWN - BODY
//		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment
//
//		assertEquals( movedActionBlock , connectedMoveForwardBlockA.getNextBlock() );// Initial state
//		blockRepository.moveBlock("1",  "4", ConnectionType.BODY);
//		//assertEquals( null , connectedMoveForwardBlockA.getNextBlock());
//		assertEquals( movedActionBlock, connectedIfBlockA.getFirstBlockOfBody());
//		//assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);
//
//		//TESTS CONNECTIONTYPE.DOWN - CONDITION ( SEE NEGATIVE TEST CASES)
//	}
//
//	// TESTS CONNECTIONTYPE.UP
//	@Test
//	public void testBRMoveBlockPositiveWithUP() {
//		// We here imagine we want to move the headBlock of a chain of blocks.
//		// By design we have chosen that if a HeadBlock is moved all blocks below will
//		// move with it.
//		// With this test we'll check that all blocks are still in the chain after
//		// moving the HeadBlock.
//
//		// Initialize Situation
//		movedActionBlock.setNextBlock(connectedMoveForwardBlockA);
//		connectedMoveForwardBlockA.setNextBlock(connectedMoveForwardBlockB);
//		connectedMoveForwardBlockB.setNextBlock(connectedIfBlockA);
//		connectedIfBlockA.setFirstBlockOfBody(connectedIfBlockB);
//
//		blockRepository.moveBlock("1",  null, ConnectionType.NOCONNECTION);
//	}
//
//	@Test
//	public void testBRMoveBlockPositiveWithBODY() {
//		when(blockRepository.getBlockByID("5")).thenReturn(connectedIfBlockB);
//
//		//TESTS CONNECTIONTYPE.BODY - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)
//
//		//TESTS CONNECTIONTYPE.BODY - DOWN
//		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment
//
//		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
//		blockRepository.moveBlock("1", "2", ConnectionType.DOWN);
//		//assertEquals(null , connectedIfBlockA.getFirstBlockOfBody());
//		assertEquals( movedActionBlock , connectedMoveForwardBlockA.getNextBlock());
//
//		//TESTS CONNECTIONTYPE.BODY - UP
//		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment
//
////		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
////		blockRepository.moveBlock("1", "4", ConnectionType.BODY, "2", ConnectionType.UP);
////		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), null);
////		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockA);
//
//		//TESTS CONNECTIONTYPE.BODY - BODY
//		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment
//
//		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
//		blockRepository.moveBlock("1", "5", ConnectionType.BODY);
//		//assertEquals( null , connectedIfBlockA.getFirstBlockOfBody());
//		assertEquals(movedActionBlock , connectedIfBlockB.getFirstBlockOfBody());
//
//		//TESTS CONNECTIONTYPE.BODY - CONDITION ( SEE NEGATIVE TEST CASES)
//	}
//
//	@Test
//	public void testBRMoveBlockPositiveWithCONDITION() {
//		//Initialize environment
//		//when(blockRepository.getBlockByID("4")).thenReturn(connectedIfBlockA);
//		when(blockRepository.getBlockByID("5")).thenReturn(connectedIfBlockB);
//		when(blockRepository.getBlockByID("8")).thenReturn(connectedWhileBlockA);
//		when(blockRepository.getBlockByID("13")).thenReturn(movedWallInFrontBlock);
//		
//		
//		
//		
//		//TESTS CONNECTIONTYPE.CONDITION - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)
//		
//		//TEST CONENCTIONTYPE.CONDITION - CONDITION TO IFBLOCK
//		connectedIfBlockA.setConditionBlock(movedWallInFrontBlock);//Initial State
//		assertEquals(connectedIfBlockA.getConditionBlock(), movedWallInFrontBlock);
//		blockRepository.moveBlock("13",  "5", ConnectionType.CONDITION);
//		//assertEquals(null , connectedIfBlockA.getConditionBlock() );
//		assertEquals(movedWallInFrontBlock, connectedIfBlockB.getConditionBlock());
//		
//		
//		//TEST CONENCTIONTYPE.CONDITION - CONDITION TO WHILEBLOCK
//		connectedIfBlockA.setConditionBlock(movedWallInFrontBlock);//Initial State
//		assertEquals(movedWallInFrontBlock , connectedIfBlockA.getConditionBlock());
//		blockRepository.moveBlock("13",  "8", ConnectionType.CONDITION);
//		//assertEquals(null , connectedIfBlockA.getConditionBlock());
//		assertEquals(movedWallInFrontBlock, connectedWhileBlockA.getConditionBlock());
//	}
//
//	@Test
//	public void testMoveBlockWithValidReprositoryBeforeMove() {
//	}
//
//	
//	public void testMoveAssesmentBlockWitchValidConnectionOperand() {
//		
//	}
//	@Test
//	public void testMoveBlockWithChainOfBlock() {
//
//		// Test with a nextBlock for the MovedActionBlock
//		movedActionBlock.setNextBlock(connectedIfBlockB);// Initialise test environment
//		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment
//
////		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
////		assertEquals(movedActionBlock.getNextBlock(), connectedIfBlockB);// Initial state
////		blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.UP);
//		//Cannot be tested due to HeadBlocks
//
//		//assertEquals(null,connectedMoveForwardBlockA.getNextBlock());
//		//assertEquals(connectedIfBlockB, movedActionBlock.getNextBlock());
//		//assertEquals(connectedMoveForwardBlockB , connectedIfBlockB.getNextBlock());
//	}
//	
//
//	// NEGATIVE TESTS
//	
//	public void testMoveBlocInvalidConnection() {
//		
//	}
//	
//	
//}
