package events;

import domainLayer.*;
import domainLayer.elements.Orientation;

public class RobotAddedEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private Orientation orientation;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param orientation
	 */
	public RobotAddedEvent(int xCoordinate, int yCoordinate, Orientation orientation) {
		// TODO - implement RobotAddedEvent.RobotAddedEvent
		throw new UnsupportedOperationException();
	}

}