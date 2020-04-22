/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import guiLayer.shapes.ActionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.UnaryOperatorShape;
import types.BlockCategory;
import types.BlockType;

/**
 * /** GuiSnapshotTest
 * 
 * @version 0.1
 * @author group17
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GuiSnapshotTest implements Constants {

	@Spy
	private HashSet<Shape> shapesInMovement=fillData() ;
	@Spy
	private HashMap<String, Coordinate> savedCoordinates;
	@Spy
	private HashMap<String, Integer> savedHeights;
	@Spy
	@InjectMocks
	private GuiSnapshot snapshot;

	
	private HashMap<String, Coordinate> savedCoordinatesCopy;
	private HashMap<String, Integer> savedHeightsCopy;
	
	private UnaryOperatorShape newShape;
	
	private ControlShape controlShape;
	@Captor
	private ArgumentCaptor<String> idCaptor;
	@Captor
	private ArgumentCaptor<Coordinate> coordinateCaptor;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		savedCoordinates=new HashMap<String, Coordinate>();
//		savedHeights=new HashMap<String, Integer>();


	}

	private HashSet<Shape> fillData() {
		HashSet<Shape> shapesInMovement = new HashSet<Shape>();
		newShape = mock(UnaryOperatorShape.class);
		controlShape= mock(ControlShape.class);
		when(newShape.getId()).thenReturn(PALETTE_BLOCK_IDENTIFIER);
		when(newShape.getCoordinate()).thenReturn(new Coordinate(1, 5));
		when(controlShape.getId()).thenReturn("controlShape");
		when(controlShape.getHeight()).thenReturn(50);

		shapesInMovement.add(new ActionShape("0", new BlockType("", BlockCategory.ACTION), new Coordinate(1, 1)));
		shapesInMovement.add(new ControlShape("1", BlockType.IF, new Coordinate(3, 1)));
		shapesInMovement.add(new ControlShape("2", BlockType.WHILE, new Coordinate(3, 2)));
		shapesInMovement.add(newShape);
		shapesInMovement.add(controlShape);
		
		savedCoordinatesCopy=new HashMap<String, Coordinate>();
		savedHeightsCopy=new HashMap<String, Integer>();
		
		if (shapesInMovement != null) {
			for (Shape s : shapesInMovement) {
				this.savedCoordinatesCopy.put(s.getId(), s.getCoordinate());
				if(s instanceof ControlShape) {
					this.savedHeightsCopy.put(s.getId(), s.getHeight());
				}
			}
		}

		return shapesInMovement;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#GuiSnapshot(java.util.Set)}.
	 */
	@Test
	public void testGuiSnapshot() {
		GuiSnapshot snapshot = new GuiSnapshot(shapesInMovement);
		assertNotNull(snapshot);
		try {
			Field f;
			f = GuiSnapshot.class.getDeclaredField("savedCoordinates");
			f.canAccess(true);
			assertEquals(savedCoordinates, f.get(snapshot));
			f = GuiSnapshot.class.getDeclaredField("savedHeights");
			f.canAccess(true);
			assertEquals(savedHeights, f.get(snapshot));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#GuiSnapshot(java.util.Set)}.
	 */
	@Test
	public void testGuiSnapshotNullShapesInMovement() {
		GuiSnapshot snapshot = new GuiSnapshot(null);
		assertNotNull(snapshot);
		try {
			Field f;
			f = GuiSnapshot.class.getDeclaredField("savedCoordinates");
			f.canAccess(true);
			assertEquals(new HashMap<String, Coordinate>(), f.get(snapshot));
			f = GuiSnapshot.class.getDeclaredField("savedHeights");
			f.canAccess(true);
			assertEquals(new HashMap<String, Integer>(), f.get(snapshot));
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#getSavedCoordinates()}.
	 */
	@Test
	public void testGetSavedCoordinates() {
		assertEquals(savedCoordinatesCopy.size(), snapshot.getSavedCoordinates().size());
		assertTrue(savedCoordinatesCopy.values().containsAll(snapshot.getSavedCoordinates().values()));
		assertTrue(snapshot.getSavedCoordinates().values().containsAll(savedCoordinatesCopy.values()));
		
	
	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#getSavedHeights()}.
	 */
	@Test
	public void testGetSavedHeights() {
		GuiSnapshot snapshot = new GuiSnapshot(shapesInMovement);
		assertEquals(savedHeights, snapshot.getSavedHeights());
	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#setID(java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetID() {
		clearInvocations(savedCoordinates);
		snapshot.setID("newId");

		verify(savedCoordinates).remove(idCaptor.capture());
		assertEquals(PALETTE_BLOCK_IDENTIFIER, idCaptor.getValue());

		verify(savedCoordinates).put(idCaptor.capture(), coordinateCaptor.capture());
		assertEquals("newId", idCaptor.getValue());
		assertEquals(new Coordinate(1, 5), coordinateCaptor.getValue());

	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#setID(java.lang.String)}.
	 */
	@Test
	public void testSetIDWithoutIdToSet() {
		savedCoordinates.remove(PALETTE_BLOCK_IDENTIFIER);

		snapshot.setID("newId");

		verify(savedCoordinates, times(0)).remove(PALETTE_BLOCK_IDENTIFIER);
		verify(savedCoordinates, times(0)).put(any(String.class), any(Coordinate.class));

	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#setHeight(java.lang.String, int)}.
	 */
	@Test
	public void testSetHeight() {
		fail("Not yet implemented");
	}

}
