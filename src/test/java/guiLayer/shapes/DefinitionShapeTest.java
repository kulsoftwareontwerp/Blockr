/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.*;
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
 * /** DefinitionShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefinitionShapeTest implements Constants {
	@Spy
	DefinitionShape shape = new DefinitionShape("test", new BlockType("test", BlockCategory.DEFINITION),
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
	 * Test method for {@link guiLayer.shapes.DefinitionShape#getStandardHeight()}.
	 */
	@Test
	public void testGetStandardHeight() {
		assertEquals(STANDARD_HEIGHT_CONTROL_BLOCK, shape.getStandardHeight().intValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.DefinitionShape#clipOn(guiLayer.shapes.Shape, types.ConnectionType)}.
	 */
	@Test
	public void testClipOn() {
		Shape clip = spy(new ControlShape("clip", BlockType.IF, new Coordinate(10, 10)));

		for (ConnectionType t : ConnectionType.values()) {
			Coordinate previous = shape.getCoordinate();
			shape.clipOn(clip, t);

			assertEquals(previous, shape.getCoordinate());

		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.DefinitionShape#draw(java.awt.Graphics)}.
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
	 * {@link guiLayer.shapes.DefinitionShape#fillShapeWithCoordinates()}.
	 */
	@Test
	public void testFillShapeWithCoordinates() {
		HashSet<Coordinate> coordinates = shape.fillShapeWithCoordinates();
		assertTrue(coordinates.size() != 0);	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.DefinitionShape#determineTotalHeight(java.util.Set)}.
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
	 * Test method for
	 * {@link guiLayer.shapes.DefinitionShape#defineConnectionTypes()}.
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
			connectionMap.put(ConnectionType.BODY, new Coordinate(shape.getX_coord() + 30, shape.getY_coord() + 30));
			assertEquals(connectionMap, f.get(shape));

		} catch (Exception e) {
			fail("coordinateConnectionMap was not initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.DefinitionShape#initDimensions()}.
	 */
	@Test
	public void testInitDimensions() {
		shape.initDimensions();
		verify(shape).setHeight(intCaptor.capture());
		assertEquals(90, intCaptor.getValue().intValue());
		verify(shape).setWidth(intCaptor.capture());
		assertEquals(90, intCaptor.getValue().intValue());	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.DefinitionShape#DefinitionShape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testDefinitionShape() {
		BlockType t = new BlockType("test2", BlockCategory.DEFINITION);
		DefinitionShape shape = new DefinitionShape("test", t, new Coordinate(0, 0));
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
