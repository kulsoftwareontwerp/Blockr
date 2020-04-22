/**
 * 
 */
package guiLayer.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;

/**
 * GuiSnapshot, a dataBag containing all info regarding the Gui to restore a set of blocks to a previous state
 * The position of the shapes in movement and the height of all controlShapes in movement is saved in this snapshot. 
 * 
 * @version 0.1
 * @author group17
 *
 */
public class GuiSnapshot implements Constants {

	private HashMap<String, Coordinate> savedCoordinates;
	private HashMap<String, Integer> savedHeights;

	/**
	 * Create a new GuiSnapshot with a set of shapes in movement.
	 * @param shapesInMovement the set of shapes in movement to save
	 */
	public GuiSnapshot(Set<Shape> shapesInMovement) {
		super();
		savedCoordinates = new HashMap<String, Coordinate>();
		savedHeights= new HashMap<String, Integer>();
		if (shapesInMovement != null) {
			for (Shape s : shapesInMovement) {
				savedCoordinates.put(s.getId(), s.getCoordinate());
				if(s instanceof ControlShape) {
					savedHeights.put(s.getId(), s.getHeight());
				}
			}
		}

	}

	/**
	 * Retrieve a Map with all id's of all shapes in movement and their position
	 * @return  a Map with all id's of all shapes in movement and their position
	 */
	public HashMap<String, Coordinate> getSavedCoordinates() {
		return new HashMap<String, Coordinate>(savedCoordinates);
	}
	
	
	
	
	/**
	 * Retrieve a Map with all id's of controlShapes in movement and their saved Height
	 * @return a Map with all id's of controlShapes in movement and their saved Height
	 */
	public Map<String, Integer> getSavedHeights() {
		return new HashMap<String, Integer>(savedHeights);
	}

	/**
	 * If there are unset ID's in this snapshot these will be replaced with the given ID.
	 * @param ID The id to set the unset ID's to.
	 */
	public void setID(String ID) {
		if(ID!=null && !ID.equals("")&& !savedCoordinates.containsKey(ID) &&savedCoordinates.containsKey(PALETTE_BLOCK_IDENTIFIER)) {
			Coordinate c = savedCoordinates.get(PALETTE_BLOCK_IDENTIFIER);
			savedCoordinates.remove(PALETTE_BLOCK_IDENTIFIER);
			savedCoordinates.put(ID,c);
		}
	}
	
	
	/**
	 * Set the saved height for the given ID to the given height
	 * @param ID The id to set the saved height for.
	 * @param height the height to set the current ID to.
	 */
	public void setHeight(String ID ,int height) {
		if(savedHeights.containsKey(ID)) {
			savedHeights.put(ID, height);
		}
	}
	

}
