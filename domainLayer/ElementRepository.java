package domainLayer;

import java.util.*;

public class ElementRepository {

	private Collection<Element> elements;
	ElementFactory elementFactory;
	private int gameAreaHeight;
	private int gameAreaWidth;
	private static ElementRepository instance;

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
		// TODO - implement ElementRepository.initializeRobot
		throw new UnsupportedOperationException();
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

	private ElementRepository() {
		// TODO - implement ElementRepository.ElementRepository
		throw new UnsupportedOperationException();
	}

	public static ElementRepository getInstance() {
		return this.instance;
	}

}