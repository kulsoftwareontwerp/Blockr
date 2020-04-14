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

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

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
	private ActionBlock movedMoveForwardBlock;
	private ConditionBlock movedWallInFrontBlock;
	private NotBlock movedNotBlock;
	private WhileBlock movedWhileBlock;
	private IfBlock movedIfBlock;

	private IfBlock connectedIfBlockA;
	private IfBlock connectedIfBlockB;

	private NotBlock connectedNotBlockA;
	private NotBlock connectedNotBlockB;

	private ActionBlock connectedMoveForwardBlockA;
	private ActionBlock connectedMoveForwardBlockB;
	private ActionBlock connectedMoveForwardBlockC;

	private ActionBlock connectedTurnLeftBlockA;
	private ActionBlock connectedTurnLeftBlockkB;
	private ActionBlock movedTurnLeftBlock;

	private ActionBlock TurnRightBlocA;
	private ActionBlock TurnRightBlocB;
	private ActionBlock movedTurnRightBloc;

	private ConditionBlock WallInFrontBlockA;
	private ConditionBlock WallInFrontBlockB;

	private WhileBlock connectedWhileBlockA;
	private WhileBlock connectedWhileBlockB;

	private Action action;
	private Predicate predicate;

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
		movedActionBlock = spy(new ActionBlock("1", action));
		connectedMoveForwardBlockA = spy(new ActionBlock("2", action));
		connectedMoveForwardBlockB = spy(new ActionBlock("3", action));
		connectedMoveForwardBlockC = spy(new ActionBlock("16", action));

		connectedIfBlockA = spy(new IfBlock("4"));
		connectedIfBlockB = spy(new IfBlock("5"));
		movedIfBlock = spy(new IfBlock("6"));

		connectedWhileBlockA = spy(new WhileBlock("7"));
		connectedWhileBlockB = spy(new WhileBlock("8"));
		movedWhileBlock = spy(new WhileBlock("9"));

		WallInFrontBlockA = spy(new ConditionBlock("10", predicate));
		WallInFrontBlockB = spy(new ConditionBlock("11", predicate));
		movedWallInFrontBlock = spy(new ConditionBlock("12", predicate));
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
		assertEquals("1", blockRepository.moveBlock("1", "1", "2", ConnectionType.DOWN));
		verify(connectedMoveForwardBlockA).setNextBlock(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoUP() {
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);
		assertEquals("1", blockRepository.moveBlock("1", "1", "2", ConnectionType.UP));
		verify(movedActionBlock).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockA);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoBODY() {
		assertEquals("1", blockRepository.moveBlock("1", "1", "4", ConnectionType.BODY));
		verify(connectedIfBlockA).setFirstBlockOfBody(movedActionBlock);
		verify(mockHeadBlocks).remove(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoCONDITION() {
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), "4", ConnectionType.CONDITION));
		verify(connectedIfBlockA).setConditionBlock(movedNotBlock);
		verify(mockHeadBlocks).remove(movedNotBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoLEFT() {
		when(mockHeadBlocks.contains(connectedNotBlockA)).thenReturn(true);

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.LEFT));
		verify(movedNotBlock, times(2)).setOperand(connectedNotBlockA);
		verify(mockHeadBlocks).remove(connectedNotBlockA);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoOPERAND() {
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.OPERAND));
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

		assertEquals("1", blockRepository.moveBlock("1", "1", "3", ConnectionType.DOWN));
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

		assertEquals("1", blockRepository.moveBlock("1", "1", "3", ConnectionType.UP));
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

		assertEquals("1", blockRepository.moveBlock("1", "1", "4", ConnectionType.BODY));
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

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), connectedIfBlockB.getBlockId(), ConnectionType.CONDITION));
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

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), movedNotBlock.getBlockId(), ConnectionType.OPERAND));
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

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
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

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.CONDITION));
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

		assertEquals(movedWallInFrontBlock.getBlockId(), blockRepository.moveBlock(movedWallInFrontBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.OPERAND));
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

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA).setOperand(null);
		verify(movedNotBlock).setOperand(connectedNotBlockB);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(mockHeadBlocks).remove(connectedNotBlockB);
	}

	@Test
	public void testBRMoveBlockPositiveBODYtoLEFT() {

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

					if (id.equals(movedIfBlock.getBlockId())) {
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

					if (id.equals(movedIfBlock.getBlockId())) {
						return parentInfo;
					}

					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		
		assertEquals(movedIfBlock.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				movedIfBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(movedIfBlock).setConditionBlock(movedWallInFrontBlock);
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

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.DOWN));
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

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
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

		assertEquals(movedActionBlock.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId(), ConnectionType.BODY));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(connectedIfBlockB).setFirstBlockOfBody(movedActionBlock);
	}

	// MORE COMPLEX TESTS
	// CHAIN OF BLOCKS
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoUP() {

		// initialisation of situation
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);

		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockB).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).remove(connectedMoveForwardBlockA);

	}

	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA, times(2)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).remove(movedWallInFrontBlock);

	}

	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTOperandBlock() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockB, times(2)).setOperand(movedWallInFrontBlock);
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

		}).when(blockRepository).getConnectedParentIfExists(any());

		when(mockHeadBlocks.contains(connectedMoveForwardBlockC)).thenReturn(true);

		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockC.getBlockId(), ConnectionType.UP));
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

		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(movedIfBlock).setConditionBlock(null);
		verify(connectedNotBlockB, times(2)).setOperand(movedWallInFrontBlock);

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

		}).when(blockRepository).getConnectedParentIfExists(any());

		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockB);
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		// effective moved block determined by getBlockIdToPerformMoveOn
		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA).setOperand(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);

	}
	
	
	//COMPLEX SITUATIONS
	@Test
	public void testBRMoveBlockPositiveComplexIfIfConnectToCondition() {

		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		// effective moved block determined by getBlockIdToPerformMoveOn
		assertEquals(connectedIfBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedIfBlockA,times(2)).setConditionBlock(movedWallInFrontBlock);

	}
	
	
	@Test
	public void testBRMoveBlockPositiveComplexIfInIfConnectToCondition() {

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

		}).when(blockRepository).getConnectedParentIfExists(any());
		
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		
		
		// effective moved block determined by getBlockIdToPerformMoveOn
		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(connectedIfBlockA.getBlockId(),
				connectedNotBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA,times(2)).setOperand(movedWallInFrontBlock);

	}
	

	// NEGATIVE TESTS
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
			blockRepository.moveBlock(movedBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);
		} catch (NoSuchConnectedBlockException e) {
			pass = e.getMessage().equals(excMessage);
		} catch (InvalidBlockConnectionException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("moveBlock failed in the blockRepository for combination=" + movedBlockId+" "
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
	@Test
	public void testBRSocketNotFreeNOCONNECTION() {
		String excMessage = "This socket is not free";
		
		
		when(connectedIfBlockA.getNextBlock()).thenReturn(connectedIfBlockB);
		when(connectedIfBlockA.getFirstBlockOfBody()).thenReturn(movedIfBlock);
		when(connectedIfBlockA.getConditionBlock()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.BODY, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.OPERAND, excMessage);
		
		//connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);
		//NotBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.LEFT, excMessage);
	}
	
	@Test
	public void testBRSocketNotFreeDOWN() {
		String excMessage = "This socket is not free";
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
		ArrayList<String> parentInfo2 = new ArrayList<String>();
		parentInfo2.add("DOWN");
		parentInfo2.add(connectedMoveForwardBlockA.getBlockId());
		

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
						return parentInfo2;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
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
						return parentInfo2;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(),any(),any());
		
		
		
		when(connectedIfBlockA.getNextBlock()).thenReturn(connectedIfBlockB);
		when(connectedIfBlockA.getFirstBlockOfBody()).thenReturn(movedIfBlock);

		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.BODY, excMessage);
		//connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);
		
		
	}
	
	@Test
	public void testBRSocketNotFreeCONDITION() {
		String excMessage = "This socket is not free";
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
		ArrayList<String> parentInfo7 = new ArrayList<String>();
		parentInfo7.add("CONDITION");
		parentInfo7.add(connectedWhileBlockA.getBlockId());
		

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
						return parentInfo7;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
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
						return parentInfo7;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(),any(),any());
		
		
		when(mockHeadBlocks.contains(connectedNotBlockB)).thenReturn(false);
		
		when(connectedIfBlockA.getConditionBlock()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.OPERAND, excMessage);
		//connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.LEFT, excMessage);
		
		
	}
	
	
	@Test
	public void testBRSocketNotFreeOPERAND() {
		String excMessage = "This socket is not free";
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
		ArrayList<String> parentInfo14 = new ArrayList<String>();
		parentInfo14.add("OPERAND");
		parentInfo14.add(connectedNotBlockA.getBlockId());
		

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
						return parentInfo14;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
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
						return parentInfo14;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(),any(),any());
		
		NotBlock notBlockC = spy(new NotBlock("17"));
		when(mockHeadBlocks.contains(connectedNotBlockB)).thenReturn(false);
		when(blockRepository.getBlockByID("17")).thenReturn(notBlockC);
		
		when(connectedIfBlockA.getConditionBlock()).thenReturn(notBlockC);
		when(notBlockC.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), notBlockC.getBlockId(), ConnectionType.OPERAND, excMessage);
		//connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), connectedNotBlockB.getBlockId(), ConnectionType.LEFT, excMessage);
		
		
	}
	
	
	
	@Test
	public void testBRSocketNotFreeBODY() {
		String excMessage = "This socket is not free";
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
		ArrayList<String> parentInfo4 = new ArrayList<String>();
		parentInfo4.add("BODY");
		parentInfo4.add(connectedIfBlockA.getBlockId());
		

		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedIfBlock.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedIfBlock.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(),any(),any());
		

		when(connectedIfBlockB.getFirstBlockOfBody()).thenReturn(movedActionBlock);
		when(connectedIfBlockB.getNextBlock()).thenReturn(movedWhileBlock);
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(false);

		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(),connectedIfBlockB.getBlockId(), ConnectionType.BODY, excMessage);
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(),connectedIfBlockB.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(),connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);
		//connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT, excMessage);
		
		
	}
	

	// TEST USED METHODS FOR MOVE

	@Test
	public void testBRParentIfExtistsPositive() {

		movedMoveForwardBlock = Mockito.spy(new ActionBlock("17", action));
		ActionBlock movedMoveForwardBlockB = Mockito.spy(new ActionBlock("18", action));
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
	
	@Test
	public void testBRConnectedBRM() {
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
		
		ArrayList<String> parentInfo4 = new ArrayList<String>();
		parentInfo4.add("BODY");
		parentInfo4.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfo41 = new ArrayList<String>();
		parentInfo41.add("DOWN");
		parentInfo41.add(connectedIfBlockA.getBlockId());
		
		
		ArrayList<String> parentInfo42 = new ArrayList<String>();
		parentInfo42.add("CONDITION");
		parentInfo42.add(connectedIfBlockA.getBlockId());
		
		
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("OPERAND");
		parentInfo.add(connectedNotBlockA.getBlockId());
		
		
		doAnswer(new Answer<ArrayList<String>>() {
			@Override
			public ArrayList<String> answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments().length != 0) {
					Object obj = invocation.getArgument(0);
					String id = "";
					if (obj != null) {
						id = (String) obj;
					}

					if (id.equals(movedIfBlock.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals(connectedIfBlockB.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo42;
					}
					if (id.equals(connectedNotBlockB.getBlockId())) {
						return parentInfo;
					}
					
					if (id.equals("")) {
						return parentInfoDefault;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
		
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(movedIfBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(connectedIfBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(movedNotBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		
	}

}