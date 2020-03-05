package domainLayer;

public class Robot extends Element implements SolidElement {

	private Orientation orientation;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param Orientation
	 */
	public Robot(int xCoordinate, int yCoordinate, int Orientation) {
		super(xCoordinate, yCoordinate);
		// TODO - implement Robot.Robot
		throw new UnsupportedOperationException();
	}

	public int getXCoordinate() {
		// TODO - implement Robot.getXCoordinate
		throw new UnsupportedOperationException();
	}

	public int getYCoordinate() {
		// TODO - implement Robot.getYCoordinate
		throw new UnsupportedOperationException();
	}

	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * 
	 * @param orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * 
	 * @param x
	 */
	public void setXCoordinate(int x) {
		// TODO - implement Robot.setXCoordinate
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param y
	 */
	public void setYCoordinate(int y) {
		// TODO - implement Robot.setYCoordinate
		throw new UnsupportedOperationException();
	}

}