package domainLayer.blocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockType;
import types.ConnectionType;

@RunWith(MockitoJUnitRunner.class)
public class TestMoveBlockBlockRepository {

	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	@Spy
	private HashMap<String, Block> mockAllBlocks;
	@Spy
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

	private ConditionBlock WallInFrontBlockA;
	private ConditionBlock WallInFrontBlockB;

	private WhileBlock connectedWhileBlockA;
	private WhileBlock connectedWhileBlockB;
	
	private DefinitionBlock definitionBlock;

	private BlockType type;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		connectionTypes.add(ConnectionType.BODY);
		connectionTypes.add(ConnectionType.CONDITION);
		connectionTypes.add(ConnectionType.LEFT);
		connectionTypes.add(ConnectionType.DOWN);
		connectionTypes.add(ConnectionType.NOCONNECTION);
		connectionTypes.add(ConnectionType.OPERAND);
		connectionTypes.add(ConnectionType.UP);

		// Mulptiple head block reprository
		movedActionBlock = spy(new ActionBlock("1", type));
		connectedMoveForwardBlockA = spy(new ActionBlock("2", type));
		connectedMoveForwardBlockB = spy(new ActionBlock("3", type));
		connectedMoveForwardBlockC = spy(new ActionBlock("16", type));

		connectedIfBlockA = spy(new IfBlock("4"));
		connectedIfBlockB = spy(new IfBlock("5"));
		movedIfBlock = spy(new IfBlock("6"));

		connectedWhileBlockA = spy(new WhileBlock("7"));
		connectedWhileBlockB = spy(new WhileBlock("8"));
		movedWhileBlock = spy(new WhileBlock("9"));

		WallInFrontBlockA = spy(new ConditionBlock("10", type));
		WallInFrontBlockB = spy(new ConditionBlock("11", type));
		movedWallInFrontBlock = spy(new ConditionBlock("12", type));

		movedNotBlock = spy(new NotBlock("13"));
		connectedNotBlockA = spy(new NotBlock("14"));
		connectedNotBlockB = spy(new NotBlock("15"));
		
		definitionBlock = spy(new DefinitionBlock("20"));

		mockAllBlocks.put(movedActionBlock.getBlockId(), movedActionBlock);
		mockAllBlocks.put(connectedMoveForwardBlockA.getBlockId(), connectedMoveForwardBlockA);
		mockAllBlocks.put(connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockB);
		mockAllBlocks.put(connectedMoveForwardBlockC.getBlockId(), connectedMoveForwardBlockC);

		mockAllBlocks.put(connectedIfBlockA.getBlockId(), connectedIfBlockA);
		mockAllBlocks.put(connectedIfBlockB.getBlockId(), connectedIfBlockB);
		mockAllBlocks.put(movedIfBlock.getBlockId(), movedIfBlock);

		mockAllBlocks.put(connectedWhileBlockA.getBlockId(), connectedWhileBlockA);
		mockAllBlocks.put(connectedWhileBlockB.getBlockId(), connectedWhileBlockB);
		mockAllBlocks.put(movedWhileBlock.getBlockId(), movedWhileBlock);

		mockAllBlocks.put(WallInFrontBlockA.getBlockId(), WallInFrontBlockA);
		mockAllBlocks.put(WallInFrontBlockB.getBlockId(), WallInFrontBlockB);
		mockAllBlocks.put(movedWallInFrontBlock.getBlockId(), movedWallInFrontBlock);

		mockAllBlocks.put(movedNotBlock.getBlockId(), movedNotBlock);
		mockAllBlocks.put(connectedNotBlockA.getBlockId(), connectedNotBlockA);
		mockAllBlocks.put(connectedNotBlockB.getBlockId(), connectedNotBlockB);

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
		
