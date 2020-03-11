package events;

import domainLayer.elements.Orientation;

/**
 * The event thrown when the robot has been added to the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class RobotAddedEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private Orientation orientation;
	
	/**
	 * Create the RobotAddedEvent
	 * 
	 * @param 	xCoordinate
	 * 			The xCoordinate
	 * @param 	yCoordinate
	 * 			The yCoordinate
	 * @param 	orientation
	 * 			The orientation
	 */
	public RobotAddedEvent(int xCoordinate, int yCoordinate, Orientation orientation) {
		super();
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.orientation = orientation;
	}
	
	/**
	 * Retrieve the X-coordinate
	 * @return the X-coordinate of the added robot
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}
	
	/**
	 * Retrieve the Y-coordinate
	 * @return the Y-coordinate of the added robot
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}
	
	/**
	 * Retrieve the orientation
	 * @return the orientation of the added robot
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	

}