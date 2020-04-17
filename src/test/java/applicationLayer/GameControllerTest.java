/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.matchers.Any;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.WhileBlock;
import domainLayer.gamestates.InExecutionState;
import domainLayer.gamestates.ValidProgramState;

/**
 * GameControllerTest
 *
 * @version 0.1
 * @author group17
 */
public class GameControllerTest {

	@Mock(name="programBlockRepository")
	private BlockRepository programBlockRepository;
	// Voor uitleg waarom hier specifiek een constructor wordt gecalled, zie comments boven constructor in kwestie.
	@Spy @InjectMocks
	private GameController gc = new GameController(programBlockRepository);
	
	private InExecutionState inExecutionState;
	private ValidProgramState validProgramState;
	private ActionBlock actionBlock;
	private ControlBlock ifBlock;
	private ControlBlock whileBlock;
	private AssessableBlock assessableBlock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		actionBlock = new ActionBlock("actionBlockId", Mockito.mock(Action.class));
		ifBlock = spy(new IfBlock("IfBlock"));
		whileBlock = spy(new WhileBlock("WhileBlock"));
		assessableBlock = spy(new ConditionBlock("ConditionBlock", Mockito.mock(Predicate.class)));
		inExecutionState = spy(new InExecutionState(gc, actionBlock));
		validProgramState = spy(new ValidProgramState(gc));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link applicationLayer.GameController#GameController()}.
	 */
	@Test
	public void testGameController() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#fireRobotChangeEvent()}.
	 */
	@Test
	public void testFireRobotChangeEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#resetGameExecution()}.
	 */
	@Test
	public void testResetGameExecution_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		Mockito.doNothing().when(inExecutionState).reset();
		
		gc.resetGameExecution();
		
		verify(inExecutionState,atLeastOnce()).reset();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#getCurrentState()}.
	 */
	@Test
	public void testGetCurrentState() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#toState(domainLayer.gamestates.GameState)}.
	 */
	@Test
	public void testToState() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#updateState()}.
	 */
	@Test
	public void testUpdateState() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#executeBlock()}.
	 */
	@Test
	public void testExecuteBlock() {
		when(gc.getCurrentState()).thenReturn(validProgramState);
		Mockito.doNothing().when(validProgramState).execute();
		
		gc.executeBlock();
		
		verify(validProgramState,atLeastOnce()).execute();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_ActionBlock_Positive() {
		when(programBlockRepository.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findFirstBlockToBeExecuted());
		verify(programBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_ControlBlock_Positive() {
		when(programBlockRepository.findFirstBlockToBeExecuted()).thenReturn(ifBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(null, ifBlock);
		
		assertEquals(actionBlock, gc.findFirstBlockToBeExecuted());
		verify(programBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockIfBlock_Positive() {
		when(programBlockRepository.getEnclosingControlBlock(actionBlock)).thenReturn(ifBlock);
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockWhileBlock_Positive() {
		when(programBlockRepository.getEnclosingControlBlock(actionBlock)).thenReturn(whileBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(null, whileBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockNull_Positive() {
		when(programBlockRepository.getEnclosingControlBlock(actionBlock)).thenReturn(null);
		
		assertEquals(null, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockActionBlock_Positive() {
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, actionBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockControlBlock_PositiveCondition_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(assessableBlock);
		Mockito.doReturn(true).when(gc).evaluateCondition(assessableBlock);
		Mockito.doReturn(actionBlock).when(ifBlock).getFirstBlockOfBody();
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, ifBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockControlBlock_NegativeCondition_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(assessableBlock);
		Mockito.doReturn(false).when(gc).evaluateCondition(assessableBlock);
		Mockito.doReturn(actionBlock).when(ifBlock).getNextBlock();
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, ifBlock));
	}

	/**
	 * Test method for {@link applicationLayer.GameController#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram() {
		gc.checkIfValidProgram();
		verify(programBlockRepository,atLeastOnce()).checkIfValidProgram();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#fireUpdateHighlightingEvent(java.lang.String)}.
	 */
	@Test
	public void testFireUpdateHighlightingEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#onResetExecutionEvent(events.ResetExecutionEvent)}.
	 */
	@Test
	public void testOnResetExecutionEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#onUpdateGameStateEvent(events.UpdateGameStateEvent)}.
	 */
	@Test
	public void testOnUpdateGameStateEvent() {
		fail("Not yet implemented");
	}

}