		mockAllBlocks.clear();
		mockHeadBlocks.clear();

	}

	/**
	 * Test method for
	 * {@link domainLayer.blocks.BlockRepository#moveBlock(java.lang.String, java.lang.String, types.ConnectionType)}.
	 */

	// BASIC CASES
	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoDOWN() {
		mockAllBlocks.put(movedActionBlock.getBlockId(), movedActionBlock);
		mockAllBlocks.put(connectedMoveForwardBlockA.getBlockId(), connectedMoveForwardBlockA);

		assertEquals("1", blockRepository.moveBlock("1", "1", "2", ConnectionType.DOWN));
		verify(connectedMoveForwardBlockA, times(2)).setNextBlock(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoUP() {
		mockAllBlocks.put(movedActionBlock.getBlockId(), movedActionBlock);
		mockAllBlocks.put(connectedMoveForwardBlockA.getBlockId(), connectedMoveForwardBlockA);
		mockHeadBlocks.add(connectedMoveForwardBlockA);

		assertEquals("1", blockRepository.moveBlock("1", "1", "2", ConnectionType.UP));
		verify(movedActionBlock, times(2)).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoBODY() {
		assertEquals("1", blockRepository.moveBlock("1", "1", "4", ConnectionType.BODY));
		verify(connectedIfBlockA, times(2)).setFirstBlockOfBody(movedActionBlock);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoCONDITION() {
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), "4", ConnectionType.CONDITION));
		verify(connectedIfBlockA, times(2)).setConditionBlock(movedNotBlock);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoLEFT() {
		when(mockHeadBlocks.contains(connectedNotBlockA)).thenReturn(true);

		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.LEFT));
		verify(movedNotBlock, times(3)).setOperand(connectedNotBlockA);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveNOCONNECTIONtoOPERAND() {
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), connectedNotBlockA.getBlockId(), ConnectionType.OPERAND));
		verify(connectedNotBlockA, times(2)).setOperand(movedNotBlock);
		verify(mockHeadBlocks).removeIf(any());

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
		verify(connectedMoveForwardBlockB, times(2)).setNextBlock(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoUP() {

		mockHeadBlocks.add(connectedMoveForwardBlockA);
		mockHeadBlocks.add(connectedMoveForwardBlockB);

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
		verify(movedActionBlock, times(2)).setNextBlock(connectedMoveForwardBlockB);
		verify(mockHeadBlocks).add(movedActionBlock);
		verify(mockHeadBlocks).removeIf(any());

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
		verify(connectedIfBlockA, times(2)).setFirstBlockOfBody(movedActionBlock);

	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoLEFT() {//TODO
		mockHeadBlocks.add(connectedIfBlockA);
		mockHeadBlocks.add(WallInFrontBlockA);
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
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
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any(String.class));
		
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
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(String.class),any(String.class),any(ConnectionType.class));

		when(connectedIfBlockA.getNextBlock()).thenReturn(movedIfBlock);
		when(movedIfBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		
		
		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(), WallInFrontBlockA.getBlockId(),
				ConnectionType.LEFT));
		verify(connectedIfBlockA).setNextBlock(null);
		verify(connectedNotBlockA, atLeastOnce()).setOperand(WallInFrontBlockA);

		
		
	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlockDOWNtoLEFT() {
		mockHeadBlocks.add(connectedIfBlockA);
		mockHeadBlocks.add(WallInFrontBlockA);
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
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
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedParentIfExists(any(String.class));
		
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
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(String.class),any(String.class),any(ConnectionType.class));

		when(connectedIfBlockA.getNextBlock()).thenReturn(movedIfBlock);
		when(movedIfBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		
		
		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(), WallInFrontBlockA.getBlockId(),
				ConnectionType.LEFT));
		verify(connectedIfBlockA).setNextBlock(null);
		verify(connectedNotBlockA, atLeastOnce()).setOperand(WallInFrontBlockA);
	}

	@Test
	public void testBRMoveBlockPositiveDOWNtoLEFTWithChain() {//TODO
		mockHeadBlocks.add(connectedIfBlockA);
		mockHeadBlocks.add(WallInFrontBlockA);
		
		when(movedIfBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);

		blockRepository.moveBlock(movedIfBlock.getBlockId(), connectedNotBlockA.getBlockId(), WallInFrontBlockA.getBlockId(),
				ConnectionType.LEFT);

	}

	@Test
	public void testBRMoveBlockPositiveCONDITIONtoCONDITION() {

		mockAllBlocks.put(movedWallInFrontBlock.getBlockId(), movedWallInFrontBlock);
		mockAllBlocks.put(connectedIfBlockB.getBlockId(), connectedIfBlockB);
		mockAllBlocks.put(connectedIfBlockA.getBlockId(), connectedIfBlockA);

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
		verify(connectedIfBlockB, atLeastOnce()).setConditionBlock(movedWallInFrontBlock);

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
		verify(movedNotBlock, times(2)).setOperand(movedWallInFrontBlock);

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
		verify(mockHeadBlocks).removeIf(any());
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
		verify(connectedIfBlockA, times(2)).setConditionBlock(movedWallInFrontBlock);
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
		verify(connectedNotBlockB, times(2)).setOperand(movedWallInFrontBlock);
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
		verify(connectedNotBlockA, times(2)).setOperand(null);
		verify(movedNotBlock).setOperand(connectedNotBlockB);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(mockHeadBlocks).removeIf(any());
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
		verify(connectedMoveForwardBlockA, times(2)).setNextBlock(movedActionBlock);
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
		verify(movedActionBlock, times(2)).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).add(movedActionBlock);
		verify(mockHeadBlocks).removeIf(any());
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
		verify(connectedIfBlockB, times(2)).setFirstBlockOfBody(movedActionBlock);
	}
	
	

	// MORE COMPLEX TESTS
	// CHAIN OF BLOCKS
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoUP() {

		// initialisation of situation
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);

		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockB, times(2)).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).removeIf(any());

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoUP2() {

		// initialisation of situation
		when(mockHeadBlocks.contains(connectedMoveForwardBlockA)).thenReturn(true);

		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockB);
		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedMoveForwardBlockA.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockB, atLeast(1)).setNextBlock(connectedMoveForwardBlockA);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA, times(3)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).removeIf(any());

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock2() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		when(movedIfBlock.getConditionBlock()).thenReturn(connectedNotBlockA);
		assertEquals(connectedNotBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				movedIfBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA, times(3)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).removeIf(any());

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock3() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		when(movedIfBlock.getConditionBlock()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);
		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				movedIfBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockB, atLeast(1)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).removeIf(any());

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTConditionBlock4() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);
		when(connectedNotBlockB.getOperand()).thenReturn(movedNotBlock);
		assertEquals(movedNotBlock.getBlockId(), blockRepository.moveBlock(connectedNotBlockA.getBlockId(),
				connectedNotBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(movedNotBlock, atLeast(1)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).removeIf(any());

	}

	@Test
	public void testBRMoveBlockPositiveChainOfBlocksNOCONNECTIONtoLEFTOperandBlock() {

		// initialisation of situation
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockB, times(3)).setOperand(movedWallInFrontBlock);
		verify(mockHeadBlocks).removeIf(any());
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
	public void testBRMoveBlockPositiveChainOfBlocksDOWNtoUP2() {
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
		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockB);
		assertEquals(connectedMoveForwardBlockB.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedMoveForwardBlockC.getBlockId(), ConnectionType.UP));
		verify(connectedMoveForwardBlockA,times(2)).setNextBlock(null);
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
	public void testBRMoveBlockPositiveChainOfBlocksCONDITIONtoLEFT2() {
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
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockB);

		assertEquals(connectedNotBlockB.getBlockId(), blockRepository.moveBlock(movedNotBlock.getBlockId(),
				movedNotBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
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
		verify(connectedNotBlockA, times(2)).setOperand(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);

	}
	
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlocksOPERANDtoLEFT2() {
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
				movedNotBlock.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedNotBlockA,times(2)).setOperand(null);
		verify(connectedNotBlockB).setOperand(movedWallInFrontBlock);

	}
	
	@Test
	public void testBRMoveBlockPositiveChainOfBlockBODYtoUP() {

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
		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockA);
		when(mockHeadBlocks.contains(connectedMoveForwardBlockB)).thenReturn(true);
		assertEquals(connectedMoveForwardBlockA.getBlockId(), blockRepository.moveBlock(movedActionBlock.getBlockId(),
				movedActionBlock.getBlockId(), connectedMoveForwardBlockB.getBlockId(), ConnectionType.UP));
		verify(connectedIfBlockA).setFirstBlockOfBody(null);
		verify(connectedMoveForwardBlockA, times(3)).setNextBlock(connectedMoveForwardBlockB);
		verify(mockHeadBlocks).add(movedActionBlock);
		verify(mockHeadBlocks).removeIf(any());
	}

	// COMPLEX SITUATIONS
	@Test
	public void testBRMoveBlockPositiveComplexIfIfConnectToCondition() {

		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(true);
		// effective moved block determined by getBlockIdToPerformMoveOn
		assertEquals(connectedIfBlockA.getBlockId(), blockRepository.moveBlock(movedIfBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		verify(connectedIfBlockA, times(3)).setConditionBlock(movedWallInFrontBlock);

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
		verify(connectedNotBlockA, times(2)).setOperand(movedWallInFrontBlock);

	}

	// NEGATIVE TESTS
	@Test
	public void testBRMoveBlockNegativeConnectionNotPossibleNOCONNECTION() {
		String excMessage = "This socket is not free";

		// Opstellen van program
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);
		movedActionBlock.setNextBlock(connectedIfBlockA);
		connectedIfBlockA.setConditionBlock(movedNotBlock);
		movedNotBlock.setOperand(movedWallInFrontBlock);

		connectedIfBlockB.setNextBlock(connectedMoveForwardBlockB);

		assertExceptionBRMoveBlock("1", "1", connectedIfBlockB.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(connectedNotBlockA.getBlockId(), connectedNotBlockA.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT, excMessage);
		assertExceptionBRMoveBlock(WallInFrontBlockA.getBlockId(), WallInFrontBlockA.getBlockId(),
				movedNotBlock.getBlockId(), ConnectionType.OPERAND, excMessage);

	}
	
	
	@Test
	public void testBRMoveBlockNegativeConnectionNotPossible() {
		String excMessage = "The moved block is not connected to this block or socket";

		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");

		ArrayList<String> parentInfo2 = new ArrayList<String>();
		parentInfo2.add("DOWN");
		parentInfo2.add(connectedMoveForwardBlockA.getBlockId());
		
		ArrayList<String> parentInfo3 = new ArrayList<String>();
		parentInfo3.add("CONDITION");
		parentInfo3.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfo4 = new ArrayList<String>();
		parentInfo4.add("OPERAND");
		parentInfo4.add(connectedNotBlockB.getBlockId());
		
		ArrayList<String> parentInfo5 = new ArrayList<String>();
		parentInfo5.add("BODY");
		parentInfo5.add(movedIfBlock.getBlockId());

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
					if (id.equals(connectedNotBlockA.getBlockId())) {
						return parentInfo3;
					}
					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo5;
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
					if (id.equals(connectedNotBlockA.getBlockId())) {
						return parentInfo3;
					}
					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo4;
					}
					if (id.equals(connectedMoveForwardBlockB.getBlockId())) {
						return parentInfo5;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		// Opstellen van program
		connectedMoveForwardBlockA.setNextBlock(connectedIfBlockA);
		when(connectedMoveForwardBlockA.getNextBlock()).thenReturn(connectedIfBlockA);
		
		connectedIfBlockA.setConditionBlock(connectedNotBlockB);
		when(connectedIfBlockA.getConditionBlock()).thenReturn(connectedNotBlockB);
		
		connectedNotBlockB.setOperand(connectedNotBlockA);
		when(connectedNotBlockB.getOperand()).thenReturn(connectedNotBlockA);
		
		movedIfBlock.setFirstBlockOfBody(connectedMoveForwardBlockA);
		when(movedIfBlock.getFirstBlockOfBody()).thenReturn(connectedMoveForwardBlockA);
		
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId(), ConnectionType.DOWN, excMessage);
		
		assertExceptionBRMoveBlock(connectedNotBlockA.getBlockId(), connectedNotBlockA.getBlockId(),movedIfBlock.getBlockId(), ConnectionType.CONDITION, excMessage);
		
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),connectedNotBlockA.getBlockId(), ConnectionType.OPERAND, excMessage);
		
		assertExceptionBRMoveBlock(connectedMoveForwardBlockB.getBlockId(), connectedMoveForwardBlockB.getBlockId(),connectedIfBlockB.getBlockId(), ConnectionType.BODY, excMessage);

	}

	@Ignore
	public void assertExceptionBRMoveBlock(String topOfMovedChainBlockId, String movedBlockId,
			String connectedAfterMoveBlockId, ConnectionType connectionAfterMove, String excMessage) {
		boolean pass = false;

		try {
			blockRepository.moveBlock(topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId,
					connectionAfterMove);
		} catch (NoSuchConnectedBlockException e) {
			pass = e.getMessage().equals(excMessage);
		} catch (InvalidBlockConnectionException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("moveBlock failed in the blockRepository for combination=" + topOfMovedChainBlockId + " "
				+ movedBlockId + " " + connectedAfterMoveBlockId + " " + connectionAfterMove, pass);
	}

	@Test
	public void testBRMoveBlockNegativeBlocksNotInDomain() {// Specifieker volgens case, forloop dus niet gebruiken

		when(blockRepository.getBlockByID("1")).thenReturn(movedActionBlock);

		ArrayList<ConnectionType> connectionTypesB;

		String excMessage = "The requested block doesn't exist in the domain";

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
			assertExceptionBRMoveBlock("1", "1", "", connectionTypeB, excMessage);
		}

		// Test for movedBlock == null
		for (ConnectionType connectionTypeB : connectionTypes) {
			assertExceptionBRMoveBlock("40", "40", "", connectionTypeB, excMessage);
		}
	}

	@Test
	public void testBRSocketNotFreeNOCONNECTION() {
		String excMessage = "This socket is not free";

		when(connectedIfBlockA.getNextBlock()).thenReturn(connectedIfBlockB);
		when(connectedIfBlockA.getFirstBlockOfBody()).thenReturn(movedIfBlock);
		when(connectedIfBlockA.getConditionBlock()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.BODY, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), ConnectionType.OPERAND, excMessage);

		// connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);
		// NotBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), ConnectionType.LEFT, excMessage);
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

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		when(connectedIfBlockA.getNextBlock()).thenReturn(connectedIfBlockB);
		when(connectedIfBlockA.getFirstBlockOfBody()).thenReturn(movedIfBlock);

		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.BODY, excMessage);
		// connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);

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

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		when(mockHeadBlocks.contains(connectedNotBlockB)).thenReturn(false);

		when(connectedIfBlockA.getConditionBlock()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedNotBlockA.getBlockId(), ConnectionType.OPERAND, excMessage);
		// connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), ConnectionType.LEFT, excMessage);

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

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		NotBlock notBlockC = spy(new NotBlock("17"));
		when(mockHeadBlocks.contains(connectedNotBlockB)).thenReturn(false);
		when(blockRepository.getBlockByID("17")).thenReturn(notBlockC);

		when(connectedIfBlockA.getConditionBlock()).thenReturn(notBlockC);
		when(notBlockC.getOperand()).thenReturn(connectedNotBlockB);

		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedIfBlockA.getBlockId(), ConnectionType.CONDITION, excMessage);
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(), notBlockC.getBlockId(),
				ConnectionType.OPERAND, excMessage);
		// connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(),
				connectedNotBlockB.getBlockId(), ConnectionType.LEFT, excMessage);

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

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(connectedIfBlockB.getFirstBlockOfBody()).thenReturn(movedActionBlock);
		when(connectedIfBlockB.getNextBlock()).thenReturn(movedWhileBlock);
		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(false);

		when(mockHeadBlocks.contains(movedWallInFrontBlock)).thenReturn(false);
		
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.BODY, excMessage);
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.DOWN, excMessage);
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(),
				connectedIfBlockB.getBlockId(), ConnectionType.UP, excMessage);
		// connectedIfBlock not in headBlocks, should trigger an Exception
		assertExceptionBRMoveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT, excMessage);

	}
	
	
	
	
	@Test
	public void testMoveBlockPositiveDOWNtoNOCONNECTION() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add(connectedMoveForwardBlockA.getBlockId());
		
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
				}

				return null;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());

		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), "" , ConnectionType.NOCONNECTION);
		verify(connectedMoveForwardBlockA,times(2)).setNextBlock(null);
		verify(mockHeadBlocks).add(movedActionBlock);
	}

	@Test
	public void testMoveBlockDOWNtoLEFTNegative() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add(movedIfBlock.getBlockId());
		
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
				}

				return null;
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
						return parentInfo;
					}
				}

				return null;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage("This socket is not free");
		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedNotBlock.getBlockId(), WallInFrontBlockA.getBlockId() , ConnectionType.LEFT);
	}
	
	@Test
	public void testMoveBlockBodytoNoConnection() {
		
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("BODY");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
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
				}

				return null;
			}

		}).when(blockRepository).getConnectedParentIfExists(any());
