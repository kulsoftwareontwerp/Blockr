package guiLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import types.BlockType;

public class ProgramArea implements Constants {

	private HashSet<Pair<Integer, Integer>> alreadyFilledInCoordinates;
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
		this.shapesInProgramArea.add(shape);
	}

	public void removeShapeFromProgramArea(Shape shape) {
		this.shapesInProgramArea.remove(shape);
		for (Pair<Integer, Integer> pair : shape.getCoordinatesShape()) {
			alreadyFilledInCoordinates.remove(pair);
		}
	}

	public ProgramArea() {
		alreadyFilledInCoordinates = new HashSet<Pair<Integer, Integer>>();
		shapesInProgramArea = new HashSet<Shape>();
	}
	
	public boolean checkIfInProgramArea(int x) {
		return x > PROGRAM_START_X && x < PROGRAM_END_X;
	}

	public Shape getShapeFromCoordinate(int x, int y) {

		try {
			return this.getShapesInProgramArea().stream()
					.filter(e -> e.getCoordinatesShape().contains(new Pair<Integer, Integer>(x, y))).findFirst().get();
		} catch (NoSuchElementException e) {
			System.out.println("NULL");
			return null;

		}

	}

	public boolean checkIfPlaceable(HashSet<Pair<Integer, Integer>> currentCoordinates, Shape currentShape) {
		boolean placeable = !((currentCoordinates.stream().anyMatch(i -> this.alreadyFilledInCoordinates.contains(i))))
				&& currentShape.getX_coord()+currentShape.getWidth() < PROGRAM_END_X;

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

	public HashSet<Pair<Integer, Integer>> getAlreadyFilledInCoordinates() {
		return alreadyFilledInCoordinates;
	}

	public void addToAlreadyFilledInCoordinates(Shape shape) {
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

	
	
	void draw(Graphics blockrGraphics) {
	
	
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
					int tempx = p.getValue().getLeft() - 3;
					int tempy = p.getValue().getRight();
					blockrGraphics.setColor(Color.black);
					blockrGraphics.drawOval(tempx, tempy, 6, 6);
	
					if (DebugModus.CONNECTIONSTATUS.compareTo(CanvasWindow.debugModus) <= 0) {
						if(shape.checkIfOpen(p.getKey())) {
							blockrGraphics.setColor(Color.green);
						}
						else {
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

}
