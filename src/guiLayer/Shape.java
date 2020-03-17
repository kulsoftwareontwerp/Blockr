package guiLayer;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import types.BlockType;
import types.ConnectionType;

public abstract class Shape implements Constants {
	
	
	private String id;
	private BlockType type;
	private int x_coord;
	private int y_coord;
	
	private int previousX_coord;
	private int previousY_coord;
	private int previousHeight;
	
	private HashSet<Pair<Integer, Integer>> coordinatesShape;
	
	private HashMap<ConnectionType, Pair<Integer, Integer>> coordinateConnectionMap; //Plugs and Sockets

	private ConnectionType connectedVia; //NOCONNECTION if solo, Connection from connectedBlock
	
	private int height = 0;
	private int width = 0;

	public Shape(String id, BlockType type, int x, int y) {
		
		setId(id);
		setType(type);
		setX_coord(x);
		setY_coord(y);
		setConnectedVia(ConnectionType.NOCONNECTION);
		initDimensions(); //setWidth & setHeight
		coordinatesShape = createCoordinatePairs(getX_coord(), getY_coord());
		coordinateConnectionMap= new HashMap<ConnectionType, Pair<Integer,Integer>>();
		updateConnectionTypes(); //setCoordinateConnectionMap, SOCKETS AND PLUGS
	}
	
	//shape = static shape on which it will be clipped
	public abstract void clipOn(Shape shape, ConnectionType connection);
	
	public void addInternal(Shape shape) {}
	public void removeInternal(Shape shape) {}
	
	public abstract void draw(Graphics g); // Each Type of Shape implements its own method
	
	public abstract HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y);
	
	public void determineTotalDimensions() {}
	
	public Integer getStandardHeight() {
		return STANDARD_HEIGHT_BLOCK;
	}
	
	public HashSet<Shape> getInternals() {
		return new HashSet<Shape>();
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
	
	public void setCoordinatesShape(HashSet<Pair<Integer, Integer>> coordinatesShape) {
		this.coordinatesShape = coordinatesShape;
	}
	
	public HashMap<ConnectionType, Pair<Integer, Integer>> getCoordinateConnectionMap() {
		return coordinateConnectionMap;
	}
	
	public void setCoordinateConnectionMap(HashMap<ConnectionType, Pair<Integer, Integer>> coordinateConnectionMap) {
		this.coordinateConnectionMap = coordinateConnectionMap;
	}
	
	public ConnectionType getConnectedVia() {
		return connectedVia;
	}
	public void setConnectedVia(ConnectionType connectedVia) {
		this.connectedVia = connectedVia;
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
	
	public abstract void updateConnectionTypes();
	
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
	  public int hashCode() { return getId().hashCode() + getType().hashCode(); }

	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Shape)) return false;
	    Shape shapeo = (Shape) o;
	    return this.getId().equals(shapeo.getId());
	  }

	public int getPreviousHeight() {
		return previousHeight;
	}

	private void setPreviousHeight(int previousHeight) {
		this.previousHeight = previousHeight;
	}
	
}
