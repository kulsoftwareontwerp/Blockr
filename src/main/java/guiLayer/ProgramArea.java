package guiLayer;

import java.util.HashSet;

import types.BlockType;

public class ProgramArea implements Constants {
	
	private HashSet<Pair<Integer, Integer>> alreadyFilledInCoordinates;
	private Shape highlightedShape = null;
	
	public ProgramArea() {
		alreadyFilledInCoordinates = new HashSet<Pair<Integer, Integer>>();
	}
	
	
	public boolean checkIfInProgramArea(int x) {
		return x > PROGRAM_START_X && x < PROGRAM_END_X;
	}
	
	public boolean checkIfPlaceable(HashSet<Pair<Integer, Integer>> currentCoordinates, Shape currentShape) {
		boolean placeable = !((currentCoordinates.stream()
				.anyMatch(i -> this.alreadyFilledInCoordinates.contains(i))))
				&& currentShape.getX_coord() < PROGRAM_END_X;

		if ((currentShape.getType() == BlockType.If || currentShape.getType() == BlockType.While)
				&& (getHighlightedShape() != null && (getHighlightedShape().getType() == BlockType.If
						|| getHighlightedShape().getType() == BlockType.While))) {
				placeable = true;
		}
		//TODO Hotfix needed
		if(currentShape instanceof ControlShape && checkIfInProgramArea(currentShape.getX_coord())) {
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
