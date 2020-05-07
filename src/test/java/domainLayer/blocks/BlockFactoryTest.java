/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import types.BlockCategory;
import types.BlockType;

/**
 * BlockFactoryTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockFactoryTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private BlockFactory factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		factory = new BlockFactory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_ControlIf_Positive() {
		assertTrue(factory.createBlock(new BlockType("If", BlockCategory.CONTROL), null) instanceof IfBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_ControlWhile_Positive() {
		assertTrue(factory.createBlock(new BlockType("While", BlockCategory.CONTROL), null) instanceof WhileBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_Operator_Positive() {
		assertTrue(factory.createBlock(BlockType.NOT, null) instanceof NotBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_Action_Positive() {
		assertTrue(factory.createBlock(new BlockType("Action", BlockCategory.ACTION), null) instanceof ActionBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_Condition_Positive() {
		assertTrue(factory.createBlock(new BlockType("Condition", BlockCategory.CONDITION), null) instanceof ConditionBlock);
	}
	
	/**
	 * Test method for {@link domainLayer.blocks.BlockFactory#createBlock(types.BlockType, String)}.
	 */
	@Test
	public void testCreateBlock_WrongControl_IllegalArgumentException() {		
		String excMessage = "Unexpected value: WrongType";
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage(excMessage);
		
		assertTrue(factory.createBlock(new BlockType("WrongType", BlockCategory.CONTROL), null) instanceof WhileBlock);
	}
	
	

}
