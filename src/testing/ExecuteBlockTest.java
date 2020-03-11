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

import applicationLayer.*;
import domainLayer.blocks.*;
import domainLayer.gamestates.*;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteBlockTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();	
	
	private ValidProgramState validProgramState;
	
	private ActionBlock actionBlock;
	private ControlBlock controlBlock;
	private ControlBlock whileBlock;
	private AssessableBlock condition;
	
	private InExecutionState ies;
	private ValidProgramState vps;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		validProgramState = spy(new ValidProgramState(mockGameController));
		
		actionBlock = spy(new MoveForwardBlock("actionBlock"));
		controlBlock = spy(new WhileBlock("controlBlock"));
		whileBlock = spy(new WhileBlock("whileBlock"));
		condition = spy(new WallInFrontBlock("wallInFrontBlock"));
		
		ies = spy(new InExecutionState(mockGameController, actionBlock));
		vps = spy(new ValidProgramState(mockGameController));
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
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), actionBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testGCFindFirstBlockToBeExecutedPositiveNoActionBlock() {
		when(mockBlockRepository.findFirstBlockToBeExecuted()).thenReturn(controlBlock);
		when(gc.findNextActionBlockToBeExecuted(controlBlock)).thenReturn(actionBlock);
		
		assertEquals(gc.findFirstBlockToBeExecuted(), actionBlock);
		verify(mockBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(controlBlock);
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNoNextBlock() {
		when(actionBlock.getNextBlock()).thenReturn(null);
		
		assertEquals(gc.findNextActionBlockToBeExecuted(actionBlock),null);
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
	 */
	@Test
	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockActionBlock() {
		when(actionBlock.getNextBlock()).thenReturn(actionBlock);
		
		assertEquals(gc.findNextActionBlockToBeExecuted(actionBlock),actionBlock);
	}
	
//	@Mock(name="elementRepository")
//	private ElementRepository mockElementRepository;
//	
//	/**
//	 * Test method for
//	 * {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(ExecutableBlock)}.
//	 */
//	@Test
//	public void testGCFindNextActionBlockToBeExecutedPositiveNextBlockWhileBlockAssessTrue() {
//		when(actionBlock.getNextBlock()).thenReturn(whileBlock);
//		when(whileBlock.getConditionBlock()).thenReturn(condition);
//		when(condition.assess(mockElementRepository)).thenReturn(true);
//		when(whileBlock.getFirstBlockOfBody()).thenReturn(actionBlock);
//		
//		gc.findNextActionBlockToBeExecuted(actionBlock);
//		
//		verify(gc,atLeastOnce()).findNextActionBlockToBeExecuted(actionBlock);
//		
//				
//	}
	
	
	// TODO: Check if the executionState gets made correctly and if execute gets called on it
	/**
	 * Test method for
	 * {@link domainLayer.ValidProgramState#execute()}.
	 */
	@Test
	public void testVPSExecutePositive() {
		when(mockGameController.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
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
		when(ies.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		when(mockGameController.findNextActionBlockToBeExecuted(actionBlock)).thenReturn(null);
		
		ies.execute();
		verify(mockGameController,atLeastOnce()).performRobotAction(actionBlock);
		verify(ies,atLeastOnce()).setNextActionBlockToBeExecuted(null);
		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(null);
	}
	
	/**
	 * Test method for
	 * {@link domainLayer.InExecutionState#execute()}.
	 */
	@Test
	public void testIESExecutePositive() {
		when(ies.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		when(mockGameController.findNextActionBlockToBeExecuted(actionBlock)).thenReturn(actionBlock);
		
		ies.execute();
		verify(mockGameController,atLeastOnce()).performRobotAction(actionBlock);
		verify(ies,atLeastOnce()).setNextActionBlockToBeExecuted(actionBlock);
		verify(mockGameController,atLeastOnce()).fireUpdateHighlightingEvent(actionBlock.getBlockId());
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
	
	
	

}
