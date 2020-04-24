/**
 * 
 */
package domainLayer.gamestates;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import applicationLayer.GameController;



/**
 * InValidProgramStateTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class InValidProgramStateTest {

	@Mock
	private GameController gameController;
	@InjectMocks
	private InValidProgramState gameState;
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
	 * Test method for {@link domainLayer.gamestates.InValidProgramState#update()}.
	 */
	@Test
	public void testUpdate() {
		when(gameController.checkIfValidProgram()).thenReturn(true);
		gameState.update();
		verify(gameController).checkIfValidProgram();
		verify(gameController).toState(any(GameState.class));
		
	}
	
	@Test
	public void testUpdateNegative() {
		when(gameController.checkIfValidProgram()).thenReturn(false);
		gameState.update();
		verify(gameController).checkIfValidProgram();
		verify(gameController,times(0)).toState(any(GameState.class));
	}

	/**
	 * Test method for {@link domainLayer.gamestates.InValidProgramState#InValidProgramState(applicationLayer.GameController)}.
	 */
	@Test
	public void testInValidProgramState() {
		InValidProgramState state = new InValidProgramState(gameController);
		try {
			Field gameController = InValidProgramState.class.getSuperclass().getDeclaredField("gameController");
			gameController.setAccessible(true);
			assertTrue("gameController was not initialised", gameController.get(state) != null);

		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			fail("One or more of the required fields were not declared.");
		}
	}

}
