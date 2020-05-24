
package guiLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.awt.Color;
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
import org.mockito.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import events.BlockAddedEvent;
import guiLayer.commands.CommandHandler;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.CallFunctionShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import types.BlockCategory;
import types.BlockType;


/**
 * PaletteAreaTest
 *
 * @version 0.1
 * @author group17
 */
public class PaletteAreaTest implements Constants {
	
	@Mock(name = "shapeFactory")
	private ShapeFactory shapeFactory = new ShapeFactory();

	
	@Spy @InjectMocks
	private PaletteArea paletteArea = new PaletteArea(shapeFactory);
	
	private Shape testPaletteActionShape;
	private Shape testPaletteActionShape2;
	private Shape testPaletteActionShape3;
	private Shape testPaletteActionShape4;
	private Shape testPaletteActionShape5;
	private Shape testPaletteActionShape6;
	private Shape testPaletteActionShape7;
	private int initX;
	private int initY;
	private Coordinate coordinate;
	private BlockType moveForward;
	
	private CallFunctionShape testCallFunctionShape;
	private CallFunctionShape testCallFunctionShape2;
	private CallFunctionShape testCallFunctionShape3;
	
	private BlockAddedEvent blockAddedEventWithCall;
	private BlockAddedEvent blockAddedEventWithCall2;
	private BlockAddedEvent blockAddedEventWithCall3;


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initX = 5;
		initY = 5;
		coordinate = new Coordinate(initX, initY);
		
		moveForward = new BlockType("Move Forward", BlockCategory.ACTION);
		testPaletteActionShape = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape2 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape3 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape4 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape5 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape6 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testPaletteActionShape7 = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		
		
		BlockType call = new BlockType("Call", BlockCategory.CALL, "Call");
		testCallFunctionShape = Mockito.spy(new CallFunctionShape("40", call, coordinate));
		
		BlockType condition = new BlockType("Condition", BlockCategory.CONDITION, "Condition");
		testCallFunctionShape2 = Mockito.spy(new CallFunctionShape("40", condition, coordinate));
		
		BlockType definition = new BlockType("Definition", BlockCategory.DEFINITION, "Definition");
		testCallFunctionShape3 = Mockito.spy(new CallFunctionShape("41", definition, coordinate));
		
		blockAddedEventWithCall = new BlockAddedEvent("0", "", null, call, null, false);
		blockAddedEventWithCall2 = new BlockAddedEvent("40", "", null, call, null, false);
		blockAddedEventWithCall3 = Mockito.spy(new BlockAddedEvent("41", "", null, definition, null, false));
		
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
		
		PaletteArea pa = new PaletteArea(this.shapeFactory);

		try {
			Field f = PaletteArea.class.getDeclaredField("shapeFactory");
			f.setAccessible(true);
			assertNotNull(f.get(pa));
			assertEquals(shapeFactory, f.get(pa));
			
			Field f1 = PaletteArea.class.getDeclaredField("paletteVisible");
			f1.setAccessible(true);
			assertNotNull(f1.get(pa));
			assertEquals(true, f1.get(pa));
			
			Field f2 = PaletteArea.class.getDeclaredField("currentDrawHeight");
			f2.setAccessible(true);
			assertNotNull(f2.get(pa));
			assertEquals(0, f2.get(pa));
			
			Field f3 = PaletteArea.class.getDeclaredField("shapesInPalette");
			f3.setAccessible(true);
			assertNotNull(f3.get(pa));
			assertEquals(0, ((Set<Shape>)f3.get(pa)).size());
			
			

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	
	}
	
