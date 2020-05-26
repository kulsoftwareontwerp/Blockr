/**
 * 
 */
package guiLayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.kuleuven.swop.group17.Graphics.CanvasResource;

import applicationLayer.DomainController;
import commands.AddBlockCommand;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.PanelChangeEvent;
import events.UpdateHighlightingEvent;
import guiLayer.commands.CommandHandler;
import guiLayer.commands.DomainMoveCommand;
import guiLayer.commands.ExecuteBlockCommand;
import guiLayer.commands.GuiMoveCommand;
import guiLayer.commands.ResetCommand;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.CallFunctionShape;
import guiLayer.shapes.ConditionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.shapes.UnaryOperatorShape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import guiLayer.types.GuiSnapshot;
import guiLayer.types.MaskedKeyBag;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;

/**
 * CanvasWindowTest
 *
 * @version 0.1
 * @author group17
 */
public class CanvasWindowTest implements Constants {

	private static final int MASKEDKEY_DURATION = 500;

	@Mock(name = "maskedKeyTimer")
	private Timer maskedKeyTimer;

	@Mock(name = "maskedKeyBag")
	private MaskedKeyBag maskedKeyBag;

	@Mock(name = "currentSnapshot")
	private GuiSnapshot currentSnapshot;

	@Mock(name = "shapeFactory")
	private ShapeFactory shapeFactory;

	@Mock(name = "commandHandler")
	private CommandHandler commandHandler;

	@Mock(name = "programArea")
	private ProgramArea programArea;

	@Mock(name = "paletteArea")
	private PaletteArea paletteArea;

	@Mock(name = "domainController")
	private DomainController domainController;

	@Spy
	@InjectMocks
	private CanvasWindow canvasWindow = new CanvasWindow(currentSnapshot, shapeFactory, commandHandler, programArea,
			domainController, paletteArea, maskedKeyTimer, maskedKeyBag);

	@Captor
	private ArgumentCaptor<CanvasWindow> canvasWindowArgumentCaptor = ArgumentCaptor.forClass(CanvasWindow.class);

	private int initX;
	private int initY;
	private Coordinate coordinate;
	private Coordinate zeroCoordinate;

	private BlockAddedEvent blockAddedEvent;
	private BlockAddedEvent blockAddedEventWithCall;
	private BlockAddedEvent blockAddedEventWithCall2;
	private BlockAddedEvent blockAddedEventWithCall3;
	private BlockAddedEvent blockAddedEventWithLinkedShapeUP;
	private BlockAddedEvent blockAddedEventWithLinkedShapeBODY;
	private BlockAddedEvent blockAddedEventWithLinkedShapeCONDITION;
	private BlockAddedEvent blockAddedEventWithLinkedShapeDOWN;
	private BlockAddedEvent blockAddedEventWithLinkedShapeLEFT_parentOperand;
	private BlockAddedEvent blockAddedEventWithLinkedShapeLEFT_parentControl;
	private BlockAddedEvent blockAddedEventWithLinkedShapeNOCONNECTION;
	private BlockAddedEvent blockAddedEventWithLinkedShapeOPERAND;
	private BlockRemovedEvent blockRemovedEvent;
	private BlockRemovedEvent blockRemovedEventNew;
	private BlockRemovedEvent blockRemovedEventWithBeforeMove;
	private BlockRemovedEvent blockRemovedEventWithBeforeMoveWithBody;
	private BlockChangeEvent blockChangedEvent;
	private UpdateHighlightingEvent updateHighlightingEvent;
	private PanelChangeEvent panelChangeEventTrue;
	private PanelChangeEvent panelChangeEventFalse;

	private HashMap<String, Coordinate> coordinates;

	private Shape testOperandShape;
	private Shape testOperandShape2;
	private Shape testActionShape;
	private Shape testActionShapeWithZeroCoordinate;
	private Shape testPaletteActionShape;

	private ControlShape testControlShape;
	private ControlShape testControlShapeUnder;

	private UnaryOperatorShape testUnaryShape;

	private CallFunctionShape testCallFunctionShape;
	private CallFunctionShape testCallFunctionShape2;
	private CallFunctionShape testCallFunctionShape3;

	private HashSet<Shape> shapesInProgramArea;
	private BlockType moveForward;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		initX = 5;
		initY = 5;
		coordinate = new Coordinate(initX, initY);
		zeroCoordinate = new Coordinate(0, 0);

		moveForward = new BlockType("Move Forward", BlockCategory.ACTION);

		blockAddedEvent = new BlockAddedEvent("0", "", null, moveForward, null, false);
		blockAddedEventWithLinkedShapeUP = new BlockAddedEvent("0", "1", ConnectionType.UP, moveForward, null, false);
		blockAddedEventWithLinkedShapeBODY = new BlockAddedEvent("0", "1", ConnectionType.BODY, moveForward, null,
				false);
		blockAddedEventWithLinkedShapeCONDITION = new BlockAddedEvent("0", "1", ConnectionType.CONDITION, moveForward,
				null, false);
		blockAddedEventWithLinkedShapeDOWN = new BlockAddedEvent("0", "1", ConnectionType.DOWN, moveForward, null,
				false);
		blockAddedEventWithLinkedShapeLEFT_parentOperand = new BlockAddedEvent("20", "21", ConnectionType.LEFT,
				BlockType.NOT, null, false);
		blockAddedEventWithLinkedShapeLEFT_parentControl = new BlockAddedEvent("20", "10", ConnectionType.LEFT,
				BlockType.NOT, null, false);
		blockAddedEventWithLinkedShapeNOCONNECTION = new BlockAddedEvent("0", "1", ConnectionType.NOCONNECTION,
				moveForward, null, false);
		blockAddedEventWithLinkedShapeOPERAND = new BlockAddedEvent("0", "1", ConnectionType.OPERAND, moveForward, null,
				false);
		blockRemovedEvent = Mockito.spy(new BlockRemovedEvent("0", "", ConnectionType.NOCONNECTION, null, false));
		blockRemovedEventNew = Mockito.spy(new BlockRemovedEvent("40", "", ConnectionType.NOCONNECTION, null, false));
		blockRemovedEventWithBeforeMove = Mockito
				.spy(new BlockRemovedEvent("40", "10", ConnectionType.NOCONNECTION, null, false));
		blockRemovedEventWithBeforeMoveWithBody = Mockito
				.spy(new BlockRemovedEvent("40", "10", ConnectionType.BODY, null, false));
		Set<String> changedBlocks = new HashSet<>();
		changedBlocks.add("11");
		blockChangedEvent = Mockito.spy(new BlockChangeEvent("11", "11", "", ConnectionType.NOCONNECTION, "10",
				ConnectionType.DOWN, changedBlocks));
		updateHighlightingEvent = new UpdateHighlightingEvent("0");
		panelChangeEventTrue = new PanelChangeEvent(true);
		panelChangeEventFalse = new PanelChangeEvent(false);

		coordinates = new HashMap<String, Coordinate>();

		testActionShape = Mockito.spy(new ActionShape("0", moveForward, coordinate));
		testActionShapeWithZeroCoordinate = Mockito.spy(new ActionShape(blockAddedEvent.getAddedBlockID(),
				blockAddedEvent.getAddedBlockType(), zeroCoordinate));

		testControlShape = Mockito.spy(new ControlShape("10", BlockType.IF, coordinate));
		testControlShapeUnder = Mockito.spy(new ControlShape("11", BlockType.IF, coordinate));

		testOperandShape = Mockito.spy(new ConditionShape("20", BlockType.NOT, coordinate));
		testOperandShape2 = Mockito.spy(new ConditionShape("21", BlockType.NOT, coordinate));

		testUnaryShape = Mockito.spy(new UnaryOperatorShape("30", BlockType.NOT, coordinate));

