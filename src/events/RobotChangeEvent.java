package events;

import domainLayer.elements.Orientation;

/**
 * The event thrown when the robot has been changed in the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class RobotChangeEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private Orientation orientation;


	
	/**
	 * Create the ChangeEvent
	 * 
	 * @param 	xCoordinate
	 * 			The xCoordinate
	 * @param 	yCoordinate
	 * 			The yCoordinate
	 * @param 	orientation
	 * 			The orientation
	 */
	public RobotChangeEvent(int xCoordinate, int yCoordinate, Orientation orientation) {
		super();
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.orientation = orientation;
	}

	/**
	 * Retrieve the new X-coordinate
	 * @return the new X-coordinate of the robot
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}
	
	/**
	 * Retrieve the Y-coordinate
	 * @return the new Y-coordinate of the  robot
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}
	
	/**
	 * Retrieve the orientation
	 * @return the new orientation  of the robot
	 */
	public Orientation getOrientation() {
		return orientation;
	}

}