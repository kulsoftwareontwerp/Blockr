/**
 * 
 */
package guiLayer.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Spy;

import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import types.BlockType;
import types.ConnectionType;

/**
 * ShapeTest
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ShapeTest implements Constants {
	
	@Spy
	private Shape shape= new ControlShape("test", BlockType.IF, new Coordinate(0,0));

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
		assertEquals("test".hashCode()+BlockType.IF.hashCode(),shape.hashCode());
		
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
	 * Test method for {@link guiLayer.shapes.Shape#Shape(java.lang.String, types.BlockType, guiLayer.types.Coordinate)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testShape() {
		Shape shape = new ControlShape("test",BlockType.IF , new Coordinate(0,0));
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
			assertEquals(new Coordinate(0,0), f.get(shape));
			
			f = Shape.class.getDeclaredField("previousCoordinate");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertEquals(new Coordinate(INVALID_COORDINATE,INVALID_COORDINATE), f.get(shape));
			
			f = Shape.class.getDeclaredField("coordinatesShape");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertTrue(((HashSet<Shape>)f.get(shape)).size()!=0);
			
			f = Shape.class.getDeclaredField("coordinateConnectionMap");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			assertTrue(((HashMap<ConnectionType, Coordinate>)f.get(shape)).size()!=0);
			
			f = Shape.class.getDeclaredField("connectedVia");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			
			f = Shape.class.getDeclaredField("previouslyConnectedVia");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			
			f = Shape.class.getDeclaredField("height");
			f.setAccessible(true);
			assertNotEquals(0,f.get(shape));
			
			f = Shape.class.getDeclaredField("previousHeight");
			f.setAccessible(true);
			assertNotNull(f.get(shape));
			
			f = Shape.class.getDeclaredField("width");
			f.setAccessible(true);
			assertNotEquals(0,f.get(shape));
			
			f = Shape.class.getDeclaredField("cloneSupported");
			f.setAccessible(true);
			assertNotNull(f.get(shape));

			
			
			
		}catch(Exception e) {
			fail("not all fields were initialized correctly");
		}
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#clipOn(guiLayer.shapes.Shape, types.ConnectionType)}.
	 */
	@Test
	public void testClipOn() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#draw(java.awt.Graphics)}.
	 */
	@Test
	public void testDraw() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#fillShapeWithCoordinates()}.
	 */
	@Test
	public void testFillShapeWithCoordinates() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#determineTotalHeight(java.util.Set)}.
	 */
	@Test
	public void testDetermineTotalHeight() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getStandardHeight()}.
	 */
	@Test
	public void testGetStandardHeight() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getTriggerSet(types.ConnectionType)}.
	 */
	@Test
	public void testGetTriggerSet() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getId()}.
	 */
	@Test
	public void testGetId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setId(java.lang.String)}.
	 */
	@Test
	public void testSetId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getType()}.
	 */
	@Test
	public void testGetType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setType(types.BlockType)}.
	 */
	@Test
	public void testSetType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getX_coord()}.
	 */
	@Test
	public void testGetX_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setX_coord(int)}.
	 */
	@Test
	public void testSetX_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getY_coord()}.
	 */
	@Test
	public void testGetY_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setY_coord(int)}.
	 */
	@Test
	public void testSetY_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setCoordinate(guiLayer.types.Coordinate)}.
	 */
	@Test
	public void testSetCoordinate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinatesShape()}.
	 */
	@Test
	public void testGetCoordinatesShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setCoordinatesShape()}.
	 */
	@Test
	public void testSetCoordinatesShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinateConnectionMap()}.
	 */
	@Test
	public void testGetCoordinateConnectionMap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setCoordinateConnectionMap(java.util.HashMap)}.
	 */
	@Test
	public void testSetCoordinateConnectionMap() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getDefinitionShapeID()}.
	 */
	@Test
	public void testGetDefinitionShapeID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getConnectedVia()}.
	 */
	@Test
	public void testGetConnectedVia() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setConnectedVia(types.ConnectionType, java.lang.Boolean)}.
	 */
	@Test
	public void testSetConnectedVia() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#persistConnectedVia(java.lang.Boolean)}.
	 */
	@Test
	public void testPersistConnectedVia() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getHeight()}.
	 */
	@Test
	public void testGetHeight() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setHeight(int)}.
	 */
	@Test
	public void testSetHeight() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getWidth()}.
	 */
	@Test
	public void testGetWidth() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setWidth(int)}.
	 */
	@Test
	public void testSetWidth() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousX_coord()}.
	 */
	@Test
	public void testGetPreviousX_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousX_coord(int)}.
	 */
	@Test
	public void testSetPreviousX_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousY_coord()}.
	 */
	@Test
	public void testGetPreviousY_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#setPreviousY_coord(int)}.
	 */
	@Test
	public void testSetPreviousY_coord() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#clone()}.
	 */
	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getPreviousHeight()}.
	 */
	@Test
	public void testGetPreviousHeight() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#idForDisplay()}.
	 */
	@Test
	public void testIdForDisplay() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#toString()}.
	 */
	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getCoordinate()}.
	 */
	@Test
	public void testGetCoordinate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.shapes.Shape#getHeightDiff()}.
	 */
	@Test
	public void testGetHeightDiff() {
		fail("Not yet implemented");
	}

}
