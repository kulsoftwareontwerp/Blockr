import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.DomainController;
import applicationLayer.GameController;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.MoveForwardBlock;
import domainLayer.elements.ElementRepository;
import domainLayer.elements.Orientation;
import domainLayer.elements.Robot;
import domainLayer.gamestates.InExecutionState;
import domainLayer.gamestates.ResettingState;

@RunWith(MockitoJUnitRunner.class)
public class ResetGameTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();	

	private InExecutionState inExecutionState;
	private ResettingState resettingState;
	
	private ActionBlock moveForwardBlock;
	
	private Robot robot;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		moveForwardBlock = spy(new MoveForwardBlock("moveForwardBlock"));
		inExecutionState = spy(new InExecutionState(mockGameController, moveForwardBlock));
		resettingState = new ResettingState(mockGameController);
		robot = new Robot(2, 2, Orientation.UP);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Mock(name="gameController")
	private GameController mockGameController;
	@Spy @InjectMocks
	private DomainController dc;

	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#resetGameExecution()}.
	 */
	@Test
	public void testDomainController_ResetGameExecution_Positive() {
		dc.resetGameExecution();
		verify(mockGameController,atLeastOnce()).resetGameExecution();
	}
	
	@Mock(name="gameElementRepository")
	private ElementRepository ger;
	@Spy @InjectMocks
	private GameController gc;
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#resetGameExecution()}.
	 */
	@Test
	public void testGameController_ExecuteBlock_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		when(ger.getRobot()).thenReturn(robot);
	
		gc.resetGameExecution();
		verify(inExecutionState,atLeastOnce()).reset();
		verify(gc,atLeastOnce()).fireRobotChangeEvent();
	}
	

	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#resetRobot()}.
	 */
	@Test
	public void testGameController_ResetRobot_Positive() {
		when(ger.getRobot()).thenReturn(robot);
		
		gc.resetRobot();
		
		verify(ger,atLeastOnce()).removeRobot();
		verify(ger,atLeastOnce()).initializeRobot();
		verify(gc,atLeastOnce()).fireRobotChangeEvent();
	}
	
	// TODO (!)
//	/**
//	 * Test method for
//	 * {@link domainLayer.ResettingState#reset()}.
//	 */
//	@Test
//	public void testResettingState_Reset_Positive() {
//		
//		resettingState.reset();
//		
//		verify(mockGameController,atLeastOnce()).resetRobot();
//		//verify(mockGameController,atLeastOnce()).toState(any(ValidProgramState.class));
//	}
	
	@Spy @InjectMocks
	private ElementRepository er;
	
	/**
	 * Test method for
	 * {@link domainLayer.elements.ElementRepository#removeRobot()}.
	 */
	@Test
	public void testElementRepository_RemoveRobot_Positive() {
		Mockito.doReturn(robot).when(er).getRobot();
		
		er.removeRobot();
		
		verify(er,atLeastOnce()).getRobot();
	}
	
}