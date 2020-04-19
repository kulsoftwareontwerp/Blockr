package guiLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import applicationLayer.DomainController;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockType;

public class ProgramArea implements Constants {

	private HashSet<Coordinate> alreadyFilledInCoordinates;
	private Shape highlightedShape = null;

	private HashSet<Shape> shapesInProgramArea; // shapes with Id == null SHOULDN'T exist!!!!, only if dragged from
	// Palette, Id == "PALETTE"
	private Shape highlightedShapeForExecution;

	public HashSet<Shape> getShapesInProgramArea() {
		HashSet<Shape> copy = new HashSet<Shape>(shapesInProgramArea);
		return copy;
	}

	public void clearAlreadyFilledInCoordinates() {
		alreadyFilledInCoordinates.clear();
	}

	public void addShapeToProgramArea(Shape shape) {
		if (shape != null) {
			Shape presentShape = getShapeById(shape.getId());
			if (presentShape != null) {
				this.shapesInProgramArea.remove(presentShape);
			}

			this.shapesInProgramArea.add(shape);

		}
	}

	public void removeShapeFromProgramArea(Shape shape) {
		this.shapesInProgramArea.remove(shape);
		for (Coordinate pair : shape.getCoordinatesShape()) {
			alreadyFilledInCoordinates.remove(pair);
		}
	}

	public ProgramArea() {
		alreadyFilledInCoordinates = new HashSet<Coordinate>();
		shapesInProgramArea = new HashSet<Shape>();
	}

	public boolean checkIfInProgramArea(int x) {
		return x > PROGRAM_START_X && x < PROGRAM_END_X;
	}

	public Shape getShapeFromCoordinate(int x, int y) {

		try {
			return this.getShapesInProgramArea().stream()
					.filter(e -> e.getCoordinatesShape().contains(new Coordinate(x, y))).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;

		}
	}

	/**
	 * Retrieve a shape by its ID
	 * 
	 * @param id
	 * @return
	 */
	public Shape getShapeById(String id) {

		try {
			return this.getShapesInProgramArea().stream().filter(e -> e.getId().equals(id)).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public boolean checkIfPlaceable(HashSet<Coordinate> hashSet, Shape currentShape) {
		boolean placeable = !((hashSet.stream().anyMatch(i -> this.alreadyFilledInCoordinates.contains(i))))
				&& currentShape.getX_coord() + currentShape.getWidth() < PROGRAM_END_X;

		if ((currentShape.getType() == BlockType.valueOf("If") || currentShape.getType() == BlockType.valueOf("While"))
				&& (getHighlightedShape() != null && (getHighlightedShape().getType() == BlockType.valueOf("If")
						|| getHighlightedShape().getType() == BlockType.valueOf("While")))) {
			placeable = true;
		}
		// TODO Hotfix needed
		if (currentShape instanceof ControlShape && checkIfInProgramArea(currentShape.getX_coord())) {
			placeable = true;
		}
		return placeable;
	}

	public HashSet<Coordinate> getAlreadyFilledInCoordinates() {
		return alreadyFilledInCoordinates;
	}

	public void addToAlreadyFilledInCoordinates(Shape shape) {
		Shape presentShape = getShapeById(shape.getId());
		if (presentShape != null) {
			removeFromAlreadyFilledInCoordinates(presentShape);
		}
		getAlreadyFilledInCoordinates().addAll(shape.getCoordinatesShape());
	}

	public void removeFromAlreadyFilledInCoordinates(Shape shape) {
		getAlreadyFilledInCoordinates().removeAll(shape.getCoordinatesShape());
	}

	public Shape getHighlightedShape() {
		return highlightedShape;
	}

	public void setHighlightedShape(Shape highlightedShape) {
		this.highlightedShape = highlightedShape;
	}

	void draw(Graphics blockrGraphics, DomainController controller) {

		// draw all shapes in shapesInProgramArea
		if (getShapesInProgramArea() != null && !getShapesInProgramArea().isEmpty()) {
			getShapesInProgramArea().stream().forEach(((Shape e) -> e.draw(blockrGraphics)));
		}

		if (getHighlightedShapeForExecution() != null) {
			drawHighlightedBLUE(blockrGraphics, getHighlightedShapeForExecution());
		}

		if (getHighlightedShape() != null) {
			drawHighlightedGREEN(blockrGraphics, getHighlightedShape());
		}
		// only for debugging purposes
		if (DebugModus.CONNECTIONS.compareTo(CanvasWindow.debugModus) <= 0) {
			for (Shape shape : getShapesInProgramArea()) {
				for (var p : shape.getCoordinateConnectionMap().entrySet()) {
					int tempx = p.getValue().getX() - 3;
					int tempy = p.getValue().getY();
					blockrGraphics.setColor(Color.black);
					blockrGraphics.drawOval(tempx, tempy, 6, 6);

					if (DebugModus.CONNECTIONSTATUS.compareTo(CanvasWindow.debugModus) <= 0) {

						if (controller.checkIfConnectionIsOpen(shape.getId(), p.getKey(), null)) {
//						if (shape.checkIfOpen(p.getKey())) {
							blockrGraphics.setColor(Color.green);
						} else {
							blockrGraphics.setColor(Color.red);

						}

						blockrGraphics.fillOval(tempx, tempy, 6, 6);
					}
				}
			}

		}
	}

	void drawHighlightedBLUE(Graphics g, Shape shape) {
		g.setColor(Color.BLUE);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	void drawHighlightedGREEN(Graphics g, Shape shape) {
		g.setColor(Color.GREEN);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	private Shape getHighlightedShapeForExecution() {
		return highlightedShapeForExecution;
	}

	public void setHighlightedShapeForExecution(Shape shape) {
		this.highlightedShapeForExecution = shape;

	}

	public Shape getClonedHighlightedShape() {
		if (highlightedShape != null) {
			return highlightedShape.clone();
		}
		return null;
	}

	public Set<ControlShape> getAllChangedControlShapes() {
		return shapesInProgramArea.stream().filter(s-> (s instanceof ControlShape) && s.getHeightDiff()!=0 ).map(s->(ControlShape) s).collect(Collectors.toSet());
	}

}
