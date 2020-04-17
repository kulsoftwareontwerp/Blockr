package guiLayer.shapes;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import guiLayer.types.Coordinate;
import guiLayer.types.Pair;
import types.BlockType;
import types.ConnectionType;

public class UnaryOperatorShape extends Shape {

	public UnaryOperatorShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
		switchCavityStatus(ConnectionType.LEFT, true);
		switchCavityStatus(ConnectionType.OPERAND, true);	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		
		g.drawArc(startX + 80, startY + 5, 20, 20, -90, -180);
		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 5);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 90, startY + 25, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		g.drawString(getType().toString() + idForDisplay(), startX + 35, startY + 19);
		
		
	}

	@Override
	protected HashSet<Coordinate> fillShapeWithCoordinates() {
		HashSet<Coordinate> set = new HashSet<Coordinate>();

			for (int i = getX_coord()+10; i < getX_coord() + getWidth()+10; i++) {
				for (int j = getY_coord(); j < getY_coord() + getHeight(); j++) {
						set.add(new Coordinate(i, j));
				}
			}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType,Coordinate> connectionMap = new HashMap<ConnectionType,Coordinate>();
		connectionMap.put(ConnectionType.LEFT, new Coordinate(this.getX_coord()+10, this.getY_coord()+15));
		connectionMap.put(ConnectionType.OPERAND, new Coordinate(this.getX_coord()+90, this.getY_coord()+15));
		this.setCoordinateConnectionMap(connectionMap);
	}

	@Override
	public void initDimensions() {
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
