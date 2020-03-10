package events;

import domainLayer.elements.ElementType;

public class ElementAddedEvent implements EventObject {

	private int xCoordinate;
	private int yCoordinate;
	private ElementType type;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 * @param type
	 */
	public ElementAddedEvent(int xCoordinate, int yCoordinate, ElementType type) {
		// TODO - implement ElementAddedEvent.ElementAddedEvent
		throw new UnsupportedOperationException();
	}

}