package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;

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
	
//	@Mock
//	private GameWorldType type;
//	@Mock(name = "gameWorld")
//	private GameWorld gameWorld;
//	@Mock (name = "gameController")
//	GameController gameController;
	@Mock(name = "blockController")
	private BlockController mockBlockController;
	@Spy
	@InjectMocks
	private DomainController dc;
	
	
	@Before
	public void setUp() throws Exception {
//		when(gameWorld.getType()).thenReturn(type);
//		when(type.supportedActions()).thenReturn(null);
//		when(type.supportedPredicates()).thenReturn(null);
		
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
		}
		
		dc.moveBlock("1", null, "", ConnectionType.NOCONNECTION);
		
		dc.moveBlock("1", "", "", ConnectionType.NOCONNECTION);
		verify(mockBlockController,times(2)).moveBlock("1", "", "", ConnectionType.NOCONNECTION);
	}
	
	@Test
	public void testDcCommand() {
		
	}

		
		


}
