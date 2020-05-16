package guiLayer.commands;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
public class GuiMoveCommandTest {

	@Mock(name="canvas")
	private CanvasWindow canvas;
	@Mock(name="beforeSnapshot")
	private GuiSnapshot beforeSnapshot;
	@Mock(name="afterSnapshot")
	private GuiSnapshot afterSnapshot;
	
	@Spy @InjectMocks
	private GuiMoveCommand command;

	
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
	 * Test method for {@link guiLayer.commands.GuiMoveCommand#GuiMoveCommand(DomainController, CanvasWindow, GuiSnapshot, GuiSnapshot)}.
	 */
	@Test
	public void testGuiMoveCommand_Positive() {
		try {
			Field beforeSnapshotField = GuiMoveCommand.class.getSuperclass().getDeclaredField("beforeSnapshot");
			beforeSnapshotField.setAccessible(true);
			assertTrue("beforeSnapshot was not initialised", beforeSnapshotField.get(command) != null);

			Field afterSnapshotField = GuiMoveCommand.class.getSuperclass().getDeclaredField("afterSnapshot");
			afterSnapshotField.setAccessible(true);
			assertTrue("afterSnapshot was not initialised", afterSnapshotField.get(command) != null);

			Field canvasField = GuiMoveCommand.class.getDeclaredField("canvas");
			canvasField.setAccessible(true);
			assertTrue("canvas was not initialised", canvasField.get(command) != null);
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

	/**
	 * Test method for {@link guiLayer.commands.GuiMoveCommand#execute()}.
	 */
	@Test
	public void testExecute_Positive() {
		command.execute();
		
		verify(canvas,atLeastOnce()).placeShapes();
		verify(canvas,atLeastOnce()).setCurrentSnapshot(null);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.GuiMoveCommand#undo()}.
	 */
	@Test
	public void testUndo_Positive() {
		command.undo();
		
		verify(canvas,atLeastOnce()).placeShapes();
		verify(canvas,atLeastOnce()).setCurrentSnapshot(null);
	}
}
