package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.GameController;
import domainLayer.BlockRepository;
import domainLayer.GameState;
import domainLayer.InExecutionState;
import domainLayer.InValidProgramState;
import domainLayer.ResettingState;
import domainLayer.ValidProgramState;


@RunWith(MockitoJUnitRunner.class)
public class UpdateStateTest {
	
	
	private GameController gameController;
	private GameState gameState;
	private InValidProgramState invalidGameState;
	private ValidProgramState validGameState;
	private InExecutionState executeGameState;
	private ResettingState ressetingGameState;
	private BlockRepository blockRepo;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		gameController = spy(new GameController());
		blockRepo = spy(BlockRepository.getInstance());
		
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testUpdateStateValidProgramArea() {
		//Test 
		
		when(blockRepo.checkIfValidProgram()).thenReturn(true);
		gameController.updateState();
		assertEquals(gameController.getCurrentState().getClass(), ValidProgramState.class);
	}
	
	@Test
	public void testUpdateStateInvalidProgramArea() {
		
	}

}
