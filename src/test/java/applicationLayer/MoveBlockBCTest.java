package applicationLayer;

import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;

import domainLayer.blocks.*;
import events.*;
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

	@Mock
	private ActionBlock movedActionBlock;
	@Mock
	private ActionBlock movedMoveForwardBlock;
	@Mock
	private ConditionBlock movedWallInFrontBlock;
	@Mock
	private NotBlock movedNotBlock;
	@Mock
	private WhileBlock movedWhileBlock;
	@Mock
	private IfBlock movedIfBlock;

	@Before
	public void setUp() throws Exception {
		when(mockBlockReprository.getBlockByID("1")).thenReturn(movedActionBlock);
		when(mockBlockReprository.getBlockByID("3")).thenReturn(movedWhileBlock);

		when(movedActionBlock.clone()).thenReturn(movedActionBlock);
		when(movedMoveForwardBlock.clone()).thenReturn(movedMoveForwardBlock);
		when(movedWhileBlock.clone()).thenReturn(movedWhileBlock);

		Set<Block> blocksUnderneath = new HashSet<Block>();
		blocksUnderneath.add(movedMoveForwardBlock);

		when(mockBlockReprository.getAllBlocksConnectedToAndAfterACertainBlock(movedActionBlock))
				.thenReturn(blocksUnderneath);

		ArrayList<String> infoParent = new ArrayList<String>();
		infoParent.add("DOWN");
		infoParent.add("3");
		when(mockBlockReprository.getConnectedParentIfExists("1")).thenReturn(infoParent);
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

		when(mockBlockReprository.getBlockIdToPerformMoveOn(any(String.class), any(String.class),
				any(ConnectionType.class))).thenReturn("1");

		for (ConnectionType connectionType : ConnectionsWithoutNoConnection) {

			bc.moveBlock("1", "", "3", connectionType);
			verify(mockBlockReprository).moveBlock("1", "1", "3", connectionType);

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onUpdateGameStateEvent(any(UpdateGameStateEvent.class));

			updateMoveOrder.verify(mockDomainListener, atLeastOnce())
					.onResetExecutionEvent(any(ResetExecutionEvent.class));

			updateMoveOrder.verify(mockGuiListener, atLeastOnce()).onBlockChangeEvent(any(BlockChangeEvent.class));

		}
	}

	@Test
	public void testSnapshot() {
		
		ArrayList<String> parentInfo = new ArrayList<String>();
		parentInfo.add("DOWN");
		parentInfo.add("2");

		when(mockBlockReprository.getBlockIdToPerformMoveOn(any(String.class), any(String.class),
				any(ConnectionType.class))).thenReturn("1");
		
		bc.addDomainListener(mockDomainListener);
		bc.addListener(mockGuiListener);

		// mockDomainListeners.add(mockDomainListener);
		blockIdsInRepository.add("1");
		blockIdsInRepository.add("2");
		blockIdsInRepository.add("3");
		
		@SuppressWarnings("unchecked")
		ArrayList<ConnectionType> ConnectionsWithoutNoConnection = (ArrayList<ConnectionType>) connectionTypes.clone();
		ConnectionsWithoutNoConnection.remove(ConnectionType.NOCONNECTION);

		for (ConnectionType connectionType : ConnectionsWithoutNoConnection) {
			assertTrue(null != bc.moveBlock("1", "", "3", connectionType));
			
		}
	}

}
