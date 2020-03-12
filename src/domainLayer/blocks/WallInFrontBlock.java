package domainLayer.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import domainLayer.elements.*;
/**
 * The concrete class for the concept of a WallInFront block.
 * 
 * @version 0.1
 * @author group17
 */
public class WallInFrontBlock extends ConditionBlock {

	/**
	 * Create a WallInFront Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public WallInFrontBlock(String blockId) {
		super(blockId);
	}

	
	@Override
	public boolean assess(ElementRepository elementsRepo) {		
		HashMap<String, Integer> newCoordinates = elementsRepo.getCoordinatesInFrontOfRobot();
		HashSet<Element> elements = elementsRepo.getElements(newCoordinates.get("X"),newCoordinates.get("Y"));
		
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			if (element instanceof Wall) {
				return true;
			}
		}
		return false;
	}
	


}