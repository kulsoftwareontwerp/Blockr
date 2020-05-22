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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import applicationLayer.BlockController;
import domainLayer.blocks.Block;
import domainLayer.blocks.IfBlock;
import types.BlockSnapshot;
import types.ConnectionType;

public class MoveBlockCommandTest {
	
	@Mock(name="blockController")
	private BlockController blockController;
	private String topOfMovedChainBlockId;
	private String movedBlockId;
	private String connectedAfterMoveBlockId;
	private ConnectionType connectionAfterMove;
	private MoveBlockCommand command;
	
	@Mock
	private BlockSnapshot snapshotMock;

	private Block block = new IfBlock("blockId");
	private Set<Block> blocksSet = new HashSet<Block>();

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		topOfMovedChainBlockId = "topOfMovedChainBlockId";
		movedBlockId = "movedBlockId";
		connectedAfterMoveBlockId = "connectedAfterMoveBlockId";
		connectionAfterMove = ConnectionType.DOWN;
		blocksSet.add(block);
		
		MockitoAnnotations.initMocks(this);
		
		command = new MoveBlockCommand(blockController, topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link commands.MoveBlockCommand#MoveBlockCommand(BlockController, String, String, String, ConnectionType)}.
	 */
	@Test
	public void testMoveBlockCommand_Positive() {
		//MoveBlockCommand command = new MoveBlockCommand(blockController, topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove);
		try {
			Field blockControllerField = MoveBlockCommand.class.getDeclaredField("blockController");
			blockControllerField.setAccessible(true);
			assertTrue("blockController was not initialised", blockControllerField.get(command) != null);

			Field topOfMovedChainBlockIdField = MoveBlockCommand.class.getDeclaredField("topOfMovedChainBlockId");
			topOfMovedChainBlockIdField.setAccessible(true);
			assertTrue("topOfMovedChainBlockIdField was not initialised", topOfMovedChainBlockIdField.get(command) != null);

			Field movedBlockIdField = MoveBlockCommand.class.getDeclaredField("movedBlockId");
			movedBlockIdField.setAccessible(true);
			assertTrue("movedBlockId was not initialised", movedBlockIdField.get(command) != null);

			Field connectedAfterMoveBlockIdField = MoveBlockCommand.class.getDeclaredField("connectedAfterMoveBlockId");
			connectedAfterMoveBlockIdField.setAccessible(true);
			assertTrue("connectedAfterMoveBlockId was not initialised", connectedAfterMoveBlockIdField.get(command) != null);

			Field connectionAfterMoveField = MoveBlockCommand.class.getDeclaredField("connectionAfterMove");
			connectionAfterMoveField.setAccessible(true);
			assertTrue("connectionAfterMove was not initialised", connectionAfterMoveField.get(command) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link commands.MoveBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_Positive() {
		when(blockController.moveBlock(topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove)).thenReturn(snapshotMock);
		
		command.execute();
		
		try {
			Field executionSnapshotField = MoveBlockCommand.class.getDeclaredField("snapshot");
			executionSnapshotField.setAccessible(true);
			assertEquals(snapshotMock, executionSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
		
	}
	
	/**
	 * Test method for {@link commands.MoveBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNull_Positive() {
		command.undo();
		
		Mockito.verifyNoMoreInteractions(blockController);
	}
	
	/**
	 * Test method for {@link commands.MoveBlockCommand#undo()}.
	 */
	@Test
	public void testUndo_SnapshotNotNull_Positive() {
		// Workaround to make snapshot not null
		when(blockController.moveBlock(topOfMovedChainBlockId, movedBlockId, connectedAfterMoveBlockId, connectionAfterMove)).thenReturn(snapshotMock);
		command.execute();
		
		when(snapshotMock.getBlock()).thenReturn(block);
		when(snapshotMock.getConnectedBlockBeforeSnapshot()).thenReturn(block);
		when(snapshotMock.getChangingBlocks()).thenReturn(blocksSet);
		
		command.undo();
		
		try {
			Field blockSnapshotField = MoveBlockCommand.class.getDeclaredField("snapshot");
			blockSnapshotField.setAccessible(true);
			assertEquals(null, blockSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}		
	}
}
