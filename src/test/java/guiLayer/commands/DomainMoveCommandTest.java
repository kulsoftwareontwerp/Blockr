package guiLayer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.objenesis.instantiator.annotations.Instantiator;

import applicationLayer.BlockController;
import applicationLayer.DomainController;
import commands.AddBlockCommand;
import domainLayer.blocks.Block;
import domainLayer.blocks.IfBlock;
import guiLayer.CanvasWindow;
import guiLayer.types.GuiSnapshot;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;

@RunWith(MockitoJUnitRunner.class)
public class DomainMoveCommandTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Mock(name="controller")
	private DomainController controller;
	@Mock(name="canvas")
	private CanvasWindow canvas;
	@Mock(name="beforeSnapshot")
	private GuiSnapshot beforeSnapshot;
	@Mock(name="afterSnapshot")
	private GuiSnapshot afterSnapshot;
	
	@Spy @InjectMocks
	private DomainMoveCommand command;

	
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
	public void testDomainMoveCommand_ControllerNotNull_Positive() {
		try {
			Field beforeSnapshotField = DomainMoveCommand.class.getSuperclass().getDeclaredField("beforeSnapshot");
			beforeSnapshotField.setAccessible(true);
			assertTrue("beforeSnapshot was not initialised", beforeSnapshotField.get(command) != null);

			Field afterSnapshotField = DomainMoveCommand.class.getSuperclass().getDeclaredField("afterSnapshot");
			afterSnapshotField.setAccessible(true);
			assertTrue("afterSnapshot was not initialised", afterSnapshotField.get(command) != null);

			Field controllerField = DomainMoveCommand.class.getDeclaredField("controller");
			controllerField.setAccessible(true);
			assertTrue("controller was not initialised", controllerField.get(command) != null);

			Field canvasField = DomainMoveCommand.class.getDeclaredField("canvas");
			canvasField.setAccessible(true);
			assertTrue("canvas was not initialised", canvasField.get(command) != null);

			Field executedField = DomainMoveCommand.class.getDeclaredField("executed");
			executedField.setAccessible(true);
			assertFalse("executed boolean was not initialised", (boolean) executedField.get(command));

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link guiLayer.commands.DomainMoveCommand#DomainMoveCommand(DomainController, CanvasWindow, GuiSnapshot, GuiSnapshot)}.
	 */
	@Test
	public void testDomainMoveCommand_ControllerNull_IllegalArgumentException() {		
		String excMessage = "A DomainMoveCommand needs a DomainController.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			DomainMoveCommand command = new DomainMoveCommand(null, canvas, beforeSnapshot, afterSnapshot);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		DomainMoveCommand command = new DomainMoveCommand(null, canvas, beforeSnapshot, afterSnapshot);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.DomainMoveCommand#execute()}.
	 */
	@Test
	public void testExecute_ExecutedFalse_Positive() {
		command.execute();
		
		try {
			Field executedField = DomainMoveCommand.class.getDeclaredField("executed");
			executedField.setAccessible(true);
			assertTrue("executed boolean was not initialised", (boolean) executedField.get(command));

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link guiLayer.commands.DomainMoveCommand#execute()}.
	 */
	@Test
	public void testExecute_ExecutedTrue_Positive() {
		// Quick workaround to set executed boolean true
		command.execute();
		
		command.execute();

		verify(controller,atLeastOnce()).redo();
	}
	
	/**
	 * Test method for {@link guiLayer.commands.DomainMoveCommand#undo()}.
	 */
	@Test
	public void testUndo_Positive() {
		command.undo();

		verify(controller,atLeastOnce()).undo();
		verify(canvas,atLeastOnce()).setCurrentSnapshot(null);
	}
}











