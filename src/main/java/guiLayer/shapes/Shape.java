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

/**
 * Shape
 * 
 * @version 0.1
 * @author group17
 *
 */
public abstract class Shape implements Constants, Cloneable {

	private String id;
	private BlockType type;
	private Coordinate coordinate;

	private Coordinate previousCoordinate;
	private HashSet<Coordinate> coordinatesShape;

	private HashMap<ConnectionType, Coordinate> coordinateConnectionMap; // Plugs and Sockets

	private ConnectionType connectedVia; // NOCONNECTION if solo, Connection from connectedBlock
	private ConnectionType tempConnectedVia;

	private ConnectionType previouslyConnectedVia;

	private int height = 0;

	private int previousHeight;
	private int width = 0;

	private boolean cloneSupported;

	/**
	 * Create a new shape with the given id, type and coordinate
	 * 
	 * @param id         the id for the shape
	 * @param type       the type of the shape
	 * @param coordinate the coordinate for the shape.
	 */
	public Shape(String id, BlockType type, Coordinate coordinate) {
		cloneSupported = true;
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
		coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>();
		defineConnectionTypes(); // setCoordinateConnectionMap, SOCKETS AND PLUGS
	}

	/**
	 * Retrieve the ID of the DefinitionShape associated with this shape. Returns
	 * Null when there is no associated DefinitionShape.
	 * 
	 * @return the ID of the DefinitionShape associated with this shape. Returns
	 *         Null when there is no associated DefinitionShape.
	 */
	public String getDefinitionShapeID() {
		return null;
	}

	/**
	 * Retrieve the id for this shape
	 * 
	 * @return the id for this shape
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of this shape
	 * 
	 * @param id the id to set this shape to
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieve the type of this shape
	 * 
	 * @return the BlockType associated with this shape
	 */
	public BlockType getType() {
		return type;
	}

	/**
	 * Set the BlockType of this shape
	 * 
	 * @param type the blockType for this shape
	 * @throws NullPointerException when type is null
	 */
	public void setType(BlockType type) {
		if (type == null) {
			throw new NullPointerException("there must be a type set for the shape");
		}
		this.type = type;
	}

	/**
	 * Retrieve the coordinate of this shape
	 * 
	 * @return the coordinate of this shape.
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}

	/**
	 * Set the coordinate for this shape
	 * 
	 * @param coordinate the coordinate for this shape, if Null is given a
	 *                   coordinate with x=0 and y=0 will be set.
	 */
	public void setCoordinate(Coordinate coordinate) {
		if (coordinate == null) {
			this.coordinate = new Coordinate(0, 0);
		} else {
			this.coordinate = coordinate;
		}
	}

	/**
	 * Retrieve the x coordinate of this shape
	 * 
	 * @return the x coordinate of this shape
	 */
	public int getX_coord() {
		if (coordinate == null) {
			return 0;
		}
		return coordinate.getX();
	}

	/**
	 * Set the x coordinate for this shape
	 * 
	 * @param x set the x part of the coordinate for this shape, if there is no
	 *          coordinate for this shape is given a coordinate with x=the given x
	 *          and y=0 will be set.
	 */
	public void setX_coord(int x) {
		if (coordinate == null) {
			this.coordinate = new Coordinate(x, 0);
		} else {
			this.coordinate = this.coordinate.setX(x);
		}
	}

	/**
	 * Retrieve the y coordinate of this shape
	 * 
	 * @return the y coordinate for this shape
	 */
	public int getY_coord() {
		if (coordinate == null) {
			return 0;
		}
		return coordinate.getY();
	}

	/**
	 * Set the y coordinate for this shape
	 * 
	 * @param y set the y part of the coordinate for this shape, if there is no
	 *          coordinate for this shape is given a coordinate with x=0 and y= the
	 *          given y will be set.
	 */
	public void setY_coord(int y) {
		if (coordinate == null) {
			this.coordinate = new Coordinate(0, y);
		} else {
			this.coordinate = this.coordinate.setY(y);
		}
	}

	/**
	 * Retrieve the previous x coordinate.
	 * 
	 * @return the previous x coordinate
	 */
	public int getPreviousX_coord() {
		if (previousCoordinate == null) {
			return 0;
		}
		return previousCoordinate.getX();
	}

	/**
	 * Set the previous x coordinate
	 * 
	 * @param x the previous x coordinate
	 */
	public void setPreviousX_coord(int x) {
		if (previousCoordinate == null) {
			this.previousCoordinate = new Coordinate(x, 0);
		} else {
			this.previousCoordinate = this.previousCoordinate.setX(x);
		}
	}

	/**
	 * Retrieve the previous y coordinate
	 * 
	 * @return the previous y coordinate
	 */
	public int getPreviousY_coord() {
		if (previousCoordinate == null) {
			return 0;
		}
		return previousCoordinate.getY();
	}

	/**
	 * Set the previous Y coordinate
	 * 
	 * @param y the previous y coordinate
	 */
	public void setPreviousY_coord(int y) {
		if (previousCoordinate == null) {
			this.previousCoordinate = new Coordinate(0, y);
		} else {
			this.previousCoordinate = this.previousCoordinate.setY(y);
		}
	}

