package guiLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import applicationLayer.DomainController;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import types.BlockType;

/**
 * ProgramArea, the programArea describes the area where all blocks are placed
 * for the execution of the game.
 * 
 * @version 0.1
 * @author group17
 *
 */
public class ProgramArea implements Constants {

	private HashSet<Coordinate> alreadyFilledInCoordinates;
	private Shape highlightedShape = null;

	private HashSet<Shape> shapesInProgramArea;
	private Shape highlightedShapeForExecution;
	private int programAndGameBorder= INITIAL_PROGRAM_GAME_BORDER_X;

	/**
	 * Create a new ProgramArea
	 */
	public ProgramArea() {
		alreadyFilledInCoordinates = new HashSet<Coordinate>();
		this.shapesInProgramArea = new HashSet<Shape>();
	}

	
	/**
	 * @param programAndGameBorder the programAndGameBorder to set
	 */
	public void setProgramAndGameBorder(int programAndGameBorder) {
		this.programAndGameBorder = programAndGameBorder;
	}
	/**
	 * Retrieve a set with all the shapes in the programArea
	 * 
	 * @return a set with all the shapes in the programArea
	 */
	public Set<Shape> getShapesInProgramArea() {
		HashSet<Shape> copy = new HashSet<Shape>(shapesInProgramArea);
		return copy;
	}

	/**
	 * Remove all alreadyFilledCoordinates
	 */
	public void clearAlreadyFilledInCoordinates() {
		alreadyFilledInCoordinates.clear();
	}

	/**
	 * Add a shape to the programArea, if the id of the given shape is already
	 * present in the programArea that shape will be replaced with the given shape.
	 * If a shape is added to the programArea it's not yet added to the set of
	 * AlreadyFilledCoordinates
	 * 
	 * @param shape the shape to add to the programArea. If the shape is null
	 *              nothing happens.
	 */
	public void addShapeToProgramArea(Shape shape) {
		if (shape != null) {
			Shape presentShape = getShapeById(shape.getId());
			if (presentShape != null) {
				this.shapesInProgramArea.remove(presentShape);
			}

			this.shapesInProgramArea.add(shape);

		}
	}

	/**
	 * Remove the given shape from the programArea and from the
	 * alreadyFilledCoordinates.
	 * 
	 * @param shape
	 */
	public void removeShapeFromProgramArea(Shape shape) {
		this.shapesInProgramArea.remove(shape);
		for (Coordinate pair : shape.getCoordinatesShape()) {
			alreadyFilledInCoordinates.remove(pair);
		}
	}

	/**
	 * Check if the given x coordinate is present in the programArea.
	 * 
	 * @param x the x coordinate to check
	 * @return a flag indicating if the given x coordinate is present in the
	 *         programArea.
	 */
	public boolean checkIfInProgramArea(int x) {
		return x > PROGRAM_START_X && x < programAndGameBorder;
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
		Optional<Shape> shape = this.getShapesInProgramArea().stream()
				.filter(e -> e.getCoordinatesShape().contains(new Coordinate(x, y))).findFirst();

		if (shape.isPresent()) {
			return shape.get();
		} else {
			return null;
		}
	}

	/**
	 * Retrieve a shape by its ID
	 * 
	 * @param id the id to get the associated shape of
	 * @return The shape associated with the given ID or null if no shape with the
	 *         given ID was found.
	 */
	public Shape getShapeById(String id) {
		Optional<Shape> shape = this.getShapesInProgramArea().stream().filter(e -> e.getId().equals(id)).findFirst();
		if (shape.isPresent()) {
			return shape.get();
		} else {
			return null;
		}
	}

	/**
	 * Check if the given shape can be placed in the programArea.
	 * 
	 * @param shapeToPlace the shape to place in the programArea
	 * @return a flag indicating if the shape can be placed in the programArea.
	 */
	public boolean checkIfPlaceable(Shape shapeToPlace) {
		boolean placeable = !((shapeToPlace.getCoordinatesShape().stream()
				.anyMatch(i -> this.alreadyFilledInCoordinates.contains(i))))
				&& shapeToPlace.getX_coord() + shapeToPlace.getWidth() < programAndGameBorder;

		if ((shapeToPlace.getType() == BlockType.IF || shapeToPlace.getType() == BlockType.WHILE)
				&& (getHighlightedShapeForConnections() != null
						&& (getHighlightedShapeForConnections().getType() == BlockType.IF
								|| getHighlightedShapeForConnections().getType() == BlockType.WHILE))) {
			placeable = true;
		}
		if (shapeToPlace instanceof ControlShape && checkIfInProgramArea(shapeToPlace.getX_coord())) {
			placeable = true;
		}
		return placeable;
	}

	/**
	 * Retrieve a set with all the coordinates that are already occupied by another shape.
	 * 
	 * @return a set with all the coordinates that are already occupied by another shape
	 */
	public Set<Coordinate> getAlreadyFilledInCoordinates() {
		return alreadyFilledInCoordinates;
	}

	/**
	 * Add the coordinates of the shape to the already filled coordinates, if the id
	 * of the given shape is already present the coordinates of that shape will be
	 * replaced with the coordinates of the given shape.
	 * 
	 * @param shape the shape to add to the already filled coordinates
	 */
	public void addToAlreadyFilledInCoordinates(Shape shape) {
		Shape presentShape = getShapeById(shape.getId());
		if (presentShape != null) {
			removeFromAlreadyFilledInCoordinates(presentShape);
		}
		getAlreadyFilledInCoordinates().addAll(shape.getCoordinatesShape());
	}

