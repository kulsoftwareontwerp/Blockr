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
import commands.ResetCommand;

/**
 * ResettingStateTest
 *
 * @version 0.1
 * @author group17
 */
public class ResettingStateTest {

	@Mock(name="gameController")
	private GameController gameController;
	@Spy @InjectMocks
	private ResettingState rs;
	
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
	 * Test method for {@link domainLayer.gamestates.ResettingState#reset()}.
	 */
	@Test
	public void testReset_UpdatedFalse_Positive() {
		rs.setUpdated(false);
		
		rs.reset();
		
		verify(gameController,atLeastOnce()).handleCommand(Mockito.any(ResetCommand.class));
		assertFalse(rs.getUpdated());
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#reset()}.
	 */
	@Test
	public void testReset_UpdatedTrue_Positive() {
		rs.setUpdated(true);
		
		rs.reset();
		
		verify(gameController,atLeastOnce()).resetGame();
		assertFalse(rs.getUpdated());
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#update()}.
	 */
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link domainLayer.gamestates.ResettingState#ResettingState(applicationLayer.GameController)}.
	 */
	@Test
	public void testResettingState() {
		fail("Not yet implemented");
	}

}
