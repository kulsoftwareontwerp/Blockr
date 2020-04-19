/**
 * 
 */
package guiLayer.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;

/**
 * /** GuiAction
 * 
 * @version 0.1
 * @author group17
 *
 */
public class GuiSnapshot implements Constants {

	private HashMap<String, Coordinate> savedCoordinates;
	private HashMap<String, Integer> savedHeights;

	/**
	 * 
	 * @param shapesInMovement
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
	 * @return the savedCoordinates
	 */
	public HashMap<String, Coordinate> getSavedCoordinates() {
		return new HashMap<String, Coordinate>(savedCoordinates);
	}
	
	/**
	 * @return the savedHeights
	 */
	public HashMap<String, Integer> getSavedHeights() {
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
	


}
