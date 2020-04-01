package domainLayer.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import domainLayer.elements.Element;
import domainLayer.elements.ElementRepository;
import domainLayer.elements.SolidElement;

/**
 * The concrete class for the concept of a moveForward block.
 * 
 * @version 0.1
 * @author group17
 */
public class MoveForwardBlock extends ActionBlock {

	/**
	 * Create a MoveForward Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public MoveForwardBlock(String blockId) {
		super(blockId);
	}
	
	@Override
	public void execute(ElementRepository elementsRepo) {
		HashMap<String, Integer> coordsInFrontOfRobot = elementsRepo.getCoordinatesInFrontOfRobot();
		int newXCo = coordsInFrontOfRobot.get("X");
		int newYCo = coordsInFrontOfRobot.get("Y");
		HashSet<Element> elements = elementsRepo.getElements(newXCo, newYCo);
		
		// Check if the robot stays within the boundries of the Game Area, else do nothing
		if(newXCo < 0 || newXCo > elementsRepo.getGameAreaWidth()-1 || newYCo < 0 || newYCo > elementsRepo.getGameAreaHeight()-1) {
			return;
		}
		
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			// If another solid element is already in the new spot of the robot, the position of the robot stays the same.
			if(element instanceof SolidElement) {
				return;
			}
		}
		
		// If no solid objects are found on the new position, we move the robot into that position.
		elementsRepo.updateRobotPosition(newXCo,newYCo);		
	}

}