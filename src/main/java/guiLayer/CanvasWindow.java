package guiLayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

import applicationLayer.DomainController;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.GUIListener;
import events.PanelChangeEvent;
import events.UpdateHighlightingEvent;
import guiLayer.commands.CommandHandler;
import guiLayer.commands.DomainMoveCommand;
import guiLayer.commands.ExecuteBlockCommand;
import guiLayer.commands.GuiMoveCommand;
import guiLayer.commands.ResetCommand;
import guiLayer.shapes.CallFunctionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.DefinitionShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import guiLayer.types.GuiSnapshot;
import guiLayer.types.MaskedKeyBag;
import guiLayer.types.MaskedKeyPressed;
import types.BlockType;
import types.ConnectionType;

public class CanvasWindow extends CanvasResource implements GUIListener, Constants {

	private CommandHandler commandHandler;

	private ProgramArea programArea;
	private PaletteArea paletteArea;

	private DomainController domainController;
	private ShapeFactory shapeFactory;

	private Set<Shape> shapesInMovement;
	private Set<Shape> shapeClonesInMovement;

	private Set<String> blocksUnderneath;

	private Shape currentShape = null;
	private Shape movedShape = null;

	private MaskedKeyBag maskedKeyBag;

	private GuiSnapshot currentSnapshot;

	// This Constructor is only used for Testing Purposes. This should NEVER be
	// called upon, and is only public for the purpose of instantiating the
	// CanvasWindow.class in the tests
	public CanvasWindow(GuiSnapshot snapshot, ShapeFactory shapeFactory, CommandHandler commandHandler,
			ProgramArea programArea, DomainController domainController, PaletteArea paletteArea, Timer maskedKeyTimer,
			MaskedKeyBag maskedKeyBag) {
		super("TEST-TITLE");
		this.maskedKeyBag = maskedKeyBag;
		this.maskedKeyTimer = maskedKeyTimer;
		this.currentSnapshot = snapshot;
		this.shapeFactory = shapeFactory;
		this.commandHandler = commandHandler;
		this.programArea = programArea;
		this.shapesInMovement = new HashSet<Shape>();
		this.shapeClonesInMovement = new HashSet<Shape>();
		this.domainController = domainController;
		this.paletteArea = paletteArea;
	}


	private boolean undoMode;

	private Coordinate offsetCurrentShape;

	/**
	 * The debugModus of this canvasWindow.
	 */
	public static DebugModus debugModus = DebugModus.NONE;

	/**
	 * Create a new canvasWindow
	 * 
	 * @param title the title of the canvasWindow
	 * @param dc    the domainController to perform actions on
	 */
	public CanvasWindow(String title, DomainController dc) {
		super(title);

		offsetCurrentShape = new Coordinate(0, 0);

		this.undoMode = false;

		calculateWindowHeight();

		commandHandler = new CommandHandler(this);

		maskedKeyBag = new MaskedKeyBag(false, false);


		super.width = WIDTH;
		this.domainController = dc;
		this.domainController.addGameListener(this);
		setShapeFactory(new ShapeFactory());
		this.programArea = new ProgramArea();
		this.paletteArea = new PaletteArea(getShapeFactory());

		this.blocksUnderneath = new HashSet<String>();
		resetShapesInMovement();

	}

	/**
	 * Sets the height of the window to accommodate all the shapes
	 */
	private void calculateWindowHeight() {
		// Calculate Total Height of the CanvasWindow based on the different type of
		// blocks
		int totalHeight = 200; // 5x40px for the titles in the palette
		for (var type : BlockType.values()) {
			switch (type.cat()) {
			case ACTION:
				totalHeight += 45;
				break;
			case CONDITION:
				totalHeight += 35;
				break;
			case CONTROL:
				totalHeight += 105;
				break;
			case OPERATOR:
				totalHeight += 35;
				break;
			case DEFINITION:
				totalHeight += 105;
				break;
			case CALL:
				totalHeight += 45;
				break;
			default:
				break;
			}
		}

		totalHeight += 25; // Padding at the bottom
		
		super.height = totalHeight;
		System.out.println(totalHeight);
	
	}

