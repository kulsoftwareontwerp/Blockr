/**
 * 
 */
package applicationLayer;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import commands.AddBlockCommand;
import commands.BlockCommand;
import commands.CommandHandler;
import commands.RemoveBlockCommand;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;

/**
 * DomainControllerTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class DomainControllerTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Mock(name = "gameWorld")
	private GameWorld gameWorld;
	@Mock(name = "gameController")
	private GameController gameController;
	@Mock(name = "blockController")
	private BlockController blockController;
	@Mock(name = "commandHandler")
	private CommandHandler commandHandler;
	@Spy
	@InjectMocks
	private DomainController dc;

	@Captor
	ArgumentCaptor<AddBlockCommand> addBlockCommandCaptor = ArgumentCaptor.forClass(AddBlockCommand.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#DomainController(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDomainController() {
		fail("Not yet implemented");
	}

	/*
	 * BEGIN Add Block Tests
	 */

	private void assertExceptionDCAddBlockCombination(BlockType bt, String cb, ConnectionType ct, String excMessage) {
		boolean pass = false;
		try {
			dc.addBlock(bt, cb, ct);
		} catch (IllegalArgumentException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("addBlock failed in the domainController for combination: BlockType=" + bt.toString()
				+ " ConnectedBlockId=" + cb + " ConnectionType=" + ct.toString(), pass);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeNoBlockType() {
		String excMessage = "No blockType given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (ConnectionType c : ConnectionType.values()) {
			dc.addBlock(null, "", c);
			assertExceptionDCAddBlockCombination(null, "", c, excMessage);
			verifyNoInteractions(commandHandler);
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectedBlockNoConnection() {
		String excMessage = "No connection given for connected block.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			dc.addBlock((BlockType) b, "connectedBlockId", ConnectionType.NOCONNECTION);
			assertExceptionDCAddBlockCombination((BlockType) b, "connectedBlockId", ConnectionType.NOCONNECTION,
					excMessage);
			verifyNoInteractions(commandHandler);
		}

	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectionTypeNull() {
		String excMessage = "Null given as connection, use ConnectionType.NOCONNECTION.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		dc.addBlock(BlockType.IF, "connectedBlockId", null);
		verifyNoInteractions(commandHandler);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectionNoConnectedBlock() {
		String excMessage = "No connected block given with connection.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				dc.addBlock((BlockType) b, null, c);
				assertExceptionDCAddBlockCombination((BlockType) b, null, c, excMessage);
				verifyNoInteractions(commandHandler);
			}
		}

	}
	
	

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockPositiveNoConnectedBlock() {
		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			dc.addBlock((BlockType) b, "", ConnectionType.NOCONNECTION);

			verify(commandHandler,atLeastOnce()).handle(addBlockCommandCaptor.capture());
			BlockCommand command = addBlockCommandCaptor.getValue();

			try {
				Field f;
				f = AddBlockCommand.class.getDeclaredField("blockType");
				f.setAccessible(true);
				assertEquals(b, f.get(command));
				f = AddBlockCommand.class.getDeclaredField("connection");
				f.setAccessible(true);
				assertEquals(ConnectionType.NOCONNECTION, f.get(command));
				f = AddBlockCommand.class.getDeclaredField("connectedBlockId");
				f.setAccessible(true);
				assertEquals("", f.get(command));

			} catch (Exception e) {
				System.out.println("exception at field injection");
			}

		}

	}

	/*
	 * END Add Block Tests
	 */

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#removeBlock(java.lang.String)}.
	 */
	@Test
	public void testRemoveBlock_BlockIdEmptyString_IllegalArgumentException() {
		String excMessage = "No blockType given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		try {
			dc.removeBlock("");
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}

		Mockito.verifyNoInteractions(commandHandler);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#removeBlock(java.lang.String)}.
	 */
	@Test
	public void testRemoveBlock_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No blockType given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		try {
			dc.removeBlock(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}

		Mockito.verifyNoInteractions(commandHandler);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#removeBlock(java.lang.String)}.
	 */
	@Test
	public void testRemoveBlock_Positive() {
		dc.removeBlock("AnyBlockId");

		verify(commandHandler, atLeastOnce()).handle(Mockito.any(RemoveBlockCommand.class));
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#resetGameExecution()}.
	 */
	@Test
	public void testResetGameExecution_Positive() {
		dc.resetGameExecution();
		verify(gameController, atLeastOnce()).resetGameExecution();
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addGameListener(events.GUIListener)}.
	 */
	@Test
	public void testAddGameListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#removeGameListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveGameListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#executeBlock()}.
	 */
	@Test
	public void testExecuteBlock_Positive() {
		dc.executeBlock();
		verify(gameController, atLeastOnce()).executeBlock();
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getFirstBlockBelow(java.lang.String)}.
	 */
	@Test
	public void testGetFirstBlockBelow() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint() {
		fail("Not yet implemented");
	}

}
