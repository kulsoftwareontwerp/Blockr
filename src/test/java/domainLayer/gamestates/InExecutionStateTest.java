/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.GameController;
import commands.ExecuteBlockCommand;
import commands.GameWorldCommand;
import domainLayer.blocks.ActionBlock;

/**
 * InExecutionStateTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class InExecutionStateTest {


	@Mock(name="gameController")
	private GameController gameController;
	@Mock(name="nextActionBlockToBeExecuted")
	private ActionBlock nextActionBlockToBeExecuted;
	@Spy @InjectMocks
	private InExecutionState ies;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#reset()}.
	 */
	@Test
	public void testReset() {
		ies.reset();
		verify(gameController).toState(any(ResettingState.class));
		ies.reset();
		
		verify(gameController,atLeastOnce()).toState(Mockito.any(ResettingState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#execute()}.
	 */
	@Test
	public void testExecute() {
		ies.execute();
		verify(gameController).handleCommand(any(GameWorldCommand.class));
	}
	@Test
	public void testExecute_nextActionBlockToBeExecutedNull_Positive() {
		Mockito.doReturn(null).when(ies).getNextActionBlockToBeExecuted();
		
		ies.execute();
		
		Mockito.verifyNoMoreInteractions(gameController);
	}
	
	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#execute()}.
	 */
	@Test
	public void testExecute_nextActionBlockToBeExecutedNotNull_Positive() {		
		ies.execute();
		
		verify(gameController,atLeastOnce()).handleCommand(Mockito.any(ExecuteBlockCommand.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#update()}.
	 */
	@Test
	public void testUpdate() {
		ies.update();
		verify(gameController).toState(any(GameState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#InExecutionState(applicationLayer.GameController, domainLayer.nextActionBlockToBeExecuteds.ActionBlock)}.
	 */
	@Test
	public void testInExecutionState() {
		InExecutionState state = new InExecutionState(gameController, nextActionBlockToBeExecuted);
		try {
			Field gameController = InExecutionState.class.getSuperclass().getDeclaredField("gameController");
			gameController.setAccessible(true);
			assertTrue("gameController was not initialised", gameController.get(state) != null);
			
			Field actionBlock = InExecutionState.class.getDeclaredField("nextActionBlockToBeExecuted");
			actionBlock.setAccessible(true);
			assertTrue("ActionBlock was not initialised", actionBlock.get(state) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#getNextActionBlockToBeExecuted()}.
	 */
	@Test
	public void testGetNextActionBlockToBeExecuted() {
		assertEquals(nextActionBlockToBeExecuted,ies.getNextActionBlockToBeExecuted());
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#setNextActionBlockToBeExecuted(domainLayer.nextActionBlockToBeExecuteds.ActionBlock)}.
	 */
	@Test
	public void testSetNextActionBlockToBeExecuted() {
		ies.setNextActionBlockToBeExecuted(nextActionBlockToBeExecuted);
		try {
			Field ActionBlock = InExecutionState.class.getDeclaredField("nextActionBlockToBeExecuted");
			ActionBlock.setAccessible(true);
			assertTrue("ActionBlock was not initialised", ActionBlock.get(ies) != null);
		}catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
}