//		
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
				}

				return null;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		blockRepository.moveBlock(movedIfBlock.getBlockId(), movedIfBlock.getBlockId(), "" , ConnectionType.NOCONNECTION);
		verify(mockHeadBlocks).add(movedIfBlock);
		verify(connectedIfBlockA,times(2)).setFirstBlockOfBody(null);
	}
	
	@Test
	public void testMoveBlockOperandtoNoConnection() {
		
		
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

					if (id.equals(movedNotBlock.getBlockId())) {
						return parentInfo;
					}
				}

				return null;
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
						return parentInfo;
					}
				}

				return null;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		blockRepository.moveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(), "" , ConnectionType.NOCONNECTION);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(connectedNotBlockA,times(2)).setOperand(null);
	}
	
	@Test
	public void testMoveBlockConditiontoNoConnection() {
		
		
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
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
				}

				return null;
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
						return parentInfo;
					}
				}

				return null;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());

		blockRepository.moveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(), "" , ConnectionType.NOCONNECTION);
		verify(mockHeadBlocks).add(movedNotBlock);
		verify(connectedIfBlockA,times(2)).setConditionBlock(null);
	}
	
	@Test
	public void testMoveBlockNegativeBfmNull() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
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
						return parentInfo;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(blockRepository.getBlockByID(connectedIfBlockA.getBlockId())).thenReturn(null ,connectedIfBlockA);
		
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage("The requested block doesn't exist in the domain");
		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId() , ConnectionType.DOWN);
		
		
	}
	
	@Test
	public void testMoveBlockNegativeAfmNullCondition() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("CONDITION");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
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
						return parentInfo;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(blockRepository.getBlockByID(connectedIfBlockB.getBlockId())).thenReturn(null);
		
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage("The requested block doesn't exist in the domain");
		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId() , ConnectionType.DOWN);
		
		
	}
	
	@Test
	public void testMoveBlockNegativeAfmNullOPERAND() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("OPERAND");
		parentInfo.add(connectedNotBlockA.getBlockId());
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
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
						return parentInfo;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(blockRepository.getBlockByID(connectedIfBlockB.getBlockId())).thenReturn(null);
		
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage("The requested block doesn't exist in the domain");
		blockRepository.moveBlock(movedNotBlock.getBlockId(), movedNotBlock.getBlockId(), connectedIfBlockB.getBlockId() , ConnectionType.CONDITION);
		
		
	}
	
	@Test
	public void testMoveBlockNegativeAfmNullBODY() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("BODY");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
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
						return parentInfo;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(blockRepository.getBlockByID(connectedIfBlockB.getBlockId())).thenReturn(null);
		
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage("The requested block doesn't exist in the domain");
		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId() , ConnectionType.DOWN);
		
		
	}
	
	
	@Test
	public void testMoveBlockNegativeAfmNullDOWN() {
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add(connectedIfBlockA.getBlockId());
		
		ArrayList<String> parentInfoDefault = new ArrayList<String>();
		parentInfoDefault.add("NOCONNECTION");
		parentInfoDefault.add("");
		
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
						return parentInfo;
					}
				}

				return parentInfoDefault;
			}

		}).when(blockRepository).getConnectedBlockBeforeMove(any(), any(), any());
		
		when(blockRepository.getBlockByID(connectedIfBlockB.getBlockId())).thenReturn(null);
		
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage("The requested block doesn't exist in the domain");
		blockRepository.moveBlock(movedActionBlock.getBlockId(), movedActionBlock.getBlockId(), connectedIfBlockB.getBlockId() , ConnectionType.DOWN);
		
		
	}
	

	// TEST USED METHODS FOR MOVE

	@Test
	public void testBRParentIfExtistsPositive() {

		movedMoveForwardBlock = Mockito.spy(new ActionBlock("17", type));
		
		definitionBlock = spy(new DefinitionBlock("20"));
		mockAllBlocks.put(definitionBlock.getBlockId(), definitionBlock);
		
		ActionBlock movedMoveForwardBlockB = Mockito.spy(new ActionBlock("18", type));
		mockAllBlocks.put(movedMoveForwardBlock.getBlockId(), movedMoveForwardBlock);
		mockAllBlocks.put(movedMoveForwardBlockB.getBlockId(), movedMoveForwardBlockB);


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
		
		ArrayList<String> parentInfo7 = new ArrayList<String>();
		parentInfo7.add("BODY");
		parentInfo7.add("20");



		when(movedActionBlock.getNextBlock()).thenReturn(connectedMoveForwardBlockA);
		when(connectedMoveForwardBlockA.getNextBlock()).thenReturn(connectedMoveForwardBlockB);

		when(movedWhileBlock.getFirstBlockOfBody()).thenReturn(movedMoveForwardBlock);
		when(movedWhileBlock.getNextBlock()).thenReturn(movedMoveForwardBlockB);

		when(movedWhileBlock.getConditionBlock()).thenReturn(movedNotBlock);
		when(movedNotBlock.getOperand()).thenReturn(connectedNotBlockA);
		when(connectedNotBlockA.getOperand()).thenReturn(connectedNotBlockB);
		
		when(definitionBlock.getFirstBlockOfBody()).thenReturn(movedActionBlock);

		assertEquals(parentInfo, blockRepository.getConnectedParentIfExists(connectedMoveForwardBlockA.getBlockId()));
		assertEquals(parentInfo2, blockRepository.getConnectedParentIfExists(movedNotBlock.getBlockId()));
		assertEquals(parentInfo3, blockRepository.getConnectedParentIfExists(connectedNotBlockA.getBlockId()));
		assertEquals(parentInfo4, blockRepository.getConnectedParentIfExists(connectedNotBlockB.getBlockId()));
		assertEquals(parentInfo5, blockRepository.getConnectedParentIfExists(movedMoveForwardBlock.getBlockId()));
		assertEquals(parentInfo6, blockRepository.getConnectedParentIfExists(movedMoveForwardBlockB.getBlockId()));
		assertEquals(parentInfo7, blockRepository.getConnectedParentIfExists(movedActionBlock.getBlockId()));
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
	public void testBRConnectedBFM() {

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

		assertEquals(parentInfo4, blockRepository.getConnectedBlockBeforeMove(movedIfBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfo4, blockRepository.getConnectedBlockBeforeMove(connectedIfBlockB.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(movedNotBlock.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));
		assertEquals(parentInfoDefault, blockRepository.getConnectedBlockBeforeMove(connectedNotBlockB.getBlockId(),
				movedWallInFrontBlock.getBlockId(), ConnectionType.LEFT));

	}
	
	
	

}
