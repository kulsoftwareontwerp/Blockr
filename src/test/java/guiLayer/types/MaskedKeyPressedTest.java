/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * /** MaskedKeyPressedTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MaskedKeyPressedTest {

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
	 * Test method for {@link guiLayer.types.MaskedKeyPressed#run()}.
	 */
	@Test
	public void testRunBoth() {
		MaskedKeyBag bag = spy(new MaskedKeyBag(false, true));
		MaskedKeyPressed pressed = new MaskedKeyPressed(bag, true);
		pressed.run();
		verify(bag).pressCtrl(false);
		verify(bag).pressShift(false);
	}
	/**
	 * Test method for {@link guiLayer.types.MaskedKeyPressed#run()}.
	 */
	@Test
	public void testRunOnlyCtrl() {
		MaskedKeyBag bag = spy(new MaskedKeyBag(false, true));
		MaskedKeyPressed pressed = new MaskedKeyPressed(bag, false);
		pressed.run();
		verify(bag).pressCtrl(false);
		verify(bag,times(0)).pressShift(false);
	}
	/**
	 * Test method for
	 * {@link guiLayer.types.MaskedKeyPressed#MaskedKeyPressed(guiLayer.types.MaskedKeyBag, java.lang.Boolean)}.
	 */
	@Test
	public void testMaskedKeyPressed() {
		MaskedKeyBag bag = new MaskedKeyBag(false, true);
		MaskedKeyPressed pressed = new MaskedKeyPressed(bag, true);
		try {
			Field f;
			f = MaskedKeyPressed.class.getDeclaredField("resetBoth");
			f.setAccessible(true);
			assertTrue((Boolean) f.get(pressed));
			f = MaskedKeyPressed.class.getDeclaredField("bag");
			f.setAccessible(true);
			assertEquals(bag, f.get(pressed));
		} catch (Exception e) {
			fail("fields not initialized");
		}
	}

}