	/**
	 * Test method for {@link guiLayer.PaletteArea#PaletteArea(guiLayer.ShapeFactory)}.
	 */
	@Test
	public void testPaletteArea_ShapeFactoryEqualsNull() {
		shapeFactory = null;
		String excMessage = "A paletteArea needs a ShapeFactory";
		PaletteArea pa;
		try {
			pa =  new PaletteArea(this.shapeFactory);			
		} catch (Exception e) {
			assertEquals(excMessage, e.getMessage());
		}	
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#isPaletteVisible()}.
	 */
	@Test
	public void testGetPaletteVisible() {
		paletteArea.isPaletteVisible();
		try {		
			Field f1 = PaletteArea.class.getDeclaredField("paletteVisible");
			f1.setAccessible(true);
			assertNotNull(f1.get(paletteArea));
			assertEquals(true, f1.get(paletteArea));
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#setPaletteVisible(java.lang.Boolean)}.
	 */
	@Test
	public void testSetPaletteVisible_false() {
		paletteArea.setPaletteVisible(false);
		try {		
			Field f1 = PaletteArea.class.getDeclaredField("paletteVisible");
			f1.setAccessible(true);
			assertNotNull(f1.get(paletteArea));
			assertEquals(false, f1.get(paletteArea));
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**

	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_PaletteVisible_currentHeightNotZero() {
		
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);

		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, new Coordinate(ACTION_BLOCK_INIT_OFFSET,135) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WHILE, new Coordinate(CONTROL_BLOCK_INIT_OFFSET,240) )).thenReturn(testPaletteActionShape);
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.NOT, new Coordinate(OPERATOR_BLOCK_INIT_OFFSET,385) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION, new Coordinate(CONDITION_BLOCK_INIT_OFFSET,540) )).thenReturn(testPaletteActionShape);
		
		try {		
			Field f1 = PaletteArea.class.getDeclaredField("currentDrawHeight");
			f1.setAccessible(true);
			f1.set(paletteArea, 10);
			
			paletteArea.paint(g);
			
			verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));
			assertEquals(10, f1.get(paletteArea));
			
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}
	
	/**

	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_PaletteVisible() {
		
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);
		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, new Coordinate(ACTION_BLOCK_INIT_OFFSET,135) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WHILE, new Coordinate(CONTROL_BLOCK_INIT_OFFSET,240) )).thenReturn(testPaletteActionShape);
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.NOT, new Coordinate(OPERATOR_BLOCK_INIT_OFFSET,385) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION, new Coordinate(CONDITION_BLOCK_INIT_OFFSET,540) )).thenReturn(testPaletteActionShape);
		
		
		
		paletteArea.paint(g);
		
		verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));

	}
	
	/**

	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_PaletteNotVisible() {
		
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);
		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, new Coordinate(ACTION_BLOCK_INIT_OFFSET,135) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WHILE, new Coordinate(CONTROL_BLOCK_INIT_OFFSET,240) )).thenReturn(testPaletteActionShape);
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.NOT, new Coordinate(OPERATOR_BLOCK_INIT_OFFSET,385) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION, new Coordinate(CONDITION_BLOCK_INIT_OFFSET,540) )).thenReturn(testPaletteActionShape);
		
		try {		
			Field f1 = PaletteArea.class.getDeclaredField("paletteVisible");
			f1.setAccessible(true);
			f1.set(paletteArea, false);
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
		
		paletteArea.paint(g);
		
		verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));
		verify(g, atLeastOnce()).drawString("Too many blocks", 5, 30);

	}
	
	/**

	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_PaletteVisible_ShapesInPaletteDifferentSizeToBlockTypes() {
		
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.green);
		
		HashSet<Shape> shapesInPalette = new HashSet<Shape>();
		shapesInPalette.add(testPaletteActionShape);
		shapesInPalette.add(testPaletteActionShape2);
		shapesInPalette.add(testPaletteActionShape3);
		shapesInPalette.add(testPaletteActionShape4);
		shapesInPalette.add(testPaletteActionShape5);
		shapesInPalette.add(testPaletteActionShape6);
		shapesInPalette.add(testPaletteActionShape7);
		
		
		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, new Coordinate(ACTION_BLOCK_INIT_OFFSET,135) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WHILE, new Coordinate(CONTROL_BLOCK_INIT_OFFSET,240) )).thenReturn(testPaletteActionShape);
		
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.NOT, new Coordinate(OPERATOR_BLOCK_INIT_OFFSET,385) )).thenReturn(testPaletteActionShape);
		when(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION, new Coordinate(CONDITION_BLOCK_INIT_OFFSET,540) )).thenReturn(testPaletteActionShape);

		try {		
			Field f1 = PaletteArea.class.getDeclaredField("shapesInPalette");
			f1.setAccessible(true);
			f1.set(paletteArea, shapesInPalette);
		
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
		
		
		paletteArea.paint(g);
		
		verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));

	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#checkIfInPalette(int)}.
	 */
	@Test
	public void testCheckIfInPalette_Positive() {
		assertTrue(paletteArea.checkIfInPalette(0));
	}
	
	/**
	 * Test method for {@link guiLayer.PaletteArea#checkIfInPalette(int)}.
	 */
	@Test
	public void testCheckIfInPalette_false() {
		assertFalse(paletteArea.checkIfInPalette(5000));
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapeFromCoordinate(int, int)}.
	 */
	@Test
	public void testGetShapeFromCoordinate_NotPresent() {
		assertNull(paletteArea.getShapeFromCoordinate(initX, initY));
	}
	
	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapeFromCoordinate(int, int)}.
	 */
	@Test
	public void testGetShapeFromCoordinate_Present() {
		
		HashSet<Shape> shapesInPalette = new HashSet<Shape>();
		shapesInPalette.add(testPaletteActionShape);
		
		try {		
			Field f1 = PaletteArea.class.getDeclaredField("shapesInPalette");
			f1.setAccessible(true);
			f1.set(paletteArea, shapesInPalette);
		
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
		
		assertNotNull(paletteArea.getShapeFromCoordinate(initX, initY));
	}

	/**
	 * Test method for {@link guiLayer.PaletteArea#getShapesInPalette()}.
	 */
	@Test
	public void testGetShapesInPalette() {
		assertNotNull(paletteArea.getShapesInPalette());
	}

}
