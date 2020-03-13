package domainLayer.blocks;

import domainLayer.elements.*;


/**
 * The concrete class for the concept of a TurnLeft block.
 * 
 * @version 0.1
 * @author group17
 */
public class TurnLeftBlock extends ActionBlock {

	/**
	 * Create a TurnLeft Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public TurnLeftBlock(String blockId) {
		super(blockId);
	}
	
	
	@Override
	public void execute(ElementRepository elementsRepo) {
		Robot robot = elementsRepo.getRobot();
		Orientation currentOrientation = robot.getOrientation();
		Orientation newOrientation = currentOrientation.getLeft();
		robot.setOrientation(newOrientation);
	}

}