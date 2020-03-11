package applicationLayer;

import domainLayer.*;

public class RobotChangeEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private Orientation orientation;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param orientation
	 */
	public RobotChangeEvent(int xCoordinate, int yCoordinate, Orientation orientation) {

	}

}