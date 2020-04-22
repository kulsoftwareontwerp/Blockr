/**
 * 
 */
package guiLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;

import applicationLayer.DomainController;
import applicationLayer.GameController;
import events.BlockAddedEvent;
import events.BlockRemovedEvent;
import guiLayer.commands.CommandHandler;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.GuiSnapshot;
import types.BlockType;
import types.ConnectionType;

/**
 * CanvasWindowTest
 *
 * @version 0.1
 * @author group17
 */
public class CanvasWindowTest implements Constants {
	
	@Mock(name="currentSnapshot")
	private GuiSnapshot currentSnapshot;
	
	@Mock(name="shapeFactory")
	private ShapeFactory shapeFactory;
	
	@Mock(name="commandHandler")
	private CommandHandler commandHandler;
	
	@Mock(name="programArea")
	private ProgramArea programArea;
	
	@Mock(name="domainController")
	private DomainController domainController;
	
	@Spy @InjectMocks
	private CanvasWindow canvasWindow = new CanvasWindow(currentSnapshot, shapeFactory, commandHandler, programArea, domainController);
	
	private int initX;
	private int initY;
	private Coordinate coordinate;
	private Coordinate zeroCoordinate;
	
	private BlockAddedEvent blockAddedEvent;
	private BlockAddedEvent blockAddedEventWithLinkedShape;
	private BlockRemovedEvent blockRemovedEvent;
	
	private HashMap<String, Coordinate> coordinates;
	
	private Shape testActionShape;
	private Shape testActionShapeWithZeroCoordinate;
	
	private ControlShape testControlShape;
	
	private HashSet<Shape> shapesInProgramArea;
	

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
		
		blockAddedEvent = new BlockAddedEvent("0", "", null, BlockType.NOT, null);
		blockAddedEventWithLinkedShape = new BlockAddedEvent("0", "1", ConnectionType.UP , BlockType.NOT, null);
		blockRemovedEvent = new BlockRemovedEvent("0", "", ConnectionType.NOCONNECTION, null);
		
		coordinates = new HashMap<String, Coordinate>();
		
		
		testActionShape = Mockito.spy(new ActionShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), coordinate));
		testActionShapeWithZeroCoordinate = Mockito.spy(new ActionShape(blockAddedEvent.getAddedBlockID(), blockAddedEvent.getAddedBlockType(), zeroCoordinate));

		shapesInProgramArea = new HashSet<Shape>();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#paint(java.awt.Graphics)}.
	 */
	@Test
	public void testPaint() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleMouseEvent(int, int, int, int)}.
	 */
	@Test
	public void testHandleMouseEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#handleKeyEvent(int, int, char)}.
	 */
	@Test
	public void testHandleKeyEvent() {
		fail("Not yet implemented");
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
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
		
		}
	
	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockAdded(events.BlockAddedEvent)}.
	 */
	@Test
	public void testOnBlockAdded_ContainsKeyNotPaletteBlockIdentifier_LinkedBlockIDNotEmpty_Positive() {
		
		coordinates.put("0", coordinate);
		shapesInProgramArea.add(testActionShape);
		
		Shape linkedShape = Mockito.spy(new ActionShape(blockAddedEventWithLinkedShape.getLinkedBlockID(), null, coordinate));
		
		when(currentSnapshot.getSavedCoordinates()).thenReturn(coordinates);
		when(shapeFactory.createShape(blockAddedEventWithLinkedShape.getAddedBlockID(), blockAddedEventWithLinkedShape.getAddedBlockType(), coordinate )).thenReturn(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesInProgramArea);
		when(programArea.getShapeById("1")).thenReturn(linkedShape);
	
		canvasWindow.onBlockAdded(blockAddedEventWithLinkedShape);
		
		verify(commandHandler,atLeastOnce()).setAddedId(blockAddedEventWithLinkedShape.getAddedBlockID());
		verify(programArea,atLeastOnce()).addShapeToProgramArea(testActionShape);
		verify(programArea,atLeastOnce()).clearAlreadyFilledInCoordinates();
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea, atLeastOnce()).addToAlreadyFilledInCoordinates(testActionShape);
		verify(testActionShape, atLeastOnce()).defineConnectionTypes();
		
		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.UP , true);
		verify(testActionShape, atLeastOnce()).clipOn(linkedShape, ConnectionType.UP);
		verify(linkedShape, atLeastOnce()).setConnectedVia(ConnectionType.DOWN, true);
		verify(testActionShape, atLeastOnce()).setConnectedVia( ConnectionType.UP , false);
		verify(testActionShape, atLeastOnce()).setCoordinatesShape();
		verify(programArea , atLeastOnce()).addShapeToProgramArea(linkedShape);
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
		
		canvasWindow.onBlockRemoved(blockRemovedEvent);
		
		for (Shape shape2 : shapesToBeRemovedFromProgramArea) {
			verify(programArea,atLeastOnce()).removeShapeFromProgramArea(shape2);
		}
		verify(programArea, atLeastOnce()).clearAlreadyFilledInCoordinates();
		
		for (Shape shape : shapesInProgramArea) {
			verify(shape,atLeastOnce()).setCoordinatesShape();
			verify(programArea,atLeastOnce()).addToAlreadyFilledInCoordinates(shape);
			verify(shape,atLeastOnce()).defineConnectionTypes();
		}
		
		verify(canvasWindow, atLeastOnce()).resetShapesInMovement();
		
		
		
		
		
		//updatePositionOfAllShapesAccordingToChangesOfTheControlShapes();
		


		for (ControlShape c : changedControlShapes) {
			Set<Shape> shapesToMove = domainController.getAllBlockIDsBelowCertainBlock(c.getId()).stream()
					.filter(s -> !s.equals(c.getId())).map(s -> programArea.getShapeById(s))
					.collect(Collectors.toSet());
			for (Shape shape : shapesToMove) {
				if (shape != null) {
					programArea.removeFromAlreadyFilledInCoordinates(shape);
					shape.setY_coord(shape.getY_coord() + c.getHeightDiff());
					shape.setCoordinatesShape();
					shape.defineConnectionTypes();
					programArea.addToAlreadyFilledInCoordinates(shape);
				}
			}
		}
		
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onBlockChangeEvent(events.BlockChangeEvent)}.
	 */
	@Test
	public void testOnBlockChangeEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onPanelChangedEvent(events.PanelChangeEvent)}.
	 */
	@Test
	public void testOnPanelChangedEvent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link guiLayer.CanvasWindow#onUpdateHighlightingEvent(events.UpdateHighlightingEvent)}.
	 */
	@Test
	public void testOnUpdateHighlightingEvent() {
		fail("Not yet implemented");
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
