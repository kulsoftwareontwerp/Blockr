package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.CoolGameWorld.events.GUIListener;
import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldSnapshot;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import commands.CommandHandler;
import commands.MoveBlockCommand;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.WhileBlock;
import exceptions.InvalidBlockConnectionException;
import exceptions.NoSuchConnectedBlockException;
import types.BlockType;
import types.ConnectionType;

@RunWith(MockitoJUnitRunner.class)
public class MoveBlockDCTest {
	
	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
	private ArrayList<BlockType> executableBlockTypes = new ArrayList<BlockType>();
	private ArrayList<BlockType> assessableBlockTypes = new ArrayList<BlockType>();
	private Set<String> blockIdsInRepository = new HashSet<String>();
	
	@Spy
	private GameWorld gameWorld = new GameWorld() {
		
		@Override
		public GameWorldSnapshot saveState() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void restoreState(GameWorldSnapshot state) throws IllegalArgumentException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void performAction(Action action)
				throws UnsupportedOperationException, NullPointerException, RuntimeException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void paint(Graphics graphics) throws NullPointerException, RuntimeException {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public GameWorldType getType() {
			return mockGameWorldType;
		}
		
		@Override
		public Boolean evaluate(Predicate predicate)
				throws UnsupportedOperationException, NullPointerException, RuntimeException {
			return false;
		}
	};
	
	@Mock(name="gameController")
	private GameController mockGameController;
	@Mock(name="blockController")
	private BlockController mockBlockController;
	@Mock(name="commandHandler")
	private CommandHandler mockCommandHandler;
	@Spy @InjectMocks
	private DomainController dc;
	
	@Mock
	private GameWorldType mockGameWorldType;
	@Mock
	private GUIListener mockGuiListener;
	
	@Mock
	private Predicate mockPredicate;
	@Mock
	private Action mockAction;
	
	
	@Before
	public void setUp() throws Exception {
		
		connectionTypes.add(ConnectionType.BODY);
		connectionTypes.add(ConnectionType.CONDITION);
		connectionTypes.add(ConnectionType.LEFT);
		connectionTypes.add(ConnectionType.DOWN);
		connectionTypes.add(ConnectionType.NOCONNECTION);
		connectionTypes.add(ConnectionType.OPERAND);
		connectionTypes.add(ConnectionType.UP);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testDCMoveBlockNegativeConnectionTypeNull() {

		try {
			dc.moveBlock("", "", "", null);
		} catch (Exception e) {
			verify(mockCommandHandler,times(0)).handle(any(MoveBlockCommand.class));
	
		}

		try {
			dc.moveBlock("1", "", "", null);

		} catch (Exception e) {
			verify(mockCommandHandler,times(0)).handle(any(MoveBlockCommand.class));
		}

		try {
			dc.moveBlock("1", "", "", null);

		} catch (Exception e) {
			verify(mockCommandHandler,times(0)).handle(any(MoveBlockCommand.class));
		}

		try {
			dc.moveBlock("1", "", "2", null);

		} catch (Exception e) {
			verify(mockCommandHandler,times(0)).handle(any(MoveBlockCommand.class));
		}

		try {
			dc.moveBlock("1", "", "3", null);

		} catch (Exception e) {
			verify(mockCommandHandler,times(0)).handle(any(MoveBlockCommand.class));
		}

	}

	@Test
	public void testDCMoveBlockNegativeExceptions() {
		String noMovedBlockIdMsg = "No movedBlockID given";
		String NullAndNoConnectionMsg = "Null given as connection, use ConnectionType.NOCONNECTION.";
		String NoConnectedAfterMsg = "No blockId given for connectedAfterMovedBlockID";

		// topOfMovedChainBlockId
		for (ConnectionType c : connectionTypes) {
			try {
				dc.moveBlock("", "", "", c);
			} catch (IllegalArgumentException e) {
				assertEquals(e.getClass(), IllegalArgumentException.class);
				assertTrue(e.getMessage().equals(noMovedBlockIdMsg));
			}

			try {
				dc.moveBlock(null, "", "", c);
			} catch (IllegalArgumentException e) {
				assertEquals(e.getClass(), IllegalArgumentException.class);
				assertTrue(e.getMessage().equals(noMovedBlockIdMsg));
			}
		}

		// connectionAfterMove
		try {
			dc.moveBlock("1", "", null, null);
		} catch (IllegalArgumentException e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
			assertTrue(e.getMessage().equals(NullAndNoConnectionMsg));

		}

		// ConnectionAfterMove = "" and Connection Is not Noconnection.
		ArrayList<ConnectionType> ConnectionsWithoutNoConnection = (ArrayList<ConnectionType>) connectionTypes.clone();
		ConnectionsWithoutNoConnection.remove(ConnectionType.NOCONNECTION);

		for (ConnectionType c : ConnectionsWithoutNoConnection) {
			try {
				dc.moveBlock("1", "", "", c);
			} catch (IllegalArgumentException e) {
				assertEquals(e.getClass(), IllegalArgumentException.class);
				assertTrue(e.getMessage().equals(NoConnectedAfterMsg));
			}
		}
	}

	@Test
	public void testDCMovePositive() {
		
		ArrayList<ConnectionType> ConnectionsWithoutNoConnection = (ArrayList<ConnectionType>) connectionTypes.clone();
		ConnectionsWithoutNoConnection.remove(ConnectionType.NOCONNECTION);

		for (ConnectionType connectionType : ConnectionsWithoutNoConnection) {

			dc.moveBlock("1", "", "3", connectionType);
			dc.moveBlock("1", "2", "3", connectionType);
			verify(mockCommandHandler,atLeast(2)).handle(any(MoveBlockCommand.class));
		}
		
		dc.moveBlock("1", null, "", ConnectionType.NOCONNECTION);
		
		dc.moveBlock("1", "", "", ConnectionType.NOCONNECTION);
		verify(mockCommandHandler, atLeast(2)).handle(any(MoveBlockCommand.class));
	}

		
		


}
