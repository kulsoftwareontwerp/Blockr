/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockType;
import types.ConnectionType;

/**
 * ShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ShapeTest implements Constants {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	@Spy
	private Shape shape = spy(new ControlShape("test", BlockType.IF, new Coordinate(0, 0)));

	@Spy
	private Shape emptyShape = spy(new Shape("test", BlockType.NOT, new Coordinate(0, 0)) {

		@Override
		void initDimensions() {
		}

		@Override
		HashSet<Coordinate> fillShapeWithCoordinates() {
			return new HashSet<Coordinate>();
		}

		@Override
		public void draw(Graphics g) {
		}

		@Override
		public void defineConnectionTypes() {
		}

		@Override
		public void clipOn(Shape shape, ConnectionType connection) {
		}
	});

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
	 * Test method for {@link guiLayer.shapes.Shape#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Shape s = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));
		assertEquals("test".hashCode() + BlockType.IF.hashCode(), s.hashCode());

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviouslyConnectedVia()}.
	 */
	@Test
	public void testGetPreviouslyConnectedVia() {
		assertEquals(ConnectionType.NOCONNECTION, shape.getPreviouslyConnectedVia());
		shape.setConnectedVia(ConnectionType.BODY, true);
		assertEquals(ConnectionType.NOCONNECTION, shape.getPreviouslyConnectedVia());
		shape.setConnectedVia(ConnectionType.UP, true);
		assertEquals(ConnectionType.BODY, shape.getPreviouslyConnectedVia());

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#Shape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testShape() {
		Shape shape = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));
		try {
			Field f;
			f = Shape.class.getDeclaredField("id");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals("test", f.get(shape));

			f = Shape.class.getDeclaredField("type");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(BlockType.IF, f.get(shape));

			f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(new Coordinate(0, 0), f.get(shape));

			f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(new Coordinate(INVALID_COORDINATE, INVALID_COORDINATE), f.get(shape));

			f = Shape.class.getDeclaredField("coordinatesShape");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertTrue(((HashSet<Shape>) f.get(shape)).size() != 0);

			f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertTrue(((HashMap<ConnectionType, Coordinate>) f.get(shape)).size() != 0);

			f = Shape.class.getDeclaredField("connectedVia");
			f.setAccessible(true);
			assertNotNull(f.get(shape));

			f = Shape.class.getDeclaredField("previouslyConnectedVia");
			f.setAccessible(true);
			assertNotNull(f.get(shape));

			f = Shape.class.getDeclaredField("height");
			f.setAccessible(true);
			assertNotEquals(0, f.get(shape));

			f = Shape.class.getDeclaredField("previousHeight");
			f.setAccessible(true);
			assertNotNull(f.get(shape));

			f = Shape.class.getDeclaredField("width");
			f.setAccessible(true);
			assertNotEquals(0, f.get(shape));

			f = Shape.class.getDeclaredField("cloneSupported");
			f.setAccessible(true);
			assertNotNull(f.get(shape));

		} catch (Exception e) {
			fail("not all fields were initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getStandardHeight()}.
	 */
	@Test
	public void testGetStandardHeight() {
		Shape not = new UnaryOperatorShape("test", BlockType.NOT, new Coordinate(0, 0));
		assertEquals(STANDARD_HEIGHT_BLOCK, not.getStandardHeight().intValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#getTriggerSet(types.ConnectionType)}.
	 */
	@Test
	public void testGetTriggerSet() {
		HashMap<ConnectionType, Coordinate> coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>();
		coordinateConnectionMap.put(ConnectionType.DOWN, new Coordinate(5, 10));
		coordinateConnectionMap.put(ConnectionType.UP, new Coordinate(5, 0));
		coordinateConnectionMap.put(ConnectionType.BODY, new Coordinate(10, 4));
		coordinateConnectionMap.put(ConnectionType.CONDITION, new Coordinate(10, 2));

		try {
			Field f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			f.set(shape, coordinateConnectionMap);
		} catch (Exception e) {
			fail("field id is not present");
		}

		HashSet<Coordinate> triggerSet;

		for (ConnectionType ct : coordinateConnectionMap.keySet()) {
			triggerSet = new HashSet<Coordinate>();
			int x_current = coordinateConnectionMap.get(ct).getX();
			int y_current = coordinateConnectionMap.get(ct).getY();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					triggerSet.add(new Coordinate(i, j));
				}
			}

			assertEquals(triggerSet, shape.getTriggerSet(ct));

		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#getTriggerSet(types.ConnectionType)}.
	 */
	@Test
	public void testGetTriggerSetNullOrNotPresent() {
		HashMap<ConnectionType, Coordinate> coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>();
		coordinateConnectionMap.put(ConnectionType.DOWN, new Coordinate(5, 10));
		coordinateConnectionMap.put(ConnectionType.UP, new Coordinate(5, 0));
		coordinateConnectionMap.put(ConnectionType.BODY, new Coordinate(10, 4));
		coordinateConnectionMap.put(ConnectionType.CONDITION, new Coordinate(10, 2));

		try {
			Field f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			f.set(shape, coordinateConnectionMap);
		} catch (Exception e) {
			fail("field id is not present");
		}

		HashSet<Coordinate> triggerSet;

		triggerSet = new HashSet<Coordinate>();

		assertEquals(triggerSet, shape.getTriggerSet(null));
		assertEquals(triggerSet, shape.getTriggerSet(ConnectionType.LEFT));

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getId()}.
	 */
	@Test
	public void testGetId() {
		assertEquals("test", shape.getId());

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setId(java.lang.String)}.
	 */
	@Test
	public void testSetId() {
		shape.setId("test2");
		try {
			Field f = Shape.class.getDeclaredField("id");
			f.setAccessible(true);
			assertEquals("test2", f.get(shape));
		} catch (Exception e) {
			fail("field id is not present");
		}

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(BlockType.IF, shape.getType());
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setType(types.BlockType)}.
	 */
	@Test
	public void testSetType() {
		shape.setType(BlockType.WHILE);
		try {
			Field f = Shape.class.getDeclaredField("type");
			f.setAccessible(true);
			assertEquals(BlockType.WHILE, f.get(shape));
		} catch (Exception e) {
			fail("field type is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setType(types.BlockType)}.
	 */
	@Test
	public void testSetTypeNull() {
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage("there must be a type set for the shape");
		shape.setType(null);
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousX_coord()}.
	 */
	@Test
	public void testGetPreviousX_coord() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, new Coordinate(5, 9));

			assertEquals(5, shape.getPreviousX_coord());

		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousX_coord()}.
	 */
	@Test
	public void testGetPreviousX_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, null);

			assertEquals(0, shape.getPreviousX_coord());

		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousX_coord(int)}.
	 */
	@Test
	public void testSetPreviousX_coord() {
		shape.setPreviousX_coord(99);
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			assertEquals(99, ((Coordinate) f.get(shape)).getX());
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousX_coord(int)}.
	 */
	@Test
	public void testSetPreviousX_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, null);

			shape.setPreviousX_coord(99);

			assertEquals(new Coordinate(99, 0), f.get(shape));
		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousY_coord()}.
	 */
	@Test
	public void testGetPreviousY_coord() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, new Coordinate(5, 9));

			assertEquals(9, shape.getPreviousY_coord());

		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousY_coord()}.
	 */
	@Test
	public void testGetPreviousY_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, null);

			assertEquals(0, shape.getPreviousY_coord());

		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousY_coord(int)}.
	 */
	@Test
	public void testSetPreviousY_coord() {
		shape.setPreviousY_coord(77);
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			assertEquals(77, ((Coordinate) f.get(shape)).getY());
		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousY_coord(int)}.
	 */
	@Test
	public void testSetPreviousY_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			f.set(shape, null);

			shape.setPreviousY_coord(77);

			assertEquals(new Coordinate(0, 77), f.get(shape));
		} catch (Exception e) {
			fail("field previousCoordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinate()}.
	 */
	@Test
	public void testGetCoordinate() {
		Coordinate c = new Coordinate(5, 9);
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, c);

			assertEquals(c, shape.getCoordinate());

		} catch (Exception e) {
			fail("field coordinate is not present");
		}	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getX_coord()}.
	 */
	@Test
	public void testGetX_coord() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, new Coordinate(5, 9));

			assertEquals(5, shape.getX_coord());

		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getX_coord()}.
	 */
	@Test
	public void testGetX_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, null);

			assertEquals(0, shape.getX_coord());

		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setX_coord(int)}.
	 */
	@Test
	public void testSetX_coord() {
		shape.setX_coord(99);
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			assertEquals(99, ((Coordinate) f.get(shape)).getX());
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setX_coord(int)}.
	 */
	@Test
	public void testSetX_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, null);

			shape.setX_coord(99);

			assertEquals(new Coordinate(99, 0), f.get(shape));
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getY_coord()}.
	 */
	@Test
	public void testGetY_coord() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, new Coordinate(5, 9));

			assertEquals(9, shape.getY_coord());

		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getY_coord()}.
	 */
	@Test
	public void testGetY_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, null);

			assertEquals(0, shape.getY_coord());

		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setY_coord(int)}.
	 */
	@Test
	public void testSetY_coord() {
		shape.setY_coord(97);
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			assertEquals(97, ((Coordinate) f.get(shape)).getY());
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setX_coord(int)}.
	 */
	@Test
	public void testSetY_coord_null() {
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			f.set(shape, null);

			shape.setY_coord(97);

			assertEquals(new Coordinate(0, 97), f.get(shape));
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#setCoordinate(guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testSetCoordinate() {
		shape.setCoordinate(new Coordinate(5, 3));
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);
			assertEquals(new Coordinate(5, 3), f.get(shape));
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#setCoordinate(guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testSetCoordinateNull() {

		shape.setCoordinate(null);
		try {
			Field f = Shape.class.getDeclaredField("coordinate");
			f.setAccessible(true);

			assertEquals(new Coordinate(0, 0), f.get(shape));
		} catch (Exception e) {
			fail("field coordinate is not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinatesShape()}.
	 */
	@Test
	public void testGetCoordinatesShape() {
		HashSet<Coordinate> coordinatesShape = new HashSet<Coordinate>();
		coordinatesShape.add(new Coordinate(5, 10));
		coordinatesShape.add(new Coordinate(0, 11));
		coordinatesShape.add(new Coordinate(9, 12));
		coordinatesShape.add(new Coordinate(8, 13));
		coordinatesShape.add(new Coordinate(6, 10));
		coordinatesShape.add(new Coordinate(9, 10));
		
		try {

			Field f;

			f= Shape.class.getDeclaredField("coordinatesShape");
			f.setAccessible(true);
			f.set(shape, coordinatesShape);
			

		} catch (Exception e) {
			fail("fields are not present");
		}	
		assertEquals(coordinatesShape, shape.getCoordinatesShape());
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setCoordinatesShape()}.
	 */
	@Test
	public void testSetCoordinatesShape() {
		shape.setCoordinatesShape();
		verify(shape, atLeastOnce()).fillShapeWithCoordinates();

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinateConnectionMap()}.
	 */
	@Test
	public void testGetCoordinateConnectionMap() {
		assertNotEquals(0, shape.getCoordinateConnectionMap().size());

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#setCoordinateConnectionMap(java.util.HashMap)}.
	 */
	@Test
	public void testSetCoordinateConnectionMap() {
		HashMap<ConnectionType, Coordinate> testMap = new HashMap<ConnectionType, Coordinate>();
		testMap.put(ConnectionType.DOWN, new Coordinate(5, 10));
		testMap.put(ConnectionType.UP, new Coordinate(5, 0));
		testMap.put(ConnectionType.BODY, new Coordinate(10, 4));
		testMap.put(ConnectionType.CONDITION, new Coordinate(10, 2));
		shape.setCoordinateConnectionMap(testMap);

		try {
			Field f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			assertEquals(testMap, f.get(shape));
		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getDefinitionShapeID()}.
	 */
	@Test
	public void testGetDefinitionShapeID() {
		assertEquals(null, shape.getDefinitionShapeID());

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getConnectedVia()}.
	 */
	@Test
	public void testGetConnectedVia() {
		try {
			Field cv = Shape.class.getDeclaredField("connectedVia");
			Field tcv = Shape.class.getDeclaredField("tempConnectedVia");
			cv.setAccessible(true);
			tcv.setAccessible(true);

			assertEquals(ConnectionType.NOCONNECTION, shape.getConnectedVia());
			cv.set(shape, ConnectionType.BODY);
			assertEquals(ConnectionType.BODY, shape.getConnectedVia());
			tcv.set(shape, ConnectionType.DOWN);
			assertEquals(ConnectionType.DOWN, shape.getConnectedVia());
			tcv.set(shape, null);
			assertEquals(ConnectionType.BODY, shape.getConnectedVia());
			tcv.set(shape, ConnectionType.DOWN);
			assertEquals(ConnectionType.DOWN, shape.getConnectedVia());
			cv.set(shape, ConnectionType.DOWN);
			tcv.set(shape, null);
			assertEquals(ConnectionType.DOWN, shape.getConnectedVia());
		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#setConnectedVia(types.ConnectionType, java.lang.Boolean)}.
	 */
	@Test
	public void testSetConnectedVia() {
		try {
			Field cv = Shape.class.getDeclaredField("connectedVia");
			Field tcv = Shape.class.getDeclaredField("tempConnectedVia");
			cv.setAccessible(true);
			tcv.setAccessible(true);

			shape.setConnectedVia(ConnectionType.BODY, true);
			assertEquals(ConnectionType.BODY, cv.get(shape));
			assertEquals(null, tcv.get(shape));
			shape.setConnectedVia(ConnectionType.DOWN, false);
			assertEquals(ConnectionType.BODY, cv.get(shape));
			assertEquals(ConnectionType.DOWN, tcv.get(shape));
		} catch (Exception e) {
			fail("fields are not present");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.shapes.Shape#persistConnectedVia(java.lang.Boolean)}.
	 */
	@Test
	public void testPersistConnectedVia() {
		try {
			Field cv = Shape.class.getDeclaredField("connectedVia");
			Field tcv = Shape.class.getDeclaredField("tempConnectedVia");
			cv.setAccessible(true);
			tcv.setAccessible(true);

			cv.set(shape, ConnectionType.BODY);
			tcv.set(shape, ConnectionType.DOWN);

			shape.persistConnectedVia(true);
			assertEquals(ConnectionType.DOWN, cv.get(shape));
			assertEquals(null, tcv.get(shape));

			cv.set(shape, ConnectionType.BODY);
			tcv.set(shape, ConnectionType.DOWN);
			shape.persistConnectedVia(false);
			assertEquals(ConnectionType.BODY, cv.get(shape));
			assertEquals(null, tcv.get(shape));

			cv.set(shape, ConnectionType.BODY);
			tcv.set(shape, null);
			shape.persistConnectedVia(true);
			assertEquals(ConnectionType.BODY, cv.get(shape));
			assertEquals(null, tcv.get(shape));

		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getHeight()}.
	 */
	@Test
	public void testGetHeight() {

		assertEquals(0, emptyShape.getHeight());
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setHeight(int)}.
	 */
	@Test
	public void testSetHeight() {
		try {

			Field h = Shape.class.getDeclaredField("height");
			Field ph = Shape.class.getDeclaredField("previousHeight");
			h.setAccessible(true);
			ph.setAccessible(true);

			emptyShape.setHeight(40);

			assertEquals(40, h.get(emptyShape));
			assertEquals(0, ph.get(emptyShape));

			emptyShape.setHeight(10);

			assertEquals(10, h.get(emptyShape));
			assertEquals(40, ph.get(emptyShape));

		} catch (Exception e) {
			fail("fields are not present");
		}

	}
	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinateConnectionMap()}.
	 */
	@Test
	public void testDetermineTotalHeight() {
		emptyShape.determineTotalHeight(new HashSet<Shape>());
		
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getWidth()}.
	 */
	@Test
	public void testGetWidth() {
		assertEquals(0, emptyShape.getWidth());
		try {

			Field w = Shape.class.getDeclaredField("width");
			w.setAccessible(true);

			w.set(shape, 40);

			assertEquals(40, shape.getWidth());

		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setWidth(int)}.
	 */
	@Test
	public void testSetWidth() {
		try {

			Field w = Shape.class.getDeclaredField("width");
			w.setAccessible(true);

			emptyShape.setWidth(60);

			assertEquals(60, w.get(emptyShape));

		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#clone()}.
	 */
	@Test
	public void testClone() {
		Shape start = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));
		HashMap<ConnectionType, Coordinate> coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>();
		coordinateConnectionMap.put(ConnectionType.DOWN, new Coordinate(5, 10));
		coordinateConnectionMap.put(ConnectionType.UP, new Coordinate(5, 0));
		coordinateConnectionMap.put(ConnectionType.BODY, new Coordinate(10, 4));
		coordinateConnectionMap.put(ConnectionType.CONDITION, new Coordinate(10, 2));
		HashSet<Coordinate> coordinatesShape = new HashSet<Coordinate>();
		coordinatesShape.add(new Coordinate(5, 10));
		coordinatesShape.add(new Coordinate(0, 11));
		coordinatesShape.add(new Coordinate(9, 12));
		coordinatesShape.add(new Coordinate(8, 13));
		coordinatesShape.add(new Coordinate(6, 10));
		coordinatesShape.add(new Coordinate(9, 10));
		try {

			Field f;
			f= Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			f.set(start, coordinateConnectionMap);

			f= Shape.class.getDeclaredField("coordinatesShape");
			f.setAccessible(true);
			f.set(start, coordinatesShape);
			

		} catch (Exception e) {
			fail("fields are not present");
		}	
		
		Shape clone = start.clone();
		assertEquals(start, clone);
		
		try {

			Field f;
			f= Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			assertEquals(coordinateConnectionMap, f.get(clone));

			f= Shape.class.getDeclaredField("coordinatesShape");
			f.setAccessible(true);
			assertEquals(coordinatesShape, f.get(clone));
			

		} catch (Exception e) {
			fail("fields are not present");
		}	
		
		
	}
	
	/**
	 * Test method for {@link guiLayer.shapes.Shape#clone()}.
	 */
	@Test
	public void testCloneCloneNotSupported() {
		exceptionRule.expect(RuntimeException.class);
		try {

			Field cloneSupported = Shape.class.getDeclaredField("cloneSupported");
			cloneSupported.setAccessible(true);

			cloneSupported.set(emptyShape, false);
		} catch (Exception e) {
			fail("fields are not present");
		}	
	
		Shape s = emptyShape.clone();
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEqualsObject() {
		Shape shape = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));

		assertFalse(shape.equals(new Coordinate(0, 3)));
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#equals(java.lang.Object)}.
	 */
	@Test
	public void testNotEqualsID() {
		Shape shape = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));

		assertFalse(shape.equals(new ControlShape("wrongID", BlockType.IF, new Coordinate(0, 0))));
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#equals(java.lang.Object)}.
	 */
	@Test
	public void testNotEqualsBlockType() {
		Shape shape = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));

		assertFalse(shape.equals(new ControlShape("test", BlockType.WHILE, new Coordinate(0, 0))));
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals() {
		Shape shape = new ControlShape("test", BlockType.IF, new Coordinate(0, 0));
		assertTrue(shape.equals(new ControlShape("test", BlockType.IF, new Coordinate(0, 0))));
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousHeight()}.
	 */
	@Test
	public void testGetPreviousHeight() {
		assertEquals(0, emptyShape.getHeight());
		try {

			Field h = Shape.class.getDeclaredField("height");
			Field ph = Shape.class.getDeclaredField("previousHeight");
			h.setAccessible(true);
			ph.setAccessible(true);

			h.set(emptyShape, 40);

			assertEquals(0, emptyShape.getPreviousHeight());

			h.set(emptyShape, 10);
			ph.set(emptyShape, 40);

			assertEquals(40, emptyShape.getPreviousHeight());

		} catch (Exception e) {
			fail("fields are not present");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#idForDisplay()}.
	 */
	@Test
	public void testIdForDisplayDebugMode() {
		CanvasWindow.debugModus = DebugModus.IDS;
		String idForDisplay = emptyShape.idForDisplay();
		verify(emptyShape, atLeastOnce()).getId();
		assertEquals(" test", idForDisplay);

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#idForDisplay()}.
	 */
	@Test
	public void testIdForDisplayNoDebugMode() {
		CanvasWindow.debugModus = DebugModus.NONE;
		String idForDisplay = emptyShape.idForDisplay();
		assertEquals("", idForDisplay);
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("Shape [id=test, coord=" + shape.getCoordinate() + "]", shape.toString());

	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getHeightDiff()}.
	 */
	@Test
	public void testGetHeightDiff() {
		try {

			Field h = Shape.class.getDeclaredField("height");
			Field ph = Shape.class.getDeclaredField("previousHeight");
			h.setAccessible(true);
			ph.setAccessible(true);

			h.set(emptyShape, 10);
			ph.set(emptyShape, 40);

			assertEquals(-30, emptyShape.getHeightDiff());

			h.set(emptyShape, 40);
			ph.set(emptyShape, 10);

			assertEquals(30, emptyShape.getHeightDiff());

			h.set(emptyShape, 40);
			ph.set(emptyShape, 40);

			assertEquals(0, emptyShape.getHeightDiff());

		} catch (Exception e) {
			fail("fields are not present");
		}
	}

}
