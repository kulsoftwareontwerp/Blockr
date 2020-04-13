package guiLayer;

import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.ConditionBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.WhileBlock;
import types.BlockType;

public class ShapeFactory {
	
	public ShapeFactory() {
		
	}
	
	public Shape createShape(String id, BlockType type, int x, int y) {
		switch (type.cat()) {
		case ACTION:
			return new ActionShape(id, type, x, y);
		case CONTROL :
			return new ControlShape(id, type, x, y);
		case OPERATOR: 
			return new UnaryOperatorShape(id, type, x, y);
		case CONDITION: 
			return new ConditionShape(id, type, x, y);

		default: return null;
		}		
	}

}
