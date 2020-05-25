/**
 * 
 */
package guiLayer.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import guiLayer.CanvasWindow;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockType;
import types.ConnectionType;


/**
 * DefinitionShape
 * 
 * @version 0.1
 * @author group17
 *
 */
public class DefinitionShape extends Shape {

	/**
	 * Create a new shape with the given id, type and coordinate
	 * 
	 * @param id         the id for the shape
	 * @param type       the type of the shape
	 * @param coordinate the coordinate for the shape.
	 */
	public DefinitionShape(String id, BlockType type, Coordinate coordinate) {
		super(id, type, coordinate);
	}

	@Override
	public void clipOn(Shape shape, ConnectionType connection) {
		// There is no behavior for clipOn, a definitionShape can't be connected dragged to another shape.
	}

	@Override
	public void draw(Graphics g) {
		int startX = getX_coord();
		int startY = getY_coord();

		setCoordinatesShape();
		int total = getHeight();
		
		
		Color c = g.getColor();
		
		if(c.equals(Color.BLACK)) {
			g.setColor(Color.decode("#8770FA"));				
		}
		g.fillArc(startX + 20, startY + 20, 20, 20, 0, -180);
		g.fillPolygon(
				new int[] { startX, startX + 90,  startX + 90, startX + 10, startX + 10, startX + 20,startX+22, 
						startX+25, startX+30,startX+35,startX+38,   startX+40,       startX +90,startX+90,
						startX+90,startX },
				new int[] { startY, startY,startY + 30, startY + 30,startY + total - 30, startY + total - 30,startY + total - 30+5,
						startY + total - 30+8,startY + total - 30+10,startY + total - 30+8,startY + total - 30+5,startY + total - 30,    startY + total-30,startY + total,
						startY + total,startY + total},
				16);
		g.setColor(Color.BLACK);
		
		

		g.drawArc(startX + getWidth() - 70, startY + 20, 20, 20, 0, -180);
		g.drawArc(startX + getWidth() - 70, startY + total - 40, 20, 20, 0, -180);

		g.drawLine(startX, startY, startX, startY + total);
		g.drawLine(startX, startY, startX + getWidth(), startY);
		g.drawLine(startX, startY + total, startX + getWidth(), startY + total);

		g.drawLine(startX + 10, startY + 30, startX +10 , startY + total - 30);
		g.drawLine(startX + getWidth(), startY, startX + getWidth(), startY + 30);
		g.drawLine(startX + getWidth(), startY + total - 30, startX + getWidth(), startY + total);
		g.drawLine(startX + 10, startY + total - 30, startX + 20, startY + total - 30);
		g.drawLine(startX + 40, startY + total - 30, startX + getWidth(), startY + total - 30);
		g.drawLine(startX + 10, startY + 30, startX + 20, startY + 30);
		g.drawLine(startX + 40, startY + 30, startX + getWidth(), startY + 30);

		
		String idToShow = getId();
		if ( idToShow.equals(PALETTE_BLOCK_IDENTIFIER)) {
			idToShow= "";
		}
		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		g.drawString(getType().toString() +" "+ idToShow, startX + 10, startY + 23);
		g.setFont(f);
	}
	@Override
	protected Integer getStandardHeight() {
		return STANDARD_HEIGHT_CONTROL_BLOCK;
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
		connectionMap.put(ConnectionType.BODY, new Coordinate(this.getX_coord() + 30, this.getY_coord() + 30));
		this.setCoordinateConnectionMap(connectionMap);
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
	void initDimensions() {
		setHeight(90);
		setWidth(90);
	}

}