	/**
	 * Remove all coordinates from the given shape from the already filled
	 * Coordinates in the programArea
	 * 
	 * @param shape the shape to remove the coordinates from.
	 */
	public void removeFromAlreadyFilledInCoordinates(Shape shape) {
		getAlreadyFilledInCoordinates().removeAll(shape.getCoordinatesShape());
	}

	/**
	 * Retrieve the highlighted shape for connections
	 * 
	 * @return The highlighted shape for connections
	 */
	public Shape getHighlightedShapeForConnections() {
		return highlightedShape;
	}

	/**
	 * Set the Highlighted shape to which another shape is trying to connect.
	 * 
	 * @param highlightedShape The shape that needs to be the highlighted shape for
	 *                         connections.
	 */
	public void setHighlightedShapeForConnections(Shape highlightedShape) {
		this.highlightedShape = highlightedShape;
	}

	/**
	 * Draw the programArea
	 * 
	 * @param blockrGraphics The graphics object to draw the programArea on
	 * @param controller     the domainController, this controller is only used to
	 *                       ask the connectionStatus to show the correct
	 *                       debugInformation, if the controller is null, the
	 *                       debugInformation regarding the status of a connection
	 *                       won't be available
	 */
	void draw(Graphics blockrGraphics, DomainController controller) {
		// draw all shapes in shapesInProgramArea
		if (getShapesInProgramArea() != null && !getShapesInProgramArea().isEmpty()) {
			getShapesInProgramArea().stream().forEach(((Shape e) -> e.draw(blockrGraphics)));
		}

		if (getHighlightedShapeForExecution() != null) {
			drawHighlightedBLUE(blockrGraphics, getHighlightedShapeForExecution());
		}

		if (getHighlightedShapeForConnections() != null) {
			drawHighlightedGREEN(blockrGraphics, getHighlightedShapeForConnections());
		}
		// only for debugging purposes
		if (DebugModus.CONNECTIONS.compareTo(CanvasWindow.debugModus) <= 0) {
			for (Shape shape : getShapesInProgramArea()) {
				for (var p : shape.getCoordinateConnectionMap().entrySet()) {
					int tempx = p.getValue().getX() - 3;
					int tempy = p.getValue().getY();
					blockrGraphics.setColor(Color.black);
					blockrGraphics.drawOval(tempx, tempy, 6, 6);

					if (DebugModus.CONNECTIONSTATUS.compareTo(CanvasWindow.debugModus) <= 0 && controller != null) {

						if (controller.checkIfConnectionIsOpen(shape.getId(), p.getKey(), null)) {
							blockrGraphics.setColor(Color.green);
						} else {
							blockrGraphics.setColor(Color.red);

						}

						blockrGraphics.fillOval(tempx, tempy, 6, 6);
						blockrGraphics.setColor(Color.black);
					}
					
					// only for debugging purposes
					if (CanvasWindow.debugModus == DebugModus.FILLINGS) {
						for (Coordinate filledInCoordinate : getAlreadyFilledInCoordinates()) {
							blockrGraphics.drawOval(filledInCoordinate.getX(), filledInCoordinate.getY(), 1, 1);
						}
					}
				}
			}

		}
	}

	/**
	 * Draw a shape in blue
	 * 
	 * @param g     The graphics object to draw on
	 * @param shape the shape to draw
	 */
	private void drawHighlightedBLUE(Graphics g, Shape shape) {
		g.setColor(Color.BLUE);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	/**
	 * Draw a shape in green.
	 * 
	 * @param g     The graphics object to draw on
	 * @param shape the shape to draw
	 */
	private void drawHighlightedGREEN(Graphics g, Shape shape) {
		g.setColor(Color.GREEN);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	/**
	 * Retrieve the shape that's highlighted for execution
	 * 
	 * @return the shape that's highlighted for execution
	 */
	Shape getHighlightedShapeForExecution() {
		return highlightedShapeForExecution;
	}

	/**
	 * Set the shape highlighted for execution
	 * 
	 * @param shape The shape highlighted for execution, if the given shape is null
	 *              the highlighting will be cleared.
	 */
	public void setHighlightedShapeForExecution(Shape shape) {
		this.highlightedShapeForExecution = shape;

	}

	/**
	 * Retrieve a all ControlShapes in the programArea of which the height has
	 * changed.
	 * 
	 * @return a set with all ControlShapes in the programArea of which the height
	 *         has changed.
	 */
	public Set<ControlShape> getAllChangedControlShapes() {
		return shapesInProgramArea.stream().filter(s -> (s instanceof ControlShape) && s.getHeightDiff() != 0)
				.map(s -> (ControlShape) s).collect(Collectors.toSet());
	}

	/**
	 * Set the shapes in the programArea, all shapes without coordinates are set
	 * correctly, all connectionTypes are updated correctly.
	 */
	void placeShapes() {
		for (Shape shape : getShapesInProgramArea()) {
			shape.setCoordinatesShape();
			addToAlreadyFilledInCoordinates(shape);
			shape.defineConnectionTypes();
		}
	}

}
