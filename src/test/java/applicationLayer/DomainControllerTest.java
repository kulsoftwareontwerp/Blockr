/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

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

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import commands.AddBlockCommand;
import commands.BlockCommand;
import commands.CommandHandler;
import commands.RemoveBlockCommand;

import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;
import events.GUIListener;
import types.ConnectionType;

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

	
	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private GameWorldType mockGameWorldType;
	@Mock
	private Predicate mockPredicate;
	@Mock
	private Action mockAction;
	
	
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
		when(gameWorld.getType()).thenReturn(mockGameWorldType);
		Set<Predicate> supportedPredicates = new HashSet<Predicate>();
		supportedPredicates.add(mockPredicate);
		when(mockGameWorldType.supportedPredicates()).thenReturn(supportedPredicates);
		Set<Action> supportedActions = new HashSet<Action>();
		supportedActions.add(mockAction);
		when(mockGameWorldType.supportedActions()).thenReturn(supportedActions);
		new DomainController(gameWorld);
	}

	/*
	 * BEGIN Add Block Tests
	 */

	private void assertExceptionDCAddBlockCombination(BlockType bt, String cb, ConnectionType ct, String excMessage) {
		boolean pass = false;
		try {
			dc.addBlock(bt, null, cb, ct);
		} catch (IllegalArgumentException e) {
			pass = e.getMessage().equals(excMessage);
		}
		assertTrue("addBlock failed in the domainController for combination: BlockType=" + bt.toString()
				+ " ConnectedBlockId=" + cb + " ConnectionType=" + ct.toString(), pass);
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeNoBlockType() {
		String excMessage = "No blockType given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (ConnectionType c : ConnectionType.values()) {
			dc.addBlock(null, null, "", c);
			assertExceptionDCAddBlockCombination(null, "", c, excMessage);
			verifyNoInteractions(commandHandler);
		}
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectedBlockNoConnection() {
		String excMessage = "No connection given for connected block.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			dc.addBlock((BlockType) b, null, "connectedBlockId", ConnectionType.NOCONNECTION);
			assertExceptionDCAddBlockCombination((BlockType) b, "connectedBlockId", ConnectionType.NOCONNECTION,
					excMessage);
			verifyNoInteractions(commandHandler);
		}

	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectionTypeNull() {
		String excMessage = "Null given as connection, use ConnectionType.NOCONNECTION.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		dc.addBlock(BlockType.IF, null, "connectedBlockId", null);
		verifyNoInteractions(commandHandler);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockNegativeConnectionNoConnectedBlock() {
		String excMessage = "No connected block given with connection.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);

		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				dc.addBlock((BlockType) b, null, null, c);
				assertExceptionDCAddBlockCombination((BlockType) b, null, c, excMessage);
				verifyNoInteractions(commandHandler);
			}
		}

	}
	
	

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#addBlock(types.BlockType, String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockPositiveNoConnectedBlock() {
		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			dc.addBlock((BlockType) b, null, "", ConnectionType.NOCONNECTION);

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
		
		dc.removeBlock("");
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
		
		dc.removeBlock(null);
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
	 * Test method for {@link applicationLayer.DomainController#executeBlock()}.
	 */
	@Test
	public void testExecuteBlock_Positive() {
		dc.executeBlock();
		verify(gameController,atLeastOnce()).executeBlock();
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#resetGameExecution()}.
	 */
	@Test
	public void testResetGameExecution_Positive() {
		dc.resetGameExecution();
		verify(gameController, atLeastOnce()).resetGameExecution();
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_Positive() {
		dc.paint(null);
		verify(gameWorld,atLeastOnce()).paint(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#undo()}.
	 */
	@Test
	public void testUndo_Positive() {
		dc.undo();
		verify(commandHandler,atLeastOnce()).undo();
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#redo()}.
	 */
	@Test
	public void testRedo_Positive() {
		dc.redo();
		verify(commandHandler,atLeastOnce()).redo();
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		Set<String> expected = new HashSet<String>();
		when(blockController.getAllHeadBlocks()).thenReturn(expected);
		
		assertEquals(expected, dc.getAllHeadBlocks());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_NotNullNotEmpty_Positive() {
		dc.getAllBlockIDsUnderneath("AnyBlockId");
		
		verify(blockController, atLeastOnce()).getAllBlockIDsUnderneath("AnyBlockId");
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsUnderneath(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsUnderneath(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_BlockIdEmpty_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsUnderneath("");
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsUnderneath("");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_NotNullNotEmpty_Positive() {
		dc.getAllBlockIDsBelowCertainBlock("AnyBlockId");
		
		verify(blockController, atLeastOnce()).getAllBlockIDsBelowCertainBlock("AnyBlockId");

	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsBelowCertainBlock(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsBelowCertainBlock(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_BlockIdEmpty_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsBelowCertainBlock("");
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsBelowCertainBlock("");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_NotNullNotEmpty_Positive() {
		dc.getAllBlockIDsInBody("AnyBlockId");
		
		verify(blockController, atLeastOnce()).getAllBlockIDsInBody("AnyBlockId");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsInBody(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsInBody(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockIdEmpty_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getAllBlockIDsInBody("");
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getAllBlockIDsInBody("");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Positive() {
		dc.checkIfConnectionIsOpen("someBlockId", ConnectionType.BODY, new HashSet<String>());
		
		verify(blockController, atLeastOnce()).checkIfConnectionIsOpen("someBlockId", ConnectionType.BODY, new HashSet<String>());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No BlockID to check given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.checkIfConnectionIsOpen(null, ConnectionType.BODY, new HashSet<String>());
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.checkIfConnectionIsOpen(null, ConnectionType.BODY, new HashSet<String>());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_BlockIdEmpty_IllegalArgumentException() {
		String excMessage = "No BlockID to check given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.checkIfConnectionIsOpen("", ConnectionType.BODY, new HashSet<String>());
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.checkIfConnectionIsOpen("", ConnectionType.BODY, new HashSet<String>());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_ConnectionNull_IllegalArgumentException() {
		String excMessage = "No connection to check given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.checkIfConnectionIsOpen("SomeBlockId", null, new HashSet<String>());
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.checkIfConnectionIsOpen("SomeBlockId", null, new HashSet<String>());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_ChangingBlocksNull_Positive() {
		dc.checkIfConnectionIsOpen("someBlockId", ConnectionType.BODY, null);
		
		verify(blockController, atLeastOnce()).checkIfConnectionIsOpen("someBlockId", ConnectionType.BODY, new HashSet<String>());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_Positive() {
		dc.getBlockType("AnyBlockId");
		
		verify(blockController, atLeastOnce()).getBlockType("AnyBlockId");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_BlockIdNull_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getBlockType(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getBlockType(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_BlockIdEmpty_IllegalArgumentException() {
		String excMessage = "No blockID given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.getBlockType("");
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);
		
		dc.getBlockType("");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_Positive() {
		dc.isBlockPresent("AnyBlockId");
		
		verify(blockController, atLeastOnce()).isBlockPresent("AnyBlockId");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_BlockIdNull_Positive() {
		assertFalse(dc.isBlockPresent(null));
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#addGameListener(events.GUIListener)}.
	 */
	@Test
	public void testAddGameListener_Positive() {
		dc.addGameListener(mockGuiListener);
		
		verify(blockController, atLeastOnce()).addListener(mockGuiListener);
		verify(gameController, atLeastOnce()).addListener(mockGuiListener);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#addGameListener(events.GUIListener)}.
	 */
	@Test
	public void testAddGameListener_ListenerNull_IllegalArgumentException() {
		String excMessage = "No listener given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.addGameListener(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);	
		Mockito.verifyNoInteractions(gameController);
		
		dc.addGameListener(null);
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#removeGameListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveGameListener_Positive() {
		dc.removeGameListener(mockGuiListener);
		
		verify(blockController, atLeastOnce()).removeListener(mockGuiListener);
		verify(gameController, atLeastOnce()).removeListener(mockGuiListener);
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#removeGameListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveGameListener_ListenerNull_IllegalArgumentException() {
		String excMessage = "No listener given.";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			dc.removeGameListener(null);
		} catch (IllegalArgumentException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockController);	
		Mockito.verifyNoInteractions(gameController);
		
		dc.removeGameListener(null);
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameExecutionUseful_Positive() {
		when(gameController.isGameExecutionUseful()).thenReturn(true);
		
		assertTrue(dc.isGameExecutionUseful());
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#isGameResetUseful()}.
	 */
	@Test
	public void testIsGameResetUseful_Positive() {
		when(gameController.isGameResetUseful()).thenReturn(true);
		
		assertTrue(dc.isGameResetUseful());
	}

}
