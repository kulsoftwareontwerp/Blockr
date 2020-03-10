package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import domainLayer.gamestates.GameState;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteBlockTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();	
	
	private ActionBlock actionBlock;
	private ControlBlock controlBlock;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		actionBlock = new MoveForwardBlock("actionBlock");
		controlBlock = new WhileBlock("controlBlock");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Mock(name="gameController")
	private GameController mockGameController;
	@Spy @InjectMocks
	private DomainController dc;
	
	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#executeBlock()}.
	 */
	@Test
	public void testDCExecuteBlockPositive() {
		dc.executeBlock();
		verify(mockGameController,atLeastOnce()).executeBlock();
	}
	
	@Mock(name="gameState")
	private GameState mockGameState;
	@Spy @InjectMocks
	private GameController gc;
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#executeBlock()}.
	 */
	@Test
	public void testGCExecuteBlockPositive() {
		gc.executeBlock();
		verify(mockGameState,atLeastOnce()).execute();
	}
	
	
	@Mock(name="blockRepository")
	private BlockRepository mockBlockRepository;
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testGCFindFirstBlockToBeExecutedPositiveActionBlock() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), actionBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testGCFindFirstBlockToBeExecutedPositiveNoActionBlock() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(controlBlock);
		when(gc.findNextActionBlockToBeExecuted(controlBlock)).thenReturn(actionBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), actionBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(controlBlock);
	}
	
	
	
	
	
	
	
	
	
	

}
