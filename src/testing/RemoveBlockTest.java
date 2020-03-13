package testing;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
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

import applicationLayer.BlockController;
import applicationLayer.DomainController;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.Block;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.BlockType;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.MoveForwardBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.TurnLeftBlock;
import domainLayer.blocks.TurnRightBlock;
import domainLayer.blocks.WallInFrontBlock;
import domainLayer.blocks.WhileBlock;
import events.BlockRemovedEvent;
import events.DomainListener;
import events.GUIListener;
import events.PanelChangeEvent;
import events.ResetExecutionEvent;
import events.UpdateGameStateEvent;


@RunWith(MockitoJUnitRunner.class)
public class RemoveBlockTest {
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	private static final String FIRSTBLOK = "startingAction";
	
	private HashMap<String, Block> allBlocksInTest;

	
	
	private Set<String> idsForChainActionBlock;
	private Block startingActionBlock;
	
	

	@Before
	public void setUp() throws Exception {
		lenient().doCallRealMethod().when(headBlocks).add((Block)notNull());
		lenient().doCallRealMethod().when(allBlocks).put(anyString(), (Block)notNull());
		
		lenient().doCallRealMethod().when(headBlocks).clear();
		lenient().doCallRealMethod().when(allBlocks).clear();
		
		allBlocksInTest=new HashMap<String,Block>();
		
		
		idsForChainActionBlock = Set.of(FIRSTBLOK,"W2","C1", "C2", "C3", "A1", "A2", "A3", "W1", "WC1", "WC2",
				"WC3", "WA1", "WA2", "WA3","A4");
		
		WallInFrontBlock C3 = new WallInFrontBlock("C3");
		NotBlock C2 = new NotBlock("C2") {
			public AssessableBlock getOperand() {
				return C3;
			};
		};
		NotBlock C1 = new NotBlock("C1") {
			public AssessableBlock getOperand() {
				return C2;
			};
		};
		WallInFrontBlock WC3 = new WallInFrontBlock("WC3");
		NotBlock WC2 = new NotBlock("WC2") {
			public AssessableBlock getOperand() {
				return WC3;
			};
		};
		NotBlock WC1 = new NotBlock("WC1") {
			public AssessableBlock getOperand() {
				return WC2;
			};
		};
		TurnRightBlock WA3 = new TurnRightBlock("WA3");
		TurnLeftBlock WA2 = new TurnLeftBlock("WA2") {
			public ExecutableBlock getNextBlock() {
				return WA3;
			};
		};
		MoveForwardBlock WA1 = new MoveForwardBlock("WA1") {
			public ExecutableBlock getNextBlock() {
				return WA2;
			};
		};
		WhileBlock W1 = new WhileBlock("W1") {
			public AssessableBlock getConditionBlock() {
				return WC1;
			};
			
			@Override
			public ExecutableBlock getFirstBlockOfBody() {
				return WA1;
			};
		};
		TurnRightBlock A3 = new TurnRightBlock("A3") {
			public ExecutableBlock getNextBlock() {
				return W1;
			};
		};
		TurnLeftBlock A2 = new TurnLeftBlock("A2") {
			public ExecutableBlock getNextBlock() {		
				return A3;
			};
		};
		MoveForwardBlock A1 = new MoveForwardBlock("A1") {
			public ExecutableBlock getNextBlock() {
				return A2;
			};
		};
		startingActionBlock = new MoveForwardBlock(FIRSTBLOK);
		
		MoveForwardBlock A4 = new MoveForwardBlock("A4");
		WhileBlock W2 = new WhileBlock("W2") {
			@Override
			public AssessableBlock getConditionBlock() {
				return C1;
			};
			
			@Override
			public ExecutableBlock getFirstBlockOfBody() {
				return A1;
			}
			@Override
			public ExecutableBlock getNextBlock() {
				return A4;
			}
			
		};
		
		startingActionBlock.setNextBlock(W2);
		
		
		allBlocksInTest.put(FIRSTBLOK, startingActionBlock);
		allBlocksInTest.put("W2", W2);
		allBlocksInTest.put("C1", C1);
		allBlocksInTest.put("C2", C2);
		allBlocksInTest.put("C3", C3);
		allBlocksInTest.put("A1", A1);
		allBlocksInTest.put("A2", A2);
		allBlocksInTest.put("A3", A3);
		allBlocksInTest.put("W1", W1);
		allBlocksInTest.put("A4", A4);
		allBlocksInTest.put("WC1", WC1);
		allBlocksInTest.put("WC2", WC2);
		allBlocksInTest.put("WC3", WC3);
		allBlocksInTest.put("WA1", WA1);
		allBlocksInTest.put("WA2", WA2);
		allBlocksInTest.put("WA3", WA3);

	}
	
	private void initLists () {
		headBlocks.add(startingActionBlock);
		allBlocks.putAll(allBlocksInTest);
	}
	
	@Mock
	private GUIListener mockGuiListener;
	@Mock
	private DomainListener mockDomainListener;
	
	@Mock(name="blockController")
	private BlockController mockBc;
	@Spy @InjectMocks
	private DomainController dc;
	/**
	 * Test method for
	 * {@link applicationLayer.DomainController#removeBlock(String)}
	 */
	@Test
	public void testRemoveDCPositive() {
		ArgumentCaptor<String> blockID = ArgumentCaptor.forClass(String.class);

		dc.removeBlock(FIRSTBLOK);
		verify(mockBc).removeBlock(blockID.capture());
		assertEquals(FIRSTBLOK, blockID.getValue());		
	}
	
	

	@Mock(name="programBlockRepository")
	private BlockRepository mockBr;
	@Spy @InjectMocks
	private BlockController bc;
	
	
	
	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#removeBlock(String))}
	 */
	@Test @Ignore
	public void testRemoveBCPositiveMaxWasNotYetReached() {
		//add listeners
		bc.addListener(mockGuiListener);
		bc.addDomainListener(mockDomainListener);
	
		
		ArgumentCaptor<String> blockID = ArgumentCaptor.forClass(String.class);
		bc.removeBlock(FIRSTBLOK);
		verify(mockBr).removeBlock(blockID.capture());
		assertEquals(FIRSTBLOK, blockID.getValue());	
		
		InOrder updateResetOrder = inOrder(mockDomainListener);
		updateResetOrder.verify(mockDomainListener).onUpdateGameStateEvent(any(UpdateGameStateEvent.class));
		updateResetOrder.verify(mockDomainListener).onResetExecutionEvent(any(ResetExecutionEvent.class));
		
		verify(mockGuiListener,atLeastOnce()).onBlockRemoved(any(BlockRemovedEvent.class));
	}
	
	
	
	
	
	
	@Spy
	private HashSet<Block> headBlocks;
	@Spy
	private HashMap<String, Block> allBlocks;
	

}
