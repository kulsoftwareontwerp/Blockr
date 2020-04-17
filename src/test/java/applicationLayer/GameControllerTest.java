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

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.BlockRepository;
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
	private BlockRepository blockRepository;
	@Spy @InjectMocks
	private GameController gc;
	
	private InExecutionState inExecutionState;
	private ValidProgramState validProgramState;
	private ActionBlock actionBlock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
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
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(null);
		Mockito.doNothing().when(gc).fireRobotChangeEvent();
		
		gc.resetGameExecution();
		
		verify(inExecutionState,atLeastOnce()).reset();
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(null);
		verify(gc,atLeastOnce()).fireRobotChangeEvent();
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
	 * Test method for {@link applicationLayer.GameController#resetRobot()}.
	 */
	@Test
	public void testResetRobot() {
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
		when(blockRepository.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), actionBlock);
		verify(blockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#performRobotAction(domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testPerformRobotAction() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.GameController#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram() {
		fail("Not yet implemented");
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
