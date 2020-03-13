package domainLayer.elements;

public class ElementFactory {

	public ElementFactory() {	
	}

	/**
	 * 
	 * @param type
	 * @param xCoordinate
	 * @param yCoordinate
	 * @throws 	IllegalArgumentException
	 * 			thrown when type is null.
	 */
	public Element createElement(ElementType type, int xCoordinate, int yCoordinate) {
		Element element;
		switch (type) {
		case GOAL:
			element=new Goal(xCoordinate, yCoordinate);
			break;
		case ROBOT:
			element=new Robot(xCoordinate, yCoordinate, Orientation.UP);
			break;
		case WALL:
			element=new Wall(xCoordinate, yCoordinate);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + type);
		}
		
		return element;
	}


}