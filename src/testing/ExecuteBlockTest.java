package testing;

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

import applicationLayer.*;
import domainLayer.blocks.*;
import domainLayer.elements.*;
import domainLayer.gamestates.*;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteBlockTest extends GameController {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();	
	
	private ValidProgramState validProgramState;
	private InExecutionState inExecutionState;
	
	private ActionBlock moveForwardBlock;
	private ActionBlock turnLeftBlock;
	private ActionBlock turnRightBlock;
	private ControlBlock whileBlock;
	private ControlBlock ifBlock;

	private WallInFrontBlock wallInFrontBlock;
	
	private HashMap<String, Integer> inFrontOfRobotCoords;
	private HashSet<Element> elementsWithWall;
	private HashSet<Element> elementsWithoutWall;
	
	private Robot robot;
	private Robot robotSpy;

	private HashMap<String, Integer> robotCoords;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		validProgramState = spy(new ValidProgramState(mockGameController));
		inExecutionState = spy(new InExecutionState(mockGameController, moveForwardBlock));
		
		moveForwardBlock = spy(new MoveForwardBlock("moveForwardBlock"));
		turnLeftBlock = spy(new TurnLeftBlock("turnLeftBlock"));
		turnRightBlock = spy(new TurnRightBlock("turnRightBlock"));
		whileBlock = spy(new WhileBlock("whileBlock"));
		ifBlock = spy(new IfBlock("ifBlock"));
		wallInFrontBlock = spy(new WallInFrontBlock("wallInFrontBlock"));
		
		inFrontOfRobotCoords = new HashMap<String, Integer>();
		inFrontOfRobotCoords.put("X", 1);
		inFrontOfRobotCoords.put("Y", 1);
		elementsWithWall = new HashSet<Element>();
		elementsWithWall.add(new Wall(1, 1));
		elementsWithoutWall = new HashSet<Element>();
		

		robot = new Robot(2, 2, Orientation.UP);
		robotSpy = spy(new Robot(2, 2, Orientation.UP));

		robotCoords = new HashMap<String, Integer>();
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
	 * {@link applicationLayer.DomainController#executeBlock()}.
	 */
	@Test
	public void testDomainController_ExecuteBlock_Positive() {
		dc.executeBlock();
		verify(mockGameController,atLeastOnce()).executeBlock();
	}
	
	@Spy @InjectMocks
	private GameController gc;
	
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#executeBlock()}.
	 */
	@Test
	public void testGameController_ExecuteBlock_Positive() {
		when(gc.getCurrentState()).thenReturn(validProgramState);
	
		gc.executeBlock();
		verify(validProgramState,atLeastOnce()).execute();
	}
	
	
	@Mock(name="blockRepository")
	private BlockRepository mockBlockRepository;
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testGameController_FindFirstBlockToBeExecuted_ActionBlock_Positive() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(moveForwardBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), moveForwardBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
//	 */
//	@Test
//	public void testGameController_FindFirstBlockToBeExecuted_NoActionBlock_Positive() {
//		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(whileBlock);
//		when(gc.findNextActionBlockToBeExecuted(whileBlock)).thenReturn(moveForwardBlock);
//		
//		assertEquals(gc.findFirstBlockToBeExecuted(), moveForwardBlock);
//		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(whileBlock);
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NoNextBlock_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(null);
//		
//		assertEquals(gc.findNextActionBlockToBeExecuted(moveForwardBlock),null);
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockActionBlock_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(moveForwardBlock);
//		
//		assertEquals(gc.findNextActionBlockToBeExecuted(moveForwardBlock),moveForwardBlock);
//	}
//	
	@Mock(name="elementRepository")
	private ElementRepository mockElementRepository;
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockWhileBlockAssessTrue_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(whileBlock);
//		when(whileBlock.getConditionBlock()).thenReturn(wallInFrontBlock);
//		Mockito.doReturn(true).when(wallInFrontBlock).assess(mockElementRepository);
//		Mockito.doReturn(turnLeftBlock).when(whileBlock).getFirstBlockOfBody();
//		
//		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockWhileBlockAssessFalse_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(whileBlock);
//		when(whileBlock.getConditionBlock()).thenReturn(wallInFrontBlock);
//		Mockito.doReturn(false).when(wallInFrontBlock).assess(mockElementRepository);
//		Mockito.doReturn(turnLeftBlock).when(whileBlock).getNextBlock();
//		
//		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockIfBlockAssessFalse_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
//		when(ifBlock.getConditionBlock()).thenReturn(wallInFrontBlock);
//		Mockito.doReturn(false).when(wallInFrontBlock).assess(mockElementRepository);
//		Mockito.doReturn(turnLeftBlock).when(ifBlock).getNextBlock();
//		
//		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockIfBlockAssessTrueIsReachedFromEndOfBodyTrue_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
//		when(ifBlock.getConditionBlock()).thenReturn(wallInFrontBlock);
//		Mockito.doReturn(true).when(wallInFrontBlock).assess(mockElementRepository);
//		Mockito.doReturn(moveForwardBlock).when(ifBlock).getFirstBlockOfBody();
//		//Mockito.doReturn(true).when(gc).isReachedFromEndOfBody("moveForwardBlock", "ifBlock", turnLeftBlock);
//		Mockito.doReturn(turnLeftBlock).when(ifBlock).getNextBlock();
//		
//		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
//	}
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGameController_FindNextActionBlockToBeExecuted_NextBlockIfBlockAssessTrueIsReachedFromEndOfBodyFalse_Positive() {
//		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
//		when(ifBlock.getConditionBlock()).thenReturn(wallInFrontBlock);
//		Mockito.doReturn(true).when(wallInFrontBlock).assess(mockElementRepository);
//		Mockito.doReturn(turnLeftBlock).when(ifBlock).getFirstBlockOfBody();
//		Mockito.doReturn(ifBlock).when(turnLeftBlock).getNextBlock();
//		//Mockito.doReturn(true).when(gc).isReachedFromEndOfBody("moveForwardBlock", "ifBlock", turnLeftBlock);
//		Mockito.doReturn(turnRightBlock).when(ifBlock).getNextBlock();
//		
//		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
//	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#performRobotAction(ActionBlock)}.
	 */
	@Test
	public void testGameController_PerformRobotAction_Positive() {
		Mockito.doReturn(robot).when(mockElementRepository).getRobot();
		
		gc.performRobotAction(turnLeftBlock);
		
		verify(turnLeftBlock,atLeastOnce()).execute(mockElementRepository);		
		verify(gc,atLeastOnce()).fireRobotChangeEvent();	
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.GameState#execute()}.
	 */
	@Test
	public void testGameState_Execute_Positive() {
		GameState gameState = Mockito.mock(
				GameState.class, 
				Mockito.CALLS_REAL_METHODS);
	  
		gameState.execute();
		// Nothing should happen
	}
	
	// TODO: Check if the executionState gets made correctly and if execute gets called on it
	/**
	 * Test method for
	 * {@link domainLayer.ValidProgramState#execute()}.
	 */
	@Test
	public void testValidProgramState_Execute_Positive() {
		when(mockGameController.findFirstBlockToBeExecuted()).thenReturn(moveForwardBlock);
		
		validProgramState.execute();
		verify(mockGameController,atLeastOnce()).findFirstBlockToBeExecuted();
		verify(mockGameController,atLeastOnce()).toState(any(InExecutionState.class));
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.InExecutionState#execute()}.
	 */
	@Test
	public void testInExecutionState_Execute_NoNextActionBlockToBeExecuted_Positive() {
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(null);
		inExecutionState.execute();
		verifyNoInteractions(mockGameController);
	}
	
//	/**
//	 * Test method for
//	 * {@link domainLayer.InExecutionState#execute()}.
//	 */
//	@Test
//	public void testInExecutionState_Execute_NoNewNextActionBlockToBeExecuted_Positive() {
//		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(moveForwardBlock);
//		when(mockGameController.findNextActionBlockToBeExecuted(moveForwardBlock)).thenReturn(null);
//		
//		inExecutionState.execute();
//		verify(mockGameController,atLeastOnce()).performRobotAction(moveForwardBlock);
//		verify(inExecutionState,atLeastOnce()).setNextActionBlockToBeExecuted(null);
//		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(null);
//	}
//	
//	/**
//	 * Test method for
//	 * {@link domainLayer.InExecutionState#execute()}.
//	 */
//	@Test
//	public void testInExecutionState_Execute_Positive() {
//		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(moveForwardBlock);
//		when(mockGameController.findNextActionBlockToBeExecuted(moveForwardBlock)).thenReturn(moveForwardBlock);
//		
//		inExecutionState.execute();
//		verify(mockGameController,atLeastOnce()).performRobotAction(moveForwardBlock);
//		verify(inExecutionState,atLeastOnce()).setNextActionBlockToBeExecuted(moveForwardBlock);
//		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(moveForwardBlock.getBlockId());
//	}
	
	
	@Spy @InjectMocks
	private BlockRepository br;
	
	/**
	 * Test method for
	 * {@link domainLayer.BlockRepository#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testBlockRepository_FindFirstBlockToBeExecuted_Positive() {
		// br.findFirstBlockToBeExecuted();
		// How does one mock private attributes like collections of blocks?
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.WallInFrontBlock#assess(ElementRepository)}.
	 */
	@Test
	public void testWallInFrontBlock_Assess_WithWallTrue_Positive() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithWall).when(mockElementRepository).getElements(1,1);
		
		assertTrue(wallInFrontBlock.assess(mockElementRepository));
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.WallInFrontBlock#assess(ElementRepository)}.
	 */
	@Test
	public void testWallInFrontBlock_Assess_WithWallFalse_Positive() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithoutWall).when(mockElementRepository).getElements(1,1);
		
		assertFalse(wallInFrontBlock.assess(mockElementRepository));
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.MoveForwardBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testMoveForwardBlock_Execute_WithinBoundriesNoSolidElements_Positive() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithoutWall).when(mockElementRepository).getElements(1,1);
		Mockito.doReturn(2).when(mockElementRepository).getGameAreaWidth();
		Mockito.doReturn(2).when(mockElementRepository).getGameAreaHeight();
		
		moveForwardBlock.execute(mockElementRepository);
		
		verify(mockElementRepository,atLeastOnce()).updateRobotPosition(1, 1);
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.MoveForwardBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testMoveForwardBlock_Execute_WithinBoundriesSolidElements_Positive() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithWall).when(mockElementRepository).getElements(1,1);
		Mockito.doReturn(2).when(mockElementRepository).getGameAreaWidth();
		Mockito.doReturn(2).when(mockElementRepository).getGameAreaHeight();
		
		moveForwardBlock.execute(mockElementRepository);
		
		verify(mockElementRepository,never()).updateRobotPosition(1, 1);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.MoveForwardBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testMoveForwardBlock_Execute_OutOfBoundriesNoSolidElements_Positive() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithWall).when(mockElementRepository).getElements(1,1);
		Mockito.doReturn(1).when(mockElementRepository).getGameAreaWidth();
		//Mockito.doReturn(5).when(mockElementRepository).getGameAreaHeight();
		
		moveForwardBlock.execute(mockElementRepository);
		
		verify(mockElementRepository,never()).updateRobotPosition(1, 1);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnLeftBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnLeftBlock_Execute_RobotUp_Positive() {
		robotSpy.setOrientation(Orientation.UP);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnLeftBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.LEFT);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnLeftBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnLeftBlock_Execute_RobotRight_Positive() {
		robotSpy.setOrientation(Orientation.RIGHT);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnLeftBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.UP);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnLeftBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnLeftBlock_Execute_RobotDown_Positive() {
		robotSpy.setOrientation(Orientation.DOWN);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnLeftBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.RIGHT);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnLeftBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnLeftBlock_Execute_RobotLeft_Positive() {
		robotSpy.setOrientation(Orientation.LEFT);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnLeftBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.DOWN);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnRightBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnRightBlock_Execute_RobotUp_Positive() {
		robotSpy.setOrientation(Orientation.UP);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnRightBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.RIGHT);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnRightBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnRightBlock_Execute_RobotRight_Positive() {
		robotSpy.setOrientation(Orientation.RIGHT);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnRightBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.DOWN);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnRightBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnRightBlock_Execute_RobotDown_Positive() {
		robotSpy.setOrientation(Orientation.DOWN);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnRightBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.LEFT);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.TurnRightBlock#execute(ElementRepository)}.
	 */
	@Test
	public void testTurnRightBlock_Execute_RobotLeft_Positive() {
		robotSpy.setOrientation(Orientation.LEFT);
		Mockito.doReturn(robotSpy).when(mockElementRepository).getRobot();
		
		turnRightBlock.execute(mockElementRepository);
		
		verify(robotSpy,atLeastOnce()).setOrientation(Orientation.UP);
	}
	
	@Spy @InjectMocks
	private ElementRepository er;
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#getCoordinatesInFrontOfRobot()}.
	 */
	@Test
	public void testElementRepository_GetCoordinatesInFrontOfRobot_OrientationUp_Positive() {
		Mockito.doReturn(robot).when(er).getRobot();
		robotCoords.put("X", 2);
		robotCoords.put("Y", 1);
		
		assertEquals(robotCoords,er.getCoordinatesInFrontOfRobot());
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#getCoordinatesInFrontOfRobot()}.
	 */
	@Test
	public void testElementRepository_GetCoordinatesInFrontOfRobot_OrientationDown_Positive() {
		robot.setOrientation(Orientation.DOWN);
		Mockito.doReturn(robot).when(er).getRobot();
		
		robotCoords.put("X", 2);
		robotCoords.put("Y", 3);
		assertEquals(robotCoords,er.getCoordinatesInFrontOfRobot());
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#getCoordinatesInFrontOfRobot()}.
	 */
	@Test
	public void testElementRepository_GetCoordinatesInFrontOfRobot_OrientationLeft_Positive() {
		robot.setOrientation(Orientation.LEFT);
		Mockito.doReturn(robot).when(er).getRobot();
		
		robotCoords.put("X", 1);
		robotCoords.put("Y", 2);
		assertEquals(robotCoords,er.getCoordinatesInFrontOfRobot());
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#getCoordinatesInFrontOfRobot()}.
	 */
	@Test
	public void testElementRepository_GetCoordinatesInFrontOfRobot_OrientationRight_Positive() {
		robot.setOrientation(Orientation.RIGHT);
		Mockito.doReturn(robot).when(er).getRobot();
		
		robotCoords.put("X", 3);
		robotCoords.put("Y", 2);
		assertEquals(robotCoords,er.getCoordinatesInFrontOfRobot());
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#updateRobotPosition(int, int)}.
	 */
	@Test
	public void testElementRepository_UpdateRobotPosition_Positive() {
		Mockito.doReturn(robotSpy).when(er).getRobot();

		er.updateRobotPosition(1, 2);
		
		verify(robotSpy,atLeastOnce()).setXCoordinate(1);
		verify(robotSpy,atLeastOnce()).setYCoordinate(2);
	}
	
}
