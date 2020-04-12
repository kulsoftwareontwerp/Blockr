package domainLayer.blocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import applicationLayer.BlockController;
import applicationLayer.DomainController;
import applicationLayer.ElementController;
import applicationLayer.GameController;
import domainLayer.elements.ElementRepository;
import events.BlockChangeEvent;
import events.DomainListener;
import events.GUIListener;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockType;
import types.ConnectionType;

@RunWith(MockitoJUnitRunner.class)
public class MoveBlockBRTest {

	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
	private ArrayList<BlockType> executableBlockTypes = new ArrayList<BlockType>();
	private ArrayList<BlockType> assessableBlockTypes = new ArrayList<BlockType>();
	private Set<String> blockIdsInRepository = new HashSet<String>();

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	@Mock
	private HashMap<String, Block> mockAllBlocks;
	@Mock
	private HashSet<Block> mockHeadBlocks;
	@Spy
	@InjectMocks
	private BlockRepository blockRepository;

	@Spy
	private BlockRepository blockRepositoryValidBeforeMove;

	private ActionBlock movedActionBlock;
	private MoveForwardBlock movedMoveForwardBlock;
	private WallInFrontBlock movedWallInFrontBlock;
	private NotBlock movedNotBlock;
	private WhileBlock movedWhileBlock;
	private IfBlock movedIfBlock;

	private IfBlock connectedIfBlockA;
	private IfBlock connectedIfBlockB;

	private NotBlock connectedNotBlockA;
	private NotBlock connectedNotBlockB;

	private MoveForwardBlock connectedMoveForwardBlockA;
	private MoveForwardBlock connectedMoveForwardBlockB;
	private MoveForwardBlock connectedMoveForwardBlockC;

	private TurnLeftBlock connectedTurnLeftBlockA;
	private TurnLeftBlock connectedTurnLeftBlockkB;
	private TurnLeftBlock movedTurnLeftBlock;

	private TurnRightBlock TurnRightBlocA;
	private TurnRightBlock TurnRightBlocB;
	private TurnRightBlock movedTurnRightBloc;

	private WallInFrontBlock WallInFrontBlockA;
	private WallInFrontBlock WallInFrontBlockB;

	private WhileBlock connectedWhileBlockA;
	private WhileBlock connectedWhileBlockB;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		/*
		 * executableBlockTypes.add(BlockType.MoveForward);
		 * executableBlockTypes.add(BlockType.TurnLeft);
		 * executableBlockTypes.add(BlockType.TurnRight);
		 * executableBlockTypes.add(BlockType.While);
		 * executableBlockTypes.add(BlockType.If);
		 * 
		 * assessableBlockTypes.add(BlockType.Not);
		 * assessableBlockTypes.add(BlockType.WallInFront);
		 */

		connectionTypes.add(ConnectionType.BODY);
		connectionTypes.add(ConnectionType.CONDITION);
		connectionTypes.add(ConnectionType.LEFT);
		connectionTypes.add(ConnectionType.DOWN);
		connectionTypes.add(ConnectionType.NOCONNECTION);
		connectionTypes.add(ConnectionType.OPERAND);
		connectionTypes.add(ConnectionType.UP);

		// Mulptiple head block reprository
		movedActionBlock = spy(new MoveForwardBlock("1"));
		connectedMoveForwardBlockA = spy(new MoveForwardBlock("2"));
		connectedMoveForwardBlockB = spy(new MoveForwardBlock("3"));
		connectedMoveForwardBlockC = spy(new MoveForwardBlock("16"));

		connectedIfBlockA = spy(new IfBlock("4"));
		connectedIfBlockB = spy(new IfBlock("5"));
		movedIfBlock = spy(new IfBlock("6"));

		connectedWhileBlockA = spy(new WhileBlock("7"));
		connectedWhileBlockB = spy(new WhileBlock("8"));
		movedWhileBlock = spy(new WhileBlock("9"));

		WallInFrontBlockA = spy(new WallInFrontBlock("10"));
		WallInFrontBlockB = spy(new WallInFrontBlock("11"));
		movedWallInFrontBlock = spy(new WallInFrontBlock("12"));

		movedNotBlock = spy(new NotBlock("13"));
		connectedNotBlockA = spy(new NotBlock("14"));
		connectedNotBlockB = spy(new NotBlock("15"));

		when(blockRepository.getBlockByID("1")).thenReturn(movedActionBlock);
		when(blockRepository.getBlockByID("2")).thenReturn(connectedMoveForwardBlockA);
		when(blockRepository.getBlockByID("3")).thenReturn(connectedMoveForwardBlockB);
		when(blockRepository.getBlockByID("16")).thenReturn(connectedMoveForwardBlockC);

