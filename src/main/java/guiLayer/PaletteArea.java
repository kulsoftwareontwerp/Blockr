package guiLayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import types.BlockCategory;
import types.BlockType;

/**
 * PaletteArea, The paletteArea describes the palette where the user can pick
 * blocks from.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class PaletteArea implements Constants {

	private HashSet<Shape> shapesInPalette;
	private ShapeFactory shapeFactory;
	private Boolean paletteVisible;
	private int currentDrawHeight;

	/**
	 * Create a new PaletteArea, takes a shapeFactory to create shapes with.
	 * 
	 * @param shapeFactory the shapeFactory to create shapes with.
	 */
	public PaletteArea(ShapeFactory shapeFactory) {
		setShapeFactory(shapeFactory);
		setPaletteVisible(true);
		currentDrawHeight = 0;
		shapesInPalette = new HashSet<Shape>();
	}

	/**
	 * Is the palette visible
	 * 
	 * @return a flag indicating if the palette is visible
	 */
	public Boolean isPaletteVisible() {
		return paletteVisible;
	}

	/**
	 * Set the flag indicating if the palette is visible
	 * 
	 * @param paletteVisible a flag if the palette is visible
	 */
	public void setPaletteVisible(Boolean paletteVisible) {
		this.paletteVisible = paletteVisible;
	}

	/**
	 * Paint the palette on the given graphics object
	 * 
	 * @param g The Graphics object to draw the palette on
	 */
	public void paint(Graphics g) {
		// Palette
		
		// Rest of the Frame
		g.fill3DRect(ORIGIN, ORIGIN, PALETTE_END_X,  g.getClipBounds().height, true);

		if (!isPaletteVisible()) {
			g.setColor(Color.WHITE);
			g.drawString("Too many blocks", 5, 30);
			g.setColor(Color.BLACK);
		}

		
		
//		g.drawLine(100, 0, 100, g.getClipBounds().height);
		drawFullPalette(g);
		
	}

	private void drawFullPalette(Graphics g) {
		if (isPaletteVisible()) {
			drawTitles(g);
			if (shapesInPalette.size() != BlockType.values().length) {
				fillPalette();
			}
			shapesInPalette.forEach(e -> e.draw(g));
		}
	}

	private void drawTitles(Graphics g) {
		g.setColor(Color.WHITE);
		Font f = g.getFont();
		g.setFont(Font.decode("arial-bold-12"));
		
		
		int tempHeight = 30; // initial starting position in paletteArea to draw Strings
		g.drawLine(0, tempHeight - 20, 100, tempHeight - 20);
		g.drawString("Action Blocks", 3, tempHeight);
		tempHeight += 20;
		g.drawLine(0, tempHeight - 10, 100, tempHeight - 10);

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.ACTION) {
				tempHeight += 45;
			}
		}

		tempHeight += 20;
		g.drawLine(0, tempHeight - 20, 100, tempHeight - 20);
		g.drawString("Control Blocks", 3, tempHeight);
		tempHeight += 20;
		g.drawLine(0, tempHeight - 10, 100, tempHeight - 10);

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.CONTROL) {
				tempHeight += 105;
			}
		}

		tempHeight += 20;
		g.drawLine(0, tempHeight - 20, 100, tempHeight - 20);
		g.drawString("Operator Blocks", 3, tempHeight);
		tempHeight += 20;
		g.drawLine(0, tempHeight - 10, 100, tempHeight - 10);

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.OPERATOR) {
				tempHeight += 35;
			}
		}

		tempHeight += 25;
		g.drawLine(0, tempHeight - 20, 100, tempHeight - 20);
		g.drawString("Condition Blocks", 1, tempHeight);
		tempHeight += 20;
		g.drawLine(0, tempHeight - 10, 100, tempHeight - 10);

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.CONDITION) {
				tempHeight += 35;
			}
		}

		tempHeight += 20;
		g.drawLine(0, tempHeight - 20, 100, tempHeight - 20);
		g.drawString("Function Blocks", 3, tempHeight);
		tempHeight += 20;
		g.drawLine(0, tempHeight - 10, 100, tempHeight - 10);
		
		g.setColor(Color.BLACK);
		g.setFont(f);

	}

	private void fillPalette() {
		shapesInPalette.clear();
		int tempHeight = 50;
		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.ACTION) {
				shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType) type,
						new Coordinate(ACTION_BLOCK_INIT_OFFSET, tempHeight)));
				System.out.println(type+"action" + tempHeight);
				tempHeight += 45;
			}
		}

		tempHeight += 40;
		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.CONTROL) {

				shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType) type,
						new Coordinate(CONTROL_BLOCK_INIT_OFFSET, tempHeight)));
				System.out.println(type+"control" + tempHeight);
				tempHeight += 105;

			}
		}

		tempHeight += 40;
		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.OPERATOR) {

				shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType) type,
						new Coordinate(OPERATOR_BLOCK_INIT_OFFSET, tempHeight)));
				System.out.println(type+"operator" + tempHeight);
				tempHeight += 35;

			}
		}

		tempHeight += 45;

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.CONDITION) {

				shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType) type,
						new Coordinate(CONDITION_BLOCK_INIT_OFFSET, tempHeight)));
				System.out.println(type+"condition" + tempHeight);
				tempHeight += 35;

			}
		}

		tempHeight += 40;
		shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, BlockType.DEFINITION,
				new Coordinate(CONTROL_BLOCK_INIT_OFFSET, tempHeight)));
		System.out.println("Definition"+"control" + tempHeight);
		tempHeight += 105;

		for (var type : BlockType.values()) {
			if (type.cat() == BlockCategory.CALL) {
				shapesInPalette.add(shapeFactory.createShape(PALETTE_BLOCK_IDENTIFIER, (BlockType) type,
						new Coordinate(ACTION_BLOCK_INIT_OFFSET, tempHeight)));
				System.out.println(type+"action" + tempHeight);
				tempHeight += 45;

			}
		}

		if (currentDrawHeight == 0) {
			currentDrawHeight = tempHeight;
		}
		
		shapesInPalette.remove(null);
	}

	/**
	 * Check if the given x value is within the palette it's bounds.
	 * 
	 * @param x the x coordinate to check
	 * @return a flag indicating if the given x value is within the palette it's
	 *         bounds.
	 */
	public boolean checkIfInPalette(int x) {
		return x <= PALETTE_END_X;
	}

	/**
	 * Retrieve a shape from the given coordinates, return null if no shape is
	 * present at these coordinates.
	 * 
	 * @param x The x coordinate of the shape
	 * @param y The y coordinate of the shape
	 * @return The shape on the given coordinate or null if there is no shape at
	 *         those coordinates.
	 */
	public Shape getShapeFromCoordinate(int x, int y) {
		Optional<Shape> shape = this.getShapesInPalette().stream()
				.filter(e -> e.getCoordinatesShape().contains(new Coordinate(x, y))).findFirst();

		if (shape.isPresent()) {
			return shape.get().clone();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve all the shapes in the palette.
	 * 
	 * @return a Set containing all the shapes in the palette.
	 */
	public Set<Shape> getShapesInPalette() {
		return shapesInPalette;
	}

	/**
	 * Set the shapeFactory of this paletteArea
	 * 
	 * @param shapeFactory the shapeFactory to set this factory too.
	 * @throws NullPointerException when the shapeFactory is null
	 */
	public void setShapeFactory(ShapeFactory shapeFactory) {
		if (shapeFactory == null) {
			throw new NullPointerException("A paletteArea needs a ShapeFactory");
		}
		this.shapeFactory = shapeFactory;
	}

}
