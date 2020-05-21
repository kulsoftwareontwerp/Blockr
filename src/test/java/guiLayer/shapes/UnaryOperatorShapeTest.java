/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

import guiLayer.types.Coordinate;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;

/**
 * /** UnaryOperatorShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UnaryOperatorShapeTest {
	@Spy
	UnaryOperatorShape shape = new UnaryOperatorShape("test", new BlockType("test", BlockCategory.OPERATOR),
			new Coordinate(0, 0));

	private static class TestType extends DynaEnum<TestType> {

		protected TestType(String type, BlockCategory cat, Action action, Predicate predicate, String definition) {
			super(type, cat, action, predicate, definition);
		}

		public static void removeFromDynaEnum(DynaEnum<?> literal) {
			remove(literal);
		}

	}

	@Captor
	ArgumentCaptor<Integer> intCaptor;

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
	 * Test method for
	 * {@link guiLayer.shapes.UnaryOperatorShape#clipOn(guiLayer.shapes.Shape, types.ConnectionType)}.
	 */
	@Test
	public void testClipOn() {
		Shape clip = spy(new ControlShape("clip", BlockType.IF, new Coordinate(10, 10)));

		for (ConnectionType t : ConnectionType.values()) {
			Coordinate previous = shape.getCoordinate();
			shape.clipOn(clip, t);
			switch (t) {
			case CONDITION:
			case OPERAND:
				assertEquals(clip.getX_coord() + 80, shape.getX_coord());
				assertEquals(clip.getY_coord(), shape.getY_coord());
				break;
			case LEFT:
				assertEquals(clip.getX_coord() - 80, shape.getX_coord());
				assertEquals(clip.getY_coord(), shape.getY_coord());
				break;
			default:
				assertEquals(previous, shape.getCoordinate());
				break;

			}
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.UnaryOperatorShape#draw(java.awt.Graphics)}.
	 */
	@Test
	public void testDraw() {
		Graphics g = spy(Graphics.class);
		shape.draw(g);

		verify(g, atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		verify(g, atLeastOnce()).drawArc(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class), any(Integer.class));
		verify(g, atLeastOnce()).drawString(any(String.class), any(Integer.class), any(Integer.class));
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.UnaryOperatorShape#fillShapeWithCoordinates()}.
	 */
	@Test
	public void testFillShapeWithCoordinates() {
		HashSet<Coordinate> coordinates = shape.fillShapeWithCoordinates();
		assertTrue(coordinates.size() != 0);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.UnaryOperatorShape#defineConnectionTypes()}.
	 */
	@Test
	public void testDefineConnectionTypes() {
		try {
			Field f;

			f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			f.set(shape, new HashMap<ConnectionType, Coordinate>());

			shape.defineConnectionTypes();

			HashMap<ConnectionType, Coordinate> connectionMap = shape.getCoordinateConnectionMap();
			connectionMap.put(ConnectionType.LEFT, new Coordinate(shape.getX_coord() + 10, shape.getY_coord() + 15));
			connectionMap.put(ConnectionType.OPERAND, new Coordinate(shape.getX_coord() + 90, shape.getY_coord() + 15));

			assertEquals(connectionMap, f.get(shape));

		} catch (Exception e) {
			fail("coordinateConnectionMap was not initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.UnaryOperatorShape#initDimensions()}.
	 */
	@Test
	public void testInitDimensions() {
		shape.initDimensions();
		verify(shape).setHeight(intCaptor.capture());
		assertEquals(30, intCaptor.getValue().intValue());
		verify(shape).setWidth(intCaptor.capture());
		assertEquals(80, intCaptor.getValue().intValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.UnaryOperatorShape#UnaryOperatorShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testUnaryOperatorShape() {
		BlockType t = new BlockType("test2", BlockCategory.OPERATOR);
		UnaryOperatorShape shape = new UnaryOperatorShape("test", t,
				new Coordinate(0, 0));
		try {
			Field f;

			f = Shape.class.getDeclaredField("id");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals("test", f.get(shape));

			f = Shape.class.getDeclaredField("type");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(t, f.get(shape));

			f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(new Coordinate(0, 0), f.get(shape));

		} catch (Exception e) {
			fail("not all fields were initialized correctly");
		} finally {
			TestType.removeFromDynaEnum(t);
		}
	}

}