		when(blockRepository.getBlockByID("4")).thenReturn(connectedIfBlockA);
		when(blockRepository.getBlockByID("5")).thenReturn(connectedIfBlockB);
		when(blockRepository.getBlockByID("6")).thenReturn(movedIfBlock);

		when(blockRepository.getBlockByID("7")).thenReturn(connectedWhileBlockA);
		when(blockRepository.getBlockByID("8")).thenReturn(connectedWhileBlockB);
		when(blockRepository.getBlockByID("9")).thenReturn(movedWhileBlock);

		when(blockRepository.getBlockByID("10")).thenReturn(WallInFrontBlockA);
		when(blockRepository.getBlockByID("11")).thenReturn(WallInFrontBlockB);
		when(blockRepository.getBlockByID("12")).thenReturn(movedWallInFrontBlock);

		when(blockRepository.getBlockByID("13")).thenReturn(movedNotBlock);
		when(blockRepository.getBlockByID("14")).thenReturn(connectedNotBlockA);
		when(blockRepository.getBlockByID("15")).thenReturn(connectedNotBlockB);

	}

	@After
	public void tearDown() throws Exception {
		movedActionBlock.setNextBlock(null);
		connectedMoveForwardBlockA.setNextBlock(null);
		connectedMoveForwardBlockB.setNextBlock(null);
		connectedMoveForwardBlockC.setNextBlock(null);

		connectedIfBlockA.setNextBlock(null);
		connectedIfBlockA.setFirstBlockOfBody(null);
		connectedIfBlockA.setConditionBlock(null);

		connectedIfBlockB.setNextBlock(null);
		connectedIfBlockB.setFirstBlockOfBody(null);
		connectedIfBlockB.setConditionBlock(null);

		movedIfBlock.setNextBlock(null);
		movedIfBlock.setFirstBlockOfBody(null);
		movedIfBlock.setConditionBlock(null);

		connectedWhileBlockA.setNextBlock(null);
		connectedWhileBlockA.setFirstBlockOfBody(null);
		connectedWhileBlockA.setConditionBlock(null);

		connectedWhileBlockB.setNextBlock(null);
		connectedWhileBlockA.setFirstBlockOfBody(null);
		connectedWhileBlockA.setConditionBlock(null);

		movedWhileBlock.setNextBlock(null);
		connectedWhileBlockA.setFirstBlockOfBody(null);
		connectedWhileBlockA.setConditionBlock(null);

		movedNotBlock.setOperand(null);
		connectedNotBlockA.setOperand(null);
		connectedNotBlockB.setOperand(null);

	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#moveBlock(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */

	// BASIC CASES
	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoDOWN() {
		assertEquals("1", blockRepository.moveBlock("1", "1","2", ConnectionType.DOWN));
		verify(connectedMoveForwardBlockA).setNextBlock(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoUP() {
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);
		assertEquals("1", blockRepository.moveBlock("1","1", "2", ConnectionType.UP));
		verify(movedActionBlock).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockA);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoBODY() {
		assertEquals("1", blockRepository.moveBlock("1","1", "4", ConnectionType.BODY));
		verify(connectedIfBlockA).setFirstBlockOfBody(movedActionBlock);
		verify(mockHeadBlocks).remove(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoCONDITION() {
		assertEquals(movedNotBlock.getBlockId(),
				blockRepository.moveBlock(movedNotBlock.getBlockId(),movedNotBlock.getBlockId(), "4", ConnectionType.CONDITION));
		verify(connectedIfBlockA).setConditionBlock(movedNotBlock);
		verify(mockHeadBlocks).remove(movedNotBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoLEFT() {
		when(mockHeadBlocks.contains(connectedNotBlockA)).thenReturn(true);

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),movedNotBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), ConnectionType.LEFT));
		verify(movedNotBlock, times(2)).setOperand(connectedNotBlockA);
		verify(mockHeadBlocks).remove(connectedNotBlockA);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoOPERAND() {
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),movedNotBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), ConnectionType.OPERAND));
		verify(connectedNotBlockA).setOperand(movedNotBlock);
		verify(mockHeadBlocks).remove(movedNotBlock);

	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoDOWN() {
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals("1")) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals("1", blockRepository.moveBlock("1","1", "3", ConnectionType.DOWN));
		verify(connectedMoveForwardBlockA).setNextBlock(null);
		verify(connectedMoveForwardBlockB).setNextBlock(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoUP() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");

		// headBlocks opstellen
		when(mockHeadBlocks.contains(connectedMoveForwardBlockB)).thenReturn(true);

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals("1")) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals("1", blockRepository.moveBlock("1","1", "3", ConnectionType.UP));
		verify(connectedMoveForwardBlockA).setNextBlock(null);
		verify(movedActionBlock).setNextBlock(connectedMoveForwardBlockB);
		verify(mockHeadBlocks).add(movedActionBlock);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockB);

	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoBODY() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals("1")) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals("1", blockRepository.moveBlock("1","1", "4", ConnectionType.BODY));
		verify(connectedMoveForwardBlockA).setNextBlock(null);
		verify(connectedIfBlockA).setFirstBlockOfBody(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveCONDITIONtoCONDITION() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(connectedIfBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedWallInFrontBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),movedWallInFrontBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.CONDITION));
		verify(connectedIfBlockA).setConditionBlock(null);
		verify(connectedIfBlockB).setConditionBlock(movedWallInFrontBlock);

	}

	@Test
	public void testBRMoveBlockPositiveCONDITIONtoOPERAND() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(connectedIfBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedWallInFrontBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),movedWallInFrontBlock.getBlockId(),
				movedNotBlock.getBlockId(), ConnectionType.OPERAND));
		verify(connectedIfBlockA).setConditionBlock(null);
		verify(movedNotBlock).setOperand(movedWallInFrontBlock);

	}

	@Test
	public void testBRMoveBlockPositiveCONDITIONtoLEFT() {

		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(connectedIfBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),movedNotBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedIfBlockA).setConditionBlock(null);
		verify(movedNotBlock).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(mockHeadBlocks).remove(movedWallInFrontBlock);
	}

	@Test
	public void testBRMoveBlockPositiveOPERANDtoCONDITION() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("OPERAND");
		parentInfo.add(movedNotBlock.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedWallInFrontBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),movedWallInFrontBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.CONDITION));
		verify(movedNotBlock).setOperand(null);
		verify(connectedIfBlockA).setConditionBlock(movedWallInFrontBlock);
	}
	
	@Test
	public void testBRMoveBlockPositiveOPERANDtoOPERAND() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("OPERAND");
		parentInfo.add(connectedNotBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedWallInFrontBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),movedWallInFrontBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), ConnectionType.OPERAND));
		verify(connectedNotBlockA).setOperand(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);
	}

	@Test
	public void testBRMoveBlockPositiveOPERANDtoLEFT() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("OPERAND");
		parentInfo.add(connectedNotBlockA.getBlockId());
		
		when(mockHeadBlocks.contains(connectedNotBlockB)).thenReturn(true);

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA).setOperand(null);
		verify(movedNotBlock).setOperand(connectedNotBlockB);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(mockHeadBlocks).remove(connectedNotBlockB);
	}
	
	@Test
	public void testBRMoveBlockPositiveBODYtoDOWN() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("BODY");
		parentInfo.add(connectedIfBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedActionBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),movedActionBlock.getBlockId(),
				connectedMoveForwardBlockA.getBlockId(), ConnectionType.DOWN));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(connectedMoveForwardBlockA).setNextBlock(movedActionBlock);
	}
	
	
	@Test
	public void testBRMoveBlockPositiveBODYtoUP() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("BODY");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedActionBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),movedActionBlock.getBlockId(),
				connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(movedActionBlock).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).add(movedActionBlock);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockA);
	}
	
	@Test
	public void testBRMoveBlockPositiveBODYtoBODY() {

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("BODY");
		parentInfo.add(connectedIfBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedActionBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),movedActionBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.BODY));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(connectedIfBlockB).setFirstBlockOfBody(movedActionBlock);
	}


	
	
	//MORE COMPLEX TESTS
	//CHAIN OF BLOCKS
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoUP() {
		
		//initialisation of situation
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);
		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockB);
		
		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockB).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockA);

	}
	
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock() {
		
		//initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		when(movedIfBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		
		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),connectedNotBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).remove(movedWallInFrontBlock);
		

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTOperandBlock() {
		
		//initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);
		
		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).remove(movedWallInFrontBlock);
	}
	
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksDOWNtoUP() {
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add(connectedMoveForwardBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedActionBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(mockHeadBlocks.contains(connectedMoveForwardBlockC)).thenReturn(true);
		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockB);

		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),connectedMoveForwardBlockB.getBlockId(),connectedMoveForwardBlockC.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockA).setNextBlock(null);
		verify(connectedMoveForwardBlockB).setNextBlock(connectedMoveForwardBlockC);

	}
	
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksCONDITIONtoLEFT() {
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(movedIfBlock.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),connectedNotBlockB.getBlockId(),movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(movedIfBlock).setConditionBlock(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);

	}
	
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksOPERANDtoLEFT() {
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo13 = new ArrayList<String>();
		parentInfo13.add("OPERAND");
		parentInfo13.add(movedNotBlock.getBlockId());
		
		ArrayList<String> parentInfo14 = new ArrayList<String>();
		parentInfo14.add("OPERAND");
		parentInfo14.add(connectedNotBlockA.getBlockId());

		// Opstellen van program
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(connectedNotBlockB.getBlockId())) {
						return parentInfo13;
					}
					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo14;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		when(connectedNotBlockA.getOperand()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockB);
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		//effective moved block determined by getBlockIdToPerformMoveOn
		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),connectedNotBlockB.getBlockId(),movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA).setOperand(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);

	}
	
	
	//NEGATIVE TESTS
	@Test
	public void testBRMoveBlockNegativeConnectionNotPossible() {
		String excMessage = "This socket is not free";

		// Opstellen van program
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);
		movedActionBlock.setNextBlock(connectedIfBlockA);
		connectedIfBlockA.setConditionBlock(movedNotBlock);
		movedNotBlock.setOperand(movedWallInFrontBlock);
		movedNotBlock.setOperand(movedWallInFrontBlock);
		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);

		// opstellen allBlocks hashMap
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(movedActionBlock.getBlockId(), movedActionBlock);
		allBlocks.put(connectedMoveForwardBlockA.getBlockId(), connectedMoveForwardBlockA);
		allBlocks.put(connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockB);
		allBlocks.put(movedWhileBlock.getBlockId(), movedWhileBlock);
		allBlocks.put(movedNotBlock.getBlockId(), movedNotBlock);
		allBlocks.put(movedNotBlock.getBlockId(), movedNotBlock);
		allBlocks.put(connectedNotBlockA.getBlockId(), connectedNotBlockA);
		allBlocks.put(connectedNotBlockB.getBlockId(), connectedNotBlockB);

		for (ConnectionType connectionType : connectionTypes) {
			assertExceptionBRMoveBlock("1", "2", ConnectionType.DOWN, excMessage);
		}

	}
	
	@Ignore
	public void assertExceptionBRMoveBlock(String movedBlockId, String connectedAfterMoveBlockId,
			ConnectionType connectionAfterMove, String excMessage) {
		boolean pass = false;

		try {
			blockRepository.moveBlock(movedBlockId,movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);
		} catch (NoSuchConnectedBlockException e) {
			pass = e.getMessage().equals(excMessage);
		} catch (InvalidBlockConnectionException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("moveBlock failed in the domainController for combination=" + movedBlockId
				+ connectedAfterMoveBlockId + " " + connectionAfterMove, pass);
	}

	@Test
	public void testBRMoveBlockNegativeBlocksNotInDomain() {// Specifieker volgens case, forloop dus niet gebruiken

		when(blockRepository.getBlockByID("1")).thenReturn(movedActionBlock);

		ArrayList<ConnectionType> connectionTypesA;
		ArrayList<ConnectionType> connectionTypesB;

		String excMessage = "The requested block doens't exist in the domain";

		// Test for movedBlock == null
		for (ConnectionType connectionTypeB : connectionTypes) {
			assertExceptionBRMoveBlock("40", "", connectionTypeB, excMessage);
		}

		connectionTypesA = new ArrayList<ConnectionType>();
		connectionTypesB = new ArrayList<ConnectionType>();

		connectionTypesB.add(ConnectionType.BODY);
		connectionTypesB.add(ConnectionType.CONDITION);
		connectionTypesB.add(ConnectionType.LEFT);
		connectionTypesB.add(ConnectionType.DOWN);
		connectionTypesB.add(ConnectionType.NOCONNECTION);
		connectionTypesB.add(ConnectionType.OPERAND);
		connectionTypesB.add(ConnectionType.UP);
		// test of move idd niets doet
		for (ConnectionType connectionTypeB : connectionTypes) {
			assertExceptionBRMoveBlock("1", "", connectionTypeB, excMessage);
		}
	}
	
	
	
	//TEST USED METHODS FOR MOVE

	@Test
	public void testBRConnectionBlockBFMPositive() {

		movedMoveForwardBlock = Mockito.spy(new MoveForwardBlock("17"));
		ActionBlock movedMoveForwardBlockB = Mockito.spy(new MoveForwardBlock("18"));
		HashMap<String, Block> allBlocks = new HashMap<String, Block>();
		allBlocks.put(movedActionBlock.getBlockId(), movedActionBlock);
		allBlocks.put(connectedMoveForwardBlockA.getBlockId(), connectedMoveForwardBlockA);
		allBlocks.put(connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockB);
		allBlocks.put(movedWhileBlock.getBlockId(), movedWhileBlock);
		allBlocks.put(movedNotBlock.getBlockId(), movedNotBlock);
		allBlocks.put(movedNotBlock.getBlockId(), movedNotBlock);
		allBlocks.put(connectedNotBlockA.getBlockId(), connectedNotBlockA);
		allBlocks.put(connectedNotBlockB.getBlockId(), connectedNotBlockB);
		allBlocks.put(movedMoveForwardBlock.getBlockId(), movedMoveForwardBlock);

		Set itBlocks = allBlocks.entrySet();

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("1");

		ArrayList<String> parentInfo2 = new ArrayList<String>();
		parentInfo2.add("CONDITION");
		parentInfo2.add("9");

		ArrayList<String> parentInfo3 = new ArrayList<String>();
		parentInfo3.add("OPERAND");
		parentInfo3.add("13");

		ArrayList<String> parentInfo4 = new ArrayList<String>();
		parentInfo4.add("OPERAND");
		parentInfo4.add("14");

		ArrayList<String> parentInfo5 = new ArrayList<String>();
		parentInfo5.add("BODY");
		parentInfo5.add("9");

		ArrayList<String> parentInfo6 = new ArrayList<String>();
		parentInfo6.add("DOWN");
		parentInfo6.add("9");

		when(mockAllBlocks.entrySet()).thenReturn(itBlocks);

		when(blockRepository.getBlockByID("17")).thenReturn(movedMoveForwardBlock);
		when(blockRepository.getBlockByID("18")).thenReturn(movedMoveForwardBlockB);

		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockA);
		when(connectedMoveForwardBlockA.getNextBlock()).thenReturn(connectedMoveForwardBlockB);

		when(movedWhileBlock.getFirstBlockOfBody()).thenReturn(movedMoveForwardBlock);
		when(movedWhileBlock.getNextBlock()).thenReturn(movedMoveForwardBlockB);

		when(movedWhileBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);

		assertEquals(parentInfo, blockRepository.getConnectedParentIfExists(connectedMoveForwardBlockA.getBlockId()));
		assertEquals(parentInfo2, blockRepository.getConnectedParentIfExists(movedNotBlock.getBlockId()));
		assertEquals(parentInfo3, blockRepository.getConnectedParentIfExists(connectedNotBlockA.getBlockId()));
		assertEquals(parentInfo4, blockRepository.getConnectedParentIfExists(connectedNotBlockB.getBlockId()));
		assertEquals(parentInfo5, blockRepository.getConnectedParentIfExists(movedMoveForwardBlock.getBlockId()));
		assertEquals(parentInfo6, blockRepository.getConnectedParentIfExists(movedMoveForwardBlockB.getBlockId()));

	}

	
	@Test
	public void testBRBlockIdToPerformOnPositive() {

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("1");

		ArrayList<String> parentInfo2 = new ArrayList<String>();
		parentInfo2.add("CONDITION");
		parentInfo2.add("10");

		ArrayList<String> parentInfo3 = new ArrayList<String>();
		parentInfo3.add("OPERAND");
		parentInfo3.add("14");

		ArrayList<String> parentInfo4 = new ArrayList<String>();
		parentInfo4.add("OPERAND");
		parentInfo4.add("15");
		ArrayList<String> parentInfo5 = new ArrayList<String>();
		parentInfo5.add("NOCONNECTION");
		parentInfo5.add("");

		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals("2")) {
						return parentInfo;
					}
					if (id.equals("14")) {
						return parentInfo2;
					}
					if (id.equals("15")) {
						return parentInfo3;
					}
					if (id.equals("16")) {
						return parentInfo4;
					}

					if (id.equals("")) {
						return parentInfo5;
					}
				}

				return parentInfo5;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());

		for (ConnectionType connectionType : connectionTypes) {

			assertEquals("1", blockRepository.getBlockIdToPerformMoveOn("1", "", connectionType));
			assertEquals("10", blockRepository.getBlockIdToPerformMoveOn("10", "", connectionType));
			assertEquals("15", blockRepository.getBlockIdToPerformMoveOn("15", "", connectionType));

		}

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.getBlockIdToPerformMoveOn(
				movedNotBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.LEFT));

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.getBlockIdToPerformMoveOn(
				movedWhileBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.LEFT));
		assertEquals(movedWhileBlock.getBlockId(), blockRepository.getBlockIdToPerformMoveOn(
				movedWhileBlock.getBlockId(), movedWhileBlock.getBlockId(), ConnectionType.LEFT));

	}

	

}
