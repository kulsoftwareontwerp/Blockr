/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;
import types.DynaEnum;

/**
 * /** ControlShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ControlShapeTest implements Constants {

	@Spy
	ControlShape shape = new ControlShape("test", new BlockType("test", BlockCategory.CONTROL), new Coordinate(0, 0));

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
	 * Test method for {@link guiLayer.shapes.ControlShape#getStandardHeight()}.
	 */
	@Test
	public void testGetStandardHeight() {
		assertEquals(STANDARD_HEIGHT_CONTROL_BLOCK, shape.getStandardHeight().intValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ControlShape#clipOn(guiLayer.shapes.Shape, types.ConnectionType)}.
	 */
	@Test
	public void testClipOn() {
		Shape clip = spy(new ControlShape("clip", BlockType.IF, new Coordinate(10, 10)));

		for (ConnectionType t : ConnectionType.values()) {
			Coordinate previous = shape.getCoordinate();
			shape.clipOn(clip, t);
			switch (t) {
			case UP:
				assertEquals(10, shape.getX_coord());
				assertEquals(clip.getY_coord()-shape.getHeight(), shape.getY_coord());
				break;
			case DOWN:
				assertEquals(10, shape.getX_coord());
				assertEquals(clip.getY_coord() + clip.getHeight(), shape.getY_coord());
				break;
			case BODY:
				assertEquals(20, shape.getX_coord());
				assertEquals(clip.getY_coord() + 30, shape.getY_coord());
				break;
			case LEFT:
				assertEquals(clip.getX_coord() - shape.getWidth()+10, shape.getX_coord());
				assertEquals(clip.getY_coord(), shape.getY_coord());
				break;
			default:
				assertEquals(previous, shape.getCoordinate());
				break;

			}
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.ControlShape#draw(java.awt.Graphics)}.
	 */
	@Test
	public void testDraw() {
		Graphics g = spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);
		shape.draw(g);
		when(g.getColor()).thenReturn(Color.BLACK);
		shape.draw(g);
		verify(g, atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		verify(g, atLeastOnce()).drawArc(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class), any(Integer.class));
		verify(g, atLeastOnce()).drawString(any(String.class), any(Integer.class), any(Integer.class));
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ControlShape#fillShapeWithCoordinates()}.
	 */
	@Test
	public void testFillShapeWithCoordinates() {
		HashSet<Coordinate> coordinates = shape.fillShapeWithCoordinates();
		assertTrue(coordinates.size() != 0);
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ControlShape#determineTotalHeight(java.util.Set)}.
	 */
	@Test
	public void testDetermineTotalHeight() {
		BlockType action = new BlockType("a1", BlockCategory.ACTION);
		Set<Shape> shapes = new HashSet<Shape>();
		int height = STANDARD_HEIGHT_CONTROL_BLOCK;
		
		shape.determineTotalHeight(shapes);
		assertEquals(height, shape.getHeight());
		
		shapes.add(new ControlShape("t2", BlockType.WHILE, new Coordinate(0, 0)));
		height+=STANDARD_HEIGHT_CONTROL_BLOCK;
		shape.determineTotalHeight(shapes);
		assertEquals(height, shape.getHeight());
		
		shapes.add(new ActionShape("t3", action, new Coordinate(0, 0)));
		height+=STANDARD_HEIGHT_BLOCK;
		shape.determineTotalHeight(shapes);
		assertEquals(height, shape.getHeight());
		
		
		
		shapes.add(new ActionShape("t4", action, new Coordinate(0, 0)));
		height+=STANDARD_HEIGHT_BLOCK;
		shape.determineTotalHeight(shapes);
		assertEquals(height, shape.getHeight());
		
		
		shapes.add(new ControlShape("t5", BlockType.WHILE, new Coordinate(0, 0)));
		height+=STANDARD_HEIGHT_CONTROL_BLOCK;
		shape.determineTotalHeight(shapes);
		assertEquals(height, shape.getHeight());
		
		
		verify(shape,atLeastOnce()).defineConnectionTypes();
		verify(shape,atLeastOnce()).setCoordinatesShape();
		
		
		TestType.removeFromDynaEnum(action);

		
		
	}

	/**
	 * Test method for {@link guiLayer.shapes.ControlShape#defineConnectionTypes()}.
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
			connectionMap.put(ConnectionType.BODY, new Coordinate(shape.getX_coord() + 30, shape.getY_coord() + 30));
			connectionMap.put(ConnectionType.CONDITION,
					new Coordinate(shape.getX_coord() + (shape.getWidth() - 10), shape.getY_coord() + 15));
			assertEquals(connectionMap, f.get(shape));

		} catch (Exception e) {
			fail("coordinateConnectionMap was not initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.ControlShape#initDimensions()}.
	 */
	@Test
	public void testInitDimensions() {
		shape.initDimensions();
		verify(shape).setHeight(intCaptor.capture());
		assertEquals(90, intCaptor.getValue().intValue());
		verify(shape).setWidth(intCaptor.capture());
		assertEquals(90, intCaptor.getValue().intValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.ControlShape#ControlShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testControlShape() {
		BlockType t = new BlockType("test2", BlockCategory.CONTROL);
		ControlShape shape = new ControlShape("test", t, new Coordinate(0, 0));
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
