package guiLayer;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.NoSuchElementException;

import domainLayer.gamestates.GameState;
import types.BlockCategory;
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
				
				// Rest of the Frame
				g.drawLine(100, 0, 100, g.getClipBounds().height);
				drawFullPalette(g);

				

	}
	
	private void drawFullPalette(Graphics g) {
				
		HashSet<Shape> set = new HashSet<Shape>();
		
		int tempHeight = 30; // initial starting position in paletteArea to draw Strings
		if(getPaletteVisible()) {
			g.drawLine(0, tempHeight-20, 100, tempHeight-20);
			g.drawString("Action Blocks", 15, tempHeight);
			tempHeight +=20;
			g.drawLine(0, tempHeight -10, 100, tempHeight-10);
			
			for (var type : BlockType.values()) {
				if(type.cat() == BlockCategory.ACTION){
					set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType)type, ACTION_BLOCK_INIT_OFFSET, tempHeight));
					tempHeight += 45;
				}
			}
			
			
			tempHeight += 20;
			g.drawLine(0, tempHeight-20, 100, tempHeight-20);
			g.drawString("Control Blocks", 10, tempHeight);
			tempHeight +=20;
			g.drawLine(0, tempHeight -10, 100, tempHeight-10);
			
			for (var type : BlockType.values()) {
				if(type.cat() == BlockCategory.CONTROL){
					set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType)type, CONTROL_BLOCK_INIT_OFFSET, tempHeight));
					tempHeight += 105;
				}
			}
			
			
			tempHeight += 20;
			g.drawLine(0, tempHeight-20, 100, tempHeight-20);
			g.drawString("Operator Blocks", 5, tempHeight);
			tempHeight +=20;
			g.drawLine(0, tempHeight -10, 100, tempHeight-10);
			
			for (var type : BlockType.values()) {
				if(type.cat() == BlockCategory.OPERATOR){
					set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType)type, OPERATOR_BLOCK_INIT_OFFSET, tempHeight));
					tempHeight += 35 ;
				}
			}
			
			tempHeight += 25;
			g.drawLine(0, tempHeight-20, 100, tempHeight-20);
			g.drawString("Condition Blocks", 5, tempHeight);
			tempHeight +=20;
			g.drawLine(0, tempHeight -10, 100, tempHeight-10);
			
			for (var type : BlockType.values()) {
				if(type.cat() == BlockCategory.CONDITION){
					set.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType)type, CONDITION_BLOCK_INIT_OFFSET, tempHeight));
					tempHeight += 35 ;
				}
			}
			
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
