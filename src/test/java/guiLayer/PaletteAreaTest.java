
package guiLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * PaletteAreaTest
 *
 * @version 0.1
 * @author group17
 */
public class PaletteAreaTest {

	
//	@Spy @InjectMocks
//	private PaletteArea paletteArea = new PaletteArea();


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
	 * Test method for {@link guiLayer.PaletteArea#PaletteArea(guiLayer.ShapeFactory)}.
	 */
	@Test
	public void testPaletteArea() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#isPaletteVisible()}.
	 */
	@Test
	public void testGetPaletteVisible() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#setPaletteVisible(java.lang.Boolean)}.
	 */
	@Test
	public void testSetPaletteVisible() {
		fail("Not yet implemented");
	}

	/**

	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint() {
//		Graphics g = Mockito.spy(Graphics.class);
//		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
//		c.paint(g);
//		verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));
//		verify(g,atLeastOnce()).drawImage(any(Image.class), any(Integer.class), any(Integer.class),any());

	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#checkIfInPalette(int)}.
	 */
	@Test
	public void testCheckIfInPalette() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapeFromCoordinate(int, int)}.
	 */
	@Test
	public void testGetShapeFromCoordinate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapesInPalette()}.
	 */
	@Test
	public void testGetShapesInPalette() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapeFactory()}.
	 */
	@Test
	public void testGetShapeFactory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#setShapeFactory(guiLayer.ShapeFactory)}.
	 */
	@Test
	public void testSetShapeFactory() {
		fail("Not yet implemented");
	}

}
