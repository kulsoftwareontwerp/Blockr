package domainLayer.blocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;

public class BlocksTest {

	private IfBlock block;
	private IfBlock block2;
	private IfBlock blockWithSameIdAsBlock;
	private IfBlock blockWithIdNull;
	private IfBlock blockWithIdNull2;
//	private IfBlock spyBlock;
	private ConditionBlock conditionBlock;
	private UnaryOperatorBlock unaryOperatorBlock;
	private WhileBlock whileBlock;
	@Mock
	private GameWorld gameWorld;
	private NotBlock notBlock;
		
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
//		spyBlock = spy(new IfBlock("spy"));
		conditionBlock = spy(new ConditionBlock("conditionBlockId", new BlockType("condition", BlockCategory.CONDITION)));
		unaryOperatorBlock = new NotBlock("unaryOperatorBlockId");
		whileBlock = new WhileBlock("whileBlockId");
		notBlock = spy(new NotBlock("notBlockId"));
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
	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
	 */
	@Test
	public void testClone_Positive() {
		assertEquals(block, block.clone());
	}
	
	// TODO: how to test this?
//	/**
//	 * Test method for {@link domainLayer.blocks.Block#equals(Object)}.
//	 */
//	@Test
//	public void testClone_WithAllSetters_Positive() {
//		when(spyBlock.getConditionBlock()).thenReturn(Mockito.mock(ConditionBlock.class));
//		assertEquals(spyBlock, spyBlock.clone());
//	}


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
	
	// ----- TESTS FOR IFBLOCK -----
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








}
