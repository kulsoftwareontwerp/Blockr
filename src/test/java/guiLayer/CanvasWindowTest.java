/**
 * 
 */
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

import applicationLayer.DomainController;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.PanelChangeEvent;
import events.UpdateHighlightingEvent;
import guiLayer.commands.CommandHandler;
import guiLayer.commands.DomainMoveCommand;
import guiLayer.commands.ExecuteBlockCommand;
import guiLayer.commands.ResetCommand;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.shapes.UnaryOperatorShape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import guiLayer.types.GuiSnapshot;
import guiLayer.types.MaskedKeyBag;
import guiLayer.types.MaskedKeyPressed;
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
	
	@Mock(name="maskedKeyTimer")
	private Timer maskedKeyTimer;
	
	@Mock(name="maskedKeyBag")
	private MaskedKeyBag maskedKeyBag;
	
	@Mock(name="currentSnapshot")
	private GuiSnapshot currentSnapshot;
	
	@Mock(name="shapeFactory")
	private ShapeFactory shapeFactory;
	
	@Mock(name="commandHandler")
	private CommandHandler commandHandler;
	
	@Mock(name="programArea")
	private ProgramArea programArea;
	
	@Mock(name="paletteArea")
	private PaletteArea paletteArea;
	
	@Mock(name="domainController")
	private DomainController domainController;
	
	@Spy @InjectMocks
	private CanvasWindow canvasWindow = new CanvasWindow(currentSnapshot, shapeFactory, commandHandler, programArea, domainController, paletteArea, maskedKeyTimer, maskedKeyBag);
	
	private int initX;
	private int initY;
	private Coordinate coordinate;
	private Coordinate zeroCoordinate;
	
	private BlockAddedEvent blockAddedEvent;
	private BlockAddedEvent blockAddedEventWithLinkedShapeUP;
	private BlockAddedEvent blockAddedEventWithLinkedShapeBODY;
	private BlockAddedEvent blockAddedEventWithLinkedShapeCONDITION;
	private BlockAddedEvent blockAddedEventWithLinkedShapeDOWN;
	private BlockAddedEvent blockAddedEventWithLinkedShapeLEFT_parentOperand;
	private BlockAddedEvent blockAddedEventWithLinkedShapeLEFT_parentControl;
	private BlockAddedEvent blockAddedEventWithLinkedShapeNOCONNECTION;
	private BlockAddedEvent blockAddedEventWithLinkedShapeOPERAND;
	private BlockRemovedEvent blockRemovedEvent;
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
		zeroCoordinate = new Coordinate(0,0);
		
		moveForward = new BlockType("Move Forward", BlockCategory.ACTION);
		
		blockAddedEvent = new BlockAddedEvent("0", "", null, moveForward, null, false);
		blockAddedEventWithLinkedShapeUP = new BlockAddedEvent("0", "1", ConnectionType.UP , moveForward, null, false);
		blockAddedEventWithLinkedShapeBODY = new BlockAddedEvent("0", "1", ConnectionType.BODY , moveForward, null, false);
		blockAddedEventWithLinkedShapeCONDITION = new BlockAddedEvent("0", "1", ConnectionType.CONDITION , moveForward, null, false);
		blockAddedEventWithLinkedShapeDOWN = new BlockAddedEvent("0", "1", ConnectionType.DOWN , moveForward, null, false);
		blockAddedEventWithLinkedShapeLEFT_parentOperand = new BlockAddedEvent("20", "21", ConnectionType.LEFT , BlockType.NOT, null, false);
		blockAddedEventWithLinkedShapeLEFT_parentControl = new BlockAddedEvent("20", "10", ConnectionType.LEFT , BlockType.NOT, null, false);
		blockAddedEventWithLinkedShapeNOCONNECTION = new BlockAddedEvent("0", "1", ConnectionType.NOCONNECTION , moveForward, null, false);
		blockAddedEventWithLinkedShapeOPERAND = new BlockAddedEvent("0", "1", ConnectionType.OPERAND , moveForward, null, false);
		blockRemovedEvent = Mockito.spy(new BlockRemovedEvent("0", "", ConnectionType.NOCONNECTION, null, false));
		Set<String> changedBlocks = new HashSet<>();
		changedBlocks.add("11");
		blockChangedEvent =  Mockito.spy(new BlockChangeEvent("11", "11", "", ConnectionType.NOCONNECTION, "10", ConnectionType.DOWN, changedBlocks ));
		updateHighlightingEvent = new UpdateHighlightingEvent("0");
		panelChangeEventTrue = new PanelChangeEvent(true);
		panelChangeEventFalse = new PanelChangeEvent(false);
		
		coordinates = new HashMap<String, Coordinate>();
		
		
		testActionShape = Mockito.spy(new ActionShape("0", moveForward, coordinate));
		testActionShapeWithZeroCoordinate = Mockito.spy(new ActionShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), zeroCoordinate));

		testControlShape = Mockito.spy(new ControlShape("10", BlockType.IF, coordinate));
		testControlShapeUnder = Mockito.spy(new ControlShape("11", BlockType.IF, coordinate));
		
		testOperandShape = Mockito.spy(new ControlShape("20", BlockType.NOT, coordinate));
		testOperandShape2 = Mockito.spy(new ControlShape("21", BlockType.NOT, coordinate));
		
		testPaletteActionShape = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		
		shapesInProgramArea = new HashSet<Shape>();
		maskedKeyBag = new MaskedKeyBag(false, false);
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockChangeEvent(events.BlockChangeEvent)}.
	 */
	@Test
	public void testOnBlockChangeEvent() {
		
		HashSet<Shape>shapesFromChangeevent = new HashSet<Shape>();
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
		when(domainController.getAllBlockIDsInBody(testControlShape.getId())).thenReturn(internalsAsStringOfControlBlock);
		
		when(shapeFactory.createShape(any(String.class),any(BlockType.class), any(Coordinate.class))).thenAnswer(new Answer<Shape>() {

			@Override
			public Shape answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				BlockType type =  (BlockType) args[1];
				String id = (String) args[0];
//				if(type.cat() == BlockCategory.ACTION) {
//					return testActionShape;
//				}
//				if(type.cat() == BlockCategory.CONTROL && id.equals("10") ) {
//					return testControlShape;
//				}
				if(type.cat() == BlockCategory.CONTROL && id.equals("11")) {
					return testControlShapeUnder;
				}
				else {
					return null;					
				}
			}
			
		});
		
		canvasWindow.onBlockChangeEvent(blockChangedEvent);
		
		for (Shape shape : shapesInProgramArea.stream().filter(e-> e instanceof ControlShape).collect(Collectors.toSet())) {
			System.out.println("Test");
			//verify(commandHandler, atLeastOnce()).setHeight(shape.getId(), shape.getHeight());
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
	 * Test method for {@link com.kuleuven.swop.group17.RobotGameWorld.guiLayer.RobotCanvas#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint() {
		Graphics g = Mockito.spy(Graphics.class);
		coordinates.put("1", coordinate);
		when(g.getClipBounds()).thenReturn(new Rectangle(500,600));
		when(programArea.getAlreadyFilledInCoordinates()).thenReturn(coordinates.values().stream().collect(Collectors.toSet()));
		
		
		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, DebugModus.FILLINGS);			
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		
		canvasWindow.paint(g);
		verify(g,atLeastOnce()).drawLine(any(Integer.class), any(Integer.class), any(Integer.class), any(Integer.class));
		//verify(g,atLeastOnce()).drawImage(any(Image.class), any(Integer.class), any(Integer.class),any());
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent_KeyPressedInPalette_ShapeNotExcluded() {
		
		int x = 10;
		int y = 60;
		
		when(paletteArea.checkIfInPalette(x)).thenReturn(true);
		when(paletteArea.getShapeFromCoordinate(x, y)).thenReturn(testPaletteActionShape);
		//when(canvasWindow.getC).thenReturn(testPaletteActionShape);
		
		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 1);
		
		verify(testPaletteActionShape, atLeastOnce()).getX_coord();
		verify(testPaletteActionShape, atLeastOnce()).getY_coord();
		verify(testPaletteActionShape, atLeastOnce()).defineConnectionTypes();
		
		
		// This method wil be verified in the onDragEvent:
		//programArea.setHighlightedShapeForConnections(determineHighlightShape());
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
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
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		when(paletteArea.checkIfInPalette(x)).thenReturn(true);
		when(paletteArea.getShapeFromCoordinate(x, y)).thenReturn(testPaletteActionShape);
		when(canvasWindow.getCurrentShape()).thenReturn(testPaletteActionShape);
		when(canvasWindow.getShapesInMovement()).thenReturn(shapesInMovement);
		//when(canvasWindow.getC).thenReturn(testPaletteActionShape);
		
		canvasWindow.handleMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 1);
		
		verify(testPaletteActionShape, atLeastOnce()).getX_coord();
		verify(testPaletteActionShape, atLeastOnce()).getY_coord();
		verify(testPaletteActionShape, atLeastOnce()).defineConnectionTypes();
		
		
		// This method wil be verified in the onDragEvent:
		//programArea.setHighlightedShapeForConnections(determineHighlightShape());
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
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
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
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
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
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
		//verify(commandHandler, atLeastOnce()).handle(dmc);
		
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
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, ' ');
		
		verify(maskedKeyTimer,atLeastOnce()).cancel();
		verify(maskedKeyBag,atLeastOnce()).pressCtrl(true);
		
		
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
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}

		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_SHIFT, ' ');
		
		verify(maskedKeyTimer,atLeastOnce()).cancel();
		verify(maskedKeyBag,atLeastOnce()).pressShift(true);
//		verify(maskedKeyTimer,atLeastOnce()).schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);	
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_F5_UsefullGame() {
		
		when(domainController.isGameExecutionUseful()).thenReturn(true);
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_F5, ' ');
		
		verify(commandHandler,atLeastOnce()).handle(any(ExecuteBlockCommand.class));
		
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
		
		verify(commandHandler,atLeastOnce()).handle(any(ResetCommand.class));
		
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
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, ' ');
		
		verify(commandHandler,atLeastOnce()).undo();
		verify(maskedKeyTimer,atLeastOnce()).cancel();
		verify(maskedKeyBag,atLeastOnce()).pressShift(false);
		
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
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_Z, ' ');
		
		verify(commandHandler,atLeastOnce()).redo();
		verify(maskedKeyTimer,atLeastOnce()).cancel();
		verify(maskedKeyBag,atLeastOnce()).pressShift(true);
//		verify(maskedKeyTimer,atLeastOnce()).schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_V_KEY() {
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_U, ' ');
		
		verify(commandHandler,atLeastOnce()).undo();
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Pressed_R_KEY() {
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_R, ' ');
		
		verify(commandHandler,atLeastOnce()).redo();
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent_Typed_R_KEY() {
		
		DebugModus debugModus = Mockito.spy(DebugModus.NONE);
		try {
			Field f = CanvasWindow.class.getDeclaredField("debugModus");
			f.setAccessible(true);
			f.set(canvasWindow, debugModus);		
			
		}catch(Exception ex) {
			System.out.println("Exception while injecting");
		}
		
		when(debugModus.getNext()).thenReturn(DebugModus.CONNECTIONS);
		
		canvasWindow.handleKeyEvent(KeyEvent.KEY_TYPED, KeyEvent.VK_UNDEFINED, 'd');
		
		verify(canvasWindow,atLeastOnce()).repaint();
		
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#getShapesInMovement()}.
	 */
	@Test
	public void testGetShapesInMovement() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#CanvasWindow(java.lang.String, applicationLayer.DomainController)}.
	 */
	@Test
	public void testCanvasWindow() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		
		canvasWindow.onBlockAdded(blockAddedEvent);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
//		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
//		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
		
		}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_UP() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		//Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeUP.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeUP.getAddedBlockID(), blockAddedEventWithLinkedShapeUP.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		//when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeUP);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeUP.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_BODY() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		//Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeBODY.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeBODY.getAddedBlockID(), blockAddedEventWithLinkedShapeBODY.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		//when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeBODY);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeBODY.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_CONDITION() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
	//	Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeCONDITION.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeCONDITION.getAddedBlockID(), blockAddedEventWithLinkedShapeCONDITION.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
	//	when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeCONDITION);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeCONDITION.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_DOWN() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		//Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeDOWN.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeDOWN.getAddedBlockID(), blockAddedEventWithLinkedShapeDOWN.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		//when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeDOWN);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeDOWN.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_LEFT_OperatorShape() {
		
		coordinates.put("20", coordinate);
		shapesInProgramArea.add(testOperandShape2);
		
		//Shape linkedShape = Mockito.spy(new UnaryOperatorShape(blockAddedEventWithLinkedShapeLEFT_parentOperand.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockID(), blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockType(), coordinate )).thenReturn(testOperandShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		//when(programArea.getShapeById("21")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeLEFT_parentOperand);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeLEFT_parentOperand.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testOperandShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_LEFT_ControlShape() {
		
		coordinates.put("20", coordinate);
		shapesInProgramArea.add(testControlShape);
		
		//Shape linkedShape = Mockito.spy(new ControlShape(blockAddedEventWithLinkedShapeLEFT_parentControl.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockID(), blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockType(), coordinate )).thenReturn(testOperandShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		//when(programArea.getShapeById("10")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeLEFT_parentControl);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeLEFT_parentControl.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testOperandShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
		
//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , true);
//		//verify(testOperandShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.LEFT);
//		//verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.CONDITION, true);
//		verify(testOperandShape, atLeastOnce()).setConnectedVia( ConnectionType.LEFT , false);
//		verify(testOperandShape, atLeastOnce()).setCoordinatesShape();
//		//verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
		}
	
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_NOCONNECTION() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockID(), blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeNOCONNECTION);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeNOCONNECTION.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
		//verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		//verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		//verify(testActionShape, atLeastOnce()).defineConnectionTypes();
		
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.NOCONNECTION , true);
//		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.NOCONNECTION , false);
//		verify(testActionShape, atLeastOnce()).setCoordinatesShape();

		}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive_OPERAND() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
//		Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShapeOPERAND.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShapeOPERAND.getAddedBlockID(), blockAddedEventWithLinkedShapeOPERAND.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
//		when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShapeOPERAND);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShapeBODY.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
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
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyPaletteBlockIdentifier_LinkedBlockIDEmpty_Positive() {
		
		coordinates.put(PALETTE_BLOCK_IDENTIFIER, coordinate);
		shapesInProgramArea.add(testActionShape);
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		
		canvasWindow.onBlockAdded(blockAddedEvent);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
		
		}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyElse_LinkedBlockIDEmpty_Positive() {
		
		coordinates.put("NOT EXIST", zeroCoordinate);
		shapesInProgramArea.add(testActionShapeWithZeroCoordinate);
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), zeroCoordinate )).thenReturn(testActionShapeWithZeroCoordinate);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		
		canvasWindow.onBlockAdded(blockAddedEvent);
		
		assertEquals(false, currentSnapshot.getSavedCoordinates().containsKey(PALETTE_BLOCK_IDENTIFIER));
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEvent.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShapeWithZeroCoordinate);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShapeWithZeroCoordinate);
		verify(testActionShapeWithZeroCoordinate, atLeastOnce()).defineConnectionTypes();
		
		}
	
	

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
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
			verify(programArea,atLeastOnce()).removeShapeFromProgramArea(any(Shape.class));
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		
		for (Shape shape : shapesInProgramArea) {
			verify(shape,atLeastOnce()).setCoordinatesShape();
			verify(programArea,atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
			verify(shape,atLeastOnce()).defineConnectionTypes();
		}
		
//		verify(canvasWindow, atLeastOnce())
				
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
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
		when(domainController.getAllBlockIDsInBody(testControlShape.getId())).thenReturn(internalsAsStringOfControlBlock);
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(currentSnapshot.getSavedHeights()).thenReturn(heights);
		when(domainController.getBlockType("0")).thenReturn(new BlockType("Move Forward", BlockCategory.ACTION));
		when(blockRemovedEvent.getRemovedBlockId()).thenReturn("10");
		when(shapeFactory.createShape(any(String.class),any(BlockType.class), any(Coordinate.class))).thenAnswer(new Answer<Shape>() {

			@Override
			public Shape answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				BlockType type =  (BlockType) args[1];
				if(type.cat() == BlockCategory.ACTION) {
					return testActionShape;
				}
				else {
					return null;					
				}
			}
			
		});
		canvasWindow.onBlockRemoved(blockRemovedEvent);
		
		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea,atLeastOnce()).removeShapeFromProgramArea(shape);
		}
		
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testControlShape, atLeastOnce()).determineTotalHeight(internalsOfControlBlock);
		
		verify(commandHandler, atLeastOnce()).setHeight(testControlShape.getId(), testControlShape.getHeight());
		
		//If contains Key
			verify(commandHandler, atLeastOnce()).setHeight(testControlShape.getId(), testControlShape.getHeight());
		
		for (Shape shape : shapesInProgramArea) {
			verify(shape,atLeastOnce()).setCoordinatesShape();
			verify(programArea,atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
			verify(shape,atLeastOnce()).defineConnectionTypes();
		}
		
//		verify(canvasWindow, atLeastOnce()).resetShapesInMovement();
				
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockRemoved(events.BlockRemovedEvent)}.
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
		when(domainController.getAllBlockIDsInBody(testControlShape.getId())).thenReturn(internalsAsStringOfControlBlock);
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(currentSnapshot.getSavedHeights()).thenReturn(heights);
		when(domainController.getBlockType("0")).thenReturn(new BlockType("Move Forward", BlockCategory.ACTION));
		when(domainController.getAllBlockIDsBelowCertainBlock("10")).thenReturn(ShapesUnderControlBlockAsString);
		when(blockRemovedEvent.getRemovedBlockId()).thenReturn("10");
		when(programArea.getShapeById("11")).thenReturn(testControlShapeUnder);
		when(shapeFactory.createShape(any(String.class),any(BlockType.class), any(Coordinate.class))).thenAnswer(new Answer<Shape>() {

			@Override
			public Shape answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				BlockType type =  (BlockType) args[1];
				if(type.cat() == BlockCategory.ACTION) {
					return testActionShape;
				}
				else {
					return null;					
				}
			}
			
		});
		canvasWindow.onBlockRemoved(blockRemovedEvent);
		
		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			verify(programArea,atLeastOnce()).removeShapeFromProgramArea(shape);
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
	 * Test method for {@link guiLayer.CanvasWindow#onPanelChangedEvent(events.PanelChangeEvent)}.
	 */
	@Test
	public void testOnPanelChangedEventTrue() {

		canvasWindow.onPanelChangedEvent(panelChangeEventTrue);
		verify(paletteArea, atLeastOnce()).setPaletteVisible(true);
		
	}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onPanelChangedEvent(events.PanelChangeEvent)}.
	 */
	@Test
	public void testOnPanelChangedEventFalse() {

		canvasWindow.onPanelChangedEvent(panelChangeEventFalse);
		verify(paletteArea, atLeastOnce()).setPaletteVisible(false);
		
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onUpdateHighlightingEvent(events.UpdateHighlightingEvent)}.
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
	 * Test method for {@link guiLayer.CanvasWindow#onUpdateHighlightingEvent(events.UpdateHighlightingEvent)}.
	 */
	@Test()
	public void testOnUpdateHighlightingEvent_IDDoesntExist() {
		
		Shape highlightedShape = testActionShape;
		
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);		
		canvasWindow.onUpdateHighlightingEvent(updateHighlightingEvent);

		verify(programArea, atLeastOnce()).setHighlightedShapeForExecution(null);
		
	}
	

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onRobotChangeEvent(events.RobotChangeEvent)}.
	 */
	@Test
	public void testOnRobotChangeEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onRobotAddedEvent(events.RobotAddedEvent)}.
	 */
	@Test
	public void testOnRobotAddedEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onElementAddedEvent(events.ElementAddedEvent)}.
	 */
	@Test
	public void testOnElementAddedEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#getCurrentShape()}.
	 */
	@Test
	public void testGetCurrentShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#getX_offsetCurrentShape()}.
	 */
	@Test
	public void testGetX_offsetCurrentShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#setX_offsetCurrentShape(int)}.
	 */
	@Test
	public void testSetX_offsetCurrentShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#getY_offsetCurrentShape()}.
	 */
	@Test
	public void testGetY_offsetCurrentShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#setY_offsetCurrentShape(int)}.
	 */
	@Test
	public void testSetY_offsetCurrentShape() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#getShapeFactory()}.
	 */
	@Test
	public void testGetShapeFactory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#setShapeFactory(guiLayer.ShapeFactory)}.
	 */
	@Test
	public void testSetShapeFactory() {
		fail("Not yet implemented");
	}

}
