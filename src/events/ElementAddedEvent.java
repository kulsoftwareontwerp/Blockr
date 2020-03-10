package events;

import domainLayer.elements.ElementType;

public class ElementAddedEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private ElementType type;

	/**
	 * 
	 * @param 	xCoordinate
	 * 			The xCoordinate of the element that's been added.
	 * @param 	yCoordinate
	 * 			The yCoordinate of the element that's been added.
	 * @param 	type
	 * 			The type of element that's been added.
	 */
	public ElementAddedEvent(int xCoordinate, int yCoordinate, ElementType type) {
		// TODO - implement ElementAddedEvent.ElementAddedEvent
		throw new UnsupportedOperationException();
	}

}