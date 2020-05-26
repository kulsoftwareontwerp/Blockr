/**
 * 
 */
package guiLayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;

import org.mockito.*;

import applicationLayer.DomainController;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.ConditionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.DefinitionShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.UnaryOperatorShape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockCategory;
import types.BlockType;
import types.ConnectionType;

/**
 * ProgramAreaTest
 *
 * @version 0.1
 * @author group17
 */
public class ProgramAreaTest implements Constants{

	@Spy
	@InjectMocks
	private ProgramArea programArea = new ProgramArea();

	private int initX;
	private int initY;
	private Coordinate coordinate;
	private Coordinate zeroCoordinate;
	private Coordinate outOfBoundsCoordinate;
	private Coordinate inBoundsCoordinate;

	private Shape testOperandShape;
	private Shape testOperandShape2;
	
	private Shape testActionShapeWithID;
	private Shape testActionShape;
	private Shape testActionShapeWithZeroCoordinate;
	private Shape testActionShapeOutOfBoundsCoordinate;
	private Shape testActionShapeInBoundsCoordinate;

	private DefinitionShape testDefinitionShape;
	private ControlShape testControlShape;
	private ControlShape testControlShapeWhile;
	private ControlShape testControlShapeOOB;

	private UnaryOperatorShape testUnaryShape;

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
		outOfBoundsCoordinate = new Coordinate(5000, 5000);
		inBoundsCoordinate = new Coordinate(250, 250);
		zeroCoordinate = new Coordinate(0, 0);

		moveForward = new BlockType("Move Forward", BlockCategory.ACTION);

		testActionShapeWithID= Mockito.spy(new ActionShape("0", moveForward, coordinate));
		testActionShape = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, coordinate));
		testActionShapeWithZeroCoordinate = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, zeroCoordinate));
		testActionShapeOutOfBoundsCoordinate = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, outOfBoundsCoordinate));
		testActionShapeInBoundsCoordinate = Mockito.spy(new ActionShape(PALETTE_BLOCK_IDENTIFIER, moveForward, inBoundsCoordinate));

		testDefinitionShape = spy(new DefinitionShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION, inBoundsCoordinate));
		
		testControlShape = Mockito.spy(new ControlShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, inBoundsCoordinate));
		testControlShapeWhile = Mockito.spy(new ControlShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WHILE, inBoundsCoordinate));
		testControlShapeOOB = Mockito.spy(new ControlShape(PALETTE_BLOCK_IDENTIFIER, BlockType.IF, outOfBoundsCoordinate));

		testOperandShape = Mockito.spy(new ConditionShape("20", BlockType.NOT, coordinate));
		testOperandShape2 = Mockito.spy(new ConditionShape("21", BlockType.NOT, coordinate));

		testUnaryShape = Mockito.spy(new UnaryOperatorShape("30", BlockType.NOT, coordinate));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link guiLayer.ProgramArea#getShapesInProgramArea()}.
	 */
	@Test
	public void testGetShapesInProgramArea() {

		HashSet<Shape> shapesInProgramArea = new HashSet<Shape>();
		shapesInProgramArea.add(testActionShape);

		try {
			Field f1 = ProgramArea.class.getDeclaredField("shapesInProgramArea");
			f1.setAccessible(true);
			f1.set(programArea, shapesInProgramArea);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		assertNotNull(programArea.getShapesInProgramArea());
		assertTrue(programArea.getShapesInProgramArea().size() == 1);
	}

	/**
	 * 
	 */
	@Test
	public void testSetProgramAndGameBorder() {

		programArea.setProgramAndGameBorder(10);
		try {
			Field f1 = ProgramArea.class.getDeclaredField("programAndGameBorder");
			f1.setAccessible(true);
			assertTrue(f1.get(programArea).equals(10));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * 
	 */
	@Test
	public void testProgramArea_Positive() {

		ProgramArea pa = new ProgramArea();
		try {
			Field f = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f.setAccessible(true);
			assertNotNull(f.get(pa));
			assertEquals(0, ((HashSet<Coordinate>) f.get(pa)).size());

			Field f1 = ProgramArea.class.getDeclaredField("shapesInProgramArea");
			f1.setAccessible(true);
			assertNotNull(f1.get(pa));
			assertEquals(0, ((HashSet<Shape>) f1.get(pa)).size());

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#clearAlreadyFilledInCoordinates()}.
	 */
	@Test
	public void testClearAlreadyFilledInCoordinates() {

		HashSet<Coordinate> alreadyFilledCoordinates = new HashSet<Coordinate>();
		alreadyFilledCoordinates.add(zeroCoordinate);

		try {
			Field f0 = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f0.setAccessible(true);
			f0.set(programArea, alreadyFilledCoordinates);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		programArea.clearAlreadyFilledInCoordinates();
		try {
			Field f = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f.setAccessible(true);
			assertNotNull(f.get(programArea));
			assertEquals(0, ((HashSet<Coordinate>) f.get(programArea)).size());

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#addShapeToProgramArea(guiLayer.Shape)}.
	 */
	@Test
	public void testAddShapeToProgramArea_NotNull_Present() {
		when(programArea.getShapeById("0")).thenReturn(testActionShape);
		programArea.addShapeToProgramArea(testActionShape);
		assertTrue(programArea.getShapesInProgramArea().contains(testActionShape));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#addShapeToProgramArea(guiLayer.Shape)}.
	 */
	@Test
	public void testAddShapeToProgramArea_Null() {
		programArea.addShapeToProgramArea(null);
		assertFalse(programArea.getShapesInProgramArea().contains(testActionShape));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#addShapeToProgramArea(guiLayer.Shape)}.
	 */
	@Test
	public void testAddShapeToProgramArea_NotNull_NotPresent() {
		when(programArea.getShapeById("0")).thenReturn(null);
		programArea.addShapeToProgramArea(testActionShape);
		assertTrue(programArea.getShapesInProgramArea().contains(testActionShape));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#removeShapeFromProgramArea(guiLayer.Shape)}.
	 */
	@Test
	public void testRemoveShapeFromProgramArea() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);
		try {
			Field f0 = ProgramArea.class.getDeclaredField("shapesInProgramArea");
			f0.setAccessible(true);
			f0.set(programArea, shapesIPA);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		programArea.removeShapeFromProgramArea(testActionShape);
		assertFalse(programArea.getShapesInProgramArea().contains(testActionShape));

		try {
			Field f0 = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f0.setAccessible(true);
			assertFalse(((HashSet<Coordinate>) f0.get(programArea)).contains(coordinate));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * Test method for {@link guiLayer.ProgramArea#checkIfInProgramArea(int)}.
	 */
	@Test
	public void testCheckIfInProgramArea_Positive() {
		programArea.checkIfInProgramArea(400);
	}

	/**
	 * Test method for {@link guiLayer.ProgramArea#checkIfInProgramArea(int)}.
	 */
	@Test
	public void testCheckIfInProgramArea_False_Under() {
		programArea.checkIfInProgramArea(50);
	}

	/**
	 * Test method for {@link guiLayer.ProgramArea#checkIfInProgramArea(int)}.
	 */
	@Test
	public void testCheckIfInProgramArea_False_Upper() {
		programArea.checkIfInProgramArea(5000);
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#getShapeFromCoordinate(int, int)}.
	 */
	@Test
	public void testGetShapeFromCoordinate_Present() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);

		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		assertTrue(programArea.getShapeFromCoordinate(initX, initY).equals(testActionShape));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#getShapeFromCoordinate(int, int)}.
	 */
	@Test
	public void testGetShapeFromCoordinate_NotPresent() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		assertNull(programArea.getShapeFromCoordinate(initX, initY));
	}

	@Test
	public void testGetShapeById_Present() {

		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShapeWithID);

		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);

		assertTrue(programArea.getShapeById("0").equals(testActionShapeWithID));
	}

	@Test
	public void testGetShapeById_NotPresent() {

		HashSet<Shape> shapesIPA = new HashSet<Shape>();

		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);

		assertNull(programArea.getShapeById("0"));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_NotControlShape() {
		assertTrue(programArea.checkIfPlaceable(testActionShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_NotPlaceable_AlreadyFilledInCoord_NotControlShape() {

		HashSet<Coordinate> coordIPA = new HashSet<Coordinate>();
		coordIPA.add(coordinate);
		try {
			Field f0 = ProgramArea.class.getDeclaredField("alreadyFilledInCoordinates");
			f0.setAccessible(true);
			f0.set(programArea, coordIPA);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
		testActionShape.setCoordinatesShape();
		assertFalse(programArea.checkIfPlaceable(testActionShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_NotPlaceable_OutOfBounds_NotControlShape() {

		assertFalse(programArea.checkIfPlaceable(testActionShapeOutOfBoundsCoordinate, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_InBounds_NotControlShape() {

		assertTrue(programArea.checkIfPlaceable(testActionShapeInBoundsCoordinate, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_ControlShape() {
		assertTrue(programArea.checkIfPlaceable(testControlShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_NotPlaceable_OutOfBounds_ControlShape() {
		assertFalse(programArea.checkIfPlaceable(testControlShapeOOB, mock(DomainController.class)));
	}
	

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_NOT_Placeable_WithHighlightedNotControl_ControlShape() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});
		assertFalse(programArea.checkIfPlaceable(testControlShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_WithHighlightedIF_ControlShape() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testControlShape);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});
		assertTrue(programArea.checkIfPlaceable(testControlShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_WithHighlightedWHILE_ControlShape() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testControlShapeWhile);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});

		assertTrue(programArea.checkIfPlaceable(testControlShape, mock(DomainController.class)));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_WithHighlightedDEFINITION_ControlShape() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testDefinitionShape);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});

		assertTrue(programArea.checkIfPlaceable(testControlShape, mock(DomainController.class)));
	}
	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_WithHighlightedACTION_IN_BODY() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});
		
		DomainController dc = mock(DomainController.class);
		when(dc.checkIfBlockIsInBody(any(String.class))).thenReturn(true);
		
		assertTrue(programArea.checkIfPlaceable(testControlShape, dc));
	}
	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_NOT_Placeable_WithHighlightedACTION_NOT_IN_BODY() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);
		when(programArea.getAlreadyFilledInCoordinates()).then(new Answer<Set<Coordinate>>() {

			@Override
			public Set<Coordinate> answer(InvocationOnMock invocation) throws Throwable {
				return testControlShape.getCoordinatesShape();
			}
		});
		
		DomainController dc = mock(DomainController.class);
		when(dc.checkIfBlockIsInBody(any(String.class))).thenReturn(false);
		
		assertFalse(programArea.checkIfPlaceable(testControlShape, dc));
	}
	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#checkIfPlaceable(guiLayer.Shape, DomainController)}.
	 */
	@Test
	public void testCheckIfPlaceable_Placeable_WithHighlightedWHILE_ControlShapeWhile() {
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testControlShapeWhile);
		assertTrue(programArea.checkIfPlaceable(testControlShapeWhile, mock(DomainController.class)));
	}

	/**
	 * Test method for {@link guiLayer.ProgramArea#getAlreadyFilledInCoordinates()}.
	 */
	@Test
	public void testGetAlreadyFilledInCoordinates() {
		assertNotNull(programArea.getAlreadyFilledInCoordinates());
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#addToAlreadyFilledInCoordinates(guiLayer.Shape)}.
	 */
	@Test
	public void testAddToAlreadyFilledInCoordinates_ShapePresent() {
		when(programArea.getShapeById("0")).thenReturn(testActionShape);
		programArea.addToAlreadyFilledInCoordinates(testActionShape);
		assertTrue(programArea.getAlreadyFilledInCoordinates().contains(coordinate));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#addToAlreadyFilledInCoordinates(guiLayer.Shape)}.
	 */
	@Test
	public void testAddToAlreadyFilledInCoordinates_ShapeNotPresent() {
		when(programArea.getShapeById("0")).thenReturn(null);
		programArea.addToAlreadyFilledInCoordinates(testActionShape);
		assertTrue(programArea.getAlreadyFilledInCoordinates().contains(coordinate));
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#setHighlightedShapeForConnections(guiLayer.Shape)}.
	 */
	@Test
	public void testSetHighlightedShape() {
		programArea.setHighlightedShapeForConnections(testActionShape);

		try {
			Field f0 = ProgramArea.class.getDeclaredField("highlightedShape");
			f0.setAccessible(true);
			assertTrue(f0.get(programArea).equals(testActionShape));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#draw(java.awt.Graphics, DomainController)}.
	 */
	@Test
	public void testDraw_Negative() {
		Graphics g = Mockito.spy(Graphics.class);
		DomainController dc = null;
		programArea.draw(g, dc);
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#draw(java.awt.Graphics, DomainController)}.
	 */
	@Test
	public void testDraw_Positive_ShapesInProgramArea_NotEmpty_WithBlueHighlight_WithGreenHighLight() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		when(programArea.getHighlightedShapeForExecution()).thenReturn(testActionShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);

		DomainController dc = null;
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);
		programArea.draw(g, dc);
		when(g.getColor()).thenReturn(Color.BLACK);
		programArea.draw(g, dc);

	}

	@Captor
	private ArgumentCaptor<Color> colorCaptor;

	@Test
	public void testDraw_Positive_ShapesInProgramArea_NotEmpty_WithBlueHighlight_WithGreenHighLight_DebugModusEnabled() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		when(programArea.getHighlightedShapeForExecution()).thenReturn(testActionShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);

		CanvasWindow.debugModus = DebugModus.CONNECTIONSTATUS;

		Graphics g = Mockito.spy(Graphics.class);
		GameWorld gameWorld = mock(GameWorld.class);
		GameWorldType gameWorldType = mock(GameWorldType.class);
		when(gameWorld.getType()).thenReturn(gameWorldType);

		DomainController dc = spy(new DomainController(gameWorld));

		doReturn(true, false).when(dc).checkIfConnectionIsOpen(any(String.class), any(ConnectionType.class), any());

		when(g.getColor()).thenReturn(Color.GREEN);
		programArea.draw(g, dc);
		verify(g, atLeastOnce()).setColor(colorCaptor.capture());
		assertTrue(colorCaptor.getAllValues().contains(Color.GREEN));

		when(g.getColor()).thenReturn(Color.BLACK);
		programArea.draw(g, dc);
		verify(g, atLeastOnce()).setColor(colorCaptor.capture());
		assertTrue(colorCaptor.getAllValues().contains(Color.RED));

		CanvasWindow.debugModus = DebugModus.NONE;

	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#draw(java.awt.Graphics, DomainController)}.
	 */
	@Test
	public void testDraw_Positive_ShapesInProgramArea_NotNullButEmpty_WithBlueHighlight_WithGreenHighLight() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		when(programArea.getHighlightedShapeForExecution()).thenReturn(testActionShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);

		DomainController dc = null;
		Graphics g = Mockito.spy(Graphics.class);
		when(g.getColor()).thenReturn(Color.GREEN);
		programArea.draw(g, dc);
		when(g.getColor()).thenReturn(Color.BLACK);
		programArea.draw(g, dc);
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#draw(java.awt.Graphics, DomainController)}.
	 */
	@Test
	public void testDraw_Positive_ShapesInProgramArea_Null_WithBlueHighlight_WithGreenHighLight() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		when(programArea.getShapesInProgramArea()).thenReturn(null);
		when(programArea.getHighlightedShapeForExecution()).thenReturn(testActionShape);
		when(programArea.getHighlightedShapeForConnections()).thenReturn(testActionShape);

		Graphics g = Mockito.spy(Graphics.class);
		DomainController dc = null;
		when(g.getColor()).thenReturn(Color.GREEN);
		programArea.draw(g, dc);
		when(g.getColor()).thenReturn(Color.BLACK);
		programArea.draw(g, dc);
	}

	/**
	 * Test method for
	 * {@link guiLayer.ProgramArea#setHighlightedShapeForExecution(guiLayer.Shape)}.
	 */
	@Test
	public void testSetHighlightedShapeForExecution() {
		programArea.setHighlightedShapeForExecution(testActionShape);

		try {
			Field f0 = ProgramArea.class.getDeclaredField("highlightedShapeForExecution");
			f0.setAccessible(true);
			assertTrue(f0.get(programArea).equals(testActionShape));

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

	}

	@Test
	public void testGetAllChangedControlShapes_WithControlShapes() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testControlShape);
		try {
			Field f0 = ProgramArea.class.getDeclaredField("shapesInProgramArea");
			f0.setAccessible(true);
			f0.set(programArea, shapesIPA);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		programArea.getAllChangedControlShapes();
	}

	@Test
	public void testGetAllChangedControlShapes_WithoutControlShapes() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);
		try {
			Field f0 = ProgramArea.class.getDeclaredField("shapesInProgramArea");
			f0.setAccessible(true);
			f0.set(programArea, shapesIPA);

		} catch (Exception e) {
			fail("fields have not been initialized.");
		}

		programArea.getAllChangedControlShapes();
	}

	@Test
	public void testPlaceShapes() {
		HashSet<Shape> shapesIPA = new HashSet<Shape>();
		shapesIPA.add(testActionShape);
		when(programArea.getShapesInProgramArea()).thenReturn(shapesIPA);
		programArea.placeShapes();
	}

}
