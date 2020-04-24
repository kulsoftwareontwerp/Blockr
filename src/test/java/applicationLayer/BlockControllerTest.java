/**
 * 
 */
package applicationLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.clearInvocations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.OperatorBlock;
import domainLayer.blocks.WhileBlock;
import events.BlockAddedEvent;
import events.DomainListener;
import events.GUIListener;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;
import types.BlockCategory;
import types.BlockSnapshot;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;

/**
 * BlockControllerTest
 *
 * @version 0.1
 * @author group17
 */
@RunWith(MockitoJUnitRunner.class)
public class BlockControllerTest {
	
	@Mock(name="blockRepository")
	private BlockRepository blockRepository;
	@Spy @InjectMocks
	private BlockController bc;
	
	private ActionBlock actionBlock0;
	private ActionBlock actionBlock1;
	
	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private DomainListener mockDomainListener;

	
	

	private ArrayList<Block> allBlocksInTest = new ArrayList<Block>();

	private ArrayList<String> blockIdsInRepository = new ArrayList<String>();
	
	private ActionBlock connectedActionBlock;

	private ControlBlock connectedControlBlock;

	private ConditionBlock connectedConditionBlock;

	private OperatorBlock connectedOperatorBlock;
	private ConditionBlock newConditionBlock;
	private ActionBlock newActionBlock;

	private IfBlock newIfBlock;

	private NotBlock newNotBlock;


