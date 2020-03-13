package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
import org.mockito.internal.invocation.mockref.MockReference;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.internal.verification.NoInteractions;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.GameController;
import domainLayer.blocks.*;
import domainLayer.gamestates.*;
import events.GUIListener;
import domainLayer.elements.*;



@RunWith(MockitoJUnitRunner.class)
public class UpdateStateTest {
	
	
	
	
	@Mock
	private GameState mockInvalidGameState;
	@Mock	
	private GameState mockValidGameState;
	@Mock	
	private GameState mockExecuteGameState;
	@Mock	
	private GameState mockResettingGameState;
	@Mock	
	private BlockRepository mockBlockRepository;
	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private GameController mockgameController;
	
	private GameController gameController;
	private GameState InvalidGameState;
	private GameState ExecuteGameState;
	private GameState ResettingGameState;
	private GameState ValidGameState;
	private BlockRepository blockRepository;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		gameController = spy(new GameController());
		InvalidGameState = spy(new InValidProgramState(gameController));
		ValidGameState = spy(new ValidProgramState(gameController));
		ExecuteGameState = spy(new InExecutionState(gameController, null));
		ResettingGameState = spy(new ResettingState(gameController));
		blockRepository = spy(BlockRepository.getInstance());
		
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testUpdateStatePositiveBRInValidProgramState() {
		try {
			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockInvalidGameState);
			gameController.updateState();
			verify(mockInvalidGameState).update();
			
			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockValidGameState);
			gameController.updateState();
			verify(mockValidGameState).update();
			
			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockExecuteGameState);
			gameController.updateState();
			verify(mockExecuteGameState).update();
			
			FieldSetter.setField(gameController,GameController.class.getDeclaredField("currentState") , mockResettingGameState);
			gameController.updateState();
			verify(mockResettingGameState).update();
		} catch (NoSuchFieldException | SecurityException e) {
			assertFalse(true);
		}
	}
	
	@Test
	public void testUpdateStatePositiveGameStateUpdate() {
		try {
			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ValidGameState.update();
			
			
			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			InvalidGameState.update();
			
			
			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ExecuteGameState.update();
			
			
			FieldSetter.setField(ResettingGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ResettingGameState.update();
			
			
			verify(mockgameController,times(4)).checkIfValidProgram(); //Each State should call this method once

		} catch (NoSuchFieldException | SecurityException e) {
			assertFalse(true);
		}
		
		
	}
	
	@Test
	public void testUpdateStatePositiveGameControllerProgramSate() {
		
		try {
			FieldSetter.setField(gameController, GameController.class.getDeclaredField("programBlockRepository") , mockBlockRepository);
			gameController.checkIfValidProgram();
			verify(mockBlockRepository).checkIfValidProgram();
			
		} catch (NoSuchFieldException | SecurityException e) {
			assertFalse(true);
		}
		
	}
	
	@Test
	public void testUpdateStatePositiveGameStateGameControllerToState() {
		try {
			GameController mockGameControllerValidState = mock(GameController.class);
			when(mockgameController.checkIfValidProgram()).thenReturn(true);
			when(mockGameControllerValidState.checkIfValidProgram()).thenReturn(true);
			
			
			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockGameControllerValidState);
			ValidGameState.update();
			verify(mockGameControllerValidState,never()).toState(any(GameState.class));
			//Should not call toState method because already in correct sate
			
			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			InvalidGameState.update();
			
			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ExecuteGameState.update();
			
			verify(mockgameController,times(2)).toState(any(GameState.class));

		}
		catch (NoSuchFieldException | SecurityException e) {
			assertFalse(true);
		}
	}
	
	
	@Test
	public void testUpdateStateNegativeGameStateGameControllerToState() {
		try {
			GameController mockGameControllerInValidState = mock(GameController.class);
			when(mockgameController.checkIfValidProgram()).thenReturn(false);
			when(mockGameControllerInValidState.checkIfValidProgram()).thenReturn(false);
			
			
			FieldSetter.setField(ValidGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ValidGameState.update();
			
			//Should not call toState method because already in correct sate
			
			FieldSetter.setField(InvalidGameState, GameState.class.getDeclaredField("gameController") , mockGameControllerInValidState);
			InvalidGameState.update();
			verify(mockGameControllerInValidState,never()).toState(any(GameState.class));
			
			FieldSetter.setField(ExecuteGameState, GameState.class.getDeclaredField("gameController") , mockgameController);
			ExecuteGameState.update();
			
			verify(mockgameController,times(2)).toState(any(GameState.class));

		}
		catch (NoSuchFieldException | SecurityException e) {
			assertFalse(true);
		}
	}

}
