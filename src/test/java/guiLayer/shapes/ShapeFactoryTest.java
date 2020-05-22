/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import guiLayer.types.Coordinate;
import types.BlockCategory;
import types.BlockType;
import types.DynaEnum;

/**
 * /** ShapeFactoryTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ShapeFactoryTest {
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	@Spy
	private ShapeFactory factory;

	private static class TestType extends DynaEnum<TestType> {

		protected TestType(String type, BlockCategory cat, Action action, Predicate predicate, String definition) {
			super(type, cat, action, predicate, definition);
		}

		public static void removeFromDynaEnum(DynaEnum<?> literal) {
			remove(literal);
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link guiLayer.shapes.ShapeFactory#ShapeFactory()}.
	 */
	@Test
	public void testShapeFactory() {
		ShapeFactory f = new ShapeFactory();

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateShapeNoType() {
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage("A shape needs a type.");
		factory.createShape("test", null, new Coordinate(0, 0));
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateActionShape() {

		BlockType t = new BlockType("test", BlockCategory.ACTION);
		Shape s = factory.createShape("test", t, new Coordinate(0, 0));
		assertTrue(s instanceof ActionShape);

		TestType.removeFromDynaEnum(t);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreatControlShape() {

		Shape s = factory.createShape("test", BlockType.IF, new Coordinate(0, 0));
		assertTrue(s instanceof ControlShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreatDefinitionShape() {

		Shape s = factory.createShape("test", BlockType.DEFINITION, new Coordinate(0, 0));
		assertTrue(s instanceof DefinitionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateConditionShape() {

		BlockType t = new BlockType("test", BlockCategory.CONDITION);
		Shape s = factory.createShape("test", t, new Coordinate(0, 0));
		assertTrue(s instanceof ConditionShape);

		TestType.removeFromDynaEnum(t);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreatOperatorShape() {

		Shape s = factory.createShape("test", BlockType.NOT, new Coordinate(0, 0));
		assertTrue(s instanceof UnaryOperatorShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateCallShape() {

		BlockType t = new BlockType("test", BlockCategory.CALL, "definition");
		Shape s = factory.createShape("test", t, new Coordinate(0, 0));
		assertTrue(s instanceof CallFunctionShape);

		TestType.removeFromDynaEnum(t);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateCallShapeNoDefinition() {
		boolean notThrown = true;
		BlockType t = new BlockType("test", BlockCategory.CALL);
		try {
			factory.createShape("test", t, new Coordinate(0, 0));
		} catch (IllegalArgumentException ex) {
			notThrown = false;
			assertEquals("A functionCall needs an associated definitionShapeID.", ex.getMessage());
		} finally {
			TestType.removeFromDynaEnum(t);
		}
		if (notThrown) {
			fail("No IllegalArgumentException was thrown.");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ShapeFactory#createShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCreateNoBlockCategory() {
		boolean notThrown = true;

		BlockType t = new BlockType("test", null);
		try {
			factory.createShape("test", t, new Coordinate(0, 0));
		} catch (NullPointerException ex) {
			notThrown = false;
			assertEquals("A shape needs a category for its type.", ex.getMessage());
		} finally {
			TestType.removeFromDynaEnum(t);
		}
		if (notThrown) {
			fail("No NullPointerException was thrown.");
		}
	}

}
