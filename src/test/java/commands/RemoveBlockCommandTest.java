package commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import applicationLayer.BlockController;
import domainLayer.blocks.Block;
import domainLayer.blocks.IfBlock;
import types.BlockSnapshot;

public class RemoveBlockCommandTest {
	
	@Mock(name="blockController")
	private BlockController blockController;
	private String blockIdToBeRemoved;
	@Spy @InjectMocks
	private RemoveBlockCommand command;
	
	@Mock
	private BlockSnapshot snapshotMock;
	private Block block = new IfBlock("blockId");
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		blockIdToBeRemoved = "blockIdToBeRemoved";
		
		MockitoAnnotations.initMocks(this);
		
		command = new RemoveBlockCommand(blockController, blockIdToBeRemoved);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link commands.RemoveBlockCommand#RemoveBlockCommand(BlockController, String)}.
	 */
	@Test
	public void testRemoveBlockCommand_Positive() {
		try {
			Field blockControllerField = RemoveBlockCommand.class.getDeclaredField("blockController");
			blockControllerField.setAccessible(true);
			assertTrue("blockController was not initialised", blockControllerField.get(command) != null);

			Field blockIdToBeRemovedField = RemoveBlockCommand.class.getDeclaredField("blockIdToBeRemoved");
			blockIdToBeRemovedField.setAccessible(true);
			assertTrue("blockIdToBeRemoved was not initialised", blockIdToBeRemovedField.get(command) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link commands.RemoveBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_Positive() {
		when(blockController.removeBlock(blockIdToBeRemoved, true)).thenReturn(snapshotMock);
		
		command.execute();
		
		try {
			Field blockSnapshotField = RemoveBlockCommand.class.getDeclaredField("snapshot");
			blockSnapshotField.setAccessible(true);
			assertEquals(snapshotMock, blockSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
		
	}
	
	/**
	 * Test method for {@link commands.RemoveBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNull_Positive() {
		command.undo();
		
		Mockito.verifyNoMoreInteractions(blockController);
	}

	/**
	 * Test method for {@link commands.RemoveBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNotNull_Positive() {
		// Workaround to make snapshot not null
		when(blockController.removeBlock(blockIdToBeRemoved, true)).thenReturn(snapshotMock);
		command.execute();
		
		Set<BlockSnapshot> associatedSnapshots = new HashSet<BlockSnapshot>();
		associatedSnapshots.add(snapshotMock);
		
		when(snapshotMock.getAssociatedSnapshots()).thenReturn(associatedSnapshots);
		when(snapshotMock.getBlock()).thenReturn(block);
		
		command.undo();
		
		try {
			Field blockSnapshotField = RemoveBlockCommand.class.getDeclaredField("snapshot");
			blockSnapshotField.setAccessible(true);
			assertEquals(null, blockSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}		
	}
}
