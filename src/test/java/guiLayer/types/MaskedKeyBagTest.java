/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * /** MaskedKeyBagTest
 * 
 * @version 0.1
 * @author group17
 *
 */
public class MaskedKeyBagTest {

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
	 * {@link guiLayer.types.MaskedKeyBag#MaskedKeyBag(java.lang.Boolean, java.lang.Boolean)}.
	 */
	@Test
	public void testMaskedKeyBag() {
		MaskedKeyBag bag = new MaskedKeyBag(false, true);
		try {
			Field f;
			f = MaskedKeyBag.class.getDeclaredField("shift");
			f.setAccessible(true);
			assertFalse((Boolean) f.get(bag));
			f = MaskedKeyBag.class.getDeclaredField("ctrl");
			f.setAccessible(true);
			assertTrue((Boolean) f.get(bag));
		} catch (Exception e) {
			System.out.println("Exception while retrieving fields");
		}
	}

	/**
	 * Test method for {@link guiLayer.types.MaskedKeyBag#isShiftPressed()}.
	 */
	@Test
	public void testIsShiftPressed() {
		MaskedKeyBag bag = new MaskedKeyBag(false, true);
		assertFalse(bag.isShiftPressed());
		bag = new MaskedKeyBag(true, false);
		assertTrue(bag.isShiftPressed());
	}

	/**
	 * Test method for {@link guiLayer.types.MaskedKeyBag#isCtrlPressed()}.
	 */
	@Test
	public void testIsCtrlPressed() {
		MaskedKeyBag bag = new MaskedKeyBag(false, true);
		assertTrue(bag.isCtrlPressed());
		bag = new MaskedKeyBag(true, false);
		assertFalse(bag.isCtrlPressed());
	}

	/**
	 * Test method for {@link guiLayer.types.MaskedKeyBag#pressShift(boolean)}.
	 */
	@Test
	public void testPressShift() {
		MaskedKeyBag bag = new MaskedKeyBag(false, true);

		try {
			Field f;
			bag.pressShift(true);
			f = MaskedKeyBag.class.getDeclaredField("shift");
			f.setAccessible(true);
			assertTrue((Boolean) f.get(bag));
			bag.pressShift(false);
			f = MaskedKeyBag.class.getDeclaredField("shift");
			f.setAccessible(true);
			assertFalse((Boolean) f.get(bag));
		} catch (Exception e) {
			System.out.println("Exception while retrieving fields");
		}
	}

	/**
	 * Test method for {@link guiLayer.types.MaskedKeyBag#pressCtrl(boolean)}.
	 */
	@Test
	public void testPressCtrl() {
		MaskedKeyBag bag = new MaskedKeyBag(true, false);

		try {
			Field f;
			bag.pressCtrl(true);
			f = MaskedKeyBag.class.getDeclaredField("ctrl");
			f.setAccessible(true);
			assertTrue((Boolean) f.get(bag));
			bag.pressCtrl(false);
			f = MaskedKeyBag.class.getDeclaredField("ctrl");
			f.setAccessible(true);
			assertFalse((Boolean) f.get(bag));
		} catch (Exception e) {
			System.out.println("Exception while retrieving fields");
		}	}

}
