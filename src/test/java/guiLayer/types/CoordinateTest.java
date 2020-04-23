/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * /** CoordinateTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CoordinateTest {

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
	 * Test method for {@link guiLayer.types.Coordinate#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		// The hashcode is considered to be not good if in the following range -1000 to
		// 1000
		// 5% or more of the hashcodes are equal.

		HashMap<Integer, Integer> hashCodes = new HashMap<Integer, Integer>();
		for (int x1 = -1000; x1 <= 1000; x1++) {
			for (int y1 = 1000; y1 >= -1000; y1--) {
				Coordinate c1 = new Coordinate(x1, y1);
				int hashCode = c1.hashCode();
				hashCodes.putIfAbsent(hashCode, 0);
				hashCodes.put(hashCode, hashCodes.get(hashCode) + 1);
			}
		}

		double percent = 0.05;
		assertFalse(hashCodes.values().stream().anyMatch(s -> s >= (percent * 2000)));

	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#Coordinate(int, int)}.
	 */
	@Test
	public void testCoordinate() {
		Coordinate c = new Coordinate(5, 3);

		try {
			Field f = Coordinate.class.getDeclaredField("x");
			f.setAccessible(true);
			assertTrue("x was not initialised", (int) f.get(c) == 5);
			f = Coordinate.class.getDeclaredField("y");
			f.setAccessible(true);
			assertTrue("y was not initialised", (int) f.get(c) == 3);

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			fail("One or more of the required fields were not declared.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#getX()}.
	 */
	@Test
	public void testGetX() {
		Coordinate c = new Coordinate(5, 3);

		assertEquals(5, c.getX());
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#setX(int)}.
	 */
	@Test
	public void testSetX() {
		Coordinate c = new Coordinate(5, 3);

		Coordinate c2 = c.setX(9);

		assertEquals(9, c2.getX());
		assertEquals(5, c.getX());

	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#getY()}.
	 */
	@Test
	public void testGetY() {
		Coordinate c = new Coordinate(5, 3);

		assertEquals(3, c.getY());
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#setY(int)}.
	 */
	@Test
	public void testSetY() {
		Coordinate c = new Coordinate(5, 3);

		Coordinate c2 = c.setY(9);

		assertEquals(9, c2.getY());
		assertEquals(3, c.getY());
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Coordinate c = new Coordinate(5, 3);
		assertTrue(c.equals(c));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testNotEqualsNull() {
		Coordinate c = new Coordinate(5, 3);
		assertFalse(c.equals(null));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testNotEqualsOtherClass() {
		Coordinate c = new Coordinate(5, 3);

		assertFalse(c.equals(DebugModus.CONNECTIONS));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testNotEqualsOtherCoordinateWrongX() {
		Coordinate c = new Coordinate(5, 3);
		Coordinate c2 = new Coordinate(3, 3);
		assertFalse(c.equals(c2));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testNotEqualsOtherCoordinateWrongY() {
		Coordinate c = new Coordinate(5, 3);
		Coordinate c2 = new Coordinate(5, 9);
		assertFalse(c.equals(c2));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsOtherCoordinate() {
		Coordinate c = new Coordinate(5, 9);
		Coordinate c2 = new Coordinate(5, 9);
		assertTrue(c.equals(c2));
	}

	
	/**
	 * Test method for
	 * {@link guiLayer.types.Coordinate#toString()}.
	 */
	@Test
	public void testToString() {
		Coordinate c = new Coordinate(5, 9);
		assertEquals("Coordinate [x=5, y=9]", c.toString());
	}
	
}
