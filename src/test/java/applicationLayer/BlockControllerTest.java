/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import events.BlockAddedEvent;
import events.DomainListener;
import events.GUIListener;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import exceptions.InvalidBlockTypeException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;
import types.ExecutionSnapshot;

/**
 * BlockControllerTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockControllerTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Mock(name="blockRepository")
	private BlockRepository blockRepository;
	@Spy @InjectMocks
	private BlockController bc;
	
	private ActionBlock actionBlock0;
	private ActionBlock actionBlock1;
	private ActionBlock actionBlockSpy;
	private ControlBlock controlBlock;
	private BlockSnapshot snapshot;
	
	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private DomainListener mockDomainListener;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		actionBlock0 = new ActionBlock("0", new BlockType("Action", BlockCategory.ACTION));
		actionBlock1 = new ActionBlock("1", new BlockType("Action", BlockCategory.ACTION));
		actionBlockSpy = spy(new ActionBlock("2", new BlockType("Action", BlockCategory.ACTION)));
		controlBlock = spy(new IfBlock("ifBlock"));
		snapshot = spy(new BlockSnapshot(actionBlock0, null, null, new HashSet<Block>()));
		MockitoAnnotations.initMocks(this);
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#BlockController()}.
	 */
	@Test
	public void testBlockController_Positive() {
		new BlockController();
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getMaxNbOfBlocks()}.
	 */
	@Test
	public void testGetMaxNbOfBlocks() {
		when(blockRepository.getMaxNbOfBlocks()).thenReturn(1);
		
		assertEquals(1, bc.getMaxNbOfBlocks());
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainTrue_MaxBlocksReachedFalse_ConnectedBlockBeforeDeleteNotNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = true;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>());
		
		when(blockRepository.getConnectedParentIfExists(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(actionBlock1);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>())).thenReturn(blockSnapShot);
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(new HashSet<String>());
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = false;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, null, null, new HashSet<Block>());
		
		when(blockRepository.getConnectedBlockBeforeRemove(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(null);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, null, null, new HashSet<Block>())).thenReturn(blockSnapShot);
		HashSet<String> idsToBeRemoved = new HashSet<String>();
		idsToBeRemoved.add("idToBeRemoved");
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(idsToBeRemoved);
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_MaxBlocksReachedTrue_IsChainTrue_AllOptionsInFireBlockAdded_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		
		// Options for fireBlockAdded (TODO: how to test the recursive calls?)
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock1);
//		when(actionBlockSpy.getConditionBlock()).thenReturn(Mockito.mock(AssessableBlock.class));
		
		bc.restoreBlockSnapshot(snapshot, true);
		
		verify(mockGuiListener,atLeastOnce()).onPanelChangedEvent(Mockito.any(PanelChangeEvent.class));
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
		
		// Verifies for fireBlockAdded
		verify(mockGuiListener,atLeastOnce()).onBlockAdded(Mockito.any(BlockAddedEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_IsChainFalse_ConnectedBlockAfterSnapshotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(null);	
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedTrue_IsChainFalse_ConnectedBlockAfterSnapshotNotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(true);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlock1);	
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedFalse_ConnectedBlockBeforeAndAfterSnapshotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(false);
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(null);
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_RemovedFalse_ConnectedBlockBeforeAndAfterSnapshotNotNull_Positive() {
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(null);
		when(snapshot.getBlock()).thenReturn(actionBlockSpy);
		when(blockRepository.getConnectionType(null, actionBlockSpy)).thenReturn(ConnectionType.NOCONNECTION);
		when(blockRepository.restoreBlockSnapshot(snapshot)).thenReturn(false);
		when(snapshot.getConnectedBlockBeforeSnapshot()).thenReturn(actionBlockSpy);
		when(snapshot.getConnectedBlockAfterSnapshot()).thenReturn(actionBlockSpy);
		
		bc.restoreBlockSnapshot(snapshot, false);
		
		verify(mockDomainListener,atLeastOnce()).onUpdateGameStateEvent(Mockito.any(UpdateGameStateEvent.class));
		verify(mockDomainListener,atLeastOnce()).onResetExecutionEvent(Mockito.any(ResetExecutionEvent.class));
		verify(actionBlockSpy,atLeastOnce()).getBlockId();
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#restoreBlockSnapshot(BlockSnapshot, boolean)}.
	 */
	@Test
	public void testRestoreBlockSnapshot_SnapshotNull_NullPointerException() {
		String excMessage = "No snapshot given";
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.restoreBlockSnapshot(null, true);
		} catch (NullPointerException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		Mockito.verifyNoInteractions(blockRepository);
		
		bc.restoreBlockSnapshot(null, true);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		when(blockRepository.getAllBlockIDsUnderneath(actionBlock0)).thenReturn(blockIDsUnderNeath);
		
		assertEquals(blockIDsUnderNeath, bc.getAllBlockIDsUnderneath(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsUnderneath(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsUnderneath(Mockito.any(Block.class));
		
		bc.getAllBlockIDsUnderneath(blockIdParam);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		Set<String> blockIDsInBody = new HashSet<String>();
		when(blockRepository.getAllBlockIDsInBody(controlBlock)).thenReturn(blockIDsInBody);
		
		assertEquals(blockIDsInBody, bc.getAllBlockIDsInBody(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsInBody(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsInBody(Mockito.any(ControlBlock.class));
		
		bc.getAllBlockIDsInBody(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody_BlockNoControlBlock_InvalidBlockTypeException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		
		exceptionRule.expect(InvalidBlockTypeException.class);
		
		try {
			bc.getAllBlockIDsInBody(blockIdParam);
		} catch (InvalidBlockTypeException e) {
			
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsInBody(Mockito.any(ControlBlock.class));
		
		bc.getAllBlockIDsInBody(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		when(blockRepository.getEnclosingControlBlock(actionBlock0)).thenReturn(controlBlock);
		String controlBlockId = controlBlock.getBlockId();
		
		assertEquals(controlBlockId, bc.getEnclosingControlBlock(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_EnclosingBlockNull_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlock0);
		when(blockRepository.getEnclosingControlBlock(actionBlock0)).thenReturn(null);
		
		assertEquals(null, bc.getEnclosingControlBlock(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_GivenBlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getEnclosingControlBlock(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getEnclosingControlBlock(Mockito.any(ActionBlock.class));
		
		bc.getEnclosingControlBlock(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock_BlockNoExecutableBlock_InvalidBlockTypeException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(Mockito.mock(NotBlock.class));
		
		exceptionRule.expect(InvalidBlockTypeException.class);
		
		try {
			bc.getEnclosingControlBlock(blockIdParam);
		} catch (InvalidBlockTypeException e) {
			
		}
		
		verify(blockRepository, Mockito.times(0)).getEnclosingControlBlock(Mockito.any(ActionBlock.class));
		
		bc.getEnclosingControlBlock(blockIdParam);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		Set<String> blockIDsUnderNeath = new HashSet<String>();
		when(blockRepository.getAllBlockIDsInBody(controlBlock)).thenReturn(blockIDsUnderNeath);
		
		assertEquals(blockIDsUnderNeath, bc.getAllBlockIDsBelowCertainBlock(blockIdParam));
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.getAllBlockIDsBelowCertainBlock(blockIdParam);
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		verify(blockRepository, Mockito.times(0)).getAllBlockIDsBelowCertainBlock(null);
		
		bc.getAllBlockIDsBelowCertainBlock(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks_Positive() {
		Set<ControlBlock> allHeadControlBlocks = new HashSet<ControlBlock>();
		allHeadControlBlocks.add(controlBlock);
		when(blockRepository.getAllHeadControlBlocks()).thenReturn(allHeadControlBlocks);
		String controlBlockId = controlBlock.getBlockId();
		Set<String> allHeadControlBlocksID = new HashSet<String>();
		allHeadControlBlocksID.add(controlBlockId);
		
		assertEquals(allHeadControlBlocksID, bc.getAllHeadControlBlocks());		
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testAddDomainListener_Positive() {
		bc.addDomainListener(mockDomainListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testRemoveDomainListener_Positive() {
		bc.removeDomainListener(mockDomainListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener_Positive() {
		bc.removeListener(mockGuiListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener_Positive() {
		bc.addListener(mockGuiListener);
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks_Positive() {
		Set<Block> allHeadBlocks = new HashSet<Block>();
		allHeadBlocks.add(controlBlock);
		when(blockRepository.getAllHeadBlocks()).thenReturn(allHeadBlocks);
		String controlBlockId = controlBlock.getBlockId();
		Set<String> allHeadBlocksID = new HashSet<String>();
		allHeadBlocksID.add(controlBlockId);
		
		assertEquals(allHeadBlocksID, bc.getAllHeadBlocks());		
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_Present_Positive() {
		when(blockRepository.getBlockByID("blockId")).thenReturn(actionBlock0);
		
		assertTrue(bc.isBlockPresent("blockId"));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#isBlockPresent(String)}.
	 */
	@Test
	public void testIsBlockPresent_NotPresent_Positive() {
		when(blockRepository.getBlockByID("blockId")).thenReturn(null);
		
		assertFalse(bc.isBlockPresent("blockId"));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(controlBlock);
		when(controlBlock.getBlockType()).thenReturn(BlockType.IF);
		
		assertEquals(BlockType.IF, bc.getBlockType(blockIdParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#getBlockType(String)}.
	 */
	@Test
	public void testGetBlockType_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		bc.getBlockType(blockIdParam);
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		when(blockRepository.getBlockByID("anyBlockId")).thenReturn(controlBlock);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, controlBlock)).thenReturn(true);
		
		Set<String> changingBlocks = new HashSet<String>();
		changingBlocks.add("anyBlockId");
		assertTrue(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, changingBlocks));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_False_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		when(blockRepository.getBlockByID("anyBlockId")).thenReturn(controlBlock);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, controlBlock)).thenReturn(false);
		
		Set<String> changingBlocks = new HashSet<String>();
		changingBlocks.add("anyBlockId");
		assertFalse(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, changingBlocks));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_NotContainsConnection_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		
		assertFalse(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_SizeZero_Positive() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(actionBlockSpy);
		Set<ConnectionType> supportedTypes = new HashSet<ConnectionType>();
		supportedTypes.add(ConnectionType.BODY);
		when(actionBlockSpy.getSupportedConnectionTypes()).thenReturn(supportedTypes);
		when(blockRepository.checkIfConnectionIsOpen(actionBlockSpy, ConnectionType.BODY, null)).thenReturn(true);
		
		assertTrue(bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>()));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#checkIfConnectionIsOpen(String, ConnectionType, Set)}.
	 */
	@Test
	public void testCheckIfConnectionIsOpen_BlockNull_NoSuchConnectedBlockException() {
		String blockIdParam = "blockId";
		when(blockRepository.getBlockByID(blockIdParam)).thenReturn(null);
		
		String excMessage = "The given blockID is not present in the domain.";
		exceptionRule.expect(NoSuchConnectedBlockException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>());
		} catch (NoSuchConnectedBlockException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		bc.checkIfConnectionIsOpen(blockIdParam, ConnectionType.BODY, new HashSet<String>());
	}

}