	/**
	 * Retrieve all the coordinates within this shape, execute setCoordinatesShape
	 * first to retrieve the latest coordinates of this shape
	 * 
	 * @return all coordinates within this shape
	 */
	public HashSet<Coordinate> getCoordinatesShape() {
		return coordinatesShape;
	}

	/**
	 * Set the coordinates of this shape, all coordinates contained within this
	 * shape will be set and will be accessible trough getCoordinatesShape.
	 */
	public void setCoordinatesShape() {
		this.coordinatesShape = fillShapeWithCoordinates();
	}

	/**
	 * Retrieve the coordinateConnectionMap of this shape
	 * 
	 * @return the coordinateConnectionMap of this shape
	 */
	public HashMap<ConnectionType, Coordinate> getCoordinateConnectionMap() {
		return coordinateConnectionMap;
	}

	/**
	 * Set the coordinateConnectionMap of this shape
	 * 
	 * @param coordinateConnectionMap the coordinateConnectionMap of this shape
	 */
	protected void setCoordinateConnectionMap(HashMap<ConnectionType, Coordinate> coordinateConnectionMap) {
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

	/**
	 * Set the connectedVia of this shape
	 * 
	 * @param connectedVia The ConnectionType to set the connectedvia of this shape
	 *                     to.
	 * @param persist      a flag indicating if the currently set connectedVia must
	 *                     be preserved or can be forgotten after a revert.
	 */
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

	/**
	 * Retrieve the previously connectedVia ConnectionType of this shape
	 * 
	 * @return the previously connectedVia connectionType of this shape
	 */
	public ConnectionType getPreviouslyConnectedVia() {
		return previouslyConnectedVia;
	}

	private void setPreviouslyConnectedVia(ConnectionType previouslyConnectedVia) {
		this.previouslyConnectedVia = previouslyConnectedVia;
	}

	/**
	 * Retrieve the standard height of a shape
	 * 
	 * @return The standard height of a shape.
	 */
	protected Integer getStandardHeight() {
		return STANDARD_HEIGHT_BLOCK;
	}

	/**
	 * Retrieve the height of this shape
	 * 
	 * @return the height of this shape
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Retrieve the height difference between the previous height and the current
	 * height.
	 * 
	 * @return the height difference between the previous height and the current
	 *         height.
	 */
	public int getHeightDiff() {
		return getHeight() - getPreviousHeight();
	}

	/**
	 * Set the height of this shape, updates the previous height with the previous
	 * height.
	 * 
	 * @param height the height to set this shape to.
	 */
	public void setHeight(int height) {
		setPreviousHeight(getHeight());
		this.height = height;
	}

	/**
	 * Retrieve the previous height of this shape
	 * 
	 * @return the previous height of this shape
	 */
	public int getPreviousHeight() {
		return previousHeight;
	}

	private void setPreviousHeight(int previousHeight) {
		this.previousHeight = previousHeight;
	}

	/**
	 * Retrieve the width of a shape
	 * 
	 * @return the width of a shape
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set the width of a shape
	 * 
	 * @param width the width to set to the shape
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Retrieve the triggerSet for this shape at the given connection
	 * 
	 * @param connection the connection to get the triggerSet of
	 * @return a Set of Coordinates containing all the coordinates associated with
	 *         the given connection's Trigger
	 */
	public Set<Coordinate> getTriggerSet(ConnectionType connection) {
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

	/**
	 * Clip this shape on the given connection of the given shape
	 * 
	 * @param shape      the shape to clip on
	 * @param connection the connection to clip on
	 */
	public abstract void clipOn(Shape shape, ConnectionType connection);

	/**
	 * Draw this shape
	 * 
	 * @param g The graphics object to draw on.
	 */
	public abstract void draw(Graphics g); // Each Type of Shape implements its own method

	/**
	 * Fill the shape with it's coordinates, retrieve the coordinates of its filling
	 * 
	 * @return the coordinates filling a shape.
	 */
	abstract HashSet<Coordinate> fillShapeWithCoordinates();

	/**
	 * Determine the height of the shape
	 * 
	 * @param internals all blocks contained in this block.
	 */
	public void determineTotalHeight(Set<Shape> internals) {
		defineConnectionTypes();
		setCoordinatesShape();
	}

	/**
	 * Define the connections, and their Coordinates.
	 */
	public abstract void defineConnectionTypes();

	abstract void initDimensions();

	@Override
	public int hashCode() {
		return getId().hashCode() + getType().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Shape))
			return false;
		Shape shapeo = (Shape) o;
		return this.getId().equals(shapeo.getId()) && getType().equals(shapeo.getType());
	}

	@Override
	public Shape clone() {
		Shape s = null;
		try {

			if (cloneSupported) {
				s = (Shape) super.clone();
				s.coordinateConnectionMap = new HashMap<ConnectionType, Coordinate>(this.coordinateConnectionMap);
				s.coordinatesShape = new HashSet<Coordinate>(this.coordinatesShape);
			} else {
				throw new CloneNotSupportedException();
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return s;
	}

	/**
	 * Retrieve the ids to show on screen, keeps the debugmodus of the canvasWindow
	 * in mind.
	 * 
	 * @return The string to display as an id.
	 */
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

}
