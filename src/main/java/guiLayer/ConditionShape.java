package guiLayer;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import guiLayer.types.Pair;
import types.BlockType;
import types.ConnectionType;

public class ConditionShape extends Shape {

	public ConditionShape(String id, BlockType type, int x, int y) {
		super(id, type, x, y);
		switchCavityStatus(ConnectionType.LEFT, true);
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		BlockType type = getType();
		
		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		g.drawString(type.toString() + idForDisplay(), startX + 15, startY + 19);
	}

	@Override
	public HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y) {
		HashSet<Pair<Integer, Integer>> set = new HashSet<Pair<Integer, Integer>>();
		
			for (int i = x + 10; i < x + getWidth()+10; i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}

		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Pair<Integer, Integer>> connectionMap = new HashMap<ConnectionType, Pair<Integer,Integer>>();
		connectionMap.put(ConnectionType.LEFT, new Pair<Integer, Integer>(this.getX_coord()+10, this.getY_coord()+15));
		this.setCoordinateConnectionMap(connectionMap);
		
	}

	@Override
	public void initDimensions() {
		setHeight(30);
		setWidth(80);
		
	}

	@Override
	public void clipOn(Shape shapeToClipTo, ConnectionType connection) {
		setX_coord(shapeToClipTo.getX_coord() + 80);
		setY_coord(shapeToClipTo.getY_coord());
	}
	
}
