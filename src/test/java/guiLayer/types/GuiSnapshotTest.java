/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.internal.verification.NoInteractions;
import org.mockito.internal.verification.NoMoreInteractions;
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

	private HashSet<Shape> shapesInMovement;

	@Spy
	private HashMap<String, Coordinate> savedCoordinates;

	@Spy
	private HashMap<String, Integer> savedHeights;

	private GuiSnapshot snapshot;

	private UnaryOperatorShape newShape;

	private ControlShape controlShape;
	@Captor
	private ArgumentCaptor<String> idCaptor;
	@Captor
	private ArgumentCaptor<Integer> heightCaptor;
	@Captor
	private ArgumentCaptor<Coordinate> coordinateCaptor;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		savedCoordinates=new HashMap<String, Coordinate>();
//		savedHeights=new HashMap<String, Integer>();
		snapshot = fillData();

	}

	@SuppressWarnings("unchecked")
	private GuiSnapshot fillData() {

		shapesInMovement = new HashSet<Shape>();

		newShape = mock(UnaryOperatorShape.class);
		controlShape = mock(ControlShape.class);
		when(newShape.getId()).thenReturn(PALETTE_BLOCK_IDENTIFIER);
		when(newShape.getCoordinate()).thenReturn(new Coordinate(1, 5));
		when(controlShape.getId()).thenReturn("height");
		when(controlShape.getCoordinate()).thenReturn(new Coordinate(9, 5));
		when(controlShape.getHeight()).thenReturn(50);

		shapesInMovement.add(spy(new ActionShape("0", new BlockType("", BlockCategory.ACTION), new Coordinate(1, 1))));
		shapesInMovement.add(spy(new ControlShape("1", BlockType.IF, new Coordinate(3, 1))));
		shapesInMovement.add(spy(new ControlShape("2", BlockType.WHILE, new Coordinate(3, 2))));
		shapesInMovement.add(newShape);
		shapesInMovement.add(controlShape);

		if (shapesInMovement != null) {
			for (Shape s : shapesInMovement) {
				this.savedCoordinates.put(s.getId(), s.getCoordinate());
				if (s instanceof ControlShape) {
					this.savedHeights.put(s.getId(), s.getHeight());
				}
			}
		}

		GuiSnapshot snapshot = null;

		try {
			@SuppressWarnings("rawtypes")
			Class[] cArg = new Class[3];
			cArg[0] = (new HashMap<String, Coordinate>()).getClass();
			cArg[1] = (new HashMap<String, Integer>()).getClass();
			cArg[2] = Class.forName("java.util.Set");
			Constructor<GuiSnapshot> c = GuiSnapshot.class.getDeclaredConstructor(cArg);
			c.setAccessible(true);
			snapshot = c.newInstance(savedCoordinates, savedHeights, shapesInMovement);

		} catch (Exception e) {
			System.err.println("Exception during injection");
			System.out.println(e);
		}
		clearInvocations(savedCoordinates);
		clearInvocations(savedHeights);

		return snapshot;
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
	@SuppressWarnings("unchecked")
	@Test
	public void testGuiSnapshot() {

		GuiSnapshot snapshot = new GuiSnapshot(shapesInMovement);
		assertNotNull(snapshot);
		try {
			Field f;
			f = GuiSnapshot.class.getDeclaredField("savedCoordinates");
			f.setAccessible(true);
			assertEquals(savedCoordinates.entrySet(), ((HashMap<String, Coordinate>) f.get(snapshot)).entrySet());
			f = GuiSnapshot.class.getDeclaredField("savedHeights");
			f.setAccessible(true);
			assertEquals(savedHeights.entrySet(), ((HashMap<String, Coordinate>) f.get(snapshot)).entrySet());
		} catch (Exception e) {
			fail("fields not initialized");
		}
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#GuiSnapshot(java.util.Set)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAddShapeToSnapshot() {
		Shape addShape = new ControlShape("testAdd", BlockType.WHILE, new Coordinate(99, 99));

		snapshot.addShapeToSnapshot(addShape);
		try {
			Field f;
			f = GuiSnapshot.class.getDeclaredField("savedCoordinates");
			f.setAccessible(true);

			assertEquals(new Coordinate(99, 99), ((HashMap<String, Coordinate>) f.get(snapshot)).get("testAdd"));
		} catch (Exception e) {
			fail("fields not initialized");
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
		assertEquals(savedCoordinates.size(), snapshot.getSavedCoordinates().size());
		assertTrue(savedCoordinates.values().containsAll(snapshot.getSavedCoordinates().values()));
		assertTrue(snapshot.getSavedCoordinates().values().containsAll(savedCoordinates.values()));

	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#getSavedHeights()}.
	 */
	@Test
	public void testGetSavedHeights() {
		GuiSnapshot snapshot = new GuiSnapshot(shapesInMovement);
		assertEquals(savedHeights.size(), snapshot.getSavedHeights().size());
		assertTrue(savedHeights.values().containsAll(snapshot.getSavedHeights().values()));
		assertTrue(snapshot.getSavedHeights().values().containsAll(savedHeights.values()));

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
	@SuppressWarnings("unchecked")
	@Test
	public void testSetIDWithoutIdToSet() {
		savedCoordinates.remove(PALETTE_BLOCK_IDENTIFIER);
		clearInvocations(savedCoordinates);

		snapshot.setID("newId");

		verify(savedCoordinates, never()).remove(any());
		verify(savedCoordinates, never()).put(any(String.class), any(Coordinate.class));

	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#setID(java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetIDAlreadyPresentID() {
		clearInvocations(savedCoordinates);
		snapshot.setID("height");

		verify(savedCoordinates, never()).remove(any());
		verify(savedCoordinates, never()).put(any(String.class), any(Coordinate.class));
	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#setID(java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetIDNullID() {
		clearInvocations(savedCoordinates);
		snapshot.setID(null);

		verify(savedCoordinates, never()).remove(any());
		verify(savedCoordinates, never()).put(any(String.class), any(Coordinate.class));
	}

	/**
	 * Test method for {@link guiLayer.types.GuiSnapshot#setID(java.lang.String)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetIDEmptyStringID() {
		clearInvocations(savedCoordinates);
		snapshot.setID("");

		verify(savedCoordinates, never()).remove(any());
		verify(savedCoordinates, never()).put(any(String.class), any(Coordinate.class));
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#setHeight(java.lang.String, int)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetHeight() {
		clearInvocations(savedHeights);
		snapshot.setHeight("height", 80);

		verify(savedHeights, times(1)).put(idCaptor.capture(), heightCaptor.capture());
		assertEquals("height", idCaptor.getValue());
		assertEquals(Integer.valueOf(80), heightCaptor.getValue());
	}

	/**
	 * Test method for
	 * {@link guiLayer.types.GuiSnapshot#setHeight(java.lang.String, int)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSetHeightNoHeightToSet() {
		savedHeights.remove("height");
		clearInvocations(savedHeights);
		snapshot.setHeight("height", 80);

		verify(savedHeights, never()).put(any(), any());
//		verify(savedHeights,times(1)).put(idCaptor.capture(),heightCaptor.capture());
//		assertEquals("height", idCaptor.getValue());
//		assertEquals(Integer.valueOf(80), heightCaptor.getValue());
	}

}
