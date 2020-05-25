package guiLayer.commands;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;
import guiLayer.shapes.Shape;
import guiLayer.types.GuiSnapshot;

public class CommandHandlerTest {


	@Spy
	private Stack<Command> executedCommands;
	@Spy
	private Stack<Command> undoneCommands;


	@Mock
	private CanvasWindow canvas;

	BlockCommand mock = Mockito.mock(BlockCommand.class);

	@Spy
	@InjectMocks
	private CommandHandler ch = new CommandHandler(canvas, executedCommands, undoneCommands, mock);

	@Mock
	private BlockCommand blockCommand;
	@Mock
	private ResetCommand resetCommand;
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
	 * Test method for {@link guiLayer.commands.CommandHandler#CommandHandler()}.
	 */
	@Test
	public void testCommandHandler_Positive() {
		CommandHandler newCommandHandler = new CommandHandler(canvas);
		try {
			Field executedCommands = CommandHandler.class.getDeclaredField("executedCommands");
			executedCommands.setAccessible(true);
			assertTrue("executedCommands was not initialised", executedCommands.get(newCommandHandler) != null);

			Field undoneCommands = CommandHandler.class.getDeclaredField("undoneCommands");
			undoneCommands.setAccessible(true);
			assertTrue("undoneCommands was not initialised", undoneCommands.get(newCommandHandler) != null);

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#handle(BlockCommand)}.
	 */
	@Test
	public void testHandle_BlockCommand_Positive() {
		Mockito.doNothing().when(blockCommand).execute();

		ch.handle(blockCommand);

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.empty());

		verify(blockCommand, atLeastOnce()).execute();

		assertTrue(executedCommands.contains(blockCommand));
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#handle(BlockCommand)}.
	 */
	@Test
	public void testHandle_GuiMoveCommand_Positive() {
		undoneCommands.push(blockCommand);
		executedCommands.push(resetCommand);
		BlockCommand guiMoveCommand = Mockito.mock(GuiMoveCommand.class);
		Mockito.doNothing().when(guiMoveCommand).execute();
		ch.handle(guiMoveCommand);

		assertTrue(executedCommands.stream().anyMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.size() != 0);

		verify(guiMoveCommand, atLeastOnce()).execute();

		assertTrue(executedCommands.contains(guiMoveCommand));
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#handle(GameWorldCommand)}.
	 */
	@Test
	public void testHandle_GameWorldCommand_Positive() {
		Mockito.doNothing().when(resetCommand).execute();

		ch.handle(resetCommand);

		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(executedCommands.contains(resetCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setAddedId(String)}.
	 */
	@Test
	public void testSetAddedId_CurrentBlockCommandNull_Positive() {
		CommandHandler handler = new CommandHandler(canvas, executedCommands, undoneCommands, null);

		handler.setAddedId("0");

		verify(Mockito.mock(BlockCommand.class), Mockito.never()).setAddedID(Mockito.anyString());
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setAddedId(String)}.
	 */
	@Test
	public void testSetAddedId_CurrentBlockCommandNotNull_Positive() {
		ch.setAddedId("0");

		verify(mock, atLeastOnce()).setAddedID("0");
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#setHeight(String, int)}.
	 */
	@Test
	public void testSetHeight_CurrentBlockCommandNull_Positive() {
		CommandHandler handler = new CommandHandler(canvas, executedCommands, undoneCommands, null);

		handler.setHeight("0", 10);

		verify(Mockito.mock(BlockCommand.class), Mockito.never()).setAfterActionHeight("0", 10);
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#setHeight(String, int)}.
	 */
	@Test
	public void testSetHeight_CurrentBlockCommandNotNull_Positive() {
		ch.setHeight("0", 10);

		verify(mock, atLeastOnce()).setAfterActionHeight("0", 10);
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_BothCommandStacksEmpty_Positive() {
		executedCommands.clear();

		ch.undo();

		verify(Mockito.mock(GameWorldCommand.class), Mockito.never()).undo();
		verify(Mockito.mock(BlockCommand.class), Mockito.never()).undo();
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedGameWorldCommandsNotEmpty_Positive() {
		executedCommands.push(resetCommand);

		ch.undo();

		verify(resetCommand, atLeastOnce()).undo();
		assertTrue(undoneCommands.contains(resetCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedBlockCommandsNotEmpty_Positive() {
		executedCommands.push(blockCommand);

		ch.undo();

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));

		verify(blockCommand, atLeastOnce()).undo();
		assertTrue(undoneCommands.contains(blockCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedBlockCommandsNotEmpty_Positive_DomainMoveCommand() {
		DomainMoveCommand blockCommand = mock(DomainMoveCommand.class);

		executedCommands.push(blockCommand);

		ch.undo();

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));

		verify(blockCommand, atLeastOnce()).undo();
		assertTrue(undoneCommands.contains(blockCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_BothCommandStacksEmpty_Positive() {
		undoneCommands.clear();

		ch.redo();

		verify(Mockito.mock(GameWorldCommand.class), Mockito.never()).execute();
		verify(Mockito.mock(BlockCommand.class), Mockito.never()).execute();
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneGameWorldCommandsNotEmpty_Positive() {
		undoneCommands.push(resetCommand);

		ch.redo();

		verify(resetCommand, atLeastOnce()).execute();
		assertTrue(executedCommands.contains(resetCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneBlockCommandsNotEmpty_Positive() {
		undoneCommands.push(blockCommand);

		ch.redo();

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));

		verify(blockCommand, atLeastOnce()).execute();
		assertTrue(executedCommands.contains(blockCommand));
	}

	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneBlockCommandsNotEmpty_Positive_DomainMoveCommand() {
		DomainMoveCommand blockCommand = mock(DomainMoveCommand.class);
		undoneCommands.push(blockCommand);

		ch.redo();

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));

		verify(blockCommand, atLeastOnce()).execute();
		assertTrue(executedCommands.contains(blockCommand));
	}

	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#addShapeToBeforeSnapshot(guiLayer.shapes.Shape)}.
	 */
	@Test
	public void testAddShapeToBeforeSnapshot_CurrentlyHandledBlockCommandNotNull_Positive() {
		ch.addShapeToBeforeSnapshot(shape);

		verify(mock, atLeastOnce()).addShapeToBeforeSnapshot(shape);
	}
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#addShapeToBeforeSnapshot(guiLayer.shapes.Shape)}.
	 */
	@Test
	public void testAddShapeToBeforeSnapshot_CurrentlyHandledBlockCommandNull_Positive() {
		Stack<Command> executedCommands = new Stack<Command>();
		executedCommands.add(blockCommand);
		CommandHandler commandHandler = new CommandHandler(canvas, executedCommands, undoneCommands,  null);
		
		commandHandler.addShapeToBeforeSnapshot(shape);
		
		verify(blockCommand,atLeastOnce()).addShapeToBeforeSnapshot(shape);
	}
	/**
	 * Test method for
	 * {@link guiLayer.commands.CommandHandler#clearAllGameWorldCommands()}.
	 */
	@Test
	public void testclearAllGameWorldCommands() {
		undoneCommands.push(blockCommand);
		undoneCommands.push(resetCommand);
		executedCommands.push(blockCommand);
		executedCommands.push(resetCommand);

		ch.clearAllGameWorldCommands();

		assertTrue(executedCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));
		assertTrue(undoneCommands.stream().noneMatch(s -> s instanceof GameWorldCommand));

		assertTrue(executedCommands.contains(blockCommand));
		assertTrue(undoneCommands.contains(blockCommand));
	}

}
