package guiLayer;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.NoSuchElementException;

import domainLayer.gamestates.GameState;
import types.BlockType;

public class PaletteArea implements Constants {
	
	private HashSet<Shape> shapesInPalette;
	private ShapeFactory shapeFactory;
	private Boolean paletteVisible;



	public PaletteArea(ShapeFactory shapeFactory) {
		setShapeFactory(shapeFactory);
		setPaletteVisible(true);
	}
	
	public Boolean getPaletteVisible() {
		return paletteVisible;
	}
	
	
	public void setPaletteVisible(Boolean paletteVisible) {
		this.paletteVisible = paletteVisible;
	}
	
	public void paint(Graphics g) {
		// Palette
		



				
				if(!getPaletteVisible()) {
					
					g.drawString("Too many blocks", 5, 30);
				}
				else {
					// ActionBlocks
					g.drawLine(0, 10, 100, 10); // whiteSpace
					g.drawLine(0, 40, 100, 40); // "Action Block"

					// ControlBlocks
					g.drawLine(0, 185, 100, 185); // whiteSpace
					g.drawLine(0, 215, 100, 215); // "Control Block"

					// OperatorBlocks
					g.drawLine(0, 435, 100, 435); // whiteSpace
					g.drawLine(0, 465, 100, 465); // "Operator Block"

					// ConditionBlocks
					g.drawLine(0, 515, 100, 515); // whiteSpace
					g.drawLine(0, 545, 100, 545); // "Condition Block"
				}
				// Rest of the Frame
				g.drawLine(100, 0, 100, 600);
				g.drawLine(GAME_START_X, 0, GAME_START_X, 600);
				g.drawLine(GAME_START_X, 200, GAME_END_Y, 200);
				g.drawLine(GAME_START_X, 400, GAME_END_Y, 400);
				drawFullPalette(g);

				

	}
	
	private void drawFullPalette(Graphics g) {
		
		HashSet<Shape> set = new HashSet<Shape>();
		
		if(getPaletteVisible()) {
		g.drawString("Action Blocks", 15, 30);
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.MoveForward, ACTION_BLOCK_INIT_OFFSET, ACTION_BLOCK_MOVE_FORWARD_UPPER));
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.TurnLeft, ACTION_BLOCK_INIT_OFFSET, ACTION_BLOCK_TURN_LEFT_UPPER));
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.TurnRight, ACTION_BLOCK_INIT_OFFSET, ACTION_BLOCK_TURN_RIGHT_UPPER));
		
		g.drawString("Control Blocks", 10, 205);
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.If, CONTROL_BLOCK_INIT_OFFSET, CONTROL_BLOCK_IF_UPPER));
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.While, CONTROL_BLOCK_INIT_OFFSET, CONTROL_BLOCK_WHILE_UPPER));

		g.drawString("Operator Blocks", 5, 455);
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.Not, OPERATOR_BLOCK_INIT_OFFSET, OPERATOR_BLOCK_NOT_UPPER));

		g.drawString("Condition Blocks", 5, 535);
		set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.WallInFront, CONDITION_BLOCK_INIT_OFFSET, CONDITION_BLOCK_WALL_UPPER));
		
		set.forEach(e-> e.draw(g));
		}
		
		this.shapesInPalette = set; 
		
		
		
	}
	
	public boolean checkIfInPalette(int x) {
		return x <= PALETTE_END_X ;
	}
	
	// will return null if there is no Shape in those coordinates => MOET BETER
	// GEDAAN WORDEN MET EXCEPTIONS
	public Shape getShapeFromCoordinate(int x, int y) {
		

		try {
			return this.getShapesInPalette().stream()
					.filter(e -> e.getCoordinatesShape().contains(new Pair<Integer, Integer>(x, y))).findFirst().get();
		} catch (NoSuchElementException e) {
			System.out.println("NULL");
			return null;
			
		}

	}


	public HashSet<Shape> getShapesInPalette() {
		return shapesInPalette;
	}


	public ShapeFactory getShapeFactory() {
		return shapeFactory;
	}


	public void setShapeFactory(ShapeFactory shapeFactory) {
		this.shapeFactory = shapeFactory;
	}
	
	
	
	
	
}
