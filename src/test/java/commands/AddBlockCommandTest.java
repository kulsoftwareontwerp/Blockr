package commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import applicationLayer.BlockController;
import domainLayer.blocks.Block;
import domainLayer.blocks.IfBlock;
import domainLayer.gamestates.InExecutionState;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;

public class AddBlockCommandTest {
	
	@Mock(name="blockController")
	private BlockController blockController;
	private BlockType blockType;
	private String connectedBlockId;
	private ConnectionType connectionType;
	
	@Mock
	private BlockSnapshot snapshotMock;
	private Block block = new IfBlock("blockId");

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		blockType = BlockType.IF;
		connectedBlockId = "0";
		connectionType = ConnectionType.DOWN;
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link commands.AddBlockCommand#AddBlockCommand(BlockController, BlockType, String, ConnectionType)}.
	 */
	@Test
	public void testAddBlockCommand_Positive() {
		AddBlockCommand command = new AddBlockCommand(blockController, blockType, connectedBlockId, connectionType);
		try {
			Field blockControllerField = AddBlockCommand.class.getDeclaredField("blockController");
			blockControllerField.setAccessible(true);
			assertTrue("blockController was not initialised", blockControllerField.get(command) != null);

			Field blockTypeField = AddBlockCommand.class.getDeclaredField("blockType");
			blockTypeField.setAccessible(true);
			assertTrue("blockType was not initialised", blockTypeField.get(command) != null);

			Field connectedBlockIdField = AddBlockCommand.class.getDeclaredField("connectedBlockId");
			connectedBlockIdField.setAccessible(true);
			assertTrue("connectedBlockId was not initialised", connectedBlockIdField.get(command) != null);

			Field connectionField = AddBlockCommand.class.getDeclaredField("connection");
			connectionField.setAccessible(true);
			assertTrue("connection was not initialised", connectionField.get(command) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}
	
	/**
	 * Test method for {@link commands.AddBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_SnapshotNull_Positive() {
		AddBlockCommand command = new AddBlockCommand(blockController, blockType, connectedBlockId, connectionType);
		
		when(blockController.addBlock(blockType, connectedBlockId, connectionType)).thenReturn(snapshotMock);
		
		command.execute();
		
		try {
			Field blockSnapshotField = AddBlockCommand.class.getDeclaredField("snapshot");
			blockSnapshotField.setAccessible(true);
			assertEquals(snapshotMock, blockSnapshotField.get(command));
			
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
		
	}
	
	/**
	 * Test method for {@link commands.AddBlockCommand#execute()}.
	 */
	@Test
	public void testExecute_SnapshotNotNull_Positive() {
		AddBlockCommand command = new AddBlockCommand(blockController, blockType, connectedBlockId, connectionType);
		
		// Workaround to make snapshot not null
		when(blockController.addBlock(blockType, connectedBlockId, connectionType)).thenReturn(snapshotMock);
		command.execute();
		
		command.execute();
		
		verify(blockController,atLeastOnce()).restoreBlockSnapshot(snapshotMock, false);
	}
	
	/**
	 * Test method for {@link commands.AddBlockCommand#execute()}.
	 */
	@Test
	public void testUndo_Positive() {
		AddBlockCommand command = new AddBlockCommand(blockController, blockType, connectedBlockId, connectionType);
		
		when(snapshotMock.getBlock()).thenReturn(block);
		
		// Workaround to make snapshot not null
		when(blockController.addBlock(blockType, connectedBlockId, connectionType)).thenReturn(snapshotMock);
		command.execute();
		
		command.undo();

		verify(blockController,atLeastOnce()).removeBlock("blockId", false);
	}
}

















