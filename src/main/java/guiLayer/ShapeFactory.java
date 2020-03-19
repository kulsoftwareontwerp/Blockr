package guiLayer;

import types.BlockType;

public class ShapeFactory {
	
	public ShapeFactory() {
		
	}
	
	public Shape createShape(String id, BlockType type, int x, int y) {
		Shape toReturn = null;
		switch (type) {
		case MoveForward:
		case TurnLeft:
		case TurnRight: 
			toReturn = new ActionShape(id, type, x, y);
			break;
		case If:
		case While: 
			toReturn = new ControlShape(id, type, x, y);
			break;
		case Not: 
			toReturn = new UnaryOperatorShape(id, type, x, y);
			break;
		case WallInFront: 
			toReturn = new ConditionShape(id, type, x, y);
			break;

		default:
		}
		return toReturn;
	}

}
