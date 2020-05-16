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
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;
import types.ExecutionSnapshot;

public class ResetCommandTest {
	
	@Mock(name="gameController")
	private GameController gameController;
	@Spy @InjectMocks
	private ResetCommand command;
	
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
	 * Test method for {@link commands.ResetCommand#ExecuteBlockCommand(GameController, ActionBlock)}.
	 */
	@Test
	public void testResetCommand_Positive() {
		try {
			Field gameControllerField = ResetCommand.class.getDeclaredField("gameController");
			gameControllerField.setAccessible(true);
			assertTrue("gameController was not initialised", gameControllerField.get(command) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link commands.ResetCommand#execute()}.
	 */
	@Test
	public void testExecute_Positive() {
		when(gameController.resetGame()).thenReturn(snapshotMock);
		
		command.execute();
		
		try {
			Field executionSnapshotField = ResetCommand.class.getDeclaredField("snapshot");
			executionSnapshotField.setAccessible(true);
			assertEquals(snapshotMock, executionSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
		
	}
	
	/**
	 * Test method for {@link commands.ResetCommand#undo()}.
	 */
	@Test
	public void testUndo_Positive() {
		command.undo();
		
		verify(gameController,atLeastOnce()).restoreExecutionSnapshot(null);
	}
}
