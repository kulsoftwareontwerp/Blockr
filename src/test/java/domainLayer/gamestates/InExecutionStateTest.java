/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import applicationLayer.GameController;
import commands.ExecuteBlockCommand;
import domainLayer.blocks.ActionBlock;

/**
 * InExecutionStateTest
 *
 * @version 0.1
 * @author group17
 */
public class InExecutionStateTest {

	@Mock(name="gameController")
	private GameController gameController;
	@Mock(name="nextActionBlockToBeExecuted")
	private ActionBlock nextActionBlockToBeExecuted;
	@Spy @InjectMocks
	private InExecutionState ies;
	
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
	 * Test method for {@link domainLayer.gamestates.InExecutionState#reset()}.
	 */
	@Test
	public void testReset() {
		ies.reset();
		
		verify(gameController,atLeastOnce()).toState(Mockito.any(ResettingState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#execute()}.
	 */
	@Test
	public void testExecute_nextActionBlockToBeExecutedNull_Positive() {
		Mockito.doReturn(null).when(ies).getNextActionBlockToBeExecuted();
		
		ies.execute();
		
		Mockito.verifyNoMoreInteractions(gameController);
	}
	
	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#execute()}.
	 */
	@Test
	public void testExecute_nextActionBlockToBeExecutedNotNull_Positive() {		
		ies.execute();
		
		verify(gameController,atLeastOnce()).handleCommand(Mockito.any(ExecuteBlockCommand.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#update()}.
	 */
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InExecutionState#InExecutionState(applicationLayer.GameController, domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testInExecutionState() {
		fail("Not yet implemented");
	}

}
