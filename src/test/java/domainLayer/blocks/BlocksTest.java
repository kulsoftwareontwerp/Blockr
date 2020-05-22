package domainLayer.blocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import exceptions.InvalidBlockConnectionException;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;

public class BlocksTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private IfBlock block;
	private IfBlock block2;
	private IfBlock blockWithSameIdAsBlock;
	private IfBlock blockWithIdNull;
	private IfBlock blockWithIdNull2;
	private IfBlock spyIfBlock;
	private ActionBlock spyActionBlock;
	private ConditionBlock conditionBlock;
	private UnaryOperatorBlock unaryOperatorBlock;
	private WhileBlock whileBlock;
	@Mock
	private GameWorld gameWorld;
	private NotBlock notBlock;
	private DefinitionBlock definitionBlock;
	private CallFunctionBlock callBlock;
		
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		block = new IfBlock("0");
		block2 = new IfBlock("1");
		blockWithSameIdAsBlock = new IfBlock("0");
		blockWithIdNull = new IfBlock(null);
		blockWithIdNull2 = new IfBlock(null);		
		spyIfBlock = spy(new IfBlock("spy"));
		spyActionBlock = spy(new ActionBlock("actionBlock", new BlockType("ACTION", BlockCategory.ACTION)));
		conditionBlock = spy(new ConditionBlock("conditionBlockId", new BlockType("condition", BlockCategory.CONDITION)));
		unaryOperatorBlock = new NotBlock("unaryOperatorBlockId");
		whileBlock = new WhileBlock("whileBlockId");
		notBlock = spy(new NotBlock("notBlockId"));
		definitionBlock = spy(new DefinitionBlock("definitionBlock"));
		callBlock = spy(new CallFunctionBlock("CallBlock", new BlockType("Call "+ "DefinitionBlock", BlockCategory.CALL, "DefinitionBlock")));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	// ----- TESTS FOR BLOCK -----
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#hashCode()}.
	 */
	@Test
	public void testHashCode_Positive() {
		assertEquals(79, block.hashCode());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#hashCode()}.
	 */
	@Test
	public void testHashCode_blockIdNull_Positive() {
		assertEquals(31, blockWithIdNull.hashCode());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_Positive() {
		assertTrue(block.equals(block));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_Null_Positive() {
		assertFalse(block.equals(null));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_DifferentClass_Positive() {
		assertFalse(block.equals(BlockType.IF));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_BlockIdNull_OtherBlockIdNotNull_Positive() {
		assertFalse(blockWithIdNull.equals(block));
	}

	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_BlockIdNull_OtherBlockIdNull_Positive() {
		assertTrue(blockWithIdNull.equals(blockWithIdNull2));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_BlockIdNotNull_OtherBlockIdNotNull_Positive() {
		assertFalse(block.equals(block2));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testEquals_BlockIdNotNull_OtherBlockIdEqual_Positive() {
		assertTrue(block.equals(blockWithSameIdAsBlock));
	}

	/**
	 * Test method for {@link domainLayer.blocks.Block#clone()}.
	 */
	@Test
	public void testClone_Positive() {
		Block clone = (Block) block.clone();
		assertTrue(clone != block);
		assertTrue(clone.equals(block));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#clone()}.
	 */
	@Test
	public void testClone_WithAllIfs_Positive() {
		block.setConditionBlock(conditionBlock);
		block.setFirstBlockOfBody(spyIfBlock);
		block.setNextBlock(spyActionBlock);
		
		Block clone = (Block) block.clone();
		assertTrue(clone != block);
		assertTrue(clone.equals(block));
		
		verify(conditionBlock,atLeastOnce()).clone();
		verify(spyIfBlock,atLeastOnce()).clone();
		verify(spyActionBlock,atLeastOnce()).clone();
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#clone()}.
	 */
	@Test
	public void testClone_Operand_Positive() {
		notBlock.setOperand(conditionBlock);
		
		Block clone = (Block) notBlock.clone();
		assertTrue(clone != notBlock);
		
		verify(conditionBlock,atLeastOnce()).clone();
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#clone()}.
	 */
	@Test
	public void testSetNextBlock_InvalidBlockConnectionException() {
		String excMessage = "The new block and/or the connected block is no ExecutableBlock.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			block.setNextBlock(notBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		block.setNextBlock(notBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#clone()}.
	 */
	@Test
	public void testSetFirstBlockOfBody_InvalidBlockConnectionException() {
		String excMessage = "The new block and/or the connected block is no ExecutableBlock.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			block.setFirstBlockOfBody(notBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		block.setFirstBlockOfBody(notBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#setOperand(Block)}.
	 */
	@Test
	public void testParseToValidOperation_InvalidBlockConnectionException() {
		String excMessage = "This block is no AssessableBlock.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			block.setOperand(block2);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		block.setOperand(block2);
	}

	/**
	 * Test method for {@link domainLayer.blocks.Block#setOperand(AssessableBlock)}.
	 */
	@Test
	public void testSetOperand_InvalidBlockConnectionException() {
		String excMessage = "The connected block doesn't have the requested connection.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			spyActionBlock.setOperand(notBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		spyActionBlock.setOperand(notBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#setNextBlock(ExecutableBlock)}.
	 */
	@Test
	public void testSetNextBlock_Executable_InvalidBlockConnectionException() {
		String excMessage = "The connected block doesn't have the requested connection.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			notBlock.setNextBlock(spyActionBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		notBlock.setNextBlock(spyActionBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#setFirstBlockOfBody(ExecutableBlock)}.
	 */
	@Test
	public void testSetFirstBlockOfBody_Executable_InvalidBlockConnectionException() {
		String excMessage = "The connected block doesn't have the requested connection.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			notBlock.setFirstBlockOfBody(spyActionBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		notBlock.setFirstBlockOfBody(spyActionBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#setConditionBlock(AssessableBlock)}.
	 */
	@Test
	public void testSetConditionBlock_Assessable_InvalidBlockConnectionException() {
		String excMessage = "The connected block doesn't have the requested connection.";
		exceptionRule.expect(InvalidBlockConnectionException.class);
		exceptionRule.expectMessage(excMessage);
		
		try {
			notBlock.setConditionBlock(conditionBlock);
		} catch (InvalidBlockConnectionException e) {
			assertEquals(excMessage, e.getMessage());
		}
		
		notBlock.setConditionBlock(conditionBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.Block#getBlockId()}.
	 */
	@Test
	public void testGetBlockId_Positive() {
		assertEquals("0", block.getBlockId());
	}

	// ----- TESTS FOR EXECUTABLEBLOCK -----
	// Already tested indirectly

	// ----- TESTS FOR ASSESSABLEBLOCK -----
	// Already tested indirectly
	
	
	// ----- TESTS FOR ACTIONBLOCK -----
	// Already tested indirectly
	
	// ----- TESTS FOR CONTROLBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.ControlBlock#getSupportedConnectionTypes()}.
	 */
	@Test
	public void testGetSupportedConnectionTypesControlBlock_Positive() {
		Set<ConnectionType>supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
		supportedConnectionTypes.add(ConnectionType.BODY);
		supportedConnectionTypes.add(ConnectionType.CONDITION);
		
		assertEquals(supportedConnectionTypes, block.getSupportedConnectionTypes());
	}
	
	// ----- TESTS FOR OPERATORBLOCK -----
	// Already tested indirectly
	
	// ----- TESTS FOR CONDITIONBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.ConditionBlock#getBlockType()}.
	 */
	@Test
	public void testGetBlockType_Positive() {
		assertEquals(new BlockType("condition", BlockCategory.CONDITION).getClass(), conditionBlock.getBlockType().getClass());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.ConditionBlock#getSupportedConnectionTypes()}.
	 */
	@Test
	public void testGetSupportedConnectionTypesConditionBlock_Positive() {
		Set<ConnectionType>supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.LEFT);
		
		assertEquals(supportedConnectionTypes, conditionBlock.getSupportedConnectionTypes());
	}

	// ----- TESTS FOR UNARYOPERATORBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.UnaryOperatorBlock#getSupportedConnectionTypes()}.
	 */
	@Test
	public void testGetSupportedConnectionTypesUnaryOperatorBlock_Positive() {
		Set<ConnectionType>supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.LEFT);
		supportedConnectionTypes.add(ConnectionType.OPERAND);
		
		assertEquals(supportedConnectionTypes, unaryOperatorBlock.getSupportedConnectionTypes());
	}
	
	// ----- TESTS FOR WHILEBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.WhileBlock#getBlockType()}.
	 */
	@Test
	public void testGetBlockTypeWhileBlock_Positive() {
		assertEquals(BlockType.WHILE, whileBlock.getBlockType());
	}
	
	// ----- TESTS FOR IFBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.IfBlock#getBlockType()}.
	 */
	@Test
	public void testGetBlockTypeIfBlock_Positive() {
		assertEquals(BlockType.IF, block.getBlockType());
	}
	
	// ----- TESTS FOR NOTBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testAssessNotBlock_Positive() {
		when(notBlock.getOperand()).thenReturn(conditionBlock);
		Mockito.doReturn(true).when(conditionBlock).assess(gameWorld);
		
		assertFalse(notBlock.assess(gameWorld));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testAssessNotBlock_AssessFalse_Positive() {
		when(notBlock.getOperand()).thenReturn(conditionBlock);
		Mockito.doReturn(false).when(conditionBlock).assess(gameWorld);
		
		assertTrue(notBlock.assess(gameWorld));
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testGetBlockTypeNotBlock_Positive() {
		assertEquals(BlockType.NOT, notBlock.getBlockType());
	}

	// ----- TESTS FOR DEFINITIONBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDefinitionBlockGetSupportedConnectionTypes_Positive() {
		Set<ConnectionType> supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.BODY);
		
		assertEquals(supportedConnectionTypes, definitionBlock.getSupportedConnectionTypes());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDefinitionBlockSetGetCallStack_Positive() {
		Stack<String> callStack = new Stack<String>();
		callStack.add("id");
		definitionBlock.setCallStack(callStack);
		
		assertEquals(callStack, definitionBlock.getCallStack());
	}

	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDefinitionBlockSetGetFirstBlockOfBody_Positive() {
		definitionBlock.setFirstBlockOfBody(block);
		assertEquals(block, definitionBlock.getFirstBlockOfBody());
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDefinitionBlockPopFromCallStack_Positive() {
		CallFunctionBlock callBlock = new CallFunctionBlock("CallBlock", new BlockType("Call "+ "definitionBlock", BlockCategory.CALL, "definitionBlock"));
		
		definitionBlock.pushToCallStack(callBlock);
		assertTrue(definitionBlock.popFromCallStack() != null);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testDefinitionBlockClearCallStack_Positive() {
		definitionBlock.clearCallStack();
		assertEquals(null, definitionBlock.popFromCallStack());
	}
	
	// ----- TESTS FOR CALLFUNCTIONBLOCK -----
	/**
	 * Test method for {@link domainLayer.blocks.NotBlock#assess(com.kuleuven.swop.group17.GameWorldApi.GameWorld)}.
	 */
	@Test
	public void testCallFunctionBlockGetSupportedConnectionTypes_Positive() {
		Set<ConnectionType> supportedConnectionTypes = new HashSet<ConnectionType>();
		supportedConnectionTypes.add(ConnectionType.UP);
		supportedConnectionTypes.add(ConnectionType.DOWN);
		
		assertEquals(supportedConnectionTypes, callBlock.getSupportedConnectionTypes());
	}

}
