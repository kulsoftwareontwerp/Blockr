package guiLayer.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import types.BlockType;
import types.ConnectionType;

/**
 * ControlShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ControlShape extends Shape implements Constants {
	/**
	 * Create a new shape with the given id, type and coordinate
	 * 
	 * @param id         the id for the shape
	 * @param type       the type of the shape
	 * @param coordinate the coordinate for the shape.
	 */
	public ControlShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();

		setCoordinatesShape();
		int total = getHeight();

		Color c = g.getColor();
		
		if(c.equals(Color.BLACK)) {
			g.setColor(Color.decode("#FAB580"));				
		}
	
		g.fillArc(startX + 20, startY + 20, 20, 20, 0, -180);
		g.fillPolygon(
				new int[] { startX, startX + 10, startX + 12, startX + 15, startX + 20, startX + 25, startX + 28,
						startX + 30, startX + 90, startX + 90, startX + 85, startX + 82, startX + 80, startX + 82,
						startX + 85, startX + 90, startX + 90, startX + 10, startX + 10, startX + 20,startX+22, 
						startX+25, startX+30,startX+35,startX+38,   startX+40,       startX +90,startX+90,
						startX+90,startX },
				new int[] { startY, startY, startY + 5, startY + 8, startY + 10, startY + 8, startY + 5, 
						startY, startY,startY + 5, startY + 7, startY + 10, startY + 15, startY + 20,
						startY + 23, startY + 25,startY + 30, startY + 30,startY + total - 30, startY + total - 30,startY + total - 30+5,
						startY + total - 30+8,startY + total - 30+10,startY + total - 30+8,startY + total - 30+5,startY + total - 30,    startY + total-30,startY + total,
						startY + total,startY + total},
				30);
		g.fillArc(startX + 10, startY + total - 10, 20, 20, 0, -180);
		g.setColor(Color.BLACK);

		g.drawArc(startX + 10, startY - 10, 20, 20, 0, -180);
		g.drawArc(startX + getWidth() - 70, startY + 20, 20, 20, 0, -180);
		g.drawArc(startX + getWidth() - 70, startY + total - 40, 20, 20, 0, -180);
		g.drawArc(startX + 10, startY + total - 10, 20, 20, 0, -180);
		g.drawArc(startX + getWidth() - 10, startY + 5, 20, 20, -90, -180);

		g.drawLine(startX, startY, startX, startY + total);
		g.drawLine(startX, startY, startX + 10, startY);
		g.drawLine(startX, startY + total, startX + 10, startY + total);

		g.drawLine(startX + 10, startY + 30, startX + 10, startY + total - 30);
		g.drawLine(startX + 30, startY, startX + getWidth(), startY);
		g.drawLine(startX + getWidth() - 50, startY + total - 30, startX + getWidth(), startY + total - 30);
		g.drawLine(startX + getWidth(), startY, startX + getWidth(), startY + 5);
		g.drawLine(startX + getWidth(), startY + 25, startX + getWidth(), startY + 30);
		g.drawLine(startX + getWidth(), startY + total - 30, startX + getWidth(), startY + total);
		g.drawLine(startX + 10, startY + total - 30, startX + getWidth() - 70, startY + total - 30);
		g.drawLine(startX + 10, startY + 30, startX + getWidth() - 70, startY + 30);
		g.drawLine(startX + getWidth() - 50, startY + 30, startX + getWidth(), startY + 30);
		g.drawLine(startX + 30, startY + total, startX + getWidth(), startY + total);

		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		g.drawString(getType().toString() + idForDisplay(), startX + 10, startY + 23);
		g.setFont(f);
	}

	@Override
	HashSet<Coordinate> fillShapeWithCoordinates() {
		HashSet<Coordinate> set = new HashSet<Coordinate>();
		for (int i = getX_coord(); i < getX_coord() + getWidth(); i++) {
			for (int j = getY_coord(); j < getY_coord() + getHeight(); j++) {
				if (!(j > 25 + getY_coord() && j <= getY_coord() + getHeight() - 25 && i > getX_coord()
						&& i <= getX_coord() + getWidth())) // to give room for the clip, otherwise it won't work
					set.add(new Coordinate(i, j));
			}
		}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Coordinate> connectionMap = new HashMap<ConnectionType, Coordinate>();
		connectionMap.put(ConnectionType.UP, new Coordinate(this.getX_coord() + 20, this.getY_coord()));
		connectionMap.put(ConnectionType.CONDITION,
				new Coordinate(this.getX_coord() + (getWidth() - 10), this.getY_coord() + 15));
		connectionMap.put(ConnectionType.BODY, new Coordinate(this.getX_coord() + 30, this.getY_coord() + 30));
		connectionMap.put(ConnectionType.DOWN,
				new Coordinate(this.getX_coord() + 20, this.getY_coord() + (getHeight())));
		this.setCoordinateConnectionMap(connectionMap);

	}

	@Override
	void initDimensions() {
		setHeight(90);
		setWidth(90);

	}

	@Override
	public void determineTotalHeight(Set<Shape> mapSetOfIdsToShapes) {
		int tempHeight = getStandardHeight();
		for (Shape shape : mapSetOfIdsToShapes) {
			tempHeight += shape.getStandardHeight();
		}
		setHeight(tempHeight);
		defineConnectionTypes();
		setCoordinatesShape();
	}

	@Override
	protected Integer getStandardHeight() {
		return STANDARD_HEIGHT_CONTROL_BLOCK;
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
		case LEFT:
			setX_coord(shapeToClipTo.getX_coord() - getWidth() + 10);
			setY_coord(shapeToClipTo.getY_coord());
			break;
		default:
			; // Do nothing
		}

	}

}
