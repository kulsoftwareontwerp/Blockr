package domainLayer;

import java.util.*;

public class ElementRepository {

	private Collection<Element> elements;
	private ElementFactory factory;
	private final int gameAreaHeight=4;
	private final int gameAreaWidth=5;
	private static ElementRepository instance;

	private ElementRepository() {
		factory=new ElementFactory();
		elements=new HashSet<Element>();
	}

	
	public void removeRobot() {
		// TODO - implement ElementRepository.removeRobot
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 */
	public void getElements(int xCoordinate, int yCoordinate) {
		// TODO - implement ElementRepository.getElements
		throw new UnsupportedOperationException();
	}

	public void initializeRobot() {
		Element robot = factory.createElement(ElementType.ROBOT, 2, 3);
		elements.add(robot);
	}

	public Robot getRobot() {
		// TODO - implement ElementRepository.getRobot
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param position
	 */
	public void updateRobotPosition(HashMap<String, Integer> position) {
		// TODO - implement ElementRepository.updateRobotPosition
		throw new UnsupportedOperationException();
	}

	public void turnRobotLeft() {
		// TODO - implement ElementRepository.turnRobotLeft
		throw new UnsupportedOperationException();
	}

	public void turnRobotRight() {
		// TODO - implement ElementRepository.turnRobotRight
		throw new UnsupportedOperationException();
	}


	public static ElementRepository getInstance() {
		if(instance==null) {
			instance = new ElementRepository();
		}
		return instance;
	}


	public void addElement(ElementType type, int xCoordinate, int yCoordinate) {
		Element el = factory.createElement(type, xCoordinate, yCoordinate);
		elements.add(el);
	}

}