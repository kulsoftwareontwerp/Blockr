package events;

import domainLayer.elements.ElementType;


/**
 * The event thrown when an element has been added to the domain.
 * 
 * @version 0.1
 * @author group17
 */
public class ElementAddedEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private ElementType type;
	
	/**
	 * Create the ElementAddedEvent
	 * 
	 * @param 	xCoordinate
	 * 			The xCoordinate
	 * @param 	yCoordinate
	 * 			The yCoordinate
	 * @param 	type
	 * 			The ElementType
	 */
	public ElementAddedEvent(int xCoordinate, int yCoordinate, ElementType type) {
		super();
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.type = type;
	}


	/**
	 * Retrieve the X-coordinate
	 * @return the X-coordinate of the added element
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * Retrieve the Y-coordinate
	 * @return the Y-coordinate of the added element
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}

	/**
	 * Retrieve the ElementType
	 * @return the ElementType of the added element
	 */
	public ElementType getType() {
		return type;
	}
	
	
	



}