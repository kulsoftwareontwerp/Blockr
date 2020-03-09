package domainLayer.elements;

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
	public HashSet<Element> getElements(int xCoordinate, int yCoordinate) {
		HashSet<Element> resultSet = new HashSet<Element>();
		
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			if(element.getXCoordinate() == xCoordinate && element.getYCoordinate() == yCoordinate) {
				resultSet.add(element);
			}
		}
		
		return resultSet;
	}

	public void initializeRobot() {
		Element robot = factory.createElement(ElementType.ROBOT, 2, 3);
		elements.add(robot);
	}

	// TODO JONATHAN: can we assume there is always a robot to be found or not? 
	public Robot getRobot() {
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()){
			Element nextElem = iterator.next();
			if(nextElem.getClass().equals(Robot.class)) {
				return (Robot) nextElem;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param position
	 */
	public void updateRobotPosition(int xCo, int yCo) {
		Robot robot = getRobot();
		robot.setXCoordinate(xCo);
		robot.setYCoordinate(yCo);
	}

	public void turnRobotLeft() {
		Robot robot = getRobot();
		Orientation currentOrientation = robot.getOrientation();
		Orientation newOrientation = currentOrientation.getLeft();
		robot.setOrientation(newOrientation);
	}

	public void turnRobotRight() {
		Robot robot = getRobot();
		Orientation currentOrientation = robot.getOrientation();
		Orientation newOrientation = currentOrientation.getRight();
		robot.setOrientation(newOrientation);
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


	public void moveRobotForward() {
		Robot robot = getRobot();
		Orientation currentRobotOrientation = robot.getOrientation();
		int newXCo = robot.getXCoordinate();
		int newYCo = robot.getYCoordinate();
		switch(currentRobotOrientation) {
			case UP:
				newYCo -= 1;
			case DOWN:
				newYCo += 1;
			case LEFT:
				newXCo -= 1;
			case RIGHT:
				newXCo += 1;
		}
		HashSet<Element> elements = getElements(newXCo, newYCo);
		
		// Check if the robot stays within the boundries of the Game Area
		if(newXCo < 0 || newXCo > gameAreaWidth-1 || newYCo < 0 || newYCo > gameAreaHeight-1) {
			return;
		}
		
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			// If another solid element is already in the new spot of the robot, the position of the robot stays the same.
			if(element instanceof SolidElement) {
				return;
			}
		}
		
		// If no solid objects are found on the new position, we move the robot into that position.
		updateRobotPosition(newXCo,newYCo);
		
	}
	
	public HashSet<Element> getElements() {
		return (HashSet<Element>) elements;
	}

	// TODO: Possible improvement = reuse more code from moveRobotForward() method
	public boolean wallInFrontOfRobot() {
		Robot robot = getRobot();
		Orientation currentRobotOrientation = robot.getOrientation();
		int newXCo = robot.getXCoordinate();
		int newYCo = robot.getYCoordinate();
		switch(currentRobotOrientation) {
			case UP:
				newYCo -= 1;
			case DOWN:
				newYCo += 1;
			case LEFT:
				newXCo -= 1;
			case RIGHT:
				newXCo += 1;
		}
		HashSet<Element> elements = getElements(newXCo, newYCo);
		
		Iterator<Element> iterator = elements.iterator();
		while(iterator.hasNext()) {
			Element element = iterator.next();
			if (element instanceof Wall) {
				return true;
			}
		}
		
		return false;
	}

}