package guiLayer;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import types.BlockType;
import types.ConnectionType;

public class UnaryOperatorShape extends Shape {

	public UnaryOperatorShape(String id, BlockType type, int x, int y) {
		super(id, type, x, y);
		// TODO Auto-generated constructor stub
	}

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
		g.drawString(getType().toString(), startX + 35, startY + 19);
		
		
	}

	@Override
	public HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y) {
		HashSet<Pair<Integer, Integer>> set = new HashSet<Pair<Integer, Integer>>();

			for (int i = x+10; i < x + getWidth()+10; i++) {
				for (int j = y; j < y + getHeight(); j++) {
						set.add(new Pair<Integer, Integer>(i, j));
				}
			}
		return set;
	}

	@Override
	public void updateConnectionTypes() {
		HashMap<ConnectionType, Pair<Integer, Integer>> connectionMap = new HashMap<ConnectionType, Pair<Integer,Integer>>();
		connectionMap.put(ConnectionType.LEFT, new Pair<Integer, Integer>(this.getX_coord(), this.getY_coord()+5));
		connectionMap.put(ConnectionType.OPERAND, new Pair<Integer, Integer>(this.getX_coord()+80, this.getY_coord()+5));
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