		testPaletteActionShape = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));

		BlockType call = new BlockType("t1", BlockCategory.CALL, "def");
		testCallFunctionShape = Mockito.spy(new CallFunctionShape("40", call, coordinate));

		BlockType condition = new BlockType("t2", BlockCategory.CONDITION, "def");
		testCallFunctionShape2 = Mockito.spy(new CallFunctionShape("40", condition, coordinate));

		BlockType definition = new BlockType("Definition", BlockCategory.DEFINITION, "Definition");
		testCallFunctionShape3 = Mockito.spy(new CallFunctionShape("41", definition, coordinate));

		blockAddedEventWithCall = new BlockAddedEvent("0", "", null, call, null, false);
		blockAddedEventWithCall2 = new BlockAddedEvent("40", "", null, call, null, false);
		blockAddedEventWithCall3 = Mockito.spy(new BlockAddedEvent("41", "", null, definition, null, false));

		shapesInProgramArea = new HashSet<Shape>();
		maskedKeyBag = new MaskedKeyBag(false, false);

	}

	/**
	 * 
	 */
	@Test
	public void testCanvasWindowPositive() {

		CanvasWindow cw = new CanvasWindow("Blockr", this.domainController);

		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			assertNotNull(f.get(cw));
			assertEquals(new Coordinate(0, 0), f.get(cw));
			verify(domainController).addGameListener(canvasWindowArgumentCaptor.capture());

			Field f1 = CanvasWindow.class.getDeclaredField("undoMode");
			f1.setAccessible(true);
			assertEquals(false, f1.get(cw));

			Field f2 = CanvasWindow.class.getDeclaredField("commandHandler");
			f2.setAccessible(true);
			assertNotNull(f2.get(cw));
			Field c1 = CommandHandler.class.getDeclaredField("canvas");
			c1.setAccessible(true);
			assertNotNull(c1.get(((CommandHandler) f2.get(cw))));
			assertEquals(cw, c1.get(((CommandHandler) f2.get(cw))));

			Field f3 = CanvasWindow.class.getDeclaredField("maskedKeyBag");
			f3.setAccessible(true);
			assertNotNull(f3.get(cw));

			assertEquals(cw.getWindowWidth(), WIDTH);

			Field f4 = CanvasWindow.class.getDeclaredField("domainController");
			f4.setAccessible(true);
			assertNotNull(f4.get(cw));
			assertEquals(this.domainController, f4.get(cw));

			Field f5 = CanvasWindow.class.getDeclaredField("shapeFactory");
			f5.setAccessible(true);
			assertNotNull(f5.get(cw));

			Field f6 = CanvasWindow.class.getDeclaredField("programArea");
			f6.setAccessible(true);
			assertNotNull(f6.get(cw));

			Field f7 = CanvasWindow.class.getDeclaredField("paletteArea");
			f7.setAccessible(true);
			assertNotNull(f7.get(cw));

			Field f8 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f8.setAccessible(true);
			assertNotNull(f8.get(cw));
			assertEquals(0, ((HashSet<String>) f8.get(cw)).size());

			Field f9 = CanvasWindow.class.getDeclaredField("shapesInMovement");
			f9.setAccessible(true);
			assertNotNull(f9.get(cw));
			assertEquals(0, ((HashSet<String>) f9.get(cw)).size());

			Field f10 = CanvasWindow.class.getDeclaredField("shapeClonesInMovement");
			f10.setAccessible(true);
			assertNotNull(f10.get(cw));
			assertEquals(0, ((HashSet<String>) f10.get(cw)).size());

			assertEquals(cw, canvasWindowArgumentCaptor.getValue());

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		// verify(CanvasResource., atLeastOnce()).getX_coord();

	}

	/**
	 * 
	 */
	@Test
	public void testOnCalculateWindowHeight_SameWindowHeight() {

		try {
			Field f = CanvasResource.class.getDeclaredField("height");
			f.setAccessible(true);
			f.set(canvasWindow, 730);
		} catch (Exception e) {
			fail("field not init");
		}

		canvasWindow.calculateWindowHeight();

	}

	/**
	 * 
	 */
	@Test
	public void testShowAlert() {

		canvasWindow.showAlert("TEST");
		try {
			Field f1 = CanvasWindow.class.getDeclaredField("alertMessage");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));
			assertEquals("TEST", f1.get(canvasWindow));

			// verify(canvasWindow).repaint();
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * 
	 */
	@Test
	public void testPlaceShapes_CurrentSnapShotNotNull_WithHeights() {

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		HashSet<Shape> shapesInPorgamArea = new HashSet<>();
		shapesInPorgamArea.add(testControlShape);

		HashSet<String> idsInBody = new HashSet<String>();
		idsInBody.add("0");

		GuiSnapshot gs = new GuiSnapshot(shapesInMovement);
//		gs.setHeight("10", 20);
//		gs.setHeight("0", 20);

		HashMap<String, Integer> mapOfHeights = new HashMap<String, Integer>();
		mapOfHeights.put("10", 20);
		mapOfHeights.put("0", 20);

		HashMap<String, Coordinate> mapOfCoordinates = new HashMap<String, Coordinate>();
		mapOfCoordinates.put("10", coordinate);
		mapOfCoordinates.put("0", coordinate);

		when(domainController.getBlockType("0")).thenReturn(moveForward);
		when(domainController.getBlockType("10")).thenReturn(BlockType.IF);
		when(shapeFactory.createShape("0", moveForward, coordinate)).thenReturn(testActionShape);
		when(shapeFactory.createShape("10", BlockType.IF, coordinate)).thenReturn(testControlShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInPorgamArea);
		when(domainController.isBlockPresent("10")).thenReturn(true);
		when(domainController.getAllBlockIDsInBody("10")).thenReturn(idsInBody);

		try {
			Field f1 = CanvasWindow.class.getDeclaredField("currentSnapshot");
			f1.setAccessible(true);
			// f1.set(canvasWindow, gs);

			Field f2 = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f2.setAccessible(true);
			f2.set(programArea, new HashSet<Coordinate>());

			when(currentSnapshot.getSavedHeights()).thenReturn(mapOfHeights);
			when(currentSnapshot.getSavedCoordinates()).thenReturn(mapOfCoordinates);
			canvasWindow.placeShapes();

			// verify(canvasWindow).repaint();
		} catch (Exception e) {
			fail("fields have not been initialized.");

		}

	}

	/**
	 * 
	 */
	@Test
	public void testPlaceShapes_CurrentSnapShotNotNull_WithoutHeights() {

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		HashSet<Shape> shapesInPorgamArea = new HashSet<>();
		shapesInPorgamArea.add(testControlShape);

		HashSet<String> idsInBody = new HashSet<String>();
		idsInBody.add("0");

		when(domainController.getBlockType("0")).thenReturn(moveForward);
		when(shapeFactory.createShape("0", moveForward, coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInPorgamArea);
		when(domainController.isBlockPresent("10")).thenReturn(true);
		when(domainController.getAllBlockIDsInBody("10")).thenReturn(idsInBody);

		try {
			Field f1 = CanvasWindow.class.getDeclaredField("currentSnapshot");
			f1.setAccessible(true);
			f1.set(canvasWindow, new GuiSnapshot(shapesInMovement));

			canvasWindow.placeShapes();

			// verify(canvasWindow, atLeastOnce()).repaint();
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * 
	 */
	@Test
	public void testPlaceShapes_CurrentSnapShotNull() {
		try {
			Field f1 = CanvasWindow.class.getDeclaredField("currentSnapshot");
			f1.setAccessible(true);
			f1.set(canvasWindow, null);

			canvasWindow.placeShapes();

			// verify(canvasWindow).repaint();
		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * 
	 */
	@Test
	public void testSetUndoMode_True() {

		canvasWindow.setUndoMode(true);
		try {
			Field f1 = CanvasWindow.class.getDeclaredField("undoMode");
			f1.setAccessible(true);
			assertEquals(true, f1.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * 
	 */
	@Test
	public void testSetUndoMode_False() {
		canvasWindow.setUndoMode(false);

		try {
			Field f1 = CanvasWindow.class.getDeclaredField("undoMode");
			f1.setAccessible(true);
			assertEquals(false, f1.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockChangeEvent(events.BlockChangeEvent)}.
	 */
	@Test
	public void testOnBlockChangeEvent() {

		HashSet<Shape> shapesFromChangeevent = new HashSet<Shape>();
		shapesFromChangeevent.add(testActionShape);
		shapesFromChangeevent.add(testControlShape);
		shapesFromChangeevent.add(testControlShapeUnder);

		HashSet<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		HashSet<String> internalsAsStringOfControlBlock = new HashSet<String>();
		coordinates.put("10", coordinate);
		coordinates.put("11", coordinate);
		coordinates.put("0", coordinate);

		HashMap<String, Integer> heights = new HashMap<String, Integer>();
		heights.put("0", 30);
		heights.put("10", 120);
		heights.put("10", 90);

		shapesInProgramArea.add(testActionShape);
		shapesInProgramArea.add(testControlShape);
		shapesInProgramArea.add(testControlShapeUnder);

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(currentSnapshot.getSavedHeights()).thenReturn(heights);
		when(domainController.getBlockType("0")).thenReturn(moveForward);
		when(domainController.getBlockType("10")).thenReturn(BlockType.IF);
		when(domainController.getBlockType("11")).thenReturn(BlockType.IF);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(true);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(domainController.getAllBlockIDsInBody(testControlShape.getId()))
				.thenReturn(internalsAsStringOfControlBlock);

		when(shapeFactory.createShape(any(String.class), any(BlockType.class), any(Coordinate.class)))
				.thenAnswer(new Answer<Shape>() {

					@Override
					public Shape answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						BlockType type = (BlockType) args[1];
						String id = (String) args[0];
//				if(type.cat() == BlockCategory.ACTION) {
//					return testActionShape;
//				}
//				if(type.cat() == BlockCategory.CONTROL && id.equals("10") ) {
//					return testControlShape;
//				}
						if (type.cat() == BlockCategory.CONTROL && id.equals("11")) {
							return testControlShapeUnder;
						} else {
							return null;
						}
					}

				});

		canvasWindow.onBlockChangeEvent(blockChangedEvent);

		for (Shape shape : shapesInProgramArea.stream().filter(e -> e instanceof ControlShape)
				.collect(Collectors.toSet())) {
			System.out.println("Test");
			// verify(commandHandler, atLeastOnce()).setHeight(shape.getId(),
			// shape.getHeight());
		}

//		for (Shape shape : shapesFromChangeevent) {
//			verify(shape, atLeastOnce()).setCoordinatesShape();
//			verify(shape, atLeastOnce()).defineConnectionTypes();
//			verify(programArea, atLeastOnce()).addShapeToProgramArea(shape);
//		}
//		
//		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//
//		//determineTotalHeightsShape

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_NoAlert_TempGameBoardLargerThanInit_WindowHeightLarger_DebugModusFillings_CurrentShapeNotNull() {
//		Graphics g = Mockito.spy(Graphics.class);
		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		Graphics g = Mockito.spy(new Graphics() {
			private Color c;

			@Override
			public void translate(int x, int y) {
			}

			@Override
			public void setXORMode(Color c1) {
			}

			@Override
			public void setPaintMode() {
			}

			@Override
			public void setFont(Font font) {
			}

			@Override
			public void setColor(Color c) {
				this.c = c;
			}

			@Override
			public void setClip(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setClip(java.awt.Shape clip) {
				// TODO Auto-generated method stub

			}

			@Override
			public FontMetrics getFontMetrics(Font f) {
				return new FontMetrics(f) {

					@Override
					public Rectangle2D getStringBounds(String str, Graphics context) {
						// TODO Auto-generated method stub
						return new Rectangle(500, 300);
					}
					
				};
			}

			@Override
			public Font getFont() {
				return mock(Font.class);
			}

			@Override
			public Color getColor() {
				return c;
			}

			@Override
			public Rectangle getClipBounds() {
				return new Rectangle(6000, 6000);
			}

			@Override
			public java.awt.Shape getClip() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(AttributedCharacterIterator iterator, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(String str, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawLine(int x1, int y1, int x2, int y2) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Graphics create() {
				return this;
			}

			@Override
			public void copyArea(int x, int y, int width, int height, int dx, int dy) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clipRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		g.setColor(Color.BLACK);
		g.setClip(0, 0, 6000, 6000);
		coordinates.put("1", coordinate);
		when(g.getClipBounds()).thenReturn(new Rectangle(5000, 6000));
		when(g.create(0, 0, 100, 600)).thenReturn(g);
		when(programArea.getAlreadyFilledInCoordinates())
				.thenReturn(coordinates.values().stream().collect(Collectors.toSet()));
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(canvasWindow.getCurrentShape()).thenReturn(testActionShapeWithZeroCoordinate);

		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, DebugModus.FILLINGS);

			Field f2 = CanvasWindow.class.getDeclaredField("programAndGameBorder");
			f2.setAccessible(true);
			f2.set(canvasWindow, 100);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.paint(g);
		verify(g, atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		verify(programArea, atLeastOnce()).setProgramAndGameBorder(4750);
		verify(paletteArea, atLeastOnce()).paint(g);
		verify(domainController, atLeastOnce()).paint(g);
		verify(g, atLeastOnce()).fill3DRect(4750, ORIGIN, GAME_WIDTH, 6000, true);
		verify(programArea, atLeastOnce()).draw(g, domainController);
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).draw(g);
		verify(testActionShape, atLeastOnce()).draw(g);
		verify(g, atLeastOnce()).drawOval(22, 5, 6, 6);

	}

	/**
	 * Test method for
	 * {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_NoAlert_TempGameBoardLargerThanInit_WindowHeightLarger_DebugModusFillings_CurrentShapeSameAsInMovement() {
//		Graphics g = Mockito.spy(Graphics.class);
		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		Graphics g = Mockito.spy(new Graphics() {
			private Color c;

			@Override
			public void translate(int x, int y) {
			}

			@Override
			public void setXORMode(Color c1) {
			}

			@Override
			public void setPaintMode() {
			}

			@Override
			public void setFont(Font font) {
			}

			@Override
			public void setColor(Color c) {
				this.c = c;
			}

			@Override
			public void setClip(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setClip(java.awt.Shape clip) {
				// TODO Auto-generated method stub

			}

			@Override
			public FontMetrics getFontMetrics(Font f) {
				return new FontMetrics(f) {

					@Override
					public Rectangle2D getStringBounds(String str, Graphics context) {
						// TODO Auto-generated method stub
						return new Rectangle(500, 300);
					}
					
				};
			}

			@Override
			public Font getFont() {
				return mock(Font.class);
			}

			@Override
			public Color getColor() {
				return c;
			}

			@Override
			public Rectangle getClipBounds() {
				return new Rectangle(6000, 6000);
			}

			@Override
			public java.awt.Shape getClip() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(AttributedCharacterIterator iterator, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(String str, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawLine(int x1, int y1, int x2, int y2) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Graphics create() {
				return this;
			}

			@Override
			public void copyArea(int x, int y, int width, int height, int dx, int dy) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clipRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		g.setColor(Color.BLACK);
		g.setClip(0, 0, 6000, 6000);
		coordinates.put("1", coordinate);
		when(g.getClipBounds()).thenReturn(new Rectangle(5000, 6000));
		when(g.create(0, 0, 100, 600)).thenReturn(g);
		when(programArea.getAlreadyFilledInCoordinates())
				.thenReturn(coordinates.values().stream().collect(Collectors.toSet()));
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(canvasWindow.getCurrentShape()).thenReturn(testActionShape);

		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, DebugModus.FILLINGS);

			Field f2 = CanvasWindow.class.getDeclaredField("programAndGameBorder");
			f2.setAccessible(true);
			f2.set(canvasWindow, 100);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.paint(g);
		verify(g, atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		verify(programArea, atLeastOnce()).setProgramAndGameBorder(4750);
		verify(paletteArea, atLeastOnce()).paint(g);
		verify(domainController, atLeastOnce()).paint(g);
		verify(g, atLeastOnce()).fill3DRect(4750, ORIGIN, GAME_WIDTH, 6000, true);
		verify(programArea, atLeastOnce()).draw(g, domainController);
		// verify(testActionShapeWithZeroCoordinate, atLeastOnce()).draw(g);
		verify(testActionShape, atLeastOnce()).draw(g);
		verify(g, atLeastOnce()).drawOval(22, 5, 6, 6);

	}

	/**
	 * Test method for
	 * {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_NoAlert_TempGameBoardSmallerThanInit_WindowHeightSmaller_DebugModusNotFillings_CurrentShapeNull() {
//		Graphics g = Mockito.spy(Graphics.class);
		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		Graphics g = Mockito.spy(new Graphics() {
			private Color c;

			@Override
			public void translate(int x, int y) {
			}

			@Override
			public void setXORMode(Color c1) {
			}

			@Override
			public void setPaintMode() {
			}

			@Override
			public void setFont(Font font) {
			}

			@Override
			public void setColor(Color c) {
				this.c = c;
			}

			@Override
			public void setClip(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setClip(java.awt.Shape clip) {
				// TODO Auto-generated method stub

			}

			@Override
			public FontMetrics getFontMetrics(Font f) {
				return new FontMetrics(f) {

					@Override
					public Rectangle2D getStringBounds(String str, Graphics context) {
						// TODO Auto-generated method stub
						return new Rectangle(500, 300);
					}
					
				};
			}

			@Override
			public Font getFont() {
				return mock(Font.class);
			}

			@Override
			public Color getColor() {
				return c;
			}

			@Override
			public Rectangle getClipBounds() {
				return new Rectangle(6000, 6000);
			}

			@Override
			public java.awt.Shape getClip() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(AttributedCharacterIterator iterator, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(String str, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawLine(int x1, int y1, int x2, int y2) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Graphics create() {
				return this;
			}

			@Override
			public void copyArea(int x, int y, int width, int height, int dx, int dy) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clipRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		g.setColor(Color.BLACK);
		g.setClip(0, 0, 6000, 6000);
		coordinates.put("1", coordinate);
		when(g.getClipBounds()).thenReturn(new Rectangle(100, 100));
		when(g.create(0, 0, 100, 600)).thenReturn(g);
		when(programArea.getAlreadyFilledInCoordinates())
				.thenReturn(coordinates.values().stream().collect(Collectors.toSet()));
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(canvasWindow.getCurrentShape()).thenReturn(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, DebugModus.IDS);

			Field f2 = CanvasWindow.class.getDeclaredField("programAndGameBorder");
			f2.setAccessible(true);
			f2.set(canvasWindow, 100);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.paint(g);
		verify(g, atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		verify(paletteArea, atLeastOnce()).paint(g);
		verify(domainController, atLeastOnce()).paint(g);
		verify(g, atLeastOnce()).fill3DRect(100, ORIGIN, GAME_WIDTH, 100, true);
		verify(programArea, atLeastOnce()).draw(g, domainController);
		verify(testActionShape, atLeastOnce()).draw(g);

	}

	/**
	 * Test method for
	 * {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint_WithAlert_TempGameBoardLargerThanInit_WindowHeightLarger_DebugModusFillings_CurrentShapeNotNull() {

		Graphics g = Mockito.spy(new Graphics() {
			private Color c;

			@Override
			public void translate(int x, int y) {
			}

			@Override
			public void setXORMode(Color c1) {
			}

			@Override
			public void setPaintMode() {
			}

			@Override
			public void setFont(Font font) {
			}

			@Override
			public void setColor(Color c) {
				this.c = c;
			}

			@Override
			public void setClip(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setClip(java.awt.Shape clip) {
				// TODO Auto-generated method stub

			}

			@Override
			public FontMetrics getFontMetrics(Font f) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Font getFont() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Color getColor() {
				return c;
			}

			@Override
			public Rectangle getClipBounds() {
				return new Rectangle(6000, 6000);
			}

			@Override
			public java.awt.Shape getClip() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(AttributedCharacterIterator iterator, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawString(String str, int x, int y) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawOval(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drawLine(int x1, int y1, int x2, int y2) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor,
					ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Graphics create() {
				return this;
			}

			@Override
			public void copyArea(int x, int y, int width, int height, int dx, int dy) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clipRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public void clearRect(int x, int y, int width, int height) {
				// TODO Auto-generated method stub

			}
		});

		String msg = "ALERT TEST MESSAGE";
		g.setColor(Color.BLACK);
		g.setClip(0, 0, 6000, 6000);
		coordinates.put("1", coordinate);
		when(g.getClipBounds()).thenReturn(new Rectangle(5000, 6000));
		when(g.create(0, 0, 100, 600)).thenReturn(g);

		try {
			Field f = CanvasWindow.class.getDeclaredField("alertMessage");
			f.setAccessible(true);
			f.set(canvasWindow, msg);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.paint(g);

		verify(g, atLeastOnce()).setColor(Color.WHITE);
		verify(g, atLeastOnce()).setColor(Color.BLACK);
		verify(g, atLeastOnce()).setColor(Color.RED);
		verify(g, atLeastOnce()).fillRect(any(Integer.class), any(Integer.class), any(Integer.class),
				any(Integer.class));
		// verify(g, atLeastOnce()).drawString(msg, any(Integer.class),
		// any(Integer.class));
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_KeyPressedInPalette_ShapeNotExcluded() {

		int x = 10;
		int y = 60;

		when(paletteArea.checkIfInPalette(x)).thenReturn(true);
		when(paletteArea.getShapeFromCoordinate(x, y)).thenReturn(testPaletteActionShape);
		// when(canvasWindow.getC).thenReturn(testPaletteActionShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 1);

		verify(testPaletteActionShape, atLeastOnce()).getX_coord();
		verify(testPaletteActionShape, atLeastOnce()).getY_coord();
		verify(testPaletteActionShape, atLeastOnce()).defineConnectionTypes();

		// This method wil be verified in the onDragEvent:
		// programArea.setHighlightedShapeForConnections(determineHighlightShape());

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_KeyPressedInPalette_ShapeExcluded() {

		int x = 10;
		int y = 60;

		Set<Shape> shapesInMovement = Mockito.spy(new HashSet<Shape>());
		shapesInMovement.add(testPaletteActionShape);
		shapesInMovement.add(testActionShape);
		Shape currentShape = testPaletteActionShape;

		try {
			Field f = CanvasWindow.class.getDeclaredField("shapesInMovement");
			f.setAccessible(true);
			f.set(canvasWindow, shapesInMovement);

			Field f1 = CanvasWindow.class.getDeclaredField("currentShape");
			f1.setAccessible(true);
			f1.set(canvasWindow, currentShape);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		when(paletteArea.checkIfInPalette(x)).thenReturn(true);
		when(paletteArea.getShapeFromCoordinate(x, y)).thenReturn(testPaletteActionShape);
		when(canvasWindow.getCurrentShape()).thenReturn(testPaletteActionShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		// when(canvasWindow.getC).thenReturn(testPaletteActionShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 1);

		verify(testPaletteActionShape, atLeastOnce()).getX_coord();
		verify(testPaletteActionShape, atLeastOnce()).getY_coord();
		verify(testPaletteActionShape, atLeastOnce()).defineConnectionTypes();

		// This method wil be verified in the onDragEvent:
		// programArea.setHighlightedShapeForConnections(determineHighlightShape());

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_KeyPressedInProgramArea() {

		int x = PROGRAM_START_X + 10;
		int y = 60;

		Set<String> blocksUnderneath = new HashSet<String>();
		blocksUnderneath.add(testControlShape.getId());

		Set<Shape> shapesInPorgramArea = new HashSet<Shape>();
		shapesInPorgramArea.add(testActionShape);
		shapesInPorgramArea.add(testControlShape);
		shapesInPorgramArea.add(testOperandShape);

		when(programArea.getShapeFromCoordinate(x, y)).thenReturn(testActionShape);
		when(domainController.getAllBlockIDsUnderneath(testActionShape.getId())).thenReturn(blocksUnderneath);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInPorgramArea);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 1);

		verify(programArea, atLeastOnce()).removeShapeFromProgramArea(testControlShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InPalette_CurrentShapePaletteBlock() {

		int x = 10;
		int y = 60;
		Shape currentShape = testPaletteActionShape;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(testPaletteActionShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InPalette_CurrentShapeIsNotAPaletteBlock() {

		int x = 10;
		int y = 60;
		Shape currentShape = testActionShape;

		DomainMoveCommand dmc = new DomainMoveCommand(domainController, canvasWindow, currentSnapshot, currentSnapshot);

		Set<String> blocksUnderneath = new HashSet<String>();
		blocksUnderneath.add(testControlShape.getId());

		Set<Shape> shapesInPorgramArea = new HashSet<Shape>();
		shapesInPorgramArea.add(testActionShape);
		shapesInPorgramArea.add(testControlShape);
		shapesInPorgramArea.add(testOperandShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(testActionShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(domainController, atLeastOnce()).removeBlock(currentShape.getId());
		// verify(commandHandler, atLeastOnce()).handle(dmc);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNotNull_Placeable() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = testOperandShape;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(highlightedShape, atLeastOnce()).persistConnectedVia(true);
		verify(currentShape, atLeastOnce()).persistConnectedVia(true);
		verify(currentShape, atLeastOnce()).clipOn(highlightedShape, ConnectionType.NOCONNECTION);
		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle((any(DomainMoveCommand.class)));
		verify(domainController, atLeastOnce()).addBlock(currentShape.getType(), highlightedShape.getId(),
				ConnectionType.NOCONNECTION);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			assertNull(f.get(canvasWindow));

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeNotInPalette_HighlightedShapeNull_Placeable() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = null;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle((any(DomainMoveCommand.class)));
		verify(domainController, atLeastOnce()).moveBlock(currentShape.getId(), "", "", ConnectionType.NOCONNECTION);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			assertNull(f.get(canvasWindow));

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeNotInPalette_HighlightedShapeNull_Placeable_HeadBlocksContainCurrentShape() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = null;

		HashSet<String> headBlocks = new HashSet<String>();
		headBlocks.add(currentShape.getId());

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);
		when(domainController.getAllHeadBlocks()).thenReturn(headBlocks);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle(any(GuiMoveCommand.class));
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			assertNull(f.get(canvasWindow));

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeNotInPalette_HighlightedShapeNotNull_Placeable() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = testOperandShape;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(highlightedShape, atLeastOnce()).persistConnectedVia(true);
		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle((any(DomainMoveCommand.class)));
		verify(domainController, atLeastOnce()).moveBlock(currentShape.getId(), "", "", ConnectionType.NOCONNECTION);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			assertNull(f.get(canvasWindow));

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeNotInPalette_HighlightedShapeNotNull_Placeable_ConnectedViaSomething() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = testOperandShape;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);
		when(highlightedShape.getConnectedVia()).thenReturn(ConnectionType.UP);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testOperandShape);

			canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		verify(highlightedShape, atLeastOnce()).persistConnectedVia(true);
		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle((any(DomainMoveCommand.class)));
		verify(domainController, atLeastOnce()).moveBlock(currentShape.getId(), testOperandShape.getId(),
				highlightedShape.getId(), ConnectionType.UP);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNull_Placeable() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = null;

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(true);
		when(currentShape.getConnectedVia()).thenReturn(ConnectionType.NOCONNECTION);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(currentShape, atLeastOnce()).setCoordinatesShape();
		verify(commandHandler, atLeastOnce()).handle((any(DomainMoveCommand.class)));
		verify(domainController, atLeastOnce()).addBlock(currentShape.getType(), "", ConnectionType.NOCONNECTION);
		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			assertNull(f.get(canvasWindow));

			Field f1 = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f1.setAccessible(true);
			assertNotNull(f1.get(canvasWindow));

			Field f2 = CanvasWindow.class.getDeclaredField("blocksUnderneath");
			f2.setAccessible(true);
			assertNotNull(f2.get(canvasWindow));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeIsNull() {

		int x = 250;
		int y = 60;

		Shape currentShape = null;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInProgramArea() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);
		when(programArea.checkIfInProgramArea(x)).thenReturn(false);
		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(programArea, atLeastOnce()).setHighlightedShapeForConnections(null);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNotNull_NotPlaceable_InvalidXYCoordinateOnRevert() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(false);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeNotInPalette_HighlightedShapeNotNull_NotPlaceable_InvalidXYCoordinateOnRevert() {

		int x = 250;
		int y = 60;

		Shape currentShape = testActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(false);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testActionShape);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNotNull_NotPlaceable_InvalidXCoordinateOnRevert() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);
		when(testActionShape.getPreviousY_coord()).thenReturn(y - 1);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(false);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNotNull_NotPlaceable_InvalidYCoordinateOnRevert() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(testActionShape.getPreviousX_coord()).thenReturn(x - 1);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(false);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseReleased_InProgramArea_CurrentShapeInPalette_HighlightedShapeNotNull_NotPlaceable_ValidCoordinateOnRevert() {

		int x = 250;
		int y = 60;

		Shape currentShape = testPaletteActionShape;
		Shape highlightedShape = testOperandShape;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(paletteArea.checkIfInPalette(currentShape.getX_coord())).thenReturn(false);
		when(programArea.checkIfInProgramArea(x)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(highlightedShape);

		when(programArea.checkIfPlaceable(currentShape, domainController)).thenReturn(false);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);

		when(testActionShape.getPreviousX_coord()).thenReturn(x - 1);
		when(testActionShape.getPreviousY_coord()).thenReturn(y - 1);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 1);

		verify(testActionShape, atLeastOnce()).setX_coord(x - 1);
		verify(testActionShape, atLeastOnce()).setY_coord(y - 1);
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNull() {

		int x = 250;
		int y = 60;

		Shape currentShape = null;

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testActionShape);

		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeDown_Up() {

		int x = 0;
		int y = -20;

		Shape currentShape = testPaletteActionShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testPaletteActionShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testActionShapeWithZeroCoordinate);

		Set<Coordinate> shapeInMovementCoordSet = testActionShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testActionShapeWithZeroCoordinate, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testActionShape, ConnectionType.DOWN)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testActionShapeWithZeroCoordinate.getId(), connection,
					shapesInMovementString)).thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testActionShapeWithZeroCoordinate.getId(), connection, null))
					.thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).persistConnectedVia(false);
		verify(testPaletteActionShape, atLeastOnce()).setConnectedVia(ConnectionType.UP, false);
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).setConnectedVia(ConnectionType.DOWN, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testPaletteActionShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeUp_Down() {

		int x = 0;
		int y = 30;

		Shape currentShape = testPaletteActionShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testPaletteActionShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testActionShapeWithZeroCoordinate);

		Set<Coordinate> shapeInMovementCoordSet = testActionShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testActionShapeWithZeroCoordinate, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testActionShape, ConnectionType.UP)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testActionShapeWithZeroCoordinate.getId(), connection,
					shapesInMovementString)).thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testActionShapeWithZeroCoordinate.getId(), connection, null))
					.thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).persistConnectedVia(false);
		verify(testPaletteActionShape, atLeastOnce()).setConnectedVia(ConnectionType.DOWN, false);
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).setConnectedVia(ConnectionType.UP, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testPaletteActionShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeUp_Body() {

		int x = 15;
		int y = 45;

		Shape currentShape = testPaletteActionShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testPaletteActionShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testControlShape);

		Set<Coordinate> shapeInMovementCoordSet = testActionShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testControlShape, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testActionShape, ConnectionType.UP)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testControlShape.getId(), connection, shapesInMovementString))
					.thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testControlShape.getId(), connection, null)).thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testControlShape, atLeastOnce()).persistConnectedVia(false);
		verify(testPaletteActionShape, atLeastOnce()).setConnectedVia(ConnectionType.BODY, false);
		verify(testControlShape, atLeastOnce()).setConnectedVia(ConnectionType.UP, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testPaletteActionShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeLeft_Condition() {

		int x = 85;
		int y = 15;

		Shape currentShape = testOperandShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testOperandShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testControlShape);

		Set<Coordinate> shapeInMovementCoordSet = testOperandShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testControlShape, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testOperandShape, ConnectionType.LEFT)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testControlShape.getId(), connection, shapesInMovementString))
					.thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testControlShape.getId(), connection, null)).thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testControlShape, atLeastOnce()).persistConnectedVia(false);
		verify(testOperandShape, atLeastOnce()).setConnectedVia(ConnectionType.CONDITION, false);
		verify(testControlShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testOperandShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeCondition_Left() {

		int x = -60;
		int y = 15;

		Shape currentShape = testControlShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testControlShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testOperandShape); //

		Set<Coordinate> shapeInMovementCoordSet = testControlShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testOperandShape, coordinate); //

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testControlShape, ConnectionType.CONDITION)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testOperandShape.getId(), connection, shapesInMovementString))
					.thenReturn(true); //
			when(domainController.checkIfConnectionIsOpen(testOperandShape.getId(), connection, null)).thenReturn(true); //
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testOperandShape, atLeastOnce()).persistConnectedVia(false); //
		verify(testControlShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, false);
		verify(testOperandShape, atLeastOnce()).setConnectedVia(ConnectionType.CONDITION, false); //
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testOperandShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeLeft_Operand() {

		int x = 95;
		int y = 15;

		Shape currentShape = testOperandShape;
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testOperandShape);

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testUnaryShape);

		Set<Coordinate> shapeInMovementCoordSet = testOperandShape.getCoordinatesShape();

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testUnaryShape, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testOperandShape, ConnectionType.LEFT)).thenReturn(true);
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testUnaryShape.getId(), connection, shapesInMovementString))
					.thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testUnaryShape.getId(), connection, null)).thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testUnaryShape, atLeastOnce()).persistConnectedVia(false);
		verify(testOperandShape, atLeastOnce()).setConnectedVia(ConnectionType.OPERAND, false);
		verify(testUnaryShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testOperandShape);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_MouseDragged_CurrentShapeNotNull_ConnectionTypeOperand_Left() {

		int x = -65;
		int y = 15;

		Shape currentShape = testUnaryShape;//
		currentShape.setCoordinatesShape();

		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		shapesInMovement.add(testUnaryShape);//

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testOperandShape);

		Set<Coordinate> shapeInMovementCoordSet = testUnaryShape.getCoordinatesShape();//

		HashMap<Shape, Coordinate> shapesInProgramAreaConnectionMap = new HashMap<Shape, Coordinate>();
		shapesInProgramAreaConnectionMap.put(testOperandShape, coordinate);

		Set<String> shapesInMovementString = shapesInMovement.stream().map(e -> e.getId()).collect(Collectors.toSet());

		when(canvasWindow.isConnectionOpen(testUnaryShape, ConnectionType.OPERAND)).thenReturn(true);//
		when(canvasWindow.getCurrentShape()).thenReturn(currentShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		for (var connection : ConnectionType.values()) {
			when(domainController.checkIfConnectionIsOpen(testOperandShape.getId(), connection, shapesInMovementString))
					.thenReturn(true);
			when(domainController.checkIfConnectionIsOpen(testOperandShape.getId(), connection, null)).thenReturn(true);
		}
		try {
			Field f = CanvasWindow.class.getDeclaredField("offsetCurrentShape");
			f.setAccessible(true);
			f.set(canvasWindow, coordinate);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 1);
		verify(currentShape, atLeastOnce()).defineConnectionTypes();
		verify(testOperandShape, atLeastOnce()).persistConnectedVia(false);
		verify(testUnaryShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, false);//
		verify(testOperandShape, atLeastOnce()).setConnectedVia(ConnectionType.OPERAND, false);
		try {
			Field f = CanvasWindow.class.getDeclaredField("movedShape");
			f.setAccessible(true);
			f.set(canvasWindow, testUnaryShape);//

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		for (Shape shape : shapesInMovement) {
			verify(shape, atLeastOnce()).defineConnectionTypes();
		}
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Exception() {

		try {
			Field f = CanvasWindow.class.getDeclaredField("triggerStackOverflow");
			f.setAccessible(true);
			f.set(canvasWindow, true);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, ' ');

		// Nog een verify

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_CTRL() {

		Timer maskedKeyTimer = Mockito.spy(new Timer());
		MaskedKeyBag maskedKeyBag = Mockito.spy(new MaskedKeyBag(false, false));

		try {
			Field f = CanvasWindow.class.getDeclaredField("maskedKeyTimer");
			f.setAccessible(true);
			f.set(canvasWindow, maskedKeyTimer);

			Field f1 = CanvasWindow.class.getDeclaredField("maskedKeyBag");
			f1.setAccessible(true);
			f1.set(canvasWindow, maskedKeyBag);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, ' ');

		verify(maskedKeyTimer, atLeastOnce()).cancel();
		verify(maskedKeyBag, atLeastOnce()).pressCtrl(true);

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_SHIFT() {

		Timer maskedKeyTimer = Mockito.spy(new Timer());
		MaskedKeyBag maskedKeyBag = Mockito.spy(new MaskedKeyBag(false, false));

		try {
			Field f = CanvasWindow.class.getDeclaredField("maskedKeyTimer");
			f.setAccessible(true);
			f.set(canvasWindow, maskedKeyTimer);

			Field f1 = CanvasWindow.class.getDeclaredField("maskedKeyBag");
			f1.setAccessible(true);
			f1.set(canvasWindow, maskedKeyBag);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT, ' ');

		verify(maskedKeyTimer, atLeastOnce()).cancel();
		verify(maskedKeyBag, atLeastOnce()).pressShift(true);
//		verify(maskedKeyTimer,atLeastOnce()).schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);	
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_F5_UsefullGame() {

		when(domainController.isGameExecutionUseful()).thenReturn(true);

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_F5, ' ');

		verify(commandHandler, atLeastOnce()).handle(any(ExecuteBlockCommand.class));

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_F5_UnusefullGame() {

		when(domainController.isGameExecutionUseful()).thenReturn(false);

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_F5, ' ');

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_ESC_UsefullGame() {

		when(domainController.isGameResetUseful()).thenReturn(true);

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE, ' ');

		verify(commandHandler, atLeastOnce()).handle(any(ResetCommand.class));

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_ESC_UnusefullGame() {

		when(domainController.isGameResetUseful()).thenReturn(false);

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE, ' ');

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_Z_KEY_CTRL_NotSHIFT() {

		Timer maskedKeyTimer = Mockito.spy(new Timer());
		MaskedKeyBag maskedKeyBag = Mockito.spy(new MaskedKeyBag(false, true));

		try {
			Field f = CanvasWindow.class.getDeclaredField("maskedKeyTimer");
			f.setAccessible(true);
			f.set(canvasWindow, maskedKeyTimer);

			Field f1 = CanvasWindow.class.getDeclaredField("maskedKeyBag");
			f1.setAccessible(true);
			f1.set(canvasWindow, maskedKeyBag);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, ' ');

		verify(commandHandler, atLeastOnce()).undo();
		verify(maskedKeyTimer, atLeastOnce()).cancel();
		verify(maskedKeyBag, atLeastOnce()).pressShift(false);

	}

	@Test
	public void superRepaintTest() {
		canvasWindow.superRepaint();
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_Z_KEY_CTRL_SHIFT() {

		Timer maskedKeyTimer = Mockito.spy(new Timer());
		MaskedKeyBag maskedKeyBag = Mockito.spy(new MaskedKeyBag(true, true));

		try {
			Field f = CanvasWindow.class.getDeclaredField("maskedKeyTimer");
			f.setAccessible(true);
			f.set(canvasWindow, maskedKeyTimer);

			Field f1 = CanvasWindow.class.getDeclaredField("maskedKeyBag");
			f1.setAccessible(true);
			f1.set(canvasWindow, maskedKeyBag);
		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, ' ');

		verify(commandHandler, atLeastOnce()).redo();
		verify(maskedKeyTimer, atLeastOnce()).cancel();
		verify(maskedKeyBag, atLeastOnce()).pressShift(true);
//		verify(maskedKeyTimer,atLeastOnce()).schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_V_KEY() {

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_U, ' ');

		verify(commandHandler, atLeastOnce()).undo();

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_R_KEY() {

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_R, ' ');

		verify(commandHandler, atLeastOnce()).redo();

	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Typed_D_KEY() {
		DebugModus debugModus = Mockito.spy(DebugModus.NONE);
		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, debugModus);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");

			canvasWindow.handleKeyEvent(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, 'd');

			verify(canvasWindow, atLeastOnce()).superRepaint();
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(),
				coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		canvasWindow.onBlockAdded(blockAddedEvent);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive_AddedBlockIsCallBlock() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		Set<String> iDsUnderneath = new HashSet<String>();
		iDsUnderneath.add("11");

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithCall.getAddedBlockID(),
				blockAddedEventWithCall.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.getAllBlockIDsUnderneath("0")).thenReturn(iDsUnderneath);

		canvasWindow.onBlockAdded(blockAddedEventWithCall);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithCall.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive_AddedBlockIsCallBlock_diffDefinition() {

		coordinates.put("40", coordinate);
		shapesInProgramArea.add(testCallFunctionShape);

		Set<String> iDsUnderneath = new HashSet<String>();
		iDsUnderneath.add("11");

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithCall2.getAddedBlockID(),
				blockAddedEventWithCall2.getAddedBlockType(), coordinate)).thenReturn(testCallFunctionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.getAllBlockIDsUnderneath("40")).thenReturn(iDsUnderneath);
		when(domainController.getAllHeadBlocks()).thenReturn(new HashSet<String>());
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);

		canvasWindow.onBlockAdded(blockAddedEventWithCall2);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithCall2.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testCallFunctionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive_AddedBlockIsCallBlock_diffDefinition_diff() {

		coordinates.put("41", coordinate);
		shapesInProgramArea.add(testCallFunctionShape3);

		Set<String> iDsUnderneath = new HashSet<String>();
		iDsUnderneath.add("11");

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithCall3.getAddedBlockID(),
				blockAddedEventWithCall3.getAddedBlockType(), coordinate)).thenReturn(testCallFunctionShape3);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.getAllBlockIDsUnderneath("41")).thenReturn(iDsUnderneath);
		when(domainController.getAllHeadBlocks()).thenReturn(new HashSet<String>());
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);

		try {
			Field f = CanvasWindow.class.getDeclaredField("undoMode");
			f.setAccessible(true);
			f.set(canvasWindow, true);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.onBlockAdded(blockAddedEventWithCall3);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithCall3.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testCallFunctionShape3);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive_AddedBlockIsCallBlock_diffDefinition_NoFurtherEvents() {

		coordinates.put("41", coordinate);
		shapesInProgramArea.add(testCallFunctionShape3);

		Set<String> iDsUnderneath = new HashSet<String>();
		iDsUnderneath.add("11");

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithCall3.getAddedBlockID(),
				blockAddedEventWithCall3.getAddedBlockType(), coordinate)).thenReturn(testCallFunctionShape3);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.getAllBlockIDsUnderneath("41")).thenReturn(iDsUnderneath);
		when(domainController.getAllHeadBlocks()).thenReturn(new HashSet<String>());
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);
		when(blockAddedEventWithCall3.areMoreRelatedEventsComing()).thenReturn(true);

		try {
			Field f = CanvasWindow.class.getDeclaredField("undoMode");
			f.setAccessible(true);
			f.set(canvasWindow, true);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.onBlockAdded(blockAddedEventWithCall3);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithCall3.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testCallFunctionShape3);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_UP() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		// Shape linkedShape = Mockito.spy(new
		// ActionShape(blockAddedEventWithLinkedShapeUP.getLinkedBlockID(), null,
		// coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeUP.getAddedBlockID(),
				blockAddedEventWithLinkedShapeUP.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("1")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeUP);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeUP.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.UP , true);
//		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.UP);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.DOWN, true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.UP , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_BODY() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		// Shape linkedShape = Mockito.spy(new
		// ActionShape(blockAddedEventWithLinkedShapeBODY.getLinkedBlockID(), null,
		// coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeBODY.getAddedBlockID(),
				blockAddedEventWithLinkedShapeBODY.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("1")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeBODY);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeBODY.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.BODY , true);
//		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.BODY);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.UP, true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.BODY , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_CONDITION() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		// Shape linkedShape = Mockito.spy(new
		// ActionShape(blockAddedEventWithLinkedShapeCONDITION.getLinkedBlockID(), null,
		// coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeCONDITION.getAddedBlockID(),
				blockAddedEventWithLinkedShapeCONDITION.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("1")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeCONDITION);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeCONDITION.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.CONDITION , true);
//		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.CONDITION);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.CONDITION , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_DOWN() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		// Shape linkedShape = Mockito.spy(new
		// ActionShape(blockAddedEventWithLinkedShapeDOWN.getLinkedBlockID(), null,
		// coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeDOWN.getAddedBlockID(),
				blockAddedEventWithLinkedShapeDOWN.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("1")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeDOWN);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeDOWN.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.DOWN , true);
//		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.DOWN);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.UP, true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.DOWN , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_LEFT_OperatorShape() {

		coordinates.put("20", coordinate);
		shapesInProgramArea.add(testOperandShape2);

		// Shape linkedShape = Mockito.spy(new
		// UnaryOperatorShape(blockAddedEventWithLinkedShapeLEFT_parentOperand.getLinkedBlockID(),
		// null, coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockID(),
				blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockType(), coordinate))
						.thenReturn(testOperandShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("21")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeLEFT_parentOperand);

		verify(commandHandler, atLeastOnce())
				.setAddedId(blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testOperandShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testOperandShape, atLeastOnce()).setCoordinatesShape();
//		verify(testOperandShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , true);
//		verify(testOperandShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.LEFT);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.OPERAND, true);
//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , false);
//		verify(testOperandShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_LEFT_ControlShape() {

		coordinates.put("20", coordinate);
		shapesInProgramArea.add(testControlShape);

		// Shape linkedShape = Mockito.spy(new
		// ControlShape(blockAddedEventWithLinkedShapeLEFT_parentControl.getLinkedBlockID(),
		// null, coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockID(),
				blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockType(), coordinate))
						.thenReturn(testOperandShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		// when(programArea.getShapeById("10")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeLEFT_parentControl);

		verify(commandHandler, atLeastOnce())
				.setAddedId(blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testOperandShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , true);
//		//verify(testOperandShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.LEFT);
//		//verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.CONDITION, true);
//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , false);
//		verify(testOperandShape, atLeastOnce()).setCoordinatesShape();
//		//verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_NOCONNECTION() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockID(),
				blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockType(), coordinate))
						.thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeNOCONNECTION);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		// verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		// verify(programArea,
		// atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		// verify(testActionShape, atLeastOnce()).defineConnectionTypes();

//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.NOCONNECTION , true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.NOCONNECTION , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_OPERAND() {

		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);

//		Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeOPERAND.getLinkedBlockID(), null, coordinate));

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeOPERAND.getAddedBlockID(),
				blockAddedEventWithLinkedShapeOPERAND.getAddedBlockType(), coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
//		when(programArea.getShapeById("1")).thenReturn(linkedShape);

		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeOPERAND);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeBODY.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
//		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.OPERAND , true);
//		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.OPERAND);
//		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.LEFT, true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.OPERAND , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive() {

		coordinates.put(PALETTE_BLOCK_IDENTIFIER, coordinate);
		shapesInProgramArea.add(testActionShape);

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(),
				coordinate)).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		canvasWindow.onBlockAdded(blockAddedEvent);

		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyElse_LinkedBlockIDEmpty_Positive() {

		coordinates.put("NOT EXIST", zeroCoordinate);
		shapesInProgramArea.add(testActionShapeWithZeroCoordinate);

		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(),
				zeroCoordinate)).thenReturn(testActionShapeWithZeroCoordinate);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);

		canvasWindow.onBlockAdded(blockAddedEvent);

		assertEquals(false, currentSnapshot.getSavedCoordinates().containsKey(PALETTE_BLOCK_IDENTIFIER));
		verify(commandHandler, atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea, atLeastOnce()).addShapeToProgramArea(testActionShapeWithZeroCoordinate);
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShapeWithZeroCoordinate);
//		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).defineConnectionTypes();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveEmpty_NoFurtherEvents() {

		shapesInProgramArea.add(testCallFunctionShape);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventNew.getRemovedBlockId()).thenReturn("40");

		canvasWindow.onBlockRemoved(blockRemovedEventNew);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveEmpty_FurtherEvents() {

		shapesInProgramArea.add(testCallFunctionShape);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventNew.getRemovedBlockId()).thenReturn("40");
		when(blockRemovedEventNew.areMoreRelatedEventsComing()).thenReturn(true);

		canvasWindow.onBlockRemoved(blockRemovedEventNew);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveIF_NotBodyConnection_NoFurtherEvents() {

		shapesInProgramArea.add(testCallFunctionShape);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<String> blockIDsBelowCertainBlock = new HashSet<String>();
		blockIDsBelowCertainBlock.add("11");

		BlockType bfrm = BlockType.IF;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventWithBeforeMove.getRemovedBlockId()).thenReturn("40");
		when(domainController.getBlockType("10")).thenReturn(bfrm);
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(blockIDsBelowCertainBlock);
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);
		when(testControlShape.getHeightDiff()).thenReturn(-1);

		try {
			Field f = CanvasWindow.class.getDeclaredField("undoMode");
			f.setAccessible(true);
			f.set(canvasWindow, true);

		} catch (Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.onBlockRemoved(blockRemovedEventWithBeforeMove);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveIF_BodyConnection_NoFurtherEvents() {

		shapesInProgramArea.add(testCallFunctionShape);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<String> blockIDsBelowCertainBlock = new HashSet<String>();
		blockIDsBelowCertainBlock.add("11");

		BlockType bfrm = BlockType.IF;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventWithBeforeMoveWithBody.getRemovedBlockId()).thenReturn("40");
		when(domainController.getBlockType("10")).thenReturn(bfrm);
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(blockIDsBelowCertainBlock);
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);

		canvasWindow.onBlockRemoved(blockRemovedEventWithBeforeMoveWithBody);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveNOT_BodyConnection_NoFurtherEvents() {

		shapesInProgramArea.add(testCallFunctionShape);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<String> blockIDsBelowCertainBlock = new HashSet<String>();
		blockIDsBelowCertainBlock.add("11");

		BlockType bfrm = BlockType.NOT;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventWithBeforeMoveWithBody.getRemovedBlockId()).thenReturn("40");
		when(domainController.getBlockType("10")).thenReturn(bfrm);
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(blockIDsBelowCertainBlock);
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);

		canvasWindow.onBlockRemoved(blockRemovedEventWithBeforeMoveWithBody);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemoved_CallFunctionShape_BeforeRemoveNOT_BodyConnection_NoFurtherEvents_() {

		shapesInProgramArea.add(testCallFunctionShape2);

		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testCallFunctionShape2);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<String> blockIDsBelowCertainBlock = new HashSet<String>();
		blockIDsBelowCertainBlock.add("11");

		BlockType bfrm = BlockType.NOT;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEventWithBeforeMoveWithBody.getRemovedBlockId()).thenReturn("40");
		when(domainController.getBlockType("10")).thenReturn(bfrm);
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(blockIDsBelowCertainBlock);
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);

		canvasWindow.onBlockRemoved(blockRemovedEventWithBeforeMoveWithBody);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(commandHandler, atLeastOnce()).addShapeToBeforeSnapshot(testCallFunctionShape2);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemovedWithControlShape_WithoutInternals() {
		shapesInProgramArea.add(testControlShape);
		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testControlShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(false);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(blockRemovedEvent.getRemovedBlockId()).thenReturn("10");

		canvasWindow.onBlockRemoved(blockRemovedEvent);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();

//		for (Shape shape : shapesInProgramArea) {
//			verify(shape, atLeastOnce()).setCoordinatesShape();
//			verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
//			verify(shape, atLeastOnce()).defineConnectionTypes();
//		}

//		verify(canvasWindow, atLeastOnce())

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemovedWithControlShape_WithInternals_AndContainsIdInInternals() {

		coordinates.put("10", coordinate);
		coordinates.put("0", coordinate);

		HashMap<String, Integer> heights = new HashMap<String, Integer>();
		heights.put("0", 30);
		heights.put("10", 120);

		shapesInProgramArea.add(testControlShape);
		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testControlShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<Shape> internalsOfControlBlock = new HashSet<Shape>();
		internalsOfControlBlock.add(testActionShape);

		Set<String> internalsAsStringOfControlBlock = new HashSet<String>();
		internalsAsStringOfControlBlock.add(testActionShape.getId());

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(true);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(domainController.getAllBlockIDsInBody(testControlShape.getId()))
				.thenReturn(internalsAsStringOfControlBlock);
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(currentSnapshot.getSavedHeights()).thenReturn(heights);
		when(domainController.getBlockType("0")).thenReturn(new BlockType("Move Forward", BlockCategory.ACTION));
		when(blockRemovedEvent.getRemovedBlockId()).thenReturn("10");
		when(shapeFactory.createShape(any(String.class), any(BlockType.class), any(Coordinate.class)))
				.thenAnswer(new Answer<Shape>() {

					@Override
					public Shape answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						BlockType type = (BlockType) args[1];
						if (type.cat() == BlockCategory.ACTION) {
							return testActionShape;
						} else {
							return null;
						}
					}

				});
		canvasWindow.onBlockRemoved(blockRemovedEvent);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(shape);
		}

		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testControlShape, atLeastOnce()).determineTotalHeight(internalsOfControlBlock);

		verify(commandHandler, atLeastOnce()).setHeight(testControlShape.getId(), testControlShape.getHeight());

		// If contains Key
		verify(commandHandler, atLeastOnce()).setHeight(testControlShape.getId(), testControlShape.getHeight());

//		for (Shape shape : shapesInProgramArea) {
//			verify(shape, atLeastOnce()).setCoordinatesShape();
//			verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
//			verify(shape, atLeastOnce()).defineConnectionTypes();
//		}

//		verify(canvasWindow, atLeastOnce()).resetShapesInMovement();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
	 */
	@Test
	public void testOnBlockRemovedWithControlShape_WithInternals_AndContainsIdInInternals_AndControlShapeUnder() {

		coordinates.put("10", coordinate);
		coordinates.put("11", coordinate);
		coordinates.put("0", coordinate);

		HashMap<String, Integer> heights = new HashMap<String, Integer>();
		heights.put("0", 30);
		heights.put("10", 120);
		heights.put("10", 90);

		shapesInProgramArea.add(testControlShape);
		shapesInProgramArea.add(testControlShapeUnder);
		Set<Shape> shapesToBeRemovedFromProgramArea = new HashSet<Shape>();
		shapesToBeRemovedFromProgramArea.add(testControlShape);

		Set<ControlShape> changedControlShapes = new HashSet<ControlShape>();
		changedControlShapes.add(testControlShape);

		Set<Shape> internalsOfControlBlock = new HashSet<Shape>();
		internalsOfControlBlock.add(testActionShape);

		Set<String> internalsAsStringOfControlBlock = new HashSet<String>();
		internalsAsStringOfControlBlock.add(testActionShape.getId());

		Set<String> ShapesUnderControlBlockAsString = new HashSet<String>();
		ShapesUnderControlBlockAsString.add(testControlShapeUnder.getId());

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(domainController.isBlockPresent(testControlShape.getId())).thenReturn(true);
		when(programArea.getAllChangedControlShapes()).thenReturn(changedControlShapes);
		when(domainController.getAllBlockIDsInBody(testControlShape.getId()))
				.thenReturn(internalsAsStringOfControlBlock);
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(currentSnapshot.getSavedHeights()).thenReturn(heights);
		when(domainController.getBlockType("0")).thenReturn(new BlockType("Move Forward", BlockCategory.ACTION));
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(ShapesUnderControlBlockAsString);
		when(blockRemovedEvent.getRemovedBlockId()).thenReturn("10");
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);
		when(shapeFactory.createShape(any(String.class), any(BlockType.class), any(Coordinate.class)))
				.thenAnswer(new Answer<Shape>() {

					@Override
					public Shape answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						BlockType type = (BlockType) args[1];
						if (type.cat() == BlockCategory.ACTION) {
							return testActionShape;
						} else {
							return null;
						}
					}

				});
		canvasWindow.onBlockRemoved(blockRemovedEvent);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea, atLeastOnce()).removeShapeFromProgramArea(shape);
		}

		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testControlShape, atLeastOnce()).determineTotalHeight(internalsOfControlBlock);

		verify(commandHandler, atLeastOnce()).setHeight(testControlShape.getId(), testControlShape.getHeight());

//		for (Shape shape : shapesInProgramArea) {
//			verify(shape,atLeastOnce()).setCoordinatesShape();
//			verify(programArea,atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
//			verify(shape,atLeastOnce()).defineConnectionTypes();
//		}

//		verify(canvasWindow, atLeastOnce()).resetShapesInMovement();

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onPanelChangedEvent(events.PanelChangeEvent)}.
	 */
	@Test
	public void testOnPanelChangedEventTrue() {

		canvasWindow.onPanelChangedEvent(panelChangeEventTrue);
		verify(paletteArea, atLeastOnce()).setPaletteVisible(true);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onPanelChangedEvent(events.PanelChangeEvent)}.
	 */
	@Test
	public void testOnPanelChangedEventFalse() {

		canvasWindow.onPanelChangedEvent(panelChangeEventFalse);
		verify(paletteArea, atLeastOnce()).setPaletteVisible(false);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onUpdateHighlightingEvent(events.UpdateHighlightingEvent)}.
	 */
	@Test
	public void testOnUpdateHighlightingEvent_IDExists() {

		shapesInProgramArea.add(testActionShape);
		Shape highlightedShape = testActionShape;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		canvasWindow.onUpdateHighlightingEvent(updateHighlightingEvent);

		verify(programArea, atLeastOnce()).setHighlightedShapeForExecution(highlightedShape);

	}

	/**
	 * Test method for
	 * {@link guiLayer.CanvasWindow#onUpdateHighlightingEvent(events.UpdateHighlightingEvent)}.
	 */
	@Test()
	public void testOnUpdateHighlightingEvent_IDDoesntExist() {

		Shape highlightedShape = testActionShape;

		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		canvasWindow.onUpdateHighlightingEvent(updateHighlightingEvent);

		verify(programArea, atLeastOnce()).setHighlightedShapeForExecution(null);

	}

}
