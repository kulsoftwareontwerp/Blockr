/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.Action;

import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;
import types.BlockCategory;
import types.BlockType;

/**
 * ValidProgramStateTest
 *
 * @version 0.1
 * @author group17
 */
public class ValidProgramStateTest {

	@Mock(name="gameController")
	private GameController gameController;
	@Spy @InjectMocks
	private ValidProgramState vps;
	
	private ActionBlock actionBlock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		actionBlock = new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#execute()}.
	 */
	@Test
	public void testExecute() {
		when(gameController.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		vps.execute();
		
		verify(gameController,atLeastOnce()).toState(Mockito.any(InExecutionState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#update()}.
	 */
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ValidProgramState#ValidProgramState(applicationLayer.GameController)}.
	 */
	@Test
	public void testValidProgramState() {
		fail("Not yet implemented");
	}

}
