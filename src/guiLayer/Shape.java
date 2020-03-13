package guiLayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import types.BlockType;
import types.ConnectionType;

public class Shape {
	
	private String id;
	private BlockType type;
	private int x_coord;
	private int y_coord;
	private HashSet<Pair<Integer, Integer>> coordinatesShape;
	private HashMap<ConnectionType, Pair<Integer, Integer>> coordinateConnectionMap; //Plugs and Sockets
	private ConnectionType connectedVia; //NOCONNECTION if solo 
	private int height;
	private int width;
	private HashSet<Shape> internals;

	public HashSet<Shape> getInternals() {
		return internals;
	}

	public void setInternals(HashSet<Shape> internals) {
		this.internals = internals;
	}

	public Shape(String id, BlockType type, int x, int y) {
		setId(id);
		setType(type);
		setX_coord(x);
		setY_coord(y);
		setConnectedVia(ConnectionType.NOCONNECTION);
		coordinatesShape = new HashSet<Pair<Integer,Integer>>();
		coordinateConnectionMap= new HashMap<ConnectionType, Pair<Integer,Integer>>();
		initDimensions();
		updateConnectionTypesToShapeBasedOnType();
		setCoordinatesShape(createCoordinatePairs(getX_coord(), getY_coord()));
		internals = new HashSet<Shape>();
		
	}
	
	public HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y) {
		BlockType type = getType();
		HashSet<Pair<Integer, Integer>> set = new HashSet<Pair<Integer, Integer>>();
		switch (type) {
		case MoveForward: // 30px down, 80px left, 80 px right, 30px up
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;
		case TurnLeft:
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;
		case TurnRight:
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;
		case If: // 90px x 90px - (10,30)-(40,90)
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					if (!(j > 25+y && j <= y+getHeight()-25 && i > x && i <= x+getWidth())) // to give room for the clip, otherwise it won't wrok
						set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;
		case While:
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					if (!(j > 25+y && j <= y+getHeight()-25 && i > x && i <= x+getWidth())) // to give room for the clip, otherwise it won't wrok
						set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;

		case Not:
			for (int i = x+10; i < x + getWidth()+10; i++) {
				for (int j = y; j < y + getHeight(); j++) {
						set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;

		case WallInFront:
			for (int i = x + 10; i < x + getWidth()+10; i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}
			break;
		default:
			;
		} // Nothing has to happen

		return set;
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
		this.height = height;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}
	
	public void updateConnectionTypesToShapeBasedOnType() {
		BlockType type = this.getType();
		HashMap<ConnectionType, Pair<Integer, Integer>> connectionMap = new HashMap<ConnectionType, Pair<Integer,Integer>>();
		switch (type) {
		case MoveForward:
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()-10));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()+20));
			break;
		case TurnLeft:
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()-10));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()+20));
			
			break;
		case TurnRight:
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()-10));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()+20));
			break;

		case If:
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()-10));
			connectionMap.put(ConnectionType.CONDITION, new Pair<Integer, Integer>(this.getX_coord()+(getWidth()-10), this.getY_coord()+5));
			connectionMap.put(ConnectionType.BODY, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()+20));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()+(getHeight()-10)));
			break;
		case While:
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()-10));
			connectionMap.put(ConnectionType.CONDITION, new Pair<Integer, Integer>(this.getX_coord()+(getWidth()-10), this.getY_coord()+5));
			connectionMap.put(ConnectionType.BODY, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()+20));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+40, this.getY_coord()+(getHeight()-10)));
			break;

		case Not:
			connectionMap.put(ConnectionType.LEFT, new Pair<Integer, Integer>(this.getX_coord(), this.getY_coord()+5));
			connectionMap.put(ConnectionType.OPERAND, new Pair<Integer, Integer>(this.getX_coord()+80, this.getY_coord()+5));
			break;

		case WallInFront:
			connectionMap.put(ConnectionType.LEFT, new Pair<Integer, Integer>(this.getX_coord(), this.getY_coord()+5));
			break;
		default:
			;
		} // Nothing has to happen
		
		this.setCoordinateConnectionMap(connectionMap);
	}
	
	private void initDimensions() {
		switch (getType()) {
		case MoveForward:
			setHeight(30);
			setWidth(80);
			break;
		case TurnLeft:
			setHeight(30);
			setWidth(80);
			break;
		case TurnRight:
			setHeight(30);
			setWidth(80);
			break;
		case If:
			setHeight(90);
			setWidth(90);
			break;
		case While:
			setHeight(90);
			setWidth(90);
			break;
		case Not:
			setHeight(30);
			setWidth(80);
			break;
		case WallInFront:
			setHeight(30);
			setWidth(80);
			break;
		default:
			;
		} // Nothing has to happen
	}
	
	public void determineTotalHeight(HashSet<Shape> internals) {
		int total = 90;
		int total_y = 90;
		BlockType blockType;
		for (Shape shape : internals) {
			blockType = shape.getType();
			if(blockType == BlockType.MoveForward || blockType == BlockType.TurnLeft || blockType == BlockType.TurnRight)
				total += shape.getHeight();
			if(blockType == BlockType.If || blockType == BlockType.While) {
				if(internals != null && !(internals.isEmpty())) {
					determineTotalHeight(shape.internals);
					total += shape.getHeight();
					total_y += 10;
				}
				
			}
		}
		setHeight(total);
		setWidth(total_y);
		updateConnectionTypesToShapeBasedOnType();
		setCoordinatesShape(createCoordinatePairs(getX_coord(), getY_coord()));
	}
	
	 @Override
	  public int hashCode() { return getId().hashCode() + getType().hashCode(); }

	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Shape)) return false;
	    Shape shapeo = (Shape) o;
	    return this.getId().equals(shapeo.getId());
	  }
	
}