	/**
	 * place the shapes in the CurrentSnapshot
	 */
	public void placeShapes() {
		if (currentSnapshot != null) {
			programArea.setHighlightedShapeForExecution(null);
			for (Map.Entry<String, Coordinate> IdAndCoordinate : currentSnapshot.getSavedCoordinates().entrySet()) {

				BlockType type = domainController.getBlockType(IdAndCoordinate.getKey());

				Shape shape = shapeFactory.createShape(IdAndCoordinate.getKey(), type, IdAndCoordinate.getValue());
				determineTotalHeightControlShapes();
				shape.setCoordinatesShape();
				programArea.addToAlreadyFilledInCoordinates(shape);
				shape.defineConnectionTypes();
				programArea.addShapeToProgramArea(shape);
			}
			programArea.clearAlreadyFilledInCoordinates();

			determineTotalHeightControlShapes();
			programArea.placeShapes();

		}
		super.repaint();
	}

	/**
	 * Set the undo flag of this canvasWindow
	 * 
	 * @param undo the undo flag of this canvaswinow
	 */
	public void setUndoMode(boolean undo) {
		undoMode = undo;
	}

	/**
	 * Set the currently used snapshot.
	 * 
	 * @param snapshot The snapshot in use.
	 */
	public void setCurrentSnapshot(GuiSnapshot snapshot) {
		currentSnapshot = snapshot;

	}

	/**
	 * Retrieve the shapeFactory
	 * 
	 * @return the shapeFactory of this canvasWindow
	 */
	private ShapeFactory getShapeFactory() {
		return shapeFactory;
	}

	/**
	 * Retrieve the currentShape used in this canvasWindow, the currentShape is the
	 * shape that's currently being dragged or clicked on by the user.
	 * 
	 * @return the currentShape used in this canvasWindow
	 */
	Shape getCurrentShape() {
		return this.currentShape;
	}

	/**
	 * Retrieve the latest clone of the shapes in movement.
	 * 
	 * @return the latest clone of the shapes in movement.
	 */
	private Set<Shape> getShapeClonesInMovement() {
		return shapeClonesInMovement;
	}

	/**
	 * Retrieve all shapes in movement
	 * 
	 * @return the shapes in movement.
	 */
	Set<Shape> getShapesInMovement() {

		return shapesInMovement;
	}