	private WhileBlock newWhileBlock;


	
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		actionBlock0 = new ActionBlock("0", new BlockType("Action", BlockCategory.ACTION));
		actionBlock1 = new ActionBlock("1", new BlockType("Action", BlockCategory.ACTION));
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);
		
		
		
		
		blockIdsInRepository.add("conditionBlock");
		blockIdsInRepository.add("operatorBlock");
		blockIdsInRepository.add("controlBlock");
		blockIdsInRepository.add("actionBlock");
		blockIdsInRepository.add("noBlock");


		connectedActionBlock = spy(new ActionBlock("connectedActionBlock",new BlockType("random", BlockCategory.ACTION)));
		connectedConditionBlock = spy(new ConditionBlock("connectedConditionBlock",new BlockType("random", BlockCategory.CONDITION)));
		connectedControlBlock = spy(new WhileBlock("controlBlock"));
		connectedOperatorBlock = spy(new NotBlock("operatorBlock"));


		
		newConditionBlock = spy(new ConditionBlock("newConditionBlock",new BlockType("random", BlockCategory.CONDITION)));
		newActionBlock = spy(new ActionBlock("newActionBlock",new BlockType("random", BlockCategory.ACTION)));
		newIfBlock = spy(new IfBlock("newIfBlock"));

		newNotBlock = spy(new NotBlock("newNotBlock"));
		newWhileBlock = spy(new WhileBlock("newWhileBlock"));

		allBlocksInTest.add(connectedActionBlock);
		allBlocksInTest.add(connectedControlBlock);
		allBlocksInTest.add(connectedOperatorBlock);
		allBlocksInTest.add(connectedConditionBlock);
		allBlocksInTest.add(newIfBlock);
		allBlocksInTest.add(newNotBlock);
		allBlocksInTest.add(newActionBlock);
		allBlocksInTest.add(newWhileBlock);	
		
		
		
		when(blockRepository.addBlock(any(BlockType.class), any(String.class), any(ConnectionType.class))).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				BlockType type = (BlockType) invocation.getArgument(0);
				switch(type.cat()) {
				case ACTION:
					return newActionBlock.getBlockId();
				case CONDITION:
					return newConditionBlock.getBlockId();
				case CONTROL:
					if(type==BlockType.IF) {
						return newIfBlock.getBlockId();
					}else if(type==BlockType.WHILE) {
						return newWhileBlock.getBlockId();
					}else {
						return null;
					}
				case OPERATOR:
					return newNotBlock.getBlockId();
				default:
					return null;
				
				}
			}
		});
		
		when(blockRepository.getBlockByID(any(String.class))).thenAnswer(new Answer<Block>() {

			@Override
			public Block answer(InvocationOnMock invocation) throws Throwable {
				String id = (String) invocation.getArgument(0);
				
				
				if(allBlocksInTest.stream().anyMatch(s->s.getBlockId().equals(id))) {
					return allBlocksInTest.stream().filter(s->s.getBlockId().equals(id)).findFirst().get();
				}
				else {
					return null;
				}			
			}
			
		});
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#BlockController()}.
	 */
	@Test
	public void testBlockController() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#addBlock(types.BlockType, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testAddBlockPositiveMaxNbOfBlocksReached() {

		
		ArgumentCaptor<BlockType> blockType = ArgumentCaptor.forClass(BlockType.class);
		ArgumentCaptor<ConnectionType> connectionType = ArgumentCaptor.forClass(ConnectionType.class);
		ArgumentCaptor<String> connectedBlock = ArgumentCaptor.forClass(String.class);

		for (DynaEnum<? extends DynaEnum<?>> b : BlockType.values()) {
			for (ConnectionType c : ConnectionType.values()) {
				when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false, true);
				String cb = "connectedActionBlock";
				BlockSnapshot s = bc.addBlock((BlockType) b, cb, c);

				verify(blockRepository, atLeastOnce()).addBlock(blockType.capture(), connectedBlock.capture(),
						connectionType.capture());

				assertEquals(b, blockType.getValue());
				assertEquals(cb, connectedBlock.getValue());
				assertEquals(c, connectionType.getValue());

				InOrder updateResetOrder = inOrder(mockDomainListener);
				updateResetOrder.verify(mockDomainListener, atLeastOnce())
						.onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
				updateResetOrder.verify(mockDomainListener, atLeastOnce())
						.onResetExecutionEvent(any(ResetExecutionEvent.class));

				ArgumentCaptor<PanelChangeEvent> panelChangeEvent = ArgumentCaptor.forClass(PanelChangeEvent.class);
				verify(mockGuiListener, atLeastOnce()).onPanelChangedEvent(panelChangeEvent.capture());
				assertFalse(panelChangeEvent.getValue().isShown());
				verify(mockGuiListener, atLeastOnce()).onBlockAdded(any(BlockAddedEvent.class));
				clearInvocations(blockRepository);
			}
		}
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getMaxNbOfBlocks()}.
	 */
	@Test
	public void testGetMaxNbOfBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainTrue_MaxBlocksReachedFalse_ConnectedBlockBeforeDeleteNotNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = true;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>());
		
		when(blockRepository.getConnectedParentIfExists(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(false);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(actionBlock1);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, actionBlock1, null, new HashSet<Block>())).thenReturn(blockSnapShot);
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(new HashSet<String>());
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}
	
	/**
	 * Test method for {@link applicationLayer.BlockController#removeBlock(java.lang.String, Boolean)}.
	 */
	@Test
	public void testRemoveBlock_IsChainFalse_MaxBlocksReachedTrue_ConnectedBlockBeforeDeleteNull_Positive() {
		String blockIDParam = "0";
		Boolean isChainParam = false;
		ArrayList<String> previousConnection = new ArrayList<String>();
		previousConnection.add("UP");
		previousConnection.add("1");
		BlockSnapshot blockSnapShot = new BlockSnapshot(actionBlock0, null, null, new HashSet<Block>());
		
		when(blockRepository.getConnectedBlockBeforeRemove(blockIDParam)).thenReturn(previousConnection);
		when(blockRepository.checkIfMaxNbOfBlocksReached()).thenReturn(true);
		when(blockRepository.getBlockByID("0")).thenReturn(actionBlock0);
		when(blockRepository.getBlockByID("1")).thenReturn(null);
		when(blockRepository.getAllBlocksConnectedToAndAfterACertainBlock(actionBlock0)).thenReturn(new HashSet<Block>());
		when(bc.createNewBlockSnapshot(actionBlock0, null, null, new HashSet<Block>())).thenReturn(blockSnapShot);
		HashSet<String> idsToBeRemoved = new HashSet<String>();
		idsToBeRemoved.add("idToBeRemoved");
		when(blockRepository.removeBlock(blockIDParam, isChainParam)).thenReturn(idsToBeRemoved);
		
		assertEquals(blockSnapShot, bc.removeBlock(blockIDParam, isChainParam));
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#moveBlock(java.lang.String, java.lang.String, java.lang.String, types.ConnectionType)}.
	 */
	@Test
	public void testMoveBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsUnderneath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsInBody(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsInBody() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getEnclosingControlBlock(java.lang.String)}.
	 */
	@Test
	public void testGetEnclosingControlBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllBlockIDsBelowCertainBlock(java.lang.String)}.
	 */
	@Test
	public void testGetAllBlockIDsBelowCertainBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadControlBlocks()}.
	 */
	@Test
	public void testGetAllHeadControlBlocks() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testAddDomainListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeDomainListener(events.DomainListener)}.
	 */
	@Test
	public void testRemoveDomainListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#removeListener(events.GUIListener)}.
	 */
	@Test
	public void testRemoveListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#addListener(events.GUIListener)}.
	 */
	@Test
	public void testAddListener() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getFirstBlockBelow(java.lang.String)}.
	 */
	@Test
	public void testGetFirstBlockBelow() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link applicationLayer.BlockController#getAllHeadBlocks()}.
	 */
	@Test
	public void testGetAllHeadBlocks() {
		fail("Not yet implemented");
	}

}
