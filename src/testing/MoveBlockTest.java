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
import org.mockito.internal.invocation.mockref.MockReference;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.BlockController;
import applicationLayer.ConnectionType;
import applicationLayer.DomainController;

import domainLayer.blocks.*;

import exceptions.NoSuchConnectedBlockException;


@RunWith(MockitoJUnitRunner.class)
public class MoveBlockTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private ArrayList<String> blockIdsInRepository = new ArrayList<String>();

	@Spy
	private BlockRepository blockRepository;
	
	@Spy
	private BlockRepository blockRepositoryValidBeforeMove;

	private ActionBlock connectedActionBlockA;
	private ActionBlock connectedActionBlockB;
	private ActionBlock movedActionBlock;

	private ControlBlock connectedControlBlockA;
	private ControlBlock connectedControlBlockB;
	private ControlBlock movedControlBlock;

	private ConditionBlock connectedConditionBlockA;
	private ConditionBlock connectedConditionBlockB;
	private ConditionBlock movedConditionBlock;

	private OperatorBlock connectedOperatorBlockA;
	private OperatorBlock connectedOperatorBlockB;
	private OperatorBlock movedOperatorBlock;

	private IfBlock connectedIfBlockA;
	private IfBlock connectedIfBlockB;
	private IfBlock movedIfBlock;

	private NotBlock connectedNotBlockA;
	private NotBlock connectedNotBlockB;
	private NotBlock movedNotBlock;

	private MoveForwardBlock connectedMoveForwardBlockA;
	private MoveForwardBlock connectedMoveForwardBlockB;
	private MoveForwardBlock movedMoveForwardBlock;

	private TurnLeftBlock connectedTurnLeftBlockA;
	private TurnLeftBlock connectedTurnLeftBlockkB;
	private TurnLeftBlock movedTurnLeftBlock;

	private TurnRightBlock TurnRightBlocA;
	private TurnRightBlock TurnRightBlocB;
	private TurnRightBlock movedTurnRightBloc;

	private WallInFrontBlock WallInFrontBlockA;
	private WallInFrontBlock WallInFrontBlockB;
	private WallInFrontBlock movedWallInFrontBlock;

	private WhileBlock connectedWhileBlockA;
	private WhileBlock connectedWhileBlockB;
	private WhileBlock movedWhileBlock;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// Mulptiple head block reprository
		movedActionBlock = spy(new MoveForwardBlock("1"));
		connectedMoveForwardBlockA = spy(new MoveForwardBlock("2"));
		connectedMoveForwardBlockB = spy(new MoveForwardBlock("3"));
		
		
		connectedIfBlockA = spy(new IfBlock("4"));
		connectedIfBlockB = spy(new IfBlock("5"));
		movedIfBlock = spy(new IfBlock("7"));
		
		connectedWhileBlockA = spy(new WhileBlock("8"));
		connectedWhileBlockB = spy(new WhileBlock("9"));
		movedWhileBlock = spy(new WhileBlock("10"));
		
		WallInFrontBlockA = spy(new WallInFrontBlock("11"));
		WallInFrontBlockB = spy(new WallInFrontBlock("12"));
		movedWallInFrontBlock = spy(new WallInFrontBlock("13"));
		
		when(blockRepository.getBlockByID("1")).thenReturn(movedActionBlock);
		when(blockRepository.getBlockByID("2")).thenReturn(connectedMoveForwardBlockA);
		when(blockRepository.getBlockByID("3")).thenReturn(connectedMoveForwardBlockB);
		when(blockRepository.getBlockByID("4")).thenReturn(connectedIfBlockA);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Mock
	private BlockController mockBlockController;
	
	@Mock
	private BlockRepository mockBlockReprository;

	
	
	@Spy
	@InjectMocks
	private DomainController dc;
	
	@Spy
	@InjectMocks
	private BlockController bc;

	@Test
	public void testMoveBlockRepository() {
		// verify(mockBlockController).moveBlock("1", null , ConnectionType.NOCONNECTION
		// , "2" , ConnectionType.DOWN );
	}

	// POSITIVE TESTS
	
	@Test
	public void testPositiveMoveBlock() {
		
		dc.moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		verify(mockBlockController).moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		
		bc.moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		verify(mockBlockReprository).moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		
		
		
		
	}
	
	
	// TESTS CONNECTIONTYPE.NOCONNECTION
	@Test
	public void testMoveBlockActionBlockWithValidConnectionNOCONNECTION() {

		// TESTS CONNECTIONTYPE.NOCONNECTION - NOCONNECTION
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		

		// TESTS CONNECTIONTYPE.NOCONNECTION - DOWN
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.DOWN);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);

		// TESTS CONNECTIONTYPE.NOCONNECTION - UP
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.UP);
		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockA);

		// TESTS CONNECTIONTYPE.NOCONNECTION - BODY
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "4", ConnectionType.BODY);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);

		//TESTS CONNECTIONTYPE.NOCONNECTION - CONDITION ( SEE NEGATIVE TEST CASES)

	}

	// TESTS CONNECTIONTYPE.DOWN
	@Test
	public void testMoveBlockActionBlockWithValidConnectionDOWN() {


		//TESTS CONNECTIONTYPE.DOWN - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)

		// TESTS CONNECTIONTYPE.DOWN - DOWN
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment

		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
		blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.DOWN);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
		assertEquals(connectedMoveForwardBlockB.getNextBlock(), movedActionBlock);


		// TESTS CONNECTIONTYPE.DOWN - UP
		// Test with no nextBlock for the MovedActionBlock
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment

		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
		blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.UP);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);

		// TESTS CONNECTIONTYPE.DOWN - BODY
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment

		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
		blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "4", ConnectionType.BODY);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);

		//TESTS CONNECTIONTYPE.DOWN - CONDITION ( SEE NEGATIVE TEST CASES)
	}

	// TESTS CONNECTIONTYPE.UP
	@Test
	public void testMoveBlockActionBlockWithValidConnectionUP() {
		// We here imagine we want to move the headBlock of a chain of blocks.
		// By design we have chosen that if a HeadBlock is moved all blocks below will
		// move with it.
		// With this test we'll check that all blocks are still in the chain after
		// moving the HeadBlock.

		// Initialize Situation
		movedActionBlock.setNextBlock(connectedMoveForwardBlockA);
		connectedMoveForwardBlockA.setNextBlock(connectedMoveForwardBlockB);
		connectedMoveForwardBlockB.setNextBlock(connectedIfBlockA);
		connectedIfBlockA.setFirstBlockOfBody(connectedIfBlockB);

		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
	}

	@Test
	public void testMoveActionBlockWithValidConnectionBody() {
		when(blockRepository.getBlockByID("5")).thenReturn(connectedIfBlockB);

		//TESTS CONNECTIONTYPE.BODY - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)

		//TESTS CONNECTIONTYPE.BODY - DOWN
		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment

		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
		blockRepository.moveBlock("1", "4", ConnectionType.BODY, "2", ConnectionType.DOWN);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), null);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);

		//TESTS CONNECTIONTYPE.BODY - UP
		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment

		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
		blockRepository.moveBlock("1", "4", ConnectionType.BODY, "2", ConnectionType.UP);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), null);
		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockA);

		//TESTS CONNECTIONTYPE.BODY - BODY
		connectedIfBlockA.setFirstBlockOfBody(movedActionBlock);// Initialise test environment

		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
		blockRepository.moveBlock("1", "4", ConnectionType.BODY, "5", ConnectionType.BODY);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), null);
		assertEquals(connectedIfBlockB.getFirstBlockOfBody(), movedActionBlock);

		//TESTS CONNECTIONTYPE.BODY - CONDITION ( SEE NEGATIVE TEST CASES)
	}

	@Test
	public void testMoveActionBlockWithValidConnectionCondition() {
		//Initialize environment
		when(blockRepository.getBlockByID("4")).thenReturn(connectedIfBlockA);
		when(blockRepository.getBlockByID("5")).thenReturn(connectedIfBlockB);
		when(blockRepository.getBlockByID("8")).thenReturn(connectedWhileBlockA);
		when(blockRepository.getBlockByID("13")).thenReturn(movedWallInFrontBlock);
		
		
		
		
		//TESTS CONNECTIONTYPE.CONDITION - NOCONNECTION (See testCase testMoveBlockWithValidReprositoryBeforeMove)
		
		//TEST CONENCTIONTYPE.CONDITION - CONDITION TO IFBLOCK
		connectedIfBlockA.setConditionBlock(movedWallInFrontBlock);//Initial State
		assertEquals(connectedIfBlockA.getConditionBlock(), movedWallInFrontBlock);
		blockRepository.moveBlock("13", "4", ConnectionType.CONDITION, "5", ConnectionType.CONDITION);
		assertEquals(connectedIfBlockA.getConditionBlock(), null);
		assertEquals(connectedIfBlockB.getConditionBlock(), movedWallInFrontBlock);
		
		
		//TEST CONENCTIONTYPE.CONDITION - CONDITION TO WHILEBLOCK
		connectedIfBlockA.setConditionBlock(movedWallInFrontBlock);//Initial State
		assertEquals(connectedIfBlockA.getConditionBlock(), movedWallInFrontBlock);
		blockRepository.moveBlock("13", "4", ConnectionType.CONDITION, "8", ConnectionType.CONDITION);
		assertEquals(connectedIfBlockA.getConditionBlock(), null);
		assertEquals(connectedWhileBlockA.getConditionBlock(), movedWallInFrontBlock);
	}

	@Test
	public void testMoveBlockWithValidReprositoryBeforeMove() {

		// MovedActionBlock should have no
		// connection and thus be in
		// HeadBlocks, this means the
		// Program is invalid

		// ValidBeforeMove Repository

		ActionBlock movedActionBlock = spy(new MoveForwardBlock("1"));
		ActionBlock connectedMoveForwardBlock = spy(new MoveForwardBlock("2"));

		when(blockRepositoryValidBeforeMove.getBlockByID("1")).thenReturn(movedActionBlock);
		when(blockRepositoryValidBeforeMove.getBlockByID("2")).thenReturn(connectedMoveForwardBlock);

		// TESTS CONNECTIONTYPE.DOWN - NOCONNECTION
		connectedMoveForwardBlock.setNextBlock(movedActionBlock);//Initial State
		assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), true);// InitialState
		blockRepositoryValidBeforeMove.moveBlock("1", "2", ConnectionType.DOWN, null, ConnectionType.NOCONNECTION);
		assertEquals(connectedMoveForwardBlock.getNextBlock(), null);
		assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), false);

		//TEST HeadBlock Removed
		assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), false);
		blockRepositoryValidBeforeMove.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.DOWN);
		assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), true);


		// TESTS CONNECTIONTYPE.BODY - NOCONNECTION
		assertEquals(connectedMoveForwardBlock.getNextBlock(), movedActionBlock);// InitialState


		//TEST HeadBlock Removed
		//assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), false);
		//blockRepositoryValidBeforeMove.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.DOWN);
		//assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), true);

		// TESTS CONNECTIONTYPE.CONDITION - NOCONNECTION
		//connectedMoveForwardBlock.setNextBlock(movedActionBlock);
	}

	@Test
	public void testMoveAssesmentBlockWitchValidConnectionOperand() {
		
	}
	@Test
	public void testMoveBlockWithChainOfBlock() {

		// Test with a nextBlock for the MovedActionBlock
		movedActionBlock.setNextBlock(connectedIfBlockB);// Initialise test environment
		connectedMoveForwardBlockA.setNextBlock(movedActionBlock);// Initialise test environment

		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);// Initial state
		assertEquals(movedActionBlock.getNextBlock(), connectedIfBlockB);// Initial state
		blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.UP);

		assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
		assertEquals(movedActionBlock.getNextBlock(), connectedIfBlockB);
		assertEquals(connectedIfBlockB.getNextBlock(), connectedMoveForwardBlockB);
	}

	// NEGATIVE TESTS
	
	@Test
	public void testMoveBlocInvalidConnection() {
		
	}
	
	
}
