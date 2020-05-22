package guiLayer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;
import guiLayer.types.GuiSnapshot;

@RunWith(MockitoJUnitRunner.class)
public class ResetCommandTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Mock(name="controller")
	private DomainController controller;
	
	@Spy @InjectMocks
	private ResetCommand command;

	
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
	 * Test method for {@link guiLayer.commands.DomainMoveCommand#DomainMoveCommand(DomainController, CanvasWindow, GuiSnapshot, GuiSnapshot)}.
	 */
	@Test
	public void testResetCommand_Positive() {
		try {
			Field controllerField = ResetCommand.class.getDeclaredField("controller");
			controllerField.setAccessible(true);
			assertTrue("controller was not initialised", controllerField.get(command) != null);

			Field executedField = ResetCommand.class.getDeclaredField("executed");
			executedField.setAccessible(true);
			assertFalse("executed boolean was not initialised", (boolean) executedField.get(command));

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link guiLayer.commands.ExecuteBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_ExecutedFalse_Positive() {
		command.execute();
		
		verify(controller,atLeastOnce()).resetGameExecution();
		
		try {
			Field executedField = ResetCommand.class.getDeclaredField("executed");
			executedField.setAccessible(true);
			assertTrue("executed boolean was not initialised", (boolean) executedField.get(command));

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link guiLayer.commands.ExecuteBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_ExecutedTrue_Positive() {
		// Quick workaround to set executed boolean true
		command.execute();
		
		command.execute();

		verify(controller,atLeastOnce()).redo();
	}
	
	
	// Tests for GameWorldCommand
	/**
	 * Test method for {@link guiLayer.commands.GameWorldCommand#GameWorldCommand(DomainController)}.
	 */
	@Test
	public void testGameWorldCommand_ControllerNull_IllegalArgumentException() {		
		String excMessage = "A GameWorldCommand needs a DomainController.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			ResetCommand command = new ResetCommand(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		ResetCommand command = new ResetCommand(null);
	}

	/**
	 * Test method for {@link guiLayer.commands.GameWorldCommand#undo()}.
	 */
	@Test
	public void testGameWorldCommandUndo_Positive() {
		command.undo();
		
		verify(controller,atLeastOnce()).undo();
	}
}
