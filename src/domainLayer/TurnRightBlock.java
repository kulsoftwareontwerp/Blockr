package domainLayer;

public class TurnRightBlock extends ActionBlock {

	/**
	 * 
	 * @param blockId
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