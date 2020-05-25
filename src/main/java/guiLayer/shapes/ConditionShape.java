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
 * ConditionShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ConditionShape extends Shape {
	/**
	 * Create a new ConditionShape
	 * 
	 * @param id         the id of the ConditionShape
	 * @param type       the type of the ConditionShape
	 * @param coordinate the coordinate of the ConditionShape.
	 */
	public ConditionShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		
		
		Color c = g.getColor();
		
		if(c.equals(Color.BLACK)) {
			g.setColor(Color.decode("#61CCC3"));				
		}
		
		g.fillPolygon(
				new int[] {  startX + 10, startX + 90,  startX + 90, startX+10 },
				new int[] {  startY,  startY,startY + 30, startY + 30},
				4);
		g.fillArc(startX, startY + 5, 20, 20, -90, -180);
		g.setColor(Color.BLACK);
		
		
		BlockType type = getType();

		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		
		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		g.drawString(type.toString() + idForDisplay(), startX + 15, startY + 19);
		g.setFont(f);
	}

	@Override HashSet<Coordinate> fillShapeWithCoordinates() {
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
		this.setCoordinateConnectionMap(connectionMap);

	}

	@Override void initDimensions() {
		setHeight(30);
		setWidth(80);

	}

	@Override
	public void clipOn(Shape shapeToClipTo, ConnectionType connection) {
		switch (connection) {
		case CONDITION:
		case OPERAND:
			setX_coord(shapeToClipTo.getX_coord() + 80);
			setY_coord(shapeToClipTo.getY_coord());
			break;
		default:
			break;

		}
	}

}
