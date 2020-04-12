package applicationLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.MoveForwardBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.WallInFrontBlock;
import domainLayer.blocks.WhileBlock;
import events.BlockChangeEvent;
import events.DomainListener;
import events.GUIListener;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import types.BlockType;
import types.ConnectionType;


@RunWith(MockitoJUnitRunner.class)
public class MoveBlockBCTest {
	
	
	private ArrayList<ConnectionType> connectionTypes = new ArrayList<ConnectionType>();
	private Set<String> blockIdsInRepository = new HashSet<String>();
	
	@Mock
	private DomainListener mockDomainListener;
	@Mock
	private GUIListener mockGuiListener;
	@Spy
	private HashSet<GUIListener> mockGUIListeners;
	@Spy
	private HashSet<DomainListener> mockDomainListeners;
	@Mock(name = "programBlockRepository")
	private BlockRepository mockBlockReprository;
	@InjectMocks
	private BlockController bc;
	
	
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
	public void testBCMoveBlockPositive() {
		bc.addDomainListener(mockDomainListener);
		bc.addListener(mockGuiListener);
		
		// mockDomainListeners.add(mockDomainListener);
		InOrder updateMoveOrder = inOrder(mockDomainListener, mockGuiListener);
		blockIdsInRepository.add("1");
		blockIdsInRepository.add("2");
		blockIdsInRepository.add("3");

		// when(mockBlockReprository.moveBlock("1","",ConnectionType.NOCONNECTION)).thenReturn(blockIdsInRepository);

		ArrayList<ConnectionType> ConnectionsWithoutNoConnection = (ArrayList<ConnectionType>) connectionTypes.clone();
		ConnectionsWithoutNoConnection.remove(ConnectionType.NOCONNECTION);
		
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");
	

		when(mockBlockReprository.getConnectedBlockBeforeMove(any(String.class), any(String.class), any(ConnectionType.class))).thenReturn(parentInfo);
		when(mockBlockReprository.moveBlock(any(String.class), any(String.class),any(String.class), any(ConnectionType.class))).thenReturn("1");
		when(mockBlockReprository.getBlockIdToPerformMoveOn(any(String.class), any(String.class), any(ConnectionType.class))).thenReturn("1");

		for (ConnectionType connectionType : ConnectionsWithoutNoConnection) {
			
			bc.moveBlock("1", "", "3", connectionType);
			verify(mockBlockReprository).moveBlock("1","1", "3", connectionType);

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onUpdateGameStateEvent(any(UpdateGameStateEvent.class));

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onResetExecutionEvent(any(ResetExecutionEvent.class));

			updateMoveOrder.verify(mockGuiListener, atLeastOnce())
					.onBlockChangeEvent(any(BlockChangeEvent.class));

		}
	}

}
