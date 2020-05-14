package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import commands.*;
import types.ConnectionType;

@RunWith(MockitoJUnitRunner.class)
public class MoveBlockDCTest {
	
	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
	
	@Mock(name = "gameWorld")
	private GameWorld gameWorld;
	@Spy
	private CommandHandler handler;
	@Mock (name = "gameController")
	GameController gameController;
	@Mock(name = "blockController")
	private BlockController mockBlockController;
	@InjectMocks
	private DomainController dc;
	
	
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
			
			verify(mockBlockController,times(0)).moveBlock(any(), any(), any(), any());
		}

		try {
			dc.moveBlock("1", "", "", null);

		} catch (Exception e) {
			verify(mockBlockController,times(0)).moveBlock(any(), any(), any(), any());
		}

		try {
			dc.moveBlock("1", "", "", null);

		} catch (Exception e) {
			verify(mockBlockController,times(0)).moveBlock(any(), any(), any(), any());
		}

		try {
			dc.moveBlock("1", "", "2", null);

		} catch (Exception e) {
			verify(mockBlockController,times(0)).moveBlock(any(), any(), any(), any());
		}

		try {
			dc.moveBlock("1", "", "3", null);

		} catch (Exception e) {
			verify(mockBlockController,times(0)).moveBlock(any(), any(), any(), any());
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
			verify(mockBlockController).moveBlock("1", "", "3", connectionType);

			dc.moveBlock("1", "2", "3", connectionType);
			verify(mockBlockController).moveBlock("1", "2", "3", connectionType);
			
			verify(handler,atLeast(2)).handle(any(MoveBlockCommand.class));
		}
		
		dc.moveBlock("1", null, "", ConnectionType.NOCONNECTION);
		dc.moveBlock("1", "", "", ConnectionType.NOCONNECTION);
		verify(handler,atLeast(2)).handle(any(MoveBlockCommand.class));
		verify(mockBlockController,times(2)).moveBlock("1", "", "", ConnectionType.NOCONNECTION);

	}

		
		


}
