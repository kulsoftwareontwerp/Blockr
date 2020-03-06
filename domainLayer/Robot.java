package domainLayer;

public class Robot extends Element implements SolidElement {

	private Orientation orientation;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param orientation
	 */
	public Robot(int xCoordinate, int yCoordinate, Orientation orientation) {
		super(xCoordinate, yCoordinate);
		this.orientation=orientation;
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
	public void setXCoordinate(int xCoordinate) {
		super.setxCoordinate(xCoordinate);
	}

	/**
	 * 
	 * @param y
	 */
	public void setYCoordinate(int yCoordinate) {
		super.setyCoordinate(yCoordinate);
	}

}