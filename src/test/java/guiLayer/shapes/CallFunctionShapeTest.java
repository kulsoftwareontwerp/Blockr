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
 * /** CallFunctionShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CallFunctionShapeTest {

	@Spy
	CallFunctionShape shape = new CallFunctionShape("test", new BlockType("test", BlockCategory.CALL, "definition"),
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
	 * {@link guiLayer.shapes.CallFunctionShape#clipOn(guiLayer.shapes.Shape, types.ConnectionType)}.
	 */
	@Test
	public void testClipOn() {
	Shape clip=spy(new ControlShape("clip", BlockType.IF, new Coordinate(10,10)));
		
		for(ConnectionType t : ConnectionType.values()) {
			Coordinate previous = shape.getCoordinate();
			shape.clipOn(clip, t);
			switch(t) {
			case UP:
				assertEquals(10, shape.getX_coord());
				assertEquals(-20, shape.getY_coord());
				break;
			case DOWN:
				assertEquals(10, shape.getX_coord());
				assertEquals(clip.getY_coord() + clip.getHeight(), shape.getY_coord());
				break;
			case BODY:
				assertEquals(20, shape.getX_coord());
				assertEquals(clip.getY_coord() + 30, shape.getY_coord());
				break;
			default:
				assertEquals(previous, shape.getCoordinate());
				break;
			
			}
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.CallFunctionShape#draw(java.awt.Graphics)}.
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
	 * {@link guiLayer.shapes.CallFunctionShape#fillShapeWithCoordinates()}.
	 */
	@Test
	public void testFillShapeWithCoordinates() {
		HashSet<Coordinate> coordinates = shape.fillShapeWithCoordinates();
		assertTrue(coordinates.size() != 0);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.CallFunctionShape#defineConnectionTypes()}.
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
			connectionMap.put(ConnectionType.UP, new Coordinate(shape.getX_coord() + 20, shape.getY_coord()));
			connectionMap.put(ConnectionType.DOWN, new Coordinate(shape.getX_coord() + 20, shape.getY_coord() + 30));

			assertEquals(connectionMap, f.get(shape));

		} catch (Exception e) {
			fail("coordinateConnectionMap was not initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.CallFunctionShape#initDimensions()}.
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
	 * {@link guiLayer.shapes.CallFunctionShape#CallFunctionShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testCallFunctionShape() {
		BlockType t = new BlockType("test2", BlockCategory.CALL, "definition");
		CallFunctionShape shape = new CallFunctionShape("test", t, new Coordinate(0, 0));
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
