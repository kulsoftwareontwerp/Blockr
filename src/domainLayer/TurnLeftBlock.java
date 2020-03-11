package domainLayer;

public class TurnLeftBlock extends ActionBlock {

	/**
	 * 
	 * @param blockId
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