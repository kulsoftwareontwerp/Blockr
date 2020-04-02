package guiLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import types.BlockType;

public class ProgramArea implements Constants {

	private HashSet<Pair<Integer, Integer>> alreadyFilledInCoordinates;
	private HashMap<Shape, Pair<Integer, Integer>> openConnectionCoordinates;
	private Shape highlightedShape = null;
	private HashSet<Shape> shapesInProgramArea; // shapes with Id == null SHOULDN'T exist!!!!, only if dragged from
	// Palette, Id == "PALETTE"

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
		openConnectionCoordinates =new  HashMap<Shape, Pair<Integer, Integer>>();	}

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
				&& currentShape.getX_coord() < PROGRAM_END_X;

		if ((currentShape.getType() == BlockType.If || currentShape.getType() == BlockType.While)
				&& (getHighlightedShape() != null && (getHighlightedShape().getType() == BlockType.If
						|| getHighlightedShape().getType() == BlockType.While))) {
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

}
