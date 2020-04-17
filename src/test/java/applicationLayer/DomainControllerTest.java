/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import commands.CommandHandler;

/**
 * DomainControllerTest
 *
 * @version 0.1
 * @author group17
 */
public class DomainControllerTest {

	@Mock(name="gameWorld")
	private GameWorld gameWorld;
	@Mock(name="gameController")
	private GameController gameController;
	@Mock(name="blockController")
	private BlockController blockController;
	@Spy @InjectMocks
	private DomainController dc;
	
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
	 * Test method for {@link applicationLayer.DomainController#DomainController(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDomainController() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#removeBlock(java.lang.String)}.
	 */
	@Test
	public void testRemoveBlock() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link applicationLayer.DomainController#resetGameExecution()}.
	 */
	@Test
	public void testResetGameExecution_Positive() {
		dc.resetGameExecution();
		verify(gameController,atLeastOnce()).resetGameExecution();
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#addGameListener(events.GUIListener)}.
	 */
	@Test
	public void testAddGameListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#removeGameListener(events.GUIListener)}.
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
		verify(gameController,atLeastOnce()).executeBlock();
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#addElement(domainLayer.elements.ElementType, int, int)}.
	 */
	@Test
	public void testAddElement() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getFirstBlockBelow(java.lang.String)}.
	 */
	@Test
	public void testGetFirstBlockBelow() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.DomainController#getAllHeadControlBlocks()}.
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
	 * Test method for {@link applicationLayer.DomainController#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint() {
		fail("Not yet implemented");
	}

}
