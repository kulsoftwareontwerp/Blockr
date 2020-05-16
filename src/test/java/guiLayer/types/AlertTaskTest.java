/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;

/**
/**
 * AlertTaskTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AlertTaskTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	private CanvasWindow window;

	@Captor
	private ArgumentCaptor<String> message;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		window = spy(new CanvasWindow("test", mock(DomainController.class)));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link guiLayer.types.AlertTask#run()}.
	 */
	@Test
	public void testRun() {
		AlertTask task = spy(new AlertTask(window,"test"));
		task.run();
		verify(window).showAlert(null);
	
	}

	/**
	 * Test method for {@link guiLayer.types.AlertTask#AlertTask(guiLayer.CanvasWindow, java.lang.String)}.
	 */
	@Test
	public void testAlertTask() {
		AlertTask at = new AlertTask(window, "test");
		try {
			Field f = AlertTask.class.getDeclaredField("window");
			f.setAccessible(true);
			assertEquals(window,f.get(at));
		} catch (Exception e) {
			fail("fields not initialized");
		}
	
		verify(window).showAlert(message.capture());
		assertEquals("test", message.getValue());
	
	}
	
	/**
	 * Test method for {@link guiLayer.types.AlertTask#AlertTask(guiLayer.CanvasWindow, java.lang.String)}.
	 */
	@Test
	public void testAlertTaskNoWindow() {
		String excMessage = "This task needs a window to anounce a message on.";
		exceptionRule.expect(NullPointerException.class);
		exceptionRule.expectMessage(excMessage);
		
		AlertTask at = new AlertTask(null, "test");
		try {
			Field f = AlertTask.class.getDeclaredField("window");
			f.setAccessible(true);
			assertEquals(window,f.get(at));
		} catch (Exception e) {
			fail("fields not initialized");
		}
	
		verify(window,times(0)).showAlert(message.capture());	
	}

}
