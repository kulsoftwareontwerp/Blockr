/**
 * 
 */
package guiLayer.shapes;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import guiLayer.types.Coordinate;
import types.BlockType;
import types.ConnectionType;

/**
 * /** CallFunctionShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class CallFunctionShape extends Shape {
	private final String definitionShapeID;

	/**
	 * Create a new shape with the given id, type and coordinate
	 * 
	 * @param id         the id for the shape
	 * @param definitionShapeID the id of the DefinitionShape associated with this shape
	 * @param type       the type of the shape
	 * @param coordinate the coordinate for the shape.
	 */
	public CallFunctionShape(String id, String definitionShapeID, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
		this.definitionShapeID = definitionShapeID;
	}

	@Override
	public String getDefinitionShapeID() {
		return definitionShapeID;
	}

	@Override
	public void clipOn(Shape shape, ConnectionType connection) {
		switch (connection) {
		case UP:
			setX_coord(shape.getX_coord());
			setY_coord(shape.getY_coord() - getHeight());
			break;
		case DOWN:
			setX_coord(shape.getX_coord());
			setY_coord(shape.getY_coord() + shape.getHeight());
			break;
		case BODY:
			setX_coord(shape.getX_coord() + 10);
			setY_coord(shape.getY_coord() + 30);
			break;
		default:
			; // Do nothing
		}
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		BlockType type = getType();

		g.drawArc(startX + 10, startY - 10, 20, 20, 0, -180);
		g.drawArc(startX + 10, startY + 20, 20, 20, 0, -180);

		g.drawLine(startX, startY, startX, startY + 30);

		g.drawLine(startX + 30, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 30);
		g.drawLine(startX, startY, startX + 10, startY);
		g.drawLine(startX, startY + 30, startX + 10, startY + 30);
		g.drawLine(startX + 30, startY + 30, startX + 90, startY + 30);

		g.drawString(type.toString()+ " " + definitionShapeID + idForDisplay(), startX + 3, startY + 23);

	}

	@Override
	HashSet<Coordinate> fillShapeWithCoordinates() {
		HashSet<Coordinate> set = new HashSet<Coordinate>();
		for (int i = getX_coord(); i < getX_coord() + getWidth(); i++) {
			for (int j = getY_coord(); j < getY_coord() + getHeight(); j++) {
				set.add(new Coordinate(i, j));
			}
		}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Coordinate> connectionMap = new HashMap<ConnectionType, Coordinate>();
		connectionMap.put(ConnectionType.UP, new Coordinate(this.getX_coord() + 20, this.getY_coord()));
		connectionMap.put(ConnectionType.DOWN, new Coordinate(this.getX_coord() + 20, this.getY_coord() + 30));
		this.setCoordinateConnectionMap(connectionMap);
	}

	@Override
	void initDimensions() {
		setHeight(30);
		setWidth(80);
	}

}