	/**
	 * Set the shapeFactory
	 * 
	 * @param shapeFactory The shapeFactory to use in this canvasWindow
	 */
	private void setShapeFactory(ShapeFactory shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	/**
	 * Set the currentShape used in this canvasWindow, the currentShape is the shape
	 * that's currently being dragged or clicked on by the user.
	 * 
	 * @param shape The shape to use as currentShape
	 */
	private void setCurrentShape(Shape shape) {
		this.currentShape = shape;
	}

	/**
	 * Add a shape to the shapes in movement and also to the clone of the shapes in
	 * movement.
	 * 
	 * @param shape
	 */
	private void addToShapesInMovement(Shape shape) {
		shapesInMovement.add(shape);
		shapeClonesInMovement.add(shape.clone());
	}

	/**
	 * Reset the shapes in movement and also the clone of the shapes in movement.
	 */
	private void resetShapesInMovement() {
		this.shapesInMovement = new HashSet<Shape>();
		this.shapeClonesInMovement = new HashSet<Shape>();
	}

	/**
	 * Set the field offsetCurrentShape with offset between the mouse and the left
	 * upper corner of the currentShape.
	 * 
	 * @param x_Mouse The x position of the mouse.
	 * @param y_Mouse The y position of the mouse.
	 */
	private void setOffsetMouseAndCurrentShape(int x_Mouse, int y_Mouse) {
		this.offsetCurrentShape = new Coordinate(x_Mouse - getCurrentShape().getX_coord(),
				y_Mouse - getCurrentShape().getY_coord());
	}

	/**
	 * Reset all global variables
	 */
	private void resetGlobalVariables() {
		programArea.setHighlightedShapeForConnections(null);
		this.setCurrentShape(null);
		this.resetShapesInMovement();
	}

	@Override
	protected void paint(Graphics g) {

		Graphics blockrGraphics = g.create(PALETTE_START_X, ORIGIN, PROGRAM_END_X, super.height);
		Graphics gameAreaGraphics = g.create(GAME_START_X, ORIGIN, WIDTH - GAME_START_X, super.height);

		
		
		// only for debugging purposes
		if (debugModus == DebugModus.FILLINGS) {
			for (Coordinate filledInCoordinate : programArea.getAlreadyFilledInCoordinates()) {
				g.drawOval(filledInCoordinate.getX(), filledInCoordinate.getY(), 1, 1);
			}
		}

		// Partition CanvasWindow in different sections
		calculateWindowHeight();
		paletteArea.paint(blockrGraphics);
		
		

		domainController.paint(gameAreaGraphics);

		programArea.draw(blockrGraphics, domainController);

		// Draw the shapes in movement
		blockrGraphics.setColor(Color.black);
		if (getCurrentShape() != null)
			getCurrentShape().draw(blockrGraphics);

		for (Shape shape : getShapesInMovement()) {
			if (shape != getCurrentShape())
				shape.draw(blockrGraphics);
		}

		blockrGraphics.setColor(Color.black);
		if (DebugModus.CONNECTIONS.compareTo(CanvasWindow.debugModus) <= 0) {
			for (Shape shape : getShapesInMovement()) {
				for (var p : shape.getCoordinateConnectionMap().values()) {
					int tempx = p.getX() - 3;
					int tempy = p.getY();
					blockrGraphics.drawOval(tempx, tempy, 6, 6);
				}
			}
		}

	}

	@Override
	protected void handleMouseEvent(int id, int x, int y, int clickCount) {

		if (paletteArea.checkIfInPalette(x) && id == MouseEvent.MOUSE_PRESSED) {
			setCurrentShape(paletteArea.getShapeFromCoordinate(x, y));
			if (getCurrentShape() != null) {
				setOffsetMouseAndCurrentShape(x, y);

				addToShapesInMovement(getCurrentShape());
			}
		}

		if ((id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_PRESSED) && getCurrentShape() != null) {

			int offsetX = x - offsetCurrentShape.getX();
			int offsetY = y - offsetCurrentShape.getY();

			int diffX = offsetX - getCurrentShape().getX_coord();
			int diffy = offsetY - getCurrentShape().getY_coord();

			getCurrentShape().setX_coord(x - offsetCurrentShape.getX());
			getCurrentShape().setY_coord(y - offsetCurrentShape.getY());
			getCurrentShape().defineConnectionTypes();

			for (Shape shapeIM : getShapesInMovement()) {
				shapeIM.defineConnectionTypes();
			}

			programArea.setHighlightedShapeForConnections(determineHighlightShape());

			updateAllShapesInMovementAccordingToChangeOfLeader(diffX, diffy, getCurrentShape());

		}

		if (id == MouseEvent.MOUSE_PRESSED && x > PROGRAM_START_X && x < PROGRAM_END_X) {
			Shape shape = programArea.getShapeFromCoordinate(x, y);
			if (shape != null) {
				blocksUnderneath = domainController.getAllBlockIDsUnderneath(shape.getId());

				for (String shapeId : blocksUnderneath) {
					Shape temp = programArea.getShapesInProgramArea().stream().filter(e -> e.getId().equals(shapeId))
							.findFirst().get();

					if (temp != null) {
						addToShapesInMovement(temp);

						temp.setPreviousX_coord(temp.getX_coord());
						temp.setPreviousY_coord(temp.getY_coord());

					}
				}

				setCurrentShape(shape);

				getCurrentShape().setConnectedVia(ConnectionType.NOCONNECTION, true);

				setOffsetMouseAndCurrentShape(x, y);

				for (Shape shapeIM : getShapesInMovement()) {
					programArea.removeShapeFromProgramArea(shapeIM);
				}

			}
		}

		if (id == MouseEvent.MOUSE_RELEASED) {
			if (getCurrentShape() != null && paletteArea.checkIfInPalette(getCurrentShape().getX_coord())) {
				if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
					setCurrentShape(null);
					resetShapesInMovement();
				} else {
					commandHandler.handle(new DomainMoveCommand(domainController, this,
							new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));
					domainController.removeBlock(getCurrentShape().getId());
				}
			} else if (programArea.checkIfInProgramArea(x) && getCurrentShape() != null) {
				if (programArea.getHighlightedShapeForConnections() != null) {
					// connectedVia of highlightedshape must be persisted.
					programArea.getHighlightedShapeForConnections().persistConnectedVia(true);

					if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						// persist the connectedVia.
						getCurrentShape().persistConnectedVia(true);

						// ADD
						getCurrentShape().clipOn(programArea.getHighlightedShapeForConnections(),
								getCurrentShape().getConnectedVia());

					} else {

						// MOVE
						// if movedshape is null, then clip on is not necessary
						if (movedShape != null) {
							// movedShape connectevia must be persisted, currentshape connectedvia reverted
							// if they are different shapes
							// this order is important because movedShape and currentshape might be the
							// same.
							movedShape.persistConnectedVia(true);
							getCurrentShape().persistConnectedVia(false);

							int originalChangedShapeX = movedShape.getX_coord();
							int originalChangedShapeY = movedShape.getY_coord();
							movedShape.clipOn(programArea.getHighlightedShapeForConnections(),
									movedShape.getConnectedVia());


							// Only if the shape that's being dragged is the moved shape than it should
							// be decoupled from the chain it's in
							if (movedShape == getCurrentShape()) {
							}

							int diffX = movedShape.getX_coord() - originalChangedShapeX;
							int diffy = movedShape.getY_coord() - originalChangedShapeY;
							updateAllShapesInMovementAccordingToChangeOfLeader(diffX, diffy, movedShape);
						}
					}
				}
				getCurrentShape().setCoordinatesShape();
				boolean placeable = programArea.checkIfPlaceable(getCurrentShape());

				if (placeable) {
					if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						commandHandler.handle(new DomainMoveCommand(domainController, this,
								new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));

						if (programArea.getHighlightedShapeForConnections() != null) {
							domainController.addBlock(getCurrentShape().getType(),
									null,
									programArea.getHighlightedShapeForConnections().getId(), getCurrentShape().getConnectedVia());

						} else {

							domainController.addBlock(getCurrentShape().getType(), null, "", ConnectionType.NOCONNECTION);
						}
					} else if (programArea.getHighlightedShapeForConnections() != null) {
						commandHandler.handle(new DomainMoveCommand(domainController, this,
								new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));

						if (programArea.getHighlightedShapeForConnections().getConnectedVia()
								.equals(ConnectionType.NOCONNECTION)) {
							domainController.moveBlock(getCurrentShape().getId(), "", "", ConnectionType.NOCONNECTION);
						} else {
							domainController.moveBlock(getCurrentShape().getId(), movedShape.getId(),
									programArea.getHighlightedShapeForConnections().getId(),
									movedShape.getConnectedVia());

						}
					}
					// decouple chain of blocks from a block

					else if (!domainController.getAllHeadBlocks().contains(getCurrentShape().getId())) {
						// filter out the blocks that already a headblock.
						commandHandler.handle(new DomainMoveCommand(domainController, this,
								new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));

						domainController.moveBlock(getCurrentShape().getId(), "", "", ConnectionType.NOCONNECTION);
					}
					// ONLY GRAPHICAL MOVEMENT:
					else {
						commandHandler.handle(new GuiMoveCommand(this, new GuiSnapshot(getShapeClonesInMovement()),
								new GuiSnapshot(getShapesInMovement())));

						for (Shape shape : getShapesInMovement()) {

							shape.setCoordinatesShape();
							programArea.addShapeToProgramArea(shape);
							programArea.addToAlreadyFilledInCoordinates(shape);
						}
					}
					// NOT PLACEABLE =>
				} else {

					revertMove();
				}
				setCurrentShape(null);
				programArea.setHighlightedShapeForConnections(null);
				movedShape = null;
				offsetCurrentShape = new Coordinate(0, 0);


				resetShapesInMovement();
				blocksUnderneath = new HashSet<String>();
			} else {
				if (getCurrentShape() == null || getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
					setCurrentShape(null);
					resetShapesInMovement();
				} else {
					revertMove();
				}
				setCurrentShape(null);
				programArea.setHighlightedShapeForConnections(null);
				movedShape = null;
				offsetCurrentShape = new Coordinate(0, 0);

				resetShapesInMovement();
				blocksUnderneath = new HashSet<String>();
			}
		}

		repaint();
	}

	/**
	 * Change the position of all shapes in movement all shapes in movement
	 * according to the given difference in x and y, the leader will be excluded
	 * from the position changes.
	 * 
	 * @param diffX         the difference in the x value.
	 * @param diffy         the difference in the y value.
	 * @param excludedShape The shape to be excluded from the changes in position.
	 */
	private void updateAllShapesInMovementAccordingToChangeOfLeader(int diffX, int diffy, Shape excludedShape) {
		for (Shape shape : getShapesInMovement()) {

			if (shape != excludedShape) {
				shape.setX_coord(shape.getX_coord() + diffX);
				shape.setY_coord(shape.getY_coord() + diffy);
			}
		}

	}

	/**
	 * Determine the shape to be highlighted while dragging, this method also
	 * determines which shape connects to the highlighted shape and sets this to
	 * movedShape, this method also sets the correct connection correlated to the
	 * highlighted connection.
	 * 
	 * @return The shape to be highlighted while dragging.
	 */
	private Shape determineHighlightShape() {
		HashMap<ConnectionType, HashMap<Shape, Coordinate>> shapesInProgramAreaConnectionMap = new HashMap<ConnectionType, HashMap<Shape, Coordinate>>();

		for (ConnectionType connection : ConnectionType.values()) {
			shapesInProgramAreaConnectionMap.put(connection, new HashMap<Shape, Coordinate>());
			for (Shape shape : programArea.getShapesInProgramArea().stream()
					.filter(e -> domainController.checkIfConnectionIsOpen(e.getId(), connection,
							getShapesInMovement().stream().map(s -> s.getId()).collect(Collectors.toSet())))
					.collect(Collectors.toSet())) {
				shapesInProgramAreaConnectionMap.get(connection).put(shape,
						shape.getCoordinateConnectionMap().get(connection));
			}
		}

		Shape shape = null;

		for (Shape shapeInMovement : getShapesInMovement()) {
			// The setConnectedVia of all shapes in movement will be reverted
			shapeInMovement.persistConnectedVia(false);


			if (isConnectionOpen(shapeInMovement, ConnectionType.DOWN)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.UP),
							shapeInMovement.getTriggerSet(ConnectionType.DOWN))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.UP).entrySet().stream()
						.filter(e -> shapeInMovement.getTriggerSet(ConnectionType.DOWN).contains(e.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.UP, false);
				shape.setConnectedVia(ConnectionType.DOWN, false);
				movedShape = shapeInMovement;
			} else if (isConnectionOpen(shapeInMovement, ConnectionType.UP)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.DOWN),
							shapeInMovement.getTriggerSet(ConnectionType.UP))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.DOWN).entrySet().stream()
						.filter(p -> shapeInMovement.getTriggerSet(ConnectionType.UP).contains(p.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.DOWN, false);
				shape.setConnectedVia(ConnectionType.UP, false);
				movedShape = shapeInMovement;


			} else if (isConnectionOpen(shapeInMovement, ConnectionType.UP)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.BODY),
							shapeInMovement.getTriggerSet(ConnectionType.UP))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.BODY).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.UP).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.BODY, false);
				shape.setConnectedVia(ConnectionType.UP, false);
				movedShape = shapeInMovement;


			} else if (isConnectionOpen(shapeInMovement, ConnectionType.LEFT)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.CONDITION),
							shapeInMovement.getTriggerSet(ConnectionType.LEFT))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.CONDITION).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.LEFT).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.CONDITION, false);
				shape.setConnectedVia(ConnectionType.LEFT, false);
				movedShape = shapeInMovement;


			} else if (isConnectionOpen(shapeInMovement, ConnectionType.LEFT)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.OPERAND),
							shapeInMovement.getTriggerSet(ConnectionType.LEFT))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.OPERAND).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.LEFT).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.OPERAND, false);
				shape.setConnectedVia(ConnectionType.LEFT, false);
				movedShape = shapeInMovement;
			} else if (isConnectionOpen(shapeInMovement, ConnectionType.CONDITION)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT),
							shapeInMovement.getTriggerSet(ConnectionType.CONDITION))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.CONDITION).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.LEFT, false);
				shape.setConnectedVia(ConnectionType.CONDITION, false);
				movedShape = shapeInMovement;
			} else if (isConnectionOpen(shapeInMovement, ConnectionType.OPERAND)
					&& isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT),
							shapeInMovement.getTriggerSet(ConnectionType.OPERAND))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.OPERAND).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.LEFT, false);
				shape.setConnectedVia(ConnectionType.OPERAND, false);
				movedShape = shapeInMovement;
			}
		}

		return shape;
	}

	/**
	 * Check if a connection is open.
	 * 
	 * @param shape      The shape to check if it's connection is open.
	 * @param connection The connection to check if it's open.
	 * @return a flag indicating if a connection is open and can be used to try and
	 *         make a connection.
	 */
	private Boolean isConnectionOpen(Shape shape, ConnectionType connection) {
		return shape.getId().equals(PALETTE_BLOCK_IDENTIFIER)
				|| domainController.checkIfConnectionIsOpen(shape.getId(), connection, null)
				|| (shape == getCurrentShape()
						&& (connection == ConnectionType.UP || connection == ConnectionType.LEFT));
	}

	/**
	 * Check if there is an overlap between a triggerSet of a certain connection and
	 * a triggerSet of a shape in the programArea on another connection.
	 * 
	 * @param shapesInProgramAreaConnectionMap A Map where all shapes and their
	 *                                         coordinate are set, this map contains
	 *                                         all triggers of a certain connection.
	 * @param connectionTriggerSet             a set with all coordinates of a
	 *                                         certain connection to try and fit to
	 *                                         one of the shapes in the programArea.
	 * @return a flag indicating if there is a connection between 2 triggerSets.
	 */
	private boolean isConnectionPresent(Map<Shape, Coordinate> shapesInProgramAreaConnectionMap,
			Set<Coordinate> connectionTriggerSet) {
		for (Map.Entry<Shape, Coordinate> s : shapesInProgramAreaConnectionMap.entrySet()) {
			if (connectionTriggerSet.contains(s.getValue())) {
				return true;
			}

		}
		return false;
	}

	private Timer maskedKeyTimer = null;

	/**
	 * Revert a move in the GUI. The shapes will be placed on their last recorded
	 * position.
	 */
	private void revertMove() {
		for (Iterator<Shape> iterator = getShapesInMovement().iterator(); iterator.hasNext();) {
			Shape shape = (Shape) iterator.next();
	
			if (shape.getPreviousX_coord() == INVALID_COORDINATE || shape.getPreviousY_coord() == INVALID_COORDINATE) {
				iterator.remove();
			} else {
				shape.setX_coord(shape.getPreviousX_coord());
				shape.setY_coord(shape.getPreviousY_coord());
	
				shape.setCoordinatesShape();
				programArea.addToAlreadyFilledInCoordinates(shape);
				shape.defineConnectionTypes();
				programArea.addShapeToProgramArea(shape);
			}
	
		}
	
		getCurrentShape().setConnectedVia(getCurrentShape().getPreviouslyConnectedVia(), true);
	}


	@Override
	protected void handleKeyEvent(int id, int keyCode, char keyChar) {
		if (id == KeyEvent.KEY_PRESSED) {
			if (keyCode == KeyEvent.VK_CONTROL) {
				if (maskedKeyTimer != null) {
					maskedKeyTimer.cancel();
					maskedKeyBag.pressShift(false);
				}
				maskedKeyTimer = new Timer();
				maskedKeyTimer.schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);
				maskedKeyBag.pressCtrl(true);
			}
			if (keyCode == KeyEvent.VK_SHIFT) {
				if (maskedKeyTimer != null) {
					maskedKeyTimer.cancel();
				}
				maskedKeyTimer = new Timer();
				maskedKeyTimer.schedule(new MaskedKeyPressed(maskedKeyBag, true), MASKEDKEY_DURATION);
				maskedKeyBag.pressShift(true);
			}
			if (keyCode == KeyEvent.VK_F5) {
				// F5-Key
				if (domainController.isGameExecutionUseful()) {
					commandHandler.handle(new ExecuteBlockCommand(domainController));
				}
			}
			if (keyCode == KeyEvent.VK_ESCAPE) {
				// ESC-Key
				if (domainController.isGameResetUseful()) {
					commandHandler.handle(new ResetCommand(domainController));
				}
			}
			if (keyCode == KeyEvent.VK_Z) {
				if (maskedKeyBag.isCtrlPressed() && !maskedKeyBag.isShiftPressed()) {
					commandHandler.undo();
					if (maskedKeyTimer != null) {
						maskedKeyTimer.cancel();
						maskedKeyBag.pressShift(false);
					}
				}
				if (maskedKeyBag.isCtrlPressed() && maskedKeyBag.isShiftPressed()) {
					commandHandler.redo();
					if (maskedKeyTimer != null) {
						maskedKeyTimer.cancel();
					}
					maskedKeyTimer = new Timer();
					maskedKeyTimer.schedule(new MaskedKeyPressed(maskedKeyBag, true), MASKEDKEY_DURATION);
					maskedKeyBag.pressShift(true);
				}
			}

			if (keyCode == KeyEvent.VK_U) {
				commandHandler.undo();
			}
			if (keyCode == KeyEvent.VK_R) {
				commandHandler.redo();
			}
		}
		if (id == KeyEvent.KEY_TYPED) {

			if (keyChar == 'd') {
				// d key 68
				debugModus = debugModus.getNext();
				repaint();
			}
		}

	}

	/**
	 * Map a set of ids to a set of new shapes with coordinates and height taken
	 * from the currentSnapshot.
	 * 
	 * @param ids the id's to map to shapes
	 * @return All the ids mapped to shapes.
	 */
	private Set<Shape> mapSetOfIdsToShapes(Set<String> ids) {
		HashSet<Shape> shapes = new HashSet<Shape>();
		HashMap<String, Coordinate> coordinates = new HashMap<String, Coordinate>();
		HashMap<String, Integer> heights = new HashMap<String, Integer>();
		if (currentSnapshot != null) {
			coordinates.putAll(currentSnapshot.getSavedCoordinates());
			heights.putAll(currentSnapshot.getSavedHeights());
		}
		System.out.println(ids);

		for (String id : ids) {
			BlockType type = domainController.getBlockType(id);
			Shape shape = shapeFactory.createShape(id, type, coordinates.get(id));
			if (heights.containsKey(id)) {
				shape.setHeight(heights.get(id));
			}
			shapes.add(shape);
		}
		return shapes;
	}

	/**
	 * Determine the height of all the controlshapes
	 */
	private void determineTotalHeightControlShapes() {
		for (Shape shape : programArea.getShapesInProgramArea().stream().filter(s -> s instanceof ControlShape)
				.collect(Collectors.toSet())) {
			if (shape != null && domainController.isBlockPresent(shape.getId())) {
				shape.determineTotalHeight(mapSetOfIdsToShapes(domainController.getAllBlockIDsInBody(shape.getId())));
				System.out.println("ShapeDetermine " + shape.getId() + shape.getHeight());

				commandHandler.setHeight(shape.getId(), shape.getHeight());
			}
		}
	}

	/**
	 * Update the position of all shapes after the height of controlshapes has been
	 * changed.
	 */
	private void updatePositionOfAllShapesAccordingToChangesOfTheControlShapes() {
		Set<ControlShape> changedControlShapes = programArea.getAllChangedControlShapes();

		for (ControlShape c : changedControlShapes) {
			Set<Shape> shapesToMove = domainController.getAllBlockIDsBelowCertainBlock(c.getId()).stream()
					.filter(s -> !s.equals(c.getId())).map(s -> programArea.getShapeById(s))
					.collect(Collectors.toSet());
			if (c.getHeightDiff() < 0 && undoMode) {
				shapesToMove.removeIf(s -> currentSnapshot.getSavedCoordinates().containsKey(s.getId()));
			}

			for (Shape shape : shapesToMove) {
				if (shape != null) {
					programArea.removeFromAlreadyFilledInCoordinates(shape);
					shape.setY_coord(shape.getY_coord() + c.getHeightDiff());
				}
			}
		}

	}

	@Override
	public void onPanelChangedEvent(PanelChangeEvent event) {
		paletteArea.setPaletteVisible(event.isShown());
		super.repaint();
	}

	@Override
	public void onUpdateHighlightingEvent(UpdateHighlightingEvent event) {
		try {
			programArea.setHighlightedShapeForExecution(programArea.getShapesInProgramArea().stream()
					.filter(e -> e.getId() == event.getHighlightBlockId()).findFirst().get());
		} catch (Exception e) {
			programArea.setHighlightedShapeForExecution(null);
		} finally {
			super.repaint();
		}
	}

	@Override
	public void onBlockAdded(BlockAddedEvent event) {
		Coordinate newCoordinate;
		if (currentSnapshot.getSavedCoordinates().containsKey(event.getAddedBlockID())) {
			newCoordinate = currentSnapshot.getSavedCoordinates().get(event.getAddedBlockID());
		} else if (currentSnapshot.getSavedCoordinates().containsKey(PALETTE_BLOCK_IDENTIFIER)) {
			newCoordinate = currentSnapshot.getSavedCoordinates().get(PALETTE_BLOCK_IDENTIFIER);
		} else {
			// If there were no mistakes in other parts of the code this doesn't happen.
			newCoordinate = new Coordinate(0, 0);
		}

		Shape toAdd = shapeFactory.createShape(event.getAddedBlockID(), event.getAddedBlockType(), newCoordinate);

		// Update the ID of the snapshot in the executionStack
		commandHandler.setAddedId(event.getAddedBlockID());

		System.out.println("Block ADDED: " + toAdd.getId());

		programArea.addShapeToProgramArea(toAdd);
		programArea.clearAlreadyFilledInCoordinates();

		determineTotalHeightControlShapes();
		updatePositionOfAllShapesAccordingToChangesOfTheControlShapes();

		programArea.placeShapes();

		// Reset global variables
		resetGlobalVariables();
		super.repaint();
	}

	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) {
		// handle add to programArea in theory
		for (Shape movedShape : mapSetOfIdsToShapes(event.getChangedBlocks())) {

			movedShape.setCoordinatesShape();
			movedShape.defineConnectionTypes();
			programArea.addShapeToProgramArea(movedShape);

		}

		programArea.clearAlreadyFilledInCoordinates();

		// update internals of controlshapes
		determineTotalHeightControlShapes();
		// handle add to programArea in practice, all coordinates etc are set.

		updatePositionOfAllShapesAccordingToChangesOfTheControlShapes();

		programArea.placeShapes();

		resetGlobalVariables();

		super.repaint();
	}

	@Override
	public void onBlockRemoved(BlockRemovedEvent event) {
		Set<Shape> shapesToBeRemovedFromProgramArea = programArea.getShapesInProgramArea().stream()
				.filter(s -> s.getId().equals(event.getRemovedBlockId())).collect(Collectors.toSet());


		System.out.println(shapesToBeRemovedFromProgramArea);

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			programArea.removeShapeFromProgramArea(shape);
		}
		programArea.clearAlreadyFilledInCoordinates();

		// update internals of controlshapes
		determineTotalHeightControlShapes();
		updatePositionOfAllShapesAccordingToChangesOfTheControlShapes();

		// handle add to programArea in practice, all coordinates etc are set.
		programArea.placeShapes();

		resetGlobalVariables();
		super.repaint();
	}

}