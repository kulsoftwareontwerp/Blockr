package guiLayer.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.HashSet;

import guiLayer.types.Coordinate;
import types.BlockType;
import types.ConnectionType;

/**
 * ActionShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ActionShape extends Shape {

	/**
	 * Create a new ActionShape
	 * 
	 * @param id         the id of the actionShape
	 * @param type       the type of the actionShape
	 * @param coordinate the coordinate of the actionShape.
	 */
	public ActionShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
	}

	@Override
	public void draw(Graphics g) {

		int startX = getX_coord();
		int startY = getY_coord();

		
		Color c = g.getColor();
		
		if(c.equals(Color.BLACK)) {
			g.setColor(Color.decode("#42C3FF"));				
		}
		
		g.fillArc(startX + 10, startY + 20, 20, 20, 0, -180);	
		g.fillPolygon(new int[] {startX, startX + 10,startX+12, startX+15, startX+20,startX+25,startX+28,   startX+30,startX+90,startX+90,startX }, new int[] {startY,startY,startY+5,startY+8, startY+10,startY+8, startY+5, startY,startY,startY+30,startY+30}, 11);
	
		g.setColor(Color.BLACK);
		
		
		
		BlockType type = getType();
		
		
		g.drawArc(startX + 10, startY - 10, 20, 20, 0, -180);
		g.drawArc(startX + 10, startY + 20, 20, 20, 0, -180);


		
		g.drawLine(startX, startY, startX, startY + 30);

		g.drawLine(startX + 30, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 30);
		g.drawLine(startX, startY, startX + 10, startY);
		g.drawLine(startX, startY + 30, startX + 10, startY + 30);
		g.drawLine(startX + 30, startY + 30, startX + 90, startY + 30);
		
		

		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		g.drawString(type.toString() + idForDisplay(), startX + 3, startY + 23);

		g.setFont(f);

		
		
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

	@Override
	public void clipOn(Shape shapeToClipTo, ConnectionType connection) {
		switch (connection) {
		case UP:
			setX_coord(shapeToClipTo.getX_coord());
			setY_coord(shapeToClipTo.getY_coord() - getHeight());
			break;
		case DOWN:
			setX_coord(shapeToClipTo.getX_coord());
			setY_coord(shapeToClipTo.getY_coord() + shapeToClipTo.getHeight());
			break;
		case BODY:
			setX_coord(shapeToClipTo.getX_coord() + 10);
			setY_coord(shapeToClipTo.getY_coord() + 30);
			break;
		default:
			; // Do nothing
		}
	}

}
