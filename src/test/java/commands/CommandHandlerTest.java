package commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.verification.AtMost;

import applicationLayer.BlockController;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.IfBlock;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;

public class CommandHandlerTest {
	
//	@Mock(name="executedBlockCommands")
//	private Stack<Command> executedBlockCommands;
//	@Mock(name="undoneBlockCommands")
//	private Stack<Command> undoneBlockCommands;
//	@Mock(name="executedGameWorldCommands")
//	private Stack<Command> executedGameWorldCommands;
//	@Mock(name="undoneGameWorldCommands")
//	private Stack<Command> undoneGameWorldCommands;
	
	@Spy
	private Stack<Command> executedBlockCommands;
	@Spy
	private Stack<Command> undoneBlockCommands;
	@Spy
	private Stack<Command> executedGameWorldCommands;
	@Spy
	private Stack<Command> undoneGameWorldCommands;
	
	@Spy @InjectMocks
	private CommandHandler ch = new CommandHandler(executedBlockCommands, undoneBlockCommands, 
			executedGameWorldCommands, undoneGameWorldCommands);
	
	@Mock
	private AddBlockCommand addBlockCommand;
	@Mock
	private ResetCommand resetCommand;
	
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
	 * Test method for {@link commands.CommandHandler#CommandHandler()}.
	 */
	@Test
	public void testCommandHandler_Positive() {
		CommandHandler newCommandHandler = new CommandHandler();
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
	 * Test method for {@link commands.CommandHandler#handle(BlockCommand)}.
	 */
	@Test
	public void testHandle_BlockCommand_Positive() {
		Mockito.doNothing().when(addBlockCommand).execute();
		
		ch.handle(addBlockCommand);
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		assertTrue(undoneBlockCommands.empty());
		
		verify(addBlockCommand, atLeastOnce()).execute();
		
		assertTrue(executedBlockCommands.contains(addBlockCommand));
	}
	
	/**
	 * Test method for {@link commands.CommandHandler#handle(GameWorldCommand)}.
	 */
	@Test
	public void testHandle_GameWorldCommand_Positive() {
		Mockito.doNothing().when(resetCommand).execute();
		
		ch.handle(resetCommand);
		
		assertTrue(undoneGameWorldCommands.empty());
		assertTrue(executedGameWorldCommands.contains(resetCommand));		
	}
	
	/**
	 * Test method for {@link commands.CommandHandler#undo()}.
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
	 * Test method for {@link commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedGameWorldCommandsNotEmpty_Positive() {
		executedGameWorldCommands.push(resetCommand);
		
		ch.undo();
		
		verify(resetCommand,atLeastOnce()).undo();
		assertTrue(undoneGameWorldCommands.contains(resetCommand));
	}	
	
	/**
	 * Test method for {@link commands.CommandHandler#undo()}.
	 */
	@Test
	public void testUndo_ExecutedBlockCommandsNotEmpty_Positive() {
		executedBlockCommands.push(addBlockCommand);
		
		ch.undo();
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		
		verify(addBlockCommand,atLeastOnce()).undo();
		assertTrue(undoneBlockCommands.contains(addBlockCommand));
	}	
	
	
	
	/**
	 * Test method for {@link commands.CommandHandler#undo()}.
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
	 * Test method for {@link commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneGameWorldCommandsNotEmpty_Positive() {
		undoneGameWorldCommands.push(resetCommand);
		
		ch.redo();
		
		verify(resetCommand,atLeastOnce()).execute();
		assertTrue(executedGameWorldCommands.contains(resetCommand));
	}	
	
	/**
	 * Test method for {@link commands.CommandHandler#undo()}.
	 */
	@Test
	public void testRedo_UndoneBlockCommandsNotEmpty_Positive() {
		undoneBlockCommands.push(addBlockCommand);
		
		ch.redo();
		
		assertTrue(executedGameWorldCommands.empty());
		assertTrue(undoneGameWorldCommands.empty());
		
		verify(addBlockCommand,atLeastOnce()).execute();
		assertTrue(executedBlockCommands.contains(addBlockCommand));
	}	

	
}
