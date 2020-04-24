//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.security.InvalidParameterException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InOrder;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.internal.invocation.mockref.MockReference;
//import org.mockito.internal.util.reflection.FieldSetter;
//import org.mockito.internal.verification.NoInteractions;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
//
//import applicationLayer.GameController;
//import commands.CommandHandler;
//import domainLayer.blocks.ActionBlock;
//import domainLayer.blocks.Block;
//import domainLayer.blocks.BlockRepository;
//import domainLayer.blocks.ConditionBlock;
//import domainLayer.blocks.ControlBlock;
//import domainLayer.blocks.IfBlock;
//import domainLayer.blocks.NotBlock;
//import domainLayer.blocks.OperatorBlock;
//import domainLayer.blocks.WhileBlock;
//import domainLayer.gamestates.GameState;
//import domainLayer.gamestates.InExecutionState;
//import domainLayer.gamestates.InValidProgramState;
//import domainLayer.gamestates.ResettingState;
//import domainLayer.gamestates.ValidProgramState;
//import events.GUIListener;
//
//
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class UpdateStateTest {
//	
//
//	@Mock
//	private GameState mockInvalidGameState;
//	@Mock	
//	private GameState mockValidGameState;
//	@Mock	
//	private GameState mockExecuteGameState;
//	@Mock	
//	private GameState mockResettingGameState;
//	@Mock	
//	private BlockRepository mockBlockRepository;
//	@Mock
//	private GUIListener mockGuiListener;
//	@Mock
//	private GameController mockgameController;
//	@Mock
//	private GameWorld gameWorld;
//	@Mock
//	private  CommandHandler commandHandler;
//	
//	private GameController gameController;
//	private GameState InvalidGameState;
//	private GameState ExecuteGameState;
//	private GameState ResettingGameState;
//	private GameState ValidGameState;
//	private BlockRepository blockRepository;
//	
//	
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
//	@Before
//	public void setUp() throws Exception {
//		
//		gameController = spy(new GameController(gameWorld, commandHandler));
//		InvalidGameState = spy(new InValidProgramState(gameController));
//		ValidGameState = spy(new ValidProgramState(gameController));
//		ExecuteGameState = spy(new InExecutionState(gameController, null));
//		ResettingGameState = spy(new ResettingState(gameController));
//		blockRepository = spy(BlockRepository.getInstance());
//		
//		
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//	
//	@Test
//	public void testUpdateStatePositiveBRInValidProgramState() {
//		try {
//			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockInvalidGameState);
//			gameController.updateState();
//			verify(mockInvalidGameState).update();
//			
//			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockValidGameState);
//			gameController.updateState();
//			verify(mockValidGameState).update();
//			
//			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockExecuteGameState);
//			gameController.updateState();
//			verify(mockExecuteGameState).update();
//			
//			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockResettingGameState);
//			gameController.updateState();
//			verify(mockResettingGameState).update();
//		} catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//	}
//	
//	@Test
//	public void testUpdateStatePositiveGameStateUpdate() {
//		try {
//			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ValidGameState.update();
//			
//			
//			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			InvalidGameState.update();
//			
//			
//			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ExecuteGameState.update();
//			
//			
//			FieldSetter.setField(ResettingGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ResettingGameState.update();
//			
//			
//			verify(mockgameController,times(4)).checkIfValidProgram(); //Each State should call this method once
//
//		} catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//		
//	}
//	
//	@Test
//	public void testUpdateStatePositiveGameControllerProgramSate() {
//		
//		try {
//			FieldSetter.setField(gameController, GameController.class.getDeclaredField("programBlockRepository") , mockBlockRepository);
//			gameController.checkIfValidProgram();
//			verify(mockBlockRepository).checkIfValidProgram();
//			
//		} catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	@Test
//	public void testUpdateStatePositiveGameStateGameControllerToState() {
//		try {
//			GameController mockGameControllerValidState = mock(GameController.class);
//			when(mockgameController.checkIfValidProgram()).thenReturn(true);
//			when(mockGameControllerValidState.checkIfValidProgram()).thenReturn(true);
//			
//			
//			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockGameControllerValidState);
//			ValidGameState.update();
//			verify(mockGameControllerValidState,never()).toState(any(GameState.class));
//			//Should not call toState method because already in correct sate
//			
//			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			InvalidGameState.update();
//			
//			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ExecuteGameState.update();
//			
//			verify(mockgameController,times(2)).toState(any(GameState.class));
//
//		}
//		catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//	}
//	
//	
//	@Test
//	public void testUpdateStateNegativeGameStateGameControllerToState() {
//		try {
//			GameController mockGameControllerInValidState = mock(GameController.class);
//			when(mockgameController.checkIfValidProgram()).thenReturn(false);
//			when(mockGameControllerInValidState.checkIfValidProgram()).thenReturn(false);
//			
//			
//			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ValidGameState.update();
//			
//			//Should not call toState method because already in correct sate
//			
//			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockGameControllerInValidState);
//			InvalidGameState.update();
//			verify(mockGameControllerInValidState,never()).toState(any(GameState.class));
//			
//			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
//			ExecuteGameState.update();
//			
//			verify(mockgameController,times(2)).toState(any(GameState.class));
//
//		}
//		catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//	}
//	
//	
//	@Test
//	public void testUpdateStatePositiveBRCheckIfValidProgram() {
//		try {
//			ActionBlock actionBlockA = spy(new MoveForwardBlock("1"));
//			ActionBlock actionBlockB = spy(new MoveForwardBlock("2"));
//			ControlBlock ifBlockA = spy(new IfBlock("3"));
//			OperatorBlock operandBlock = spy(new NotBlock("4"));
//			ConditionBlock conditionBlock = spy(new WallInFrontBlock("5"));
//			
//			actionBlockA.setNextBlock(ifBlockA);
//			
//			ifBlockA.setFirstBlockOfBody(actionBlockB);
//			ifBlockA.setConditionBlock(operandBlock);
//			ifBlockA.setNextBlock(null);
//			
//			operandBlock.setOperand(conditionBlock);
//			
//			HashMap<String,Block>  allBlocks = new HashMap<String,Block>();
//			allBlocks.put("1", actionBlockA);
//			allBlocks.put("2", actionBlockB);
//			allBlocks.put("3", ifBlockA);
//			allBlocks.put("4", operandBlock);
//			allBlocks.put("5", conditionBlock);
//			
//			
//			HashSet<Block> headBlocks = new HashSet<Block>();
//			headBlocks.add(actionBlockA);
//			
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("headBlocks") , headBlocks);
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("allBlocks") , allBlocks);
//			assertEquals(true,blockRepository.checkIfValidProgram());
//			
//			
//		}catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	
//	@Test
//	public void testUpdateStatePNegativeBRCheckIfValidProgram() {
//		try {
//			ActionBlock actionBlockA = spy(new MoveForwardBlock("1"));
//			ActionBlock actionBlockB = spy(new MoveForwardBlock("2"));
//			ControlBlock ifBlockA = spy(new IfBlock("3"));
//			ConditionBlock conditionBlock = spy(new WallInFrontBlock("4"));
//			
//			OperatorBlock operandBlockA = spy(new NotBlock("5"));
//			OperatorBlock operandBlockB = spy(new NotBlock("6"));
//			OperatorBlock operandBlockC = spy(new NotBlock("7"));
//			OperatorBlock operandBlockD = spy(new NotBlock("8"));
//			
//			actionBlockA.setNextBlock(ifBlockA);
//			
//			ifBlockA.setFirstBlockOfBody(actionBlockB);
//			ifBlockA.setConditionBlock(operandBlockA);
//			ifBlockA.setNextBlock(null);
//			
//			
//			operandBlockA.setOperand(operandBlockB);
//			operandBlockB.setOperand(operandBlockC);
//			operandBlockC.setOperand(operandBlockD);
//			operandBlockD.setOperand(null);
//			
//			HashMap<String,Block>  allBlocks = new HashMap<String,Block>();
//			allBlocks.put("1", actionBlockA);
//			allBlocks.put("2", actionBlockB);
//			allBlocks.put("3", ifBlockA);
//			allBlocks.put("4", conditionBlock);
//			allBlocks.put("5", operandBlockA);
//			allBlocks.put("6", operandBlockB);
//			allBlocks.put("7", operandBlockC);
//			allBlocks.put("8", operandBlockD);
//			
//			
//			HashSet<Block> headBlocks = new HashSet<Block>();
//			headBlocks.add(actionBlockA);
//			
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("headBlocks") , headBlocks);
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("allBlocks") , allBlocks);
//			assertEquals(false,blockRepository.checkIfValidProgram());
//			
//			
//		}catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	
//	
//	@Test
//	public void testUpdateStatePositiveBRCheckIfValidProgramChainOfOperand() {
//		try {
//			ActionBlock actionBlockA = spy(new MoveForwardBlock("1"));
//			ActionBlock actionBlockB = spy(new MoveForwardBlock("2"));
//			ControlBlock ifBlockA = spy(new IfBlock("3"));
//			ConditionBlock conditionBlock = spy(new WallInFrontBlock("4"));
//			
//			OperatorBlock operandBlockA = spy(new NotBlock("5"));
//			OperatorBlock operandBlockB = spy(new NotBlock("6"));
//			OperatorBlock operandBlockC = spy(new NotBlock("7"));
//			OperatorBlock operandBlockD = spy(new NotBlock("8"));
//			
//			actionBlockA.setNextBlock(ifBlockA);
//			
//			ifBlockA.setFirstBlockOfBody(actionBlockB);
//			ifBlockA.setConditionBlock(operandBlockA);
//			ifBlockA.setNextBlock(null);
//			
//			
//			operandBlockA.setOperand(operandBlockB);
//			operandBlockB.setOperand(operandBlockC);
//			operandBlockC.setOperand(operandBlockD);
//			operandBlockD.setOperand(conditionBlock);
//			
//			HashMap<String,Block>  allBlocks = new HashMap<String,Block>();
//			allBlocks.put("1", actionBlockA);
//			allBlocks.put("2", actionBlockB);
//			allBlocks.put("3", ifBlockA);
//			allBlocks.put("4", conditionBlock);
//			allBlocks.put("5", operandBlockA);
//			allBlocks.put("6", operandBlockB);
//			allBlocks.put("7", operandBlockC);
//			allBlocks.put("8", operandBlockD);
//			
//			
//			HashSet<Block> headBlocks = new HashSet<Block>();
//			headBlocks.add(actionBlockA);
//			
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("headBlocks") , headBlocks);
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("allBlocks") , allBlocks);
//			assertEquals(true,blockRepository.checkIfValidProgram());
//			
//			
//		}catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	
//	@Test
//	public void testUpdateStatePositiveBRCheckIfValidProgramMultiControlBlock() {
//		try {
//			ActionBlock actionBlockA = spy(new MoveForwardBlock("1"));
//			ActionBlock actionBlockB = spy(new MoveForwardBlock("2"));
//			ActionBlock actionBlockC = spy(new MoveForwardBlock("3"));
//			ActionBlock actionBlockD = spy(new MoveForwardBlock("4"));
//			ActionBlock actionBlockE = spy(new MoveForwardBlock("5"));
//			
//			
//			ControlBlock ifBlockA = spy(new IfBlock("6"));
//			ControlBlock ifBlockB = spy(new IfBlock("7"));
//			ControlBlock whileBlockA = spy(new WhileBlock("8"));
//			ControlBlock whileBlockB= spy(new WhileBlock("9"));
//			
//			ConditionBlock conditionBlockA = spy(new WallInFrontBlock("10"));
//			ConditionBlock conditionBlockB = spy(new WallInFrontBlock("11"));
//			ConditionBlock conditionBlockC = spy(new WallInFrontBlock("12"));
//			ConditionBlock conditionBlockD = spy(new WallInFrontBlock("13"));
//			
//			
//			OperatorBlock operandBlockA = spy(new NotBlock("14"));
//			OperatorBlock operandBlockB = spy(new NotBlock("15"));
//			OperatorBlock operandBlockC = spy(new NotBlock("16"));
//			OperatorBlock operandBlockD = spy(new NotBlock("17"));
//			
//			actionBlockA.setNextBlock(ifBlockA);
//			
//			ifBlockA.setConditionBlock(conditionBlockC);
//			ifBlockA.setFirstBlockOfBody(whileBlockA);
//			ifBlockA.setNextBlock(actionBlockC);
//			
//			whileBlockA.setConditionBlock(operandBlockA);
//			whileBlockA.setFirstBlockOfBody(actionBlockB);
//
//			actionBlockC.setNextBlock(whileBlockB);
//			
//			whileBlockB.setConditionBlock(conditionBlockD);
//			whileBlockB.setFirstBlockOfBody(ifBlockB);
//			whileBlockB.setNextBlock(null);
//			
//			ifBlockB.setConditionBlock(operandBlockC);
//			ifBlockB.setFirstBlockOfBody(actionBlockD);
//			ifBlockB.setNextBlock(actionBlockE);
//			
//			
//			operandBlockA.setOperand(operandBlockB);
//			operandBlockB.setOperand(conditionBlockA);
//			
//			operandBlockC.setOperand(operandBlockD);
//			operandBlockD.setOperand(conditionBlockB);
//			
//			HashMap<String,Block>  allBlocks = new HashMap<String,Block>();
//			allBlocks.put("1", actionBlockA);
//			allBlocks.put("2", actionBlockB);
//			allBlocks.put("3", actionBlockC);
//			allBlocks.put("4", actionBlockD);
//			allBlocks.put("5", actionBlockE);
//			
//			allBlocks.put("6", ifBlockA);
//			allBlocks.put("7", ifBlockB);
//			allBlocks.put("8", whileBlockA);
//			allBlocks.put("9", whileBlockB);
//			
//			allBlocks.put("10", conditionBlockA);
//			allBlocks.put("11", conditionBlockB);
//			allBlocks.put("12", conditionBlockC);
//			allBlocks.put("13", conditionBlockD);
//			
//			allBlocks.put("14", operandBlockA);
//			allBlocks.put("15", operandBlockB);
//			allBlocks.put("16", operandBlockC);
//			allBlocks.put("17", operandBlockD);
//			
//			
//			HashSet<Block> headBlocks = new HashSet<Block>();
//			headBlocks.add(actionBlockA);
//			
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("headBlocks") , headBlocks);
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("allBlocks") , allBlocks);
//			assertEquals(true,blockRepository.checkIfValidProgram());
//			
//			
//		}catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	
//	@Test
//	public void testUpdateStateNegativeBRCheckIfValidProgramMultiControlBlock() {
//		try {
//			ActionBlock actionBlockA = spy(new MoveForwardBlock("1"));
//			ActionBlock actionBlockB = spy(new MoveForwardBlock("2"));
//			ActionBlock actionBlockC = spy(new MoveForwardBlock("3"));
//			ActionBlock actionBlockD = spy(new MoveForwardBlock("4"));
//			ActionBlock actionBlockE = spy(new MoveForwardBlock("5"));
//			
//			
//			ControlBlock ifBlockA = spy(new IfBlock("6"));
//			ControlBlock ifBlockB = spy(new IfBlock("7"));
//			ControlBlock whileBlockA = spy(new WhileBlock("8"));
//			ControlBlock whileBlockB= spy(new WhileBlock("9"));
//			
//			ConditionBlock conditionBlockA = spy(new WallInFrontBlock("10"));
//			ConditionBlock conditionBlockB = spy(new WallInFrontBlock("11"));
//			ConditionBlock conditionBlockC = spy(new WallInFrontBlock("12"));
//			ConditionBlock conditionBlockD = spy(new WallInFrontBlock("13"));
//			
//			
//			OperatorBlock operandBlockA = spy(new NotBlock("14"));
//			OperatorBlock operandBlockB = spy(new NotBlock("15"));
//			OperatorBlock operandBlockC = spy(new NotBlock("16"));
//			OperatorBlock operandBlockD = spy(new NotBlock("17"));
//			
//			actionBlockA.setNextBlock(ifBlockA);
//			
//			ifBlockA.setConditionBlock(conditionBlockC);
//			ifBlockA.setFirstBlockOfBody(whileBlockA);
//			ifBlockA.setNextBlock(actionBlockC);
//			
//			whileBlockA.setConditionBlock(operandBlockA);
//			whileBlockA.setFirstBlockOfBody(actionBlockB);
//
//			actionBlockC.setNextBlock(whileBlockB);
//			
//			whileBlockB.setConditionBlock(conditionBlockD);
//			whileBlockB.setFirstBlockOfBody(ifBlockB);
//			whileBlockB.setNextBlock(null);
//			
//			ifBlockB.setConditionBlock(null);
//			ifBlockB.setFirstBlockOfBody(actionBlockD);
//			ifBlockB.setNextBlock(actionBlockE);
//			
//			
//			operandBlockA.setOperand(operandBlockB);
//			operandBlockB.setOperand(conditionBlockA);
//			
//			operandBlockC.setOperand(operandBlockD);
//			operandBlockD.setOperand(conditionBlockB);
//			
//			HashMap<String,Block>  allBlocks = new HashMap<String,Block>();
//			allBlocks.put("1", actionBlockA);
//			allBlocks.put("2", actionBlockB);
//			allBlocks.put("3", actionBlockC);
//			allBlocks.put("4", actionBlockD);
//			allBlocks.put("5", actionBlockE);
//			
//			allBlocks.put("6", ifBlockA);
//			allBlocks.put("7", ifBlockB);
//			allBlocks.put("8", whileBlockA);
//			allBlocks.put("9", whileBlockB);
//			
//			allBlocks.put("10", conditionBlockA);
//			allBlocks.put("11", conditionBlockB);
//			allBlocks.put("12", conditionBlockC);
//			allBlocks.put("13", conditionBlockD);
//			
//			allBlocks.put("14", operandBlockA);
//			allBlocks.put("15", operandBlockB);
//			allBlocks.put("16", operandBlockC);
//			allBlocks.put("17", operandBlockD);
//			
//			
//			HashSet<Block> headBlocks = new HashSet<Block>();
//			headBlocks.add(actionBlockA);
//			
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("headBlocks") , headBlocks);
//			FieldSetter.setField(blockRepository, BlockRepository.class.getDeclaredField("allBlocks") , allBlocks);
//			assertEquals(false,blockRepository.checkIfValidProgram());
//			
//			
//		}catch (NoSuchFieldException | SecurityException e) {
//			assertFalse(true);
//		}
//		
//	}
//	
//	
//	
//
//}
