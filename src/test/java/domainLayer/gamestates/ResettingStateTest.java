/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import applicationLayer.GameController;
import commands.ResetCommand;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.GameController;
import commands.CommandHandler;
import commands.GameWorldCommand;
import domainLayer.blocks.ActionBlock;

/**
 * ResettingStateTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class ResettingStateTest {


	@Mock(name="gameController")
	private GameController gameController;
	@Spy @InjectMocks
	private ResettingState gameState;

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
	 * Test method for {@link domainLayer.gamestates.ResettingState#reset()}.
	 */
	@Test
	public void testReset_UpdatedFalse_Positive() {
		gameState.setUpdated(false);
		
		gameState.reset();
		
		verify(gameController,atLeastOnce()).handleCommand(Mockito.any(ResetCommand.class));
		assertFalse(gameState.getUpdated());
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#reset()}.
	 */
	@Test
	public void testReset() {
		gameState.reset();
		verify(gameController).handleCommand(any(GameWorldCommand.class));
	}
	
	@Test
	public void testResetNegative() {
		gameState.update();//putting updated on true
		gameState.reset();
		verify(gameController).resetGame();
		try {
			Field updated = ResettingState.class.getDeclaredField("updated");
			updated.setAccessible(true);
			assertTrue("gameController was not initialised", updated.get(gameState) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	@Test
	public void testReset_UpdatedTrue_Positive() {
		gameState.setUpdated(true);
		
		gameState.reset();
		
		verify(gameController,atLeastOnce()).resetGame();
		assertFalse(gameState.getUpdated());

	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#update()}.
	 */
	@Test
	public void testUpdate() {
		when(gameController.checkIfValidProgram()).thenReturn(true);
		gameState.update();//putting updated on true
		verify(gameController).checkIfValidProgram();
		try {
			Field nextState = ResettingState.class.getDeclaredField("nextState");
			nextState.setAccessible(true);
			assertTrue("gameController was not initialised", nextState.get(gameState) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	@Test
	public void testUpdateNegative() {
		when(gameController.checkIfValidProgram()).thenReturn(false);
		gameState.update();//putting updated on true
		verify(gameController).checkIfValidProgram();
		try {
			Field nextState = ResettingState.class.getDeclaredField("nextState");
			nextState.setAccessible(true);
			assertTrue("gameController was not initialised", nextState.get(gameState) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#ResettingState(applicationLayer.GameController)}.
	 */
	@Test
	public void testResettingState() {
		gameState.reset();
		verify(gameController).handleCommand(any(GameWorldCommand.class));
	}
	
	@Test
	public void testResettingStateNegative() {
		gameState.update();
		gameState.reset();
		verify(gameController).resetGame();
		try {
			Field updated = ResettingState.class.getDeclaredField("updated");
			updated.setAccessible(true);
			assertTrue("gameController was not initialised", updated.get(gameState) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	@Test
	public void testGetNextState() {
		assertTrue(null != gameState.getNextState());
	}

}
