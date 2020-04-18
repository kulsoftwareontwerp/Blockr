package guiLayer.shapes;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockType;
import types.ConnectionType;

public abstract class Shape implements Constants, Cloneable {

	private String id;
	private BlockType type;
	private Coordinate coordinate;

	private Coordinate previousCoordinate;
	private int previousHeight;

	private HashSet<Coordinate> coordinatesShape;

	private HashMap<ConnectionType, Coordinate> coordinateConnectionMap; // Plugs and Sockets

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

	public Shape(String id, BlockType type, Coordinate coordinate) {
		setId(id);
		setType(type);
		setCoordinate(coordinate);

		setPreviousX_coord(INVALID_COORDINATE);
		setPreviousY_coord(INVALID_COORDINATE);

		// note: order here is important, don't change if you don't know what you're
		// doing.
		setConnectedVia(ConnectionType.NOCONNECTION, true);
		setPreviouslyConnectedVia(ConnectionType.NOCONNECTION);

		initDimensions(); // setWidth & setHeight
		coordinatesShape = fillShapeWithCoordinates();
		coordinateConnectionMap = new HashMap<ConnectionType,Coordinate>();
		defineConnectionTypes(); // setCoordinateConnectionMap, SOCKETS AND PLUGS
	}

	// shape = static shape on which it will be clipped
	public abstract void clipOn(Shape shape, ConnectionType connection);



	public abstract void draw(Graphics g); // Each Type of Shape implements its own method

	protected abstract HashSet<Coordinate> fillShapeWithCoordinates();


	public void determineTotalHeight(Set<Shape> internals) {
		
	}

	public Integer getStandardHeight() {
		return STANDARD_HEIGHT_BLOCK;
	}


	public HashSet<Coordinate> getTriggerSet(ConnectionType connection) {
		HashSet<Coordinate> triggerSet = new HashSet<Coordinate>();

		if (getCoordinateConnectionMap().keySet().contains(connection)) {
			int x_current = getCoordinateConnectionMap().get(connection).getX();
			int y_current = getCoordinateConnectionMap().get(connection).getY();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					triggerSet.add(new Coordinate(i, j));
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
		if (coordinate == null) {
			return 0;
		}
		return coordinate.getX();
	}

	public void setX_coord(int x) {
		if (coordinate == null) {
			this.coordinate = new Coordinate(x, 0);
		} else {
			this.coordinate = this.coordinate.setX(x);
		}
	}

	public int getY_coord() {
		if (coordinate == null) {
			return 0;
		}
		return coordinate.getY();
	}

	public void setY_coord(int y) {
		if (coordinate == null) {
			this.coordinate = new Coordinate(0, y);
		} else {
			this.coordinate = this.coordinate.setY(y);
		}
	}
	
	public void setCoordinate(Coordinate coordinate) {
		if(coordinate == null) {
			this.coordinate=new Coordinate(0, 0);
		}
		else {
			this.coordinate=coordinate;
		}
	}

	public HashSet<Coordinate> getCoordinatesShape() {
		return coordinatesShape;
	}

	public void setCoordinatesShape() {
		this.coordinatesShape = fillShapeWithCoordinates();
	}

	public HashMap<ConnectionType, Coordinate> getCoordinateConnectionMap() {
		return coordinateConnectionMap;
	}

	public void setCoordinateConnectionMap(HashMap<ConnectionType, Coordinate> coordinateConnectionMap) {
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
		if (previousCoordinate == null) {
			return 0;
		}
		return previousCoordinate.getX();
	}

	public void setPreviousX_coord(int x) {
		if (previousCoordinate == null) {
			this.previousCoordinate = new Coordinate(x, 0);
		} else {
			this.previousCoordinate = this.previousCoordinate.setX(x);
		}
	}

	public int getPreviousY_coord() {
		if (previousCoordinate == null) {
			return 0;
		}
		return previousCoordinate.getY();
	}

	public void setPreviousY_coord(int y) {
		if (previousCoordinate == null) {
			this.previousCoordinate = new Coordinate(0,y);
		} else {
			this.previousCoordinate = this.previousCoordinate.setY(y);
		}
	}
	
	public void setPreviousCoordinate(Coordinate coordinate) {
		if(coordinate == null) {
			this.previousCoordinate=new Coordinate(0, 0);
		}
		else {
			this.previousCoordinate=coordinate;
		}
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
			s.coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>(
					this.coordinateConnectionMap);
			s.coordinatesShape = new HashSet<Coordinate>(this.coordinatesShape);
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
		builder.append(", coord=");
		builder.append(coordinate);
		builder.append("]");
		return builder.toString();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

}
