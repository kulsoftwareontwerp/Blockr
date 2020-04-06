package guiLayer;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import types.BlockType;
import types.ConnectionType;

public class ActionShape extends Shape {

	public ActionShape(String id, BlockType type, int x, int y) {
		super(id, type, x, y);
		// TODO Auto-generated constructor stub
		switchCavityStatus(ConnectionType.UP);
		switchCavityStatus(ConnectionType.DOWN);
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

			g.drawString(type.toString() + idForDisplay(), startX + 3, startY + 23);
		
	}

	@Override
	public HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y) {
		HashSet<Pair<Integer, Integer>> set = new HashSet<Pair<Integer, Integer>>();
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					set.add(new Pair<Integer, Integer>(i, j));
				}
			}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Pair<Integer, Integer>> connectionMap = new HashMap<ConnectionType, Pair<Integer,Integer>>();
		connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+20, this.getY_coord()));
		connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+20, this.getY_coord()+30));
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
