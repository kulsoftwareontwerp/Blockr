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


@RunWith(MockitoJUnitRunner.class)
public class MoveBlockTest {
	
	@Mock
	private BlockRepository mockBlockReprository;
	
	@Mock
	private DomainController mockDomainController;
	

	private ArrayList<String> blockIdsInRepository = new ArrayList<String>();

	
	private BlockRepository blockRepository;
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
	private IfBlock movednewIfBlock;
	
	private NotBlock connectedNotBlockA;
	private NotBlock connectedNotBlockB;
	private NotBlock movednewNotBlock;
	
	private MoveForwardBlock connectedMoveForwardBlockA;
	private MoveForwardBlock connectedMoveForwardBlockB;
	private MoveForwardBlock movedMoveForwardBlock;
	
	private TurnLeftBlock connectedTurnLeftBlockA;
	private TurnLeftBlock connectedTurnLeftBlockkB;
	private TurnLeftBlock movedTurnLeftBlock;
	
	private TurnRightBlock TurnRightBlocA;
	private TurnRightBlock TurnRightBlocB;
	private TurnRightBlock movedTurnRightBloc;
	
	private WallInFrontBlock newWallInFrontBlockA;
	private WallInFrontBlock newWallInFrontBlockB;
	private WallInFrontBlock movedWallInFrontBlockActionBlock;
	
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
		blockRepository = spy(BlockRepository.getInstance());
		//blockRepositoryValidBeforeMove = spy(BlockRepository.getInstance());
		movedActionBlock = spy(new MoveForwardBlock("1"));
		connectedMoveForwardBlockA = spy(new MoveForwardBlock("2"));
		connectedMoveForwardBlockB = spy(new MoveForwardBlock("3"));
		connectedIfBlockA = spy(new IfBlock("4"));
		
		//when(blockRepositoryValidBeforeMove.getBlockByID("1")).thenReturn(movedActionBlock);
		//when(blockRepositoryValidBeforeMove.getBlockByID("2")).thenReturn(connectedMoveForwardBlockA);
		
		
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
	@Spy @InjectMocks
	private DomainController dc;
	
	@Test
	public void testMoveBlockRepository() {
		//verify(mockBlockController).moveBlock("1", null , ConnectionType.NOCONNECTION , "2" , ConnectionType.DOWN );
	}
	
	
	//TESTS CONNECTIONTYPE.NOCONNECTION
	@Test
	public void testMoveBlockActionBlockWithValidConnectionNOCONNECTION() {
		
		
		
		//TESTS CONNECTIONTYPE.NOCONNECTION - NOCONNECTION
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, null, ConnectionType.NOCONNECTION);
		assertEquals(blockRepository.checkIfValidProgram(), false);
		
		//TESTS CONNECTIONTYPE.NOCONNECTION - DOWN
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.DOWN);
		assertEquals(connectedMoveForwardBlockA.getNextBlock(), movedActionBlock);
		
		//TESTS CONNECTIONTYPE.NOCONNECTION - UP
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "2", ConnectionType.UP);
		assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockA);
		
		//TESTS CONNECTIONTYPE.NOCONNECTION - BODY
		blockRepository.moveBlock("1", null, ConnectionType.NOCONNECTION, "4", ConnectionType.BODY);
		assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
		
		
	}
	
	//TESTS CONNECTIONTYPE.DOWN
	@Test
	public void testMoveBlockActionBlockWithValidConnectionDOWN() {
		//TESTS CONNECTIONTYPE.DOWN - NOCONNECTION
				/*assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), true);
				blockRepository.moveBlock("1", "2", ConnectionType.DOWN,null, ConnectionType.NOCONNECTION);
				assertEquals(connectedActionBlockA.getNextBlock(), null);
				assertEquals(blockRepositoryValidBeforeMove.checkIfValidProgram(), false);*/
				
				//TESTS CONNECTIONTYPE.DOWN - DOWN
				blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.DOWN);
				assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
				assertEquals(connectedMoveForwardBlockB.getNextBlock(), movedActionBlock);
				
				//TESTS CONNECTIONTYPE.NOCONNECTION - UP
				blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "3", ConnectionType.UP);
				assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
				assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);
					
				//TESTS CONNECTIONTYPE.NOCONNECTION - BODY
				blockRepository.moveBlock("1", "2", ConnectionType.DOWN, "4", ConnectionType.BODY);
				assertEquals(connectedMoveForwardBlockA.getNextBlock(), null);
				assertEquals(connectedIfBlockA.getFirstBlockOfBody(), movedActionBlock);
				assertEquals(movedActionBlock.getNextBlock(), connectedMoveForwardBlockB);
	}
}
