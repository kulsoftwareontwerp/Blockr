package guiLayer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
import guiLayer.shapes.Shape;
import guiLayer.types.GuiSnapshot;

@RunWith(MockitoJUnitRunner.class)
public class GuiMoveCommandTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Mock(name="canvas")
	private CanvasWindow canvas;
	@Mock(name="beforeSnapshot")
	private GuiSnapshot beforeSnapshot;
	@Mock(name="afterSnapshot")
	private GuiSnapshot afterSnapshot;
	
	@Spy @InjectMocks
	private GuiMoveCommand command;
	
	@Mock
	private Shape shape;

	
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
	
	
	// Tests for abstract class BlockCommand
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#BlockCommand(CanvasWindow, GuiSnapshot, GuiSnapshot)}.
	 */
	@Test
	public void testBlockCommand_CanvasNull_IllegalArgumentException() {
		String excMessage = "A BlockCommand needs a CanvasWindow.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			GuiMoveCommand command = new GuiMoveCommand(null, beforeSnapshot, afterSnapshot);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		GuiMoveCommand command = new GuiMoveCommand(null, beforeSnapshot, afterSnapshot);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#setAddedID(String)}.
	 */
	@Test
	public void testSetAddedID_BeforeSnapshotNotNull_AfterSnapshotNotNull_Positive() {
		command.setAddedID("id");
		
		verify(beforeSnapshot,atLeastOnce()).setID("id");
		verify(afterSnapshot,atLeastOnce()).setID("id");
	}
	
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#setAddedID(String)}.
	 */
	@Test
	public void testSetAddedID_BeforeSnapshotNull_AfterSnapshotNull_Positive() {
		GuiMoveCommand command = new GuiMoveCommand(canvas, null, null);
		
		command.setAddedID("id");
		
		verifyNoInteractions(beforeSnapshot);
		verifyNoInteractions(afterSnapshot);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#setAfterActionHeight(String, int)}.
	 */
	@Test
	public void testSetAfterActionHeight_AfterSnapshotNotNull_Positive() {
		command.setAfterActionHeight("id", 10);
		
		verify(afterSnapshot,atLeastOnce()).setHeight("id", 10);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#setAfterActionHeight(String, int)}.
	 */
	@Test
	public void testSetAfterActionHeight_AfterSnapshotNull_Positive() {
		GuiMoveCommand command = new GuiMoveCommand(canvas, beforeSnapshot, null);
		
		command.setAfterActionHeight("id", 10);
		
		verifyNoInteractions(afterSnapshot);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.BlockCommand#addShapeToBeforeSnapshot(guiLayer.shapes.Shape)}.
	 */
	@Test
	public void testAddShapeToBeforeSnapshot_Positive() {
		command.addShapeToBeforeSnapshot(shape);
		
		verify(beforeSnapshot,atLeastOnce()).addShapeToSnapshot(shape);
	}
}
