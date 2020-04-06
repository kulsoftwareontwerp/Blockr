package domainLayer.blocks;


import domainLayer.elements.ElementRepository;
import domainLayer.elements.Orientation;
import domainLayer.elements.Robot;

/**
 * The abstract class for the concept of a turn right block.
 * 
 * @version 0.1
 * @author group17
 */
public class TurnRightBlock extends ActionBlock {

	/**
	 * Create a TurnRight Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public TurnRightBlock(String blockId) {
		super(blockId);
	}
	
	@Override
	public void execute(ElementRepository elementsRepo) {
		Robot robot = elementsRepo.getRobot();
		Orientation currentOrientation = robot.getOrientation();
		Orientation newOrientation = currentOrientation.getRight();
		robot.setOrientation(newOrientation);
	}

}