package guiLayer.shapes;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.WhileBlock;
import guiLayer.types.Coordinate;
import types.BlockType;

public class ShapeFactory {
	
	public ShapeFactory() {
		
	}
	
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
