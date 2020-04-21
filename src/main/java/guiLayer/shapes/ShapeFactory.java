package guiLayer.shapes;

import guiLayer.types.Coordinate;
import types.BlockType;

/**
 * ShapeFactory
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ShapeFactory {
	/**
	 * Create a new shapeFactory
	 */
	public ShapeFactory() {
		
	}
	/**
	 * Create a new shape with the given id, type and coordinate
	 * @param id the id for the shape
	 * @param type the type of the shape
	 * @param coordinate the coordinate for the shape.
	 * @return a new shape with the given id, type and coordinate
	 */
	public Shape createShape(String id, BlockType type, Coordinate coordinate) {
		switch (type.cat()) {
		case ACTION:
			return new ActionShape(id, type, coordinate);
		case CONTROL :
			return new ControlShape(id, type, coordinate);
		case OPERATOR: 
			return new UnaryOperatorShape(id, type, coordinate);
		case CONDITION: 
			return new ConditionShape(id, type, coordinate);

		default: return null;
		}		
	}



}
