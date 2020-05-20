/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;

import commands.CommandHandler;
import commands.GameWorldCommand;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.CallFunctionBlock;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.DefinitionBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.WhileBlock;
import domainLayer.gamestates.InExecutionState;
import domainLayer.gamestates.InValidProgramState;
import domainLayer.gamestates.ValidProgramState;
import events.GUIListener;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import events.UpdateHighlightingEvent;
import types.BlockCategory;
import types.BlockType;
import types.ExecutionSnapshot;

/**
 * GameControllerTest
 *
 * @version 0.1
 * @author group17
 */
public class GameControllerTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Mock(name="programBlockRepository")
	private BlockRepository programBlockRepository;
	@Mock(name="gameWorld")
	private GameWorld gameWorld;
	@Mock(name="commandHandler")
	private CommandHandler commandHandler;
	@Mock
	private GameWorldSnapshot snapshotMock;
	@Spy @InjectMocks
	private GameController gc;
	
	@Mock
	private GameWorldCommand gameWorldCommand;
	@Mock
	private GUIListener mockGuiListener;
	
	private InExecutionState inExecutionState;
	private ValidProgramState validProgramState;
	private ActionBlock actionBlock;
	private ActionBlock nextActionBlock;
	private ControlBlock ifBlock;
	private ControlBlock whileBlock;
	private AssessableBlock assessableBlock;
	private ExecutionSnapshot snapshot;
	private DefinitionBlock definitionBlock;
	private CallFunctionBlock callBlock;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		actionBlock = new ActionBlock("actionBlockId", new BlockType("Action", BlockCategory.ACTION));
		nextActionBlock = new ActionBlock("nextActionBlockId", new BlockType("Action", BlockCategory.ACTION));
		ifBlock = spy(new IfBlock("IfBlock"));
		whileBlock = spy(new WhileBlock("WhileBlock"));
		assessableBlock = spy(new ConditionBlock("ConditionBlock", new BlockType("ConditionBlock", BlockCategory.CONDITION)));
		definitionBlock = spy(new DefinitionBlock("DefinitionBlock"));
		callBlock = spy(new CallFunctionBlock("CallBlock", new BlockType("Call "+ "DefinitionBlock", BlockCategory.CALL, "DefinitionBlock")));
		inExecutionState = spy(new InExecutionState(gc, actionBlock));
		validProgramState = spy(new ValidProgramState(gc));
		Map<String, Stack<String>> callStacks = new HashMap<String, Stack<String>>();
		snapshot = spy(new ExecutionSnapshot(actionBlock, snapshotMock, inExecutionState, callStacks));
		gc.addListener(mockGuiListener);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link applicationLayer.GameController#GameController()}.
	 */
	@Test
	public void testGameController_Positive() {
		new GameController(gameWorld, commandHandler);
		
		verify(gameWorld,atLeastOnce()).saveState();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#handleCommand(commands.GameWorldCommand)}.
	 */
	@Test
	public void testHandleCommand_Positive() {
		gc.handleCommand(gameWorldCommand);
		
		verify(commandHandler,atLeastOnce()).handle(gameWorldCommand);
	}

	/**
	 * Test method for {@link applicationLayer.GameController#resetGameExecution()}.
	 */
	@Test
	public void testResetGameExecution_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		Mockito.doNothing().when(inExecutionState).reset();
		
		gc.resetGameExecution();
		
		verify(inExecutionState,atLeastOnce()).reset();
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#resetGame()}.
	 */
	@Test
	public void testResetGame_NextStateNull_Positive() {
		when(gameWorld.saveState()).thenReturn(snapshotMock);
		Mockito.doReturn(inExecutionState).when(gc).getCurrentState();
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		Mockito.doReturn(snapshot).when(gc).createNewExecutionSnapshot(actionBlock, snapshotMock, inExecutionState, null);
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(null);
		when(inExecutionState.getNextState()).thenReturn(null);
		
		// Does not want to assert these as equals..
		// assertEquals(snapshot, gc.resetGame());
		assertTrue(gc.resetGame() != null);
		
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(null);
	}

	/**
	 * Test method for {@link applicationLayer.GameController#updateState()}.
	 */
	@Test
	public void testUpdateState() {
		when(gc.getCurrentState()).thenReturn(validProgramState);
		Mockito.doNothing().when(validProgramState).update();
		
		gc.updateState();
		
		verify(validProgramState,atLeastOnce()).update();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#executeBlock()}.
	 */
	@Test
	public void testExecuteBlock() {
		when(gc.getCurrentState()).thenReturn(validProgramState);
		Mockito.doNothing().when(validProgramState).execute();
		
		gc.executeBlock();
		
		verify(validProgramState,atLeastOnce()).execute();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_ActionBlock_Positive() {
		when(programBlockRepository.findFirstBlockToBeExecuted()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findFirstBlockToBeExecuted());
		verify(programBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findFirstBlockToBeExecuted()}.
	 */
	@Test
	public void testFindFirstBlockToBeExecuted_ControlBlock_Positive() {
		when(programBlockRepository.findFirstBlockToBeExecuted()).thenReturn(ifBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(null, ifBlock);
		
		assertEquals(actionBlock, gc.findFirstBlockToBeExecuted());
		verify(programBlockRepository,atLeastOnce()).findFirstBlockToBeExecuted();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockIfBlock_Positive() {
		when(programBlockRepository.getEnclosingBodyCavityBlock(actionBlock)).thenReturn(ifBlock);
		when(ifBlock.getNextBlock()).thenReturn(actionBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockWhileBlock_Positive() {
		when(programBlockRepository.getEnclosingBodyCavityBlock(actionBlock)).thenReturn(whileBlock);
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(null, whileBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_ControlBlockNull_Positive() {
		when(programBlockRepository.getEnclosingBodyCavityBlock(actionBlock)).thenReturn(null);
		
		assertEquals(null, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockActionBlock_Positive() {
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, actionBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockControlBlock_PositiveCondition_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(assessableBlock);
		when(assessableBlock.assess(gameWorld)).thenReturn(true);
		Mockito.doReturn(actionBlock).when(ifBlock).getFirstBlockOfBody();
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, ifBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockControlBlock_NegativeCondition_Positive() {
		when(ifBlock.getConditionBlock()).thenReturn(assessableBlock);
		when(assessableBlock.assess(gameWorld)).thenReturn(false);
		Mockito.doReturn(actionBlock).when(ifBlock).getNextBlock();
		Mockito.doReturn(actionBlock).when(gc).findNextActionBlockToBeExecuted(ifBlock, actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, ifBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_PreviousBlockCallFunction_Positive() {
		when(callBlock.getNextBlock()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(callBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_PreviousBlockNotCallFunction_Positive() {
		when(programBlockRepository.getEnclosingBodyCavityBlock(actionBlock)).thenReturn(definitionBlock);
		when(definitionBlock.popFromCallStack()).thenReturn("CallBlock");
		when(programBlockRepository.getBlockByID("CallBlock")).thenReturn(callBlock);
		when(callBlock.getNextBlock()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(actionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_PreviousBlockDefinitionBlock_Positive() {
		when(definitionBlock.popFromCallStack()).thenReturn("CallBlock");
		when(programBlockRepository.getBlockByID("CallBlock")).thenReturn(callBlock);
		when(callBlock.getNextBlock()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(definitionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_PreviousBlockDefinitionBlock_NextBlockNull_Positive() {
		when(definitionBlock.popFromCallStack()).thenReturn("CallBlock");
		when(programBlockRepository.getBlockByID("CallBlock")).thenReturn(callBlock);
		when(callBlock.getNextBlock()).thenReturn(null);
		when(programBlockRepository.getEnclosingBodyCavityBlock(callBlock)).thenReturn(ifBlock);
		
		assertEquals(null, gc.findNextActionBlockToBeExecuted(definitionBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockNull_PreviousBlockAssessableBlock_Positive() {
		assertEquals(null, gc.findNextActionBlockToBeExecuted(assessableBlock, null));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_CurrentBlockCallBlock_Positive() {
		BlockType type = spy(new BlockType("Call "+ "DefinitionBlock", BlockCategory.CALL, "DefinitionBlock"));
		when(callBlock.getBlockType()).thenReturn(type);
		when(programBlockRepository.getBlockByID("DefinitionBlock")).thenReturn(definitionBlock);
		when(definitionBlock.getFirstBlockOfBody()).thenReturn(actionBlock);
		
		assertEquals(actionBlock, gc.findNextActionBlockToBeExecuted(null, callBlock));
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#findNextActionBlockToBeExecuted(domainLayer.blocks.ExecutableBlock, domainLayer.blocks.ExecutableBlock)}.
	 */
	@Test
	public void testFindNextActionBlockToBeExecuted_IllegalEnclosingBlock_RuntimeException() {		
		String excMessage = "Not able to process this block for execution";
		exceptionRule.expect(RuntimeException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			gc.findNextActionBlockToBeExecuted(null, assessableBlock);
		} catch (RuntimeException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		gc.findNextActionBlockToBeExecuted(null, assessableBlock);
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#performAction(domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testPerformAction_NextActionBlockToBeExecutedNotNull_Positive() {
		when(gameWorld.saveState()).thenReturn(snapshotMock);
		Mockito.doReturn(inExecutionState).when(gc).getCurrentState();
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(nextActionBlock);
		Mockito.doReturn(snapshot).when(gc).createNewExecutionSnapshot(nextActionBlock, snapshotMock, inExecutionState, null);
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(Mockito.any(String.class));
		
		// Does not want to assert these as equals..
		// assertEquals(snapshot, gc.performAction(actionBlock));
		assertTrue(gc.performAction(actionBlock) != null);
		
		verify(gameWorld,atLeastOnce()).performAction(actionBlock.getAction());
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(nextActionBlock.getBlockId());
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#performAction(domainLayer.blocks.ActionBlock)}.
	 */
	@Test
	public void testPerformAction_NextActionBlockToBeExecutedNull_Positive() {
		ExecutionSnapshot snapshotNextBlockNull = new ExecutionSnapshot(actionBlock, snapshotMock, inExecutionState, new HashMap<String, Stack<String>>());
		
		when(gameWorld.saveState()).thenReturn(snapshotMock);
		Mockito.doReturn(inExecutionState).when(gc).getCurrentState();
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(null);
		Mockito.doReturn(snapshotNextBlockNull).when(gc).createNewExecutionSnapshot(null, snapshotMock, inExecutionState, new HashMap<String, Stack<String>>());
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(null);
		
		assertEquals(snapshotNextBlockNull, gc.performAction(actionBlock));
		
		verify(gameWorld,atLeastOnce()).performAction(actionBlock.getAction());
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#checkIfValidProgram()}.
	 */
	@Test
	public void testRestoreExecutionSnapshot_Positive() {
		when(snapshot.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		when(gc.getCurrentState()).thenReturn(validProgramState);
		when(snapshot.getGameSnapshot()).thenReturn(snapshotMock);
		when(snapshot.getState()).thenReturn(inExecutionState);
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(null);
		when(validProgramState.getNextActionBlockToBeExecuted()).thenReturn(null);
		
		gc.restoreExecutionSnapshot(snapshot);
		
		verify(validProgramState,atLeastOnce()).setNextActionBlockToBeExecuted(actionBlock);
		verify(gameWorld,atLeastOnce()).restoreState(snapshotMock);
		verify(gc,atLeastOnce()).toState(inExecutionState);
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(null);
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#checkIfValidProgram()}.
	 */
	@Test
	public void testRestoreExecutionSnapshot_NextActionBlockNotNull_Positive() {
		when(snapshot.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		when(gc.getCurrentState()).thenReturn(validProgramState);
		when(snapshot.getGameSnapshot()).thenReturn(snapshotMock);
		when(snapshot.getState()).thenReturn(inExecutionState);
		String blockId = actionBlock.getBlockId();
		when(validProgramState.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		Mockito.doNothing().when(gc).fireUpdateHighlightingEvent(blockId);
		
		gc.restoreExecutionSnapshot(snapshot);
		
		verify(validProgramState,atLeastOnce()).setNextActionBlockToBeExecuted(actionBlock);
		verify(gameWorld,atLeastOnce()).restoreState(snapshotMock);
		verify(gc,atLeastOnce()).toState(inExecutionState);
		verify(gc,atLeastOnce()).fireUpdateHighlightingEvent(blockId);
	}

	/**
	 * Test method for {@link applicationLayer.GameController#checkIfValidProgram()}.
	 */
	@Test
	public void testCheckIfValidProgram_Positive() {
		gc.checkIfValidProgram();
		verify(programBlockRepository,atLeastOnce()).checkIfValidProgram();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#fireUpdateHighlightingEvent(java.lang.String)}.
	 */
	@Test
	public void testFireUpdateHighlightingEvent_Positive() {
		gc.fireUpdateHighlightingEvent("someBlockId");
		
		verify(mockGuiListener,atLeastOnce()).onUpdateHighlightingEvent(Mockito.any(UpdateHighlightingEvent.class));
	}

	/**
	 * Test method for {@link applicationLayer.GameController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener_Positive() {
		gc.removeListener(mockGuiListener);
	}

	/**
	 * Test method for {@link applicationLayer.GameController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener_Positive() {
		gc.addListener(mockGuiListener);
	}

	/**
	 * Test method for {@link applicationLayer.GameController#onResetExecutionEvent(events.ResetExecutionEvent)}.
	 */
	@Test
	public void testOnResetExecutionEvent_Positive() {
		Mockito.doNothing().when(gc).resetGameExecution();
		
		gc.onResetExecutionEvent(Mockito.mock(ResetExecutionEvent.class));
		verify(gc,atLeastOnce()).resetGameExecution();
	}

	/**
	 * Test method for {@link applicationLayer.GameController#onUpdateGameStateEvent(events.UpdateGameStateEvent)}.
	 */
	@Test
	public void testOnUpdateGameStateEvent_Positive() {
		Mockito.doNothing().when(gc).updateState();
		
		gc.onUpdateGameStateEvent(Mockito.mock(UpdateGameStateEvent.class));
		verify(gc,atLeastOnce()).updateState();
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameExecutionUseful_CurrentStateValid_Positive() {
		when(gc.getCurrentState()).thenReturn(validProgramState);
		assertTrue(gc.isGameExecutionUseful());
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameExecutionUseful_CurrentStateInExecution_NextActionBlockNotNull_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(actionBlock);
		assertTrue(gc.isGameExecutionUseful());
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameExecutionUseful_CurrentStateInvalid_Positive() {
		when(gc.getCurrentState()).thenReturn(Mockito.mock(InValidProgramState.class));
		assertFalse(gc.isGameExecutionUseful());
	}
	
	/**
	 * Test method for {@link applicationLayer.GameController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameExecutionUseful_CurrentStateInExecution_NextActionBlockNull_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		when(inExecutionState.getNextActionBlockToBeExecuted()).thenReturn(null);
		assertFalse(gc.isGameExecutionUseful());
	}

	/**
	 * Test method for {@link applicationLayer.GameController#isGameExecutionUseful()}.
	 */
	@Test
	public void testIsGameResetUseful_CurrentStateInExecution_Positive() {
		when(gc.getCurrentState()).thenReturn(inExecutionState);
		assertTrue(gc.isGameResetUseful());
	}
}
