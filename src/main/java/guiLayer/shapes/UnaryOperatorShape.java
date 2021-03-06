package guiLayer.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import guiLayer.types.Coordinate;
import types.BlockType;
import types.ConnectionType;

/**
 * UnaryOperatorShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class UnaryOperatorShape extends Shape {

	/**
	 * Create a new shape with the given id, type and coordinate
	 * 
	 * @param id         the id for the shape
	 * @param type       the type of the shape
	 * @param coordinate the coordinate for the shape.
	 */
	public UnaryOperatorShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		

		
		Color c = g.getColor();
		
		if(c.equals(Color.BLACK)) {
			g.setColor(Color.decode("#E6517E"));				
		}
		
		g.fillPolygon(
				new int[] {  startX + 10, startX + 90, startX + 90, startX + 85, startX + 82, startX + 80, startX + 82,
						startX + 85, startX + 90, startX + 90, startX+10 },
				new int[] {  startY,  startY,startY + 5, startY + 7, startY + 10, startY + 15, startY + 20,
						startY + 23, startY + 25,startY + 30, startY + 30},
				11);
		g.fillArc(startX, startY + 5, 20, 20, -90, -180);
		g.setColor(Color.BLACK);
		
		

		g.drawArc(startX + 80, startY + 5, 20, 20, -90, -180);
		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 5);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 90, startY + 25, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		g.drawString(getType().toString() + idForDisplay(), startX + 35, startY + 19);
		g.setFont(f);

	}

	@Override
	HashSet<Coordinate> fillShapeWithCoordinates() {
		HashSet<Coordinate> set = new HashSet<Coordinate>();

		for (int i = getX_coord() + 10; i < getX_coord() + getWidth() + 10; i++) {
			for (int j = getY_coord(); j < getY_coord() + getHeight(); j++) {
				set.add(new Coordinate(i, j));
			}
		}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Coordinate> connectionMap = new HashMap<ConnectionType, Coordinate>();
		connectionMap.put(ConnectionType.LEFT, new Coordinate(this.getX_coord() + 10, this.getY_coord() + 15));
		connectionMap.put(ConnectionType.OPERAND, new Coordinate(this.getX_coord() + 90, this.getY_coord() + 15));
		this.setCoordinateConnectionMap(connectionMap);
	}

	@Override
	void initDimensions() {
		setHeight(30);
		setWidth(80);

	}

	@Override
	public void clipOn(Shape shapeToClipTo, ConnectionType connection) {
		switch (connection) {
		case LEFT:
			setX_coord(shapeToClipTo.getX_coord() - 80);
			setY_coord(shapeToClipTo.getY_coord());
			// drawShape(g, shapeToClip);
			break;
		case OPERAND:
		case CONDITION:
			setX_coord(shapeToClipTo.getX_coord() + 80);
			setY_coord(shapeToClipTo.getY_coord());
			// drawShape(g, shapeToClip);
			break;
		default:
			; // Do nothing
		}
	}

}
