package guiLayer.commands;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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
	private Stack<BlockCommand> executedBlockCommands;
	@Spy
	private Stack<BlockCommand> undoneBlockCommands;
	@Spy
	private Stack<GameWorldCommand> executedGameWorldCommands;
	@Spy
	private Stack<GameWorldCommand> undoneGameWorldCommands;
	@Mock
	private CanvasWindow canvas;
	
	BlockCommand mock = Mockito.mock(BlockCommand.class);
	
	@Spy @InjectMocks
	private CommandHandler ch = new CommandHandler(canvas, executedBlockCommands, undoneBlockCommands, 
			executedGameWorldCommands, undoneGameWorldCommands, mock);
	
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
			Field executedBlockCommands = CommandHandler.class.getDeclaredField("executedBlockCommands");
			executedBlockCommands.setAccessible(true);
			assertTrue("executedBlockCommands were not initialised", executedBlockCommands.get(newCommandHandler) != null);
			
			Field undoneBlockCommands = CommandHandler.class.getDeclaredField("undoneBlockCommands");
			undoneBlockCommands.setAccessible(true);
			assertTrue("undoneBlockCommands were not initialised", undoneBlockCommands.get(newCommandHandler) != null);

			Field executedGameWorldCommands = CommandHandler.class.getDeclaredField("executedGameWorldCommands");
			executedGameWorldCommands.setAccessible(true);
			assertTrue("executedGameWorldCommands were not initialised", executedGameWorldCommands.get(newCommandHandler) != null);

			Field undoneGameWorldCommands = CommandHandler.class.getDeclaredField("undoneGameWorldCommands");
			undoneGameWorldCommands.setAccessible(true);
			assertTrue("undoneGameWorldCommands were not initialised", undoneGameWorldCommands.get(newCommandHandler) != null);
		
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#handle(BlockCommand)}.
	 */
	@Test
	public void testHandle_BlockCommand_Positive() {
		Mockito.doNothing().when(blockCommand).execute();
		
		ch.handle(blockCommand);
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		assertTrue(undoneBlockCommands.empty());
		
		verify(blockCommand, atLeastOnce()).execute();
		
		assertTrue(executedBlockCommands.contains(blockCommand));
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#handle(GameWorldCommand)}.
	 */
	@Test
	public void testHandle_GameWorldCommand_Positive() {
		Mockito.doNothing().when(resetCommand).execute();
		
		ch.handle(resetCommand);
		
		assertTrue(undoneGameWorldCommands.empty());
		assertTrue(executedGameWorldCommands.contains(resetCommand));		
	}
	
	
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setAddedId(String)}.
	 */
	@Test
	public void testSetAddedId_CurrentBlockCommandNull_Positive() {
		CommandHandler handler = new CommandHandler(canvas, executedBlockCommands, undoneBlockCommands, 
				executedGameWorldCommands, undoneGameWorldCommands, null);
		
		handler.setAddedId("0");
		
		verify(Mockito.mock(BlockCommand.class),Mockito.never()).setAddedID(Mockito.anyString());
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setAddedId(String)}.
	 */
	@Test
	public void testSetAddedId_CurrentBlockCommandNotNull_Positive() {	
		ch.setAddedId("0");
		
		verify(mock,atLeastOnce()).setAddedID("0");
	}
	
	
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setHeight(String, int)}.
	 */
	@Test
	public void testSetHeight_CurrentBlockCommandNull_Positive() {
		CommandHandler handler = new CommandHandler(canvas, executedBlockCommands, undoneBlockCommands, 
				executedGameWorldCommands, undoneGameWorldCommands, null);
		
		handler.setHeight("0", 10);
		
		verify(Mockito.mock(BlockCommand.class),Mockito.never()).setAfterActionHeight("0", 10);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#setHeight(String, int)}.
	 */
	@Test
	public void testSetHeight_CurrentBlockCommandNotNull_Positive() {	
		ch.setHeight("0", 10);
		
		verify(mock,atLeastOnce()).setAfterActionHeight("0", 10);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_BothCommandStacksEmpty_Positive() {
		executedGameWorldCommands.clear();
		executedBlockCommands.clear();
		
		ch.undo();
		
		verify(Mockito.mock(GameWorldCommand.class),Mockito.never()).undo();
		verify(Mockito.mock(BlockCommand.class),Mockito.never()).undo();
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedGameWorldCommandsNotEmpty_Positive() {
		executedGameWorldCommands.push(resetCommand);
		
		ch.undo();
		
		verify(resetCommand,atLeastOnce()).undo();
		assertTrue(undoneGameWorldCommands.contains(resetCommand));
	}	
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedBlockCommandsNotEmpty_Positive() {
		executedBlockCommands.push(blockCommand);
		
		ch.undo();
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		
		verify(blockCommand,atLeastOnce()).undo();
		assertTrue(undoneBlockCommands.contains(blockCommand));
	}	
	
	
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_BothCommandStacksEmpty_Positive() {
		undoneGameWorldCommands.clear();
		undoneBlockCommands.clear();
		
		ch.redo();
		
		verify(Mockito.mock(GameWorldCommand.class),Mockito.never()).execute();
		verify(Mockito.mock(BlockCommand.class),Mockito.never()).execute();
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneGameWorldCommandsNotEmpty_Positive() {
		undoneGameWorldCommands.push(resetCommand);
		
		ch.redo();
		
		verify(resetCommand,atLeastOnce()).execute();
		assertTrue(executedGameWorldCommands.contains(resetCommand));
	}	
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneBlockCommandsNotEmpty_Positive() {
		undoneBlockCommands.push(blockCommand);
		
		ch.redo();
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		
		verify(blockCommand,atLeastOnce()).execute();
		assertTrue(executedBlockCommands.contains(blockCommand));
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#addShapeToBeforeSnapshot(guiLayer.shapes.Shape)}.
	 */
	@Test
	public void testAddShapeToBeforeSnapshot_CurrentlyHandledBlockCommandNotNull_Positive() {
		ch.addShapeToBeforeSnapshot(shape);
		
		verify(mock,atLeastOnce()).addShapeToBeforeSnapshot(shape);
	}
	
	/**
	 * Test method for {@link guiLayer.commands.CommandHandler#addShapeToBeforeSnapshot(guiLayer.shapes.Shape)}.
	 */
	@Test
	public void testAddShapeToBeforeSnapshot_CurrentlyHandledBlockCommandNull_Positive() {
		Stack<BlockCommand> executedBlockCommands = new Stack<BlockCommand>();
		executedBlockCommands.add(blockCommand);
		CommandHandler commandHandler = new CommandHandler(canvas, executedBlockCommands, undoneBlockCommands, 
				executedGameWorldCommands, undoneGameWorldCommands, null);
		
		commandHandler.addShapeToBeforeSnapshot(shape);
		
		verify(blockCommand,atLeastOnce()).addShapeToBeforeSnapshot(shape);
	}
	
}
