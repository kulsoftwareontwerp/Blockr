package guiLayer.shapes;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.DebugModus;
import guiLayer.types.Pair;
import types.BlockType;
import types.ConnectionType;

public abstract class Shape implements Constants, Cloneable {

	private String id;
	private BlockType type;
	private int x_coord;
	private int y_coord;

	private int previousX_coord;
	private int previousY_coord;
	private int previousHeight;

	private Boolean hasToBeRemovedOnUndo;

	private HashSet<Pair<Integer, Integer>> coordinatesShape;

	private HashMap<ConnectionType, Pair<Integer, Integer>> coordinateConnectionMap; // Plugs and Sockets
	private HashMap<ConnectionType, Boolean> connectionStatus; // Is a connection available to add something to it or
																// not.

	private HashMap<ConnectionType, Boolean> tempConnectionStatus;

	private ConnectionType connectedVia; // NOCONNECTION if solo, Connection from connectedBlock
	private ConnectionType tempConnectedVia;

	private ConnectionType previouslyConnectedVia;

	public ConnectionType getPreviouslyConnectedVia() {
		return previouslyConnectedVia;
	}

	private void setPreviouslyConnectedVia(ConnectionType previouslyConnectedVia) {
		this.previouslyConnectedVia = previouslyConnectedVia;
	}

	private int height = 0;
	private int width = 0;

	public Shape(String id, BlockType type, int x, int y) {
		hasToBeRemovedOnUndo = false;

		setId(id);
		setType(type);
		setX_coord(x);
		setY_coord(y);

		setPreviousX_coord(INVALID_COORDINATE);
		setPreviousY_coord(INVALID_COORDINATE);

		// note: order here is important, don't change if you don't know what you're
		// doing.
		setConnectedVia(ConnectionType.NOCONNECTION, true);
		setPreviouslyConnectedVia(ConnectionType.NOCONNECTION);

		initDimensions(); // setWidth & setHeight
		coordinatesShape = createCoordinatePairs();
		coordinateConnectionMap = new HashMap<ConnectionType, Pair<Integer, Integer>>();
		connectionStatus = new HashMap<ConnectionType, Boolean>();
		defineConnectionTypes(); // setCoordinateConnectionMap, SOCKETS AND PLUGS
	}

	// shape = static shape on which it will be clipped
	public abstract void clipOn(Shape shape, ConnectionType connection);

	public void addInternal(Shape shape) {
	}

	public void removeInternal(Shape shape) {
	}

	public abstract void draw(Graphics g); // Each Type of Shape implements its own method

	protected abstract HashSet<Pair<Integer, Integer>> createCoordinatePairs();

	public void determineTotalDimensions() {
	}

	public Integer getStandardHeight() {
		return STANDARD_HEIGHT_BLOCK;
	}

	public HashSet<Shape> getInternals() {
		return new HashSet<Shape>();
	}

	public synchronized Boolean getHasToBeRemovedOnUndo() {
		return hasToBeRemovedOnUndo;
	}

	public synchronized void setHasToBeRemovedOnUndo(Boolean hasToBeRemovedOnUndo) {
		this.hasToBeRemovedOnUndo = hasToBeRemovedOnUndo;
	}
/**
 * Check if the connection is open, takes the temporary switches into account.
 * @param connection the connection to check the connection status. of.
 * @return if the connection is open
 */
	public Boolean checkIfOpen(ConnectionType connection) {
		if (tempConnectionStatus == null) {
			if (connectionStatus.get(connection) == null) {
				return false;
			} else {
				return connectionStatus.get(connection);
			}
		} else {
			if (tempConnectionStatus.get(connection) == null) {
				return false;
			} else {
				return tempConnectionStatus.get(connection);
			}
		}
	}
/**
 * Switch the cavity status of the given connection. If the switch is not persisted all changes made by the switch will be thrown away on the next persistent switch.
 * @param connection The connection to switch
 * @param persist Does the switch need to be persisted.
 */
	public void switchCavityStatus(ConnectionType connection, Boolean persist) {
		if (persist) {

			if (connectionStatus.containsKey(connection)) {
				connectionStatus.put(connection, !connectionStatus.get(connection));
			} else {
				connectionStatus.put(connection, true);
			}

		} else {
			if (tempConnectionStatus == null) {
				tempConnectionStatus = new HashMap<ConnectionType, Boolean>();
				tempConnectionStatus.putAll(connectionStatus);
			}
			if (tempConnectionStatus.containsKey(connection)) {
				tempConnectionStatus.put(connection, !tempConnectionStatus.get(connection));
			} else {
				tempConnectionStatus.put(connection, true);
			}
		}
	}
	
	/**
	 * Persist or revert the temporary CavityStatus If no temporary CavityStatus is
	 * assigned this method will do nothing.
	 * 
	 * @param persist True if the temporary CavityStatus needs to be saved. False if
	 *                the temporary CavityStatus needs to be discarded.
	 */
	public void persistCavityStatus(Boolean persist) {
		if (persist && tempConnectionStatus != null) {
			connectionStatus=tempConnectionStatus;
		} else {
			tempConnectionStatus = null;
		}
	}

	public void restoreCavityStatus(Shape shape) {
		if (shape != null) {
			this.connectionStatus = new HashMap<ConnectionType, Boolean>(shape.connectionStatus);
		}
	}

	public HashSet<Pair<Integer, Integer>> getTriggerSet(ConnectionType connection) {
		HashSet<Pair<Integer, Integer>> triggerSet = new HashSet<Pair<Integer, Integer>>();

		if (getCoordinateConnectionMap().keySet().contains(connection) && checkIfOpen(connection)) {
			int x_current = getCoordinateConnectionMap().get(connection).getLeft();
			int y_current = getCoordinateConnectionMap().get(connection).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					triggerSet.add(new Pair<Integer, Integer>(i, j));
				}
			}
		}

		return triggerSet;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BlockType getType() {
		return type;
	}

	public void setType(BlockType type) {
		this.type = type;
	}

	public int getX_coord() {
		return x_coord;
	}

	public void setX_coord(int x_coord) {
		this.x_coord = x_coord;
	}

	public int getY_coord() {
		return y_coord;
	}

	public void setY_coord(int y_coord) {
		this.y_coord = y_coord;
	}

	public HashSet<Pair<Integer, Integer>> getCoordinatesShape() {
		return coordinatesShape;
	}

	public void setCoordinatesShape() {
		this.coordinatesShape = createCoordinatePairs();
	}

	public HashMap<ConnectionType, Pair<Integer, Integer>> getCoordinateConnectionMap() {
		return coordinateConnectionMap;
	}

	public void setCoordinateConnectionMap(HashMap<ConnectionType, Pair<Integer, Integer>> coordinateConnectionMap) {
		this.coordinateConnectionMap = coordinateConnectionMap;
	}

	/**
	 * Retrieve the connectedVia of this shape If a temporary connectedvia was set
	 * this will be returned
	 * 
	 * @return connectedvia or temporary connectedvia if it has been set.
	 */
	public ConnectionType getConnectedVia() {
		if (tempConnectedVia != null) {
			return tempConnectedVia;
		} else {
			return connectedVia;
		}
	}

	public void setConnectedVia(ConnectionType connectedVia, Boolean persist) {
		if (persist) {
			tempConnectedVia = null;
			setPreviouslyConnectedVia(this.getConnectedVia());
			this.connectedVia = connectedVia;
		} else {
			tempConnectedVia = connectedVia;
		}
	}

	/**
	 * Persist or revert the temporary connectedvia If no temporary connectedvia is
	 * assigned this method will do nothing.
	 * 
	 * @param persist True if the temporary connectedVia needs to be saved. False if
	 *                the temporary connectedVia needs to be discarded.
	 */
	public void persistConnectedVia(Boolean persist) {
		if (persist && tempConnectedVia != null) {
			setConnectedVia(tempConnectedVia, true);
		} else {
			tempConnectedVia = null;
		}
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		setPreviousHeight(getHeight());
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public abstract void defineConnectionTypes();

	public abstract void initDimensions();

	public int getPreviousX_coord() {
		return previousX_coord;
	}

	public void setPreviousX_coord(int previousX_coord) {
		this.previousX_coord = previousX_coord;
	}

	public int getPreviousY_coord() {
		return previousY_coord;
	}

	public void setPreviousY_coord(int previousY_coord) {
		this.previousY_coord = previousY_coord;
	}

	@Override
	public int hashCode() {
		return getId().hashCode() + getType().hashCode();
	}

	@Override
	public Shape clone() {
		Shape s = null;
		try {
			s = (Shape) super.clone();
			s.connectionStatus = new HashMap<ConnectionType, Boolean>(this.connectionStatus);
			s.coordinateConnectionMap = new HashMap<ConnectionType, Pair<Integer, Integer>>(
					this.coordinateConnectionMap);
			s.coordinatesShape = new HashSet<Pair<Integer, Integer>>(this.coordinatesShape);
		} catch (CloneNotSupportedException e) {
			new RuntimeException(e);
		}
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Shape))
			return false;
		Shape shapeo = (Shape) o;
		return this.getId().equals(shapeo.getId()) && getType().equals(shapeo.getType());
	}

	public int getPreviousHeight() {
		return previousHeight;
	}

	private void setPreviousHeight(int previousHeight) {
		this.previousHeight = previousHeight;
	}

	protected String idForDisplay() {
		if (DebugModus.IDS.compareTo(CanvasWindow.debugModus) <= 0) {
			return " " + getId();
		} else {
			return "";
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Shape [id=");
		builder.append(id);
		builder.append(", x_coord=");
		builder.append(x_coord);
		builder.append(", y_coord=");
		builder.append(y_coord);
		builder.append(", hasToBeRemovedOnUndo=");
		builder.append(hasToBeRemovedOnUndo);
		builder.append("]");
		return builder.toString();
	}

}
