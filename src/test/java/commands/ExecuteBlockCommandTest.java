package commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import domainLayer.blocks.ActionBlock;
import types.ExecutionSnapshot;

public class ExecuteBlockCommandTest {
	
	@Mock(name="gameController")
	private GameController gameController;
	@Mock(name="block")
	private ActionBlock actionBlock;
	@Spy @InjectMocks
	private ExecuteBlockCommand command;
	
	@Mock
	private ExecutionSnapshot snapshotMock;

	
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
	 * Test method for {@link commands.ExecuteBlockCommand#ExecuteBlockCommand(GameController, ActionBlock)}.
	 */
	@Test
	public void testExecuteBlockCommand_Positive() {
		try {
			Field gameControllerField = ExecuteBlockCommand.class.getDeclaredField("gameController");
			gameControllerField.setAccessible(true);
			assertTrue("gameController was not initialised", gameControllerField.get(command) != null);

			Field blockField = ExecuteBlockCommand.class.getDeclaredField("block");
			blockField.setAccessible(true);
			assertTrue("block was not initialised", blockField.get(command) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link commands.ExecuteBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_Positive() {
		when(gameController.performAction(actionBlock)).thenReturn(snapshotMock);
		
		command.execute();
		
		try {
			Field executionSnapshotField = ExecuteBlockCommand.class.getDeclaredField("snapshot");
			executionSnapshotField.setAccessible(true);
			assertEquals(snapshotMock, executionSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
		
	}
	
	/**
	 * Test method for {@link commands.ExecuteBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNull_Positive() {
		command.undo();
		
		Mockito.verifyNoMoreInteractions(gameController);
	}
	
	/**
	 * Test method for {@link commands.ExecuteBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNotNull_Positive() {
		// Workaround to make snapshot not null
		when(gameController.performAction(actionBlock)).thenReturn(snapshotMock);
		command.execute();
		
		command.undo();
		
		verify(gameController,atLeastOnce()).restoreExecutionSnapshot(snapshotMock);
		try {
			Field executionSnapshotField = ExecuteBlockCommand.class.getDeclaredField("snapshot");
			executionSnapshotField.setAccessible(true);
			assertEquals(null, executionSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}		
	}
}
