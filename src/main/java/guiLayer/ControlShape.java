package guiLayer;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;

import types.BlockType;
import types.ConnectionType;

public class ControlShape extends Shape implements Constants {
	private HashSet<Shape> internals;

	public ControlShape(String id, BlockType type, int x, int y) {
		super(id, type, x, y);
		internals = new HashSet<Shape>(); //empty on init
		switchCavityStatus(ConnectionType.UP);
		switchCavityStatus(ConnectionType.DOWN);
		switchCavityStatus(ConnectionType.CONDITION);
		switchCavityStatus(ConnectionType.BODY);
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();
		HashSet<Shape> internals = getInternals();
		
		setCoordinatesShape(createCoordinatePairs(startX, startY));
		int total = getHeight();

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
		g.drawLine(startX + getWidth() - 50, startY + total - 30, startX + getWidth(),
				startY + total - 30);
		g.drawLine(startX + getWidth(), startY, startX + getWidth(), startY + 5);
		g.drawLine(startX + getWidth(), startY + 25, startX + getWidth(), startY + 30);
		g.drawLine(startX + getWidth(), startY + total - 30, startX + getWidth(),
				startY + total);
		g.drawLine(startX + 10, startY + total - 30, startX + getWidth() - 70, startY + total - 30);
		g.drawLine(startX + 10, startY + 30, startX + getWidth() - 70, startY + 30);
		g.drawLine(startX + getWidth() - 50, startY + 30, startX + getWidth(), startY + 30);
		g.drawLine(startX + 30, startY + total, startX + getWidth(), startY + total);

		g.drawString(getType().toString()+ idForDisplay(), startX + 10, startY + 23);
	}

	@Override
	public HashSet<Pair<Integer, Integer>> createCoordinatePairs(int x, int y) {
		HashSet<Pair<Integer, Integer>> set = new HashSet<Pair<Integer, Integer>>();
			for (int i = x; i < x + getWidth(); i++) {
				for (int j = y; j < y + getHeight(); j++) {
					if (!(j > 25+y && j <= y+getHeight()-25 && i > x && i <= x+getWidth())) // to give room for the clip, otherwise it won't wrok
						set.add(new Pair<Integer, Integer>(i, j));
				}
			}
		return set;
	}

	@Override
	public void defineConnectionTypes() {
		HashMap<ConnectionType, Pair<Integer, Integer>> connectionMap = new HashMap<ConnectionType, Pair<Integer,Integer>>();
			connectionMap.put(ConnectionType.UP, new Pair<Integer, Integer>(this.getX_coord()+20, this.getY_coord()));
			connectionMap.put(ConnectionType.CONDITION, new Pair<Integer, Integer>(this.getX_coord()+(getWidth()-10), this.getY_coord()+15));
			connectionMap.put(ConnectionType.BODY, new Pair<Integer, Integer>(this.getX_coord()+30, this.getY_coord()+30));
			connectionMap.put(ConnectionType.DOWN, new Pair<Integer, Integer>(this.getX_coord()+20, this.getY_coord()+(getHeight())));
		this.setCoordinateConnectionMap(connectionMap);
		
	}

	@Override
	public void initDimensions() {
		setHeight(90);
		setWidth(90);
		
	}
	
	@Override
	public HashSet<Shape> getInternals() {
		return internals;
	}

	@Override
	public void addInternal(Shape shape) {
		this.getInternals().add(shape);
	}
	
	@Override
	public void removeInternal(Shape shape) {
		this.getInternals().remove(shape);
	}
	
	@Override
	public void determineTotalDimensions() {
		int totalHeight = determineTotalDimensions(this);
		
		setHeight(totalHeight);
		defineConnectionTypes();
		setCoordinatesShape(createCoordinatePairs(getX_coord(), getY_coord()));
	}
	
	private Integer determineTotalDimensions(Shape shape) {
		int total_height = shape.getStandardHeight();
	
		if(!shape.getInternals().isEmpty()){
			int tempExtraHeight = 0;
			for (Shape shapeInternal : shape.getInternals()) {
				
				if(shapeInternal instanceof ControlShape) {
					shapeInternal.determineTotalDimensions();
				}
				tempExtraHeight += determineTotalDimensions(shapeInternal);
			}
			return total_height+tempExtraHeight;
		}else {
		return total_height;
		}
	}
	
	@Override
	public Integer getStandardHeight() {
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
			setX_coord(shapeToClipTo.getX_coord() -getWidth()+10);
			setY_coord(shapeToClipTo.getY_coord());
			break;
		default:
			; // Do nothing
		}
		
	}
	
	
//	private Pair<Integer, Integer> determineTotalDimensions(Shape shape) {
//		int total_y = shape.getHeight();
//		int total_x = shape.getWidth();
//		int extraWidth = 0;
//		boolean flag = true;
//		
//		for (Shape shapeInternal : shape.getInternals()) {
//			if(shape instanceof ControlShape) {
//				if(flag) {
//					flag = false;
//					extraWidth = 10;
//					}
//					
//			}
//			Pair<Integer, Integer> temp = determineTotalDimensions(shapeInternal);
//			extraWidth += temp.getLeft();
//			total_y += temp.getRight();
//		}
//		
//		
//		return new Pair<Integer, Integer>(extraWidth, total_y);
//	}
	
	
	
	

}
