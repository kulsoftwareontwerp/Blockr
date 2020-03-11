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

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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
	
	private ActionBlock moveForwardBlock;
	private ActionBlock turnLeftBlock;
	private ActionBlock turnRightBlock;
	private ControlBlock controlBlock;
	private ControlBlock whileBlock;
	private ControlBlock ifBlock;
	private WallInFrontBlock condition;

	private WallInFrontBlock wallInFrontBlock;
	
	private HashMap<String, Integer> inFrontOfRobotCoords;
	private HashSet<Element> elementsWithWall;
	private HashSet<Element> elementsWithoutWall;
	
	private InExecutionState ies;
	private ValidProgramState vps;
	
	private Robot robot;
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
		
		moveForwardBlock = spy(new MoveForwardBlock("moveForwardBlock"));
		turnLeftBlock = spy(new TurnLeftBlock("turnLeftBlock"));
		turnRightBlock = spy(new TurnRightBlock("turnRightBlock"));
		controlBlock = spy(new WhileBlock("controlBlock"));
		whileBlock = spy(new WhileBlock("whileBlock"));
		ifBlock = spy(new IfBlock("ifBlock"));
		condition = spy(new WallInFrontBlock("condition"));
		wallInFrontBlock = spy(new WallInFrontBlock("wallInFrontBlock"));
		
		inFrontOfRobotCoords = new HashMap<String, Integer>();
		inFrontOfRobotCoords.put("X", 1);
		inFrontOfRobotCoords.put("Y", 1);
		elementsWithWall = new HashSet<Element>();
		elementsWithWall.add(new Wall(1, 1));
		elementsWithoutWall = new HashSet<Element>();
		
		ies = spy(new InExecutionState(mockGameController, moveForwardBlock));
		vps = spy(new ValidProgramState(mockGameController));
		
		robot = new Robot(2, 2, Orientation.UP);
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
	public void testDCExecuteBlockPositive() {
		dc.executeBlock();
		verify(mockGameController,atLeastOnce()).executeBlock();
	}
	
	@Spy @InjectMocks
	private GameController gc;
	
	// Geeft hier fail error: 'Actually, there were zero interactions with this mock.'
	// Klopt ook wel, want hij doet alleen iets in de concrete klassen. Heb echter geen idee hoe ik dit anders zou testen.
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#executeBlock()}.
	 */
	@Test
	public void testGCExecuteBlockPositive() {
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
	public void testGCFindFirstBlockToBeExecutedPositiveActionBlock() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(moveForwardBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), moveForwardBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testGCFindFirstBlockToBeExecutedPositiveNoActionBlock() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(controlBlock);
		when(gc.findNextActionBlockToBeExecuted(controlBlock)).thenReturn(moveForwardBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), moveForwardBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(controlBlock);
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNoNextBlock() {
		when(moveForwardBlock.getNextBlock()).thenReturn(null);
		
		assertEquals(gc.findNextActionBlockToBeExecuted(moveForwardBlock),null);
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockActionBlock() {
		when(moveForwardBlock.getNextBlock()).thenReturn(moveForwardBlock);
		
		assertEquals(gc.findNextActionBlockToBeExecuted(moveForwardBlock),moveForwardBlock);
	}
	
	@Mock(name="elementRepository")
	private ElementRepository mockElementRepository;
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockWhileBlockAssessTrue() {
		when(moveForwardBlock.getNextBlock()).thenReturn(whileBlock);
		when(whileBlock.getConditionBlock()).thenReturn(condition);
		Mockito.doReturn(true).when(condition).assess(mockElementRepository);
		Mockito.doReturn(turnLeftBlock).when(whileBlock).getFirstBlockOfBody();
		
		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
		
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockWhileBlockAssessFalse() {
		when(moveForwardBlock.getNextBlock()).thenReturn(whileBlock);
		when(whileBlock.getConditionBlock()).thenReturn(condition);
		Mockito.doReturn(false).when(condition).assess(mockElementRepository);
		Mockito.doReturn(turnLeftBlock).when(whileBlock).getNextBlock();
		
		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
		
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockIfBlockAssessFalse() {
		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
		when(ifBlock.getConditionBlock()).thenReturn(condition);
		Mockito.doReturn(false).when(condition).assess(mockElementRepository);
		Mockito.doReturn(turnLeftBlock).when(ifBlock).getNextBlock();
		
		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
		
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockIfBlockAssessTrueIsReachedFromEndOfBodyTrue() {
		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
		when(ifBlock.getConditionBlock()).thenReturn(condition);
		Mockito.doReturn(true).when(condition).assess(mockElementRepository);
		Mockito.doReturn(moveForwardBlock).when(ifBlock).getFirstBlockOfBody();
		//Mockito.doReturn(true).when(gc).isReachedFromEndOfBody("moveForwardBlock", "ifBlock", turnLeftBlock);
		Mockito.doReturn(turnLeftBlock).when(ifBlock).getNextBlock();
		
		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
		
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockIfBlockAssessTrueIsReachedFromEndOfBodyFalse() {
		when(moveForwardBlock.getNextBlock()).thenReturn(ifBlock);
		when(ifBlock.getConditionBlock()).thenReturn(condition);
		Mockito.doReturn(true).when(condition).assess(mockElementRepository);
		Mockito.doReturn(turnLeftBlock).when(ifBlock).getFirstBlockOfBody();
		Mockito.doReturn(ifBlock).when(turnLeftBlock).getNextBlock();
		//Mockito.doReturn(true).when(gc).isReachedFromEndOfBody("moveForwardBlock", "ifBlock", turnLeftBlock);
		Mockito.doReturn(turnRightBlock).when(ifBlock).getNextBlock();
		
		gc.findNextActionBlockToBeExecuted(moveForwardBlock);
		
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(turnLeftBlock);				
	}
	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGCperformRobotActionTurnLeftBlockPositive() {
//		Mockito.doReturn(robot).when(mockElementRepository).getRobot();
//		
//		gc.performRobotAction(turnLeftBlock);
//		
//		verify(mockElementRepository,atLeastOnce()).turnRobotLeft();	
//		verify(gc,atLeastOnce()).fireRobotChangeEvent();	
//	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.GameState#execute()}.
	 */
	@Test
	public void testGSExecutePositive() {
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
	public void testVPSExecutePositive() {
		when(mockGameController.findFirstBlockToBeExecuted()).thenReturn(moveForwardBlock);
		
		vps.execute();
		verify(mockGameController,atLeastOnce()).findFirstBlockToBeExecuted();
		verify(mockGameController,atLeastOnce()).toState(any(InExecutionState.class));
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.InExecutionState#execute()}.
	 */
	@Test
	public void testIESExecutePositiveNoNextActionBlockToBeExecuted() {
		when(ies.getNextActionBlockToBeExecuted()).thenReturn(null);
		ies.execute();
		verifyNoInteractions(mockGameController);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.InExecutionState#execute()}.
	 */
	@Test
	public void testIESExecutePositiveNoNewNextActionBlockToBeExecuted() {
		when(ies.getNextActionBlockToBeExecuted()).thenReturn(moveForwardBlock);
		when(mockGameController.findNextActionBlockToBeExecuted(moveForwardBlock)).thenReturn(null);
		
		ies.execute();
		verify(mockGameController,atLeastOnce()).performRobotAction(moveForwardBlock);
		verify(ies,atLeastOnce()).setNextActionBlockToBeExecuted(null);
		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(null);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.InExecutionState#execute()}.
	 */
	@Test
	public void testIESExecutePositive() {
		when(ies.getNextActionBlockToBeExecuted()).thenReturn(moveForwardBlock);
		when(mockGameController.findNextActionBlockToBeExecuted(moveForwardBlock)).thenReturn(moveForwardBlock);
		
		ies.execute();
		verify(mockGameController,atLeastOnce()).performRobotAction(moveForwardBlock);
		verify(ies,atLeastOnce()).setNextActionBlockToBeExecuted(moveForwardBlock);
		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(moveForwardBlock.getBlockId());
	}
	
	
	@Spy @InjectMocks
	private BlockRepository br;
	
	/**
	 * Test method for
	 * {@link domainLayer.BlockRepository#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testBRfindFirstBlockToBeExecutedPositive() {
		// br.findFirstBlockToBeExecuted();
		// TODO: Deze methode kan enkel worden opgeroepen als er exact 1 element in de headBlocks set zit (valid state)
		//		Maar hoe hou je daar rekening mee + hoe test je dat als er geen manier is om hier die headBlocks set aan te passen...
	}
	
	
	/**
	 * Test method for
	 * {@link domainLayer.WallInFrontBlock#assess(ElementRepository)}.
	 */
	@Test
	public void testWIFAssessPositiveWithWallTrue() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithWall).when(mockElementRepository).getElements(1,1);
		
		assertTrue(wallInFrontBlock.assess(mockElementRepository));
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.WallInFrontBlock#assess(ElementRepository)}.
	 */
	@Test
	public void testWIFAssessPositiveWithoutWallFalse() {
		Mockito.doReturn(inFrontOfRobotCoords).when(mockElementRepository).getCoordinatesInFrontOfRobot();
		Mockito.doReturn(elementsWithoutWall).when(mockElementRepository).getElements(1,1);
		
		assertFalse(wallInFrontBlock.assess(mockElementRepository));
	}
	
	
	@Spy @InjectMocks
	private ElementRepository er;
	
	/**
	 * Test method for
	 * {@link domainLayer.ElementRepository#getCoordinatesInFrontOfRobot()}.
	 */
	@Test
	public void testERGetCoordinatesInFrontOfRobotPositiveUP() {
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
	public void testERGetCoordinatesInFrontOfRobotPositiveDOWN() {
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
	public void testERGetCoordinatesInFrontOfRobotPositiveLEFT() {
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
	public void testERGetCoordinatesInFrontOfRobotPositiveRIGHT() {
		robot.setOrientation(Orientation.RIGHT);
		Mockito.doReturn(robot).when(er).getRobot();
		
		robotCoords.put("X", 3);
		robotCoords.put("Y", 2);
		assertEquals(robotCoords,er.getCoordinatesInFrontOfRobot());
	}
}
