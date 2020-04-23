
package applicationLayer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import events.DomainListener;
import events.GUIListener;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;

/**
 * BlockControllerTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockControllerTest {
	
	@Mock(name="blockRepository")
	private BlockRepository blockRepository;
	@Spy @InjectMocks
	private BlockController bc;
	
	private ActionBlock actionBlock0;
	private ActionBlock actionBlock1;
	
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
	public void testBlockController() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
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
	public void testGetAllBlockIDsUnderneath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testAddDomainListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testRemoveDomainListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getFirstBlockBelow(java.lang.String)}.
	 */
	@Test
	public void testGetFirstBlockBelow() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		fail("Not yet implemented");
	}

}
