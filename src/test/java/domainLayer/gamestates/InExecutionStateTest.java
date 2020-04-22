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
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.GameController;
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

	@Mock
	private ActionBlock block;
	@Mock
	private GameController gameController;
	@InjectMocks
	private InExecutionState gameState;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
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
		gameState.reset();
		verify(gameController).toState(any(ResettingState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#execute()}.
	 */
	@Test
	public void testExecute() {
		gameState.execute();
		verify(gameController).handleCommand(any(GameWorldCommand.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#update()}.
	 */
	@Test
	public void testUpdate() {
		gameState.update();
		verify(gameController).toState(any(GameState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#InExecutionState(applicationLayer.GameController, domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testInExecutionState() {
		InExecutionState state = new InExecutionState(gameController, block);
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
		assertEquals(block,gameState.getNextActionBlockToBeExecuted());
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#setNextActionBlockToBeExecuted(domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testSetNextActionBlockToBeExecuted() {
		gameState.setNextActionBlockToBeExecuted(block);
		try {
			Field ActionBlock = InExecutionState.class.getDeclaredField("nextActionBlockToBeExecuted");
			ActionBlock.setAccessible(true);
			assertTrue("ActionBlock was not initialised", ActionBlock.get(gameState) != null);
		}catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

}
