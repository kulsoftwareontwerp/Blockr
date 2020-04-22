/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
 * ValidProgramStateTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidProgramStateTest {

	@Mock
	private ActionBlock block;
	@Mock
	private GameController gameController;
	@InjectMocks
	private ValidProgramState gameState;
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
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#execute()}.
	 */
	@Test
	public void testExecute() {
		when(gameController.findFirstBlockToBeExecuted()).thenReturn(block);
		gameState.execute();
		verify(gameController).toState(any(GameState.class));
		
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#update()}.
	 */
	@Test
	public void testUpdate() {
		when(gameController.checkIfValidProgram()).thenReturn(false);
		gameState.update();
		verify(gameController).checkIfValidProgram();
		verify(gameController).toState(any(GameState.class));
	}
	
	@Test
	public void testUpdateNegative() {
		when(gameController.checkIfValidProgram()).thenReturn(true);
		gameState.update();
		verify(gameController).checkIfValidProgram();
		verify(gameController,times(0)).toState(any(GameState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#ValidProgramState(applicationLayer.GameController)}.
	 */
	@Test
	public void testValidProgramState() {
		ValidProgramState vps = new ValidProgramState(gameController);
		try {
			Field gameController = InExecutionState.class.getSuperclass().getDeclaredField("gameController");
			gameController.setAccessible(true);
			assertTrue("gameController was not initialised", gameController.get(vps) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

}
