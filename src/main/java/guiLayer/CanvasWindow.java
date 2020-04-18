package guiLayer;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.stream.Collectors;

import applicationLayer.DomainController;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.GUIListener;
import events.PanelChangeEvent;
import events.UpdateHighlightingEvent;
import guiLayer.commands.BlockCommand;
import guiLayer.commands.CommandHandler;
import guiLayer.commands.DomainMoveCommand;
import guiLayer.commands.ExecuteBlockCommand;
import guiLayer.commands.GameWorldCommand;
import guiLayer.commands.GuiMoveCommand;
import guiLayer.commands.ResetCommand;
import guiLayer.shapes.ActionShape;
import guiLayer.shapes.ConditionShape;
import guiLayer.shapes.ControlShape;
import guiLayer.shapes.Shape;
import guiLayer.shapes.ShapeFactory;
import guiLayer.shapes.UnaryOperatorShape;
import guiLayer.types.Constants;
import guiLayer.types.Coordinate;
import guiLayer.types.DebugModus;
import guiLayer.types.GuiSnapshot;
import guiLayer.types.MaskedKeyBag;
import guiLayer.types.MaskedKeyPressed;
import guiLayer.types.Pair;
import types.BlockType;
import types.ConnectionType;

public class CanvasWindow extends CanvasResource implements GUIListener, Constants {
	private static final int MASKEDKEY_DURATION = 500;

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

	private int x_offsetCurrentShape = 0;
	private int y_offsetCurrentShape = 0;

	private MaskedKeyBag maskedKeyBag;

	public static DebugModus debugModus = DebugModus.NONE;

	// methods of CanvasResource that need to be overridden:

	public CanvasWindow(String title, DomainController dc) {
		super(title);

		// Calculate Total Height of the CanvasWindow based on the different type of
		// blocks
		int totalHeight = 160; // 4x40px for the titles in the palette
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
			default:
				break;
			}
		}

		totalHeight += 25; // Padding at the bottom

		commandHandler = new CommandHandler();

		maskedKeyBag = new MaskedKeyBag(false, false);

		super.height = totalHeight;
		System.out.println(totalHeight);
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
	 * place the shapes in the CurrentSnapshot
	 */
	public void placeShapes() {
		if (currentSnapshot != null) {
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
			for (Shape shape : programArea.getShapesInProgramArea()) {
				shape.setCoordinatesShape();
				programArea.addToAlreadyFilledInCoordinates(shape);
				shape.defineConnectionTypes();
			}

		}
	}

	public void setShapesInMovement(Set<Shape> shapes) {
		shapesInMovement = shapes;
		shapeClonesInMovement = new HashSet<Shape>(shapes);
	}

	private Set<Shape> getShapeClonesInMovement() {
		return shapeClonesInMovement;
	}

	public Set<Shape> getShapesInMovement() {
		return shapesInMovement;
	}

	public void addToShapesInMovement(Shape shape) {
		shapesInMovement.add(shape);
		shapeClonesInMovement.add(shape.clone());
	}

	public void resetShapesInMovement() {
		this.shapesInMovement = new HashSet<Shape>();
		this.shapeClonesInMovement = new HashSet<Shape>();
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

		paletteArea.paint(blockrGraphics);

		domainController.paint(gameAreaGraphics);
//		gameArea.draw(gameAreaGraphics);

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

	private int[] calculateOffsetMouse(int x_Mouse, int y_Mouse, int x_Shape, int y_Shape) {
		int[] returnValue = new int[2];
		returnValue[0] = x_Mouse - x_Shape;
		returnValue[1] = y_Mouse - y_Shape;
		return returnValue;
	}

	@Override
	protected void handleMouseEvent(int id, int x, int y, int clickCount) {

		if (paletteArea.checkIfInPalette(x) && id == MouseEvent.MOUSE_PRESSED) {
			setCurrentShape(paletteArea.getShapeFromCoordinate(x, y));
			if (getCurrentShape() != null) {
				var temp = calculateOffsetMouse(x, y, getCurrentShape().getX_coord(), getCurrentShape().getY_coord());
				this.x_offsetCurrentShape = temp[0];
				this.y_offsetCurrentShape = temp[1];
				addToShapesInMovement(getCurrentShape());
			}
		}

		if ((id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_PRESSED) && getCurrentShape() != null) {

			int offsetX = x - x_offsetCurrentShape;
			int offsetY = y - y_offsetCurrentShape;

			int diffX = offsetX - getCurrentShape().getX_coord();
			int diffy = offsetY - getCurrentShape().getY_coord();

			getCurrentShape().setX_coord(x - x_offsetCurrentShape);
			getCurrentShape().setY_coord(y - y_offsetCurrentShape);
			getCurrentShape().defineConnectionTypes();

			for (Shape shapeIM : getShapesInMovement()) {
				shapeIM.defineConnectionTypes();
			}

			programArea.setHighlightedShape(determineHighlightShape());

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

				var mouseOffset = calculateOffsetMouse(x, y, getCurrentShape().getX_coord(),
						getCurrentShape().getY_coord());

				setX_offsetCurrentShape(mouseOffset[0]);

				setY_offsetCurrentShape(mouseOffset[1]);
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
				if (programArea.getHighlightedShape() != null) {
					// connectedVia of highlightedshape must be persisted.
					programArea.getHighlightedShape().persistConnectedVia(true);

					if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						// persist the connectedVia.
						getCurrentShape().persistConnectedVia(true);

						// ADD
						getCurrentShape().clipOn(programArea.getHighlightedShape(),
								getCurrentShape().getConnectedVia());

					} else {

						// MOVE
						// if movedshape is null, then clip on is not necessary
						if (movedShape != null) {
							// movedShape connectevia must be persisted, currentshape connectedvia reverted
							// if they are different shapes
							// this order is important.
							movedShape.persistConnectedVia(true);
							getCurrentShape().persistConnectedVia(false);

							int originalChangedShapeX = movedShape.getX_coord();
							int originalChangedShapeY = movedShape.getY_coord();
//							System.out.println("BeforeClipon X: " + originalChangedShapeX);
//							System.out.println("BeforeClipon Y: " + originalChangedShapeY);

							movedShape.clipOn(programArea.getHighlightedShape(), movedShape.getConnectedVia());
//							System.out.println("AfterClipon X: " + movedShape.getX_coord());
//							System.out.println("AfterClipon Y: " + movedShape.getY_coord());

							// Only if the shape that's being dragged is the moved shape than it should
							// be decoupled from the chain it's in
							if (movedShape == getCurrentShape()) {
								// decoupleFromShape(movedShape);
							}

							int diffX = movedShape.getX_coord() - originalChangedShapeX;
							int diffy = movedShape.getY_coord() - originalChangedShapeY;
//							System.out.println("diffX: " + diffX);
//							System.out.println("diffY: " + diffy);

							updateAllShapesInMovementAccordingToChangeOfLeader(diffX, diffy, movedShape);
						}

					}
				}

				getCurrentShape().setCoordinatesShape();
				boolean placeable = programArea.checkIfPlaceable(getCurrentShape().getCoordinatesShape(),
						getCurrentShape());

				if (placeable) {
					if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						commandHandler.handle(new DomainMoveCommand(domainController, this,
								new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));

						if (programArea.getHighlightedShape() != null) {
							domainController.addBlock(getCurrentShape().getType(),
									programArea.getHighlightedShape().getId(), getCurrentShape().getConnectedVia());
						} else {

							domainController.addBlock(getCurrentShape().getType(), "", ConnectionType.NOCONNECTION);
						}
					}

					// DOMAIN MOVEMENT, IF THERE IS A CHANGE IN CURRENTSHAPE.GETCONNECTIONVIA,
					// EXCEPTIONS CATCHEN!!
					else if (programArea.getHighlightedShape() != null) {
						commandHandler.handle(new DomainMoveCommand(domainController, this,
								new GuiSnapshot(getShapeClonesInMovement()), new GuiSnapshot(getShapesInMovement())));

						if (programArea.getHighlightedShape().getConnectedVia().equals(ConnectionType.NOCONNECTION)) {
							domainController.moveBlock(getCurrentShape().getId(), "", "", ConnectionType.NOCONNECTION);
						} else {
							domainController.moveBlock(getCurrentShape().getId(), movedShape.getId(),
									programArea.getHighlightedShape().getId(), movedShape.getConnectedVia());
						}
					}
					// decouple chain of blocks from a block
					// Wanneer is decouple chain nodig?

//					else if (getCurrentShape().getConnectedVia() != ConnectionType.NOCONNECTION
//							|| (getCurrentShape().getConnectedVia() == ConnectionType.NOCONNECTION
//									&& getCurrentShape().getPreviouslyConnectedVia() != ConnectionType.NOCONNECTION)
//									&& !domainController.getAllHeadBlocks().contains(getCurrentShape().getId())) {

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
				programArea.setHighlightedShape(null);
				movedShape = null;
				setX_offsetCurrentShape(0);
				setY_offsetCurrentShape(0);

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
				programArea.setHighlightedShape(null);
				movedShape = null;
				setX_offsetCurrentShape(0);
				setY_offsetCurrentShape(0);
				resetShapesInMovement();
				blocksUnderneath = new HashSet<String>();
			}
		}

		repaint();
	}

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



	private void updateAllShapesInMovementAccordingToChangeOfLeader(int diffX, int diffy, Shape excludedShape) {
		for (Shape shape : getShapesInMovement()) {

			if (shape != excludedShape) {
				shape.setX_coord(shape.getX_coord() + diffX);
				shape.setY_coord(shape.getY_coord() + diffy);
			}
		}

	}

	private Shape determineHighlightShape() {
		HashMap<ConnectionType, HashMap<Shape, Coordinate>> shapesInProgramAreaConnectionMap = new HashMap<ConnectionType, HashMap<Shape, Coordinate>>();

		for (ConnectionType connection : ConnectionType.values()) {
			shapesInProgramAreaConnectionMap.put(connection, new HashMap<Shape, Coordinate>());
			for (Shape shape : programArea.getShapesInProgramArea().stream().filter(e -> domainController.checkIfConnectionIsOpen(e.getId(), connection, getShapesInMovement().stream().map(s->s.getId()).collect(Collectors.toSet())))
					.collect(Collectors.toSet())) {
				shapesInProgramAreaConnectionMap.get(connection).put(shape,
						shape.getCoordinateConnectionMap().get(connection));
			}
		}

		Shape shape = null;
		for (Shape shapeInMovement : getShapesInMovement()) {
			// The setConnectedVia of all shapes in movement will be reverted
			shapeInMovement.persistConnectedVia(false);

			if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.UP),
					shapeInMovement.getTriggerSet(ConnectionType.DOWN))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.UP).entrySet().stream()
						.filter(e -> shapeInMovement.getTriggerSet(ConnectionType.DOWN).contains(e.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.UP, false);
				shape.setConnectedVia(ConnectionType.DOWN, false);
				movedShape = shapeInMovement;
			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.DOWN),
					shapeInMovement.getTriggerSet(ConnectionType.UP))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.DOWN).entrySet().stream()
						.filter(p -> shapeInMovement.getTriggerSet(ConnectionType.UP).contains(p.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.DOWN, false);
				shape.setConnectedVia(ConnectionType.UP, false);
				movedShape = shapeInMovement;

			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.BODY),
					shapeInMovement.getTriggerSet(ConnectionType.UP))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.BODY).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.UP).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.BODY, false);
				shape.setConnectedVia(ConnectionType.UP, false);
				movedShape = shapeInMovement;

			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.CONDITION),
					shapeInMovement.getTriggerSet(ConnectionType.LEFT))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.CONDITION).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.LEFT).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.CONDITION, false);
				shape.setConnectedVia(ConnectionType.LEFT, false);
				movedShape = shapeInMovement;

			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.OPERAND),
					shapeInMovement.getTriggerSet(ConnectionType.LEFT))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.OPERAND).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.LEFT).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.OPERAND, false);
				shape.setConnectedVia(ConnectionType.LEFT, false);
				movedShape = shapeInMovement;
			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT),
					shapeInMovement.getTriggerSet(ConnectionType.CONDITION))) {
				shape = shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT).entrySet().stream()
						.filter(q -> shapeInMovement.getTriggerSet(ConnectionType.CONDITION).contains(q.getValue()))
						.findFirst().get().getKey();
				// The connectedvia of the determinedShape must be reverted.
				shape.persistConnectedVia(false);

				shapeInMovement.setConnectedVia(ConnectionType.LEFT, false);
				shape.setConnectedVia(ConnectionType.CONDITION, false);
				movedShape = shapeInMovement;
			} else if (isConnectionPresent(shapesInProgramAreaConnectionMap.get(ConnectionType.LEFT),
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

	private boolean isConnectionPresent(HashMap<Shape, Coordinate> shapesInProgramAreaUpMap,
			HashSet<Coordinate> connectionTriggerSetDOWN) {
		for (Map.Entry<Shape, Coordinate> s : shapesInProgramAreaUpMap.entrySet()) {
			if (connectionTriggerSetDOWN.contains(s.getValue())) {
				return true;
			}

		}
		return false;
	}

	private Timer maskedKeyTimer = null;

	private GuiSnapshot currentSnapshot;

	@Override
	protected void handleKeyEvent(int id, int keyCode, char keyChar) {
		if (id == KeyEvent.KEY_PRESSED) {

			if (keyCode == KeyEvent.VK_CONTROL) {
				if (maskedKeyTimer != null) {
					maskedKeyTimer.cancel();
					maskedKeyBag.setShift(false);
				}
				maskedKeyTimer = new Timer();
				maskedKeyTimer.schedule(new MaskedKeyPressed(maskedKeyBag, false), MASKEDKEY_DURATION);
				maskedKeyBag.setCtrl(true);
			}
			if (keyCode == KeyEvent.VK_SHIFT) {
				if (maskedKeyTimer != null) {
					maskedKeyTimer.cancel();
				}
				maskedKeyTimer = new Timer();
				maskedKeyTimer.schedule(new MaskedKeyPressed(maskedKeyBag, true), MASKEDKEY_DURATION);
				maskedKeyBag.setShift(true);
			}
			if (keyCode == KeyEvent.VK_F5) {
				// F5-Key
				commandHandler.handle(new ExecuteBlockCommand(domainController));
			}
			if (keyCode == KeyEvent.VK_ESCAPE) {
				// ESC-Key
				commandHandler.handle(new ResetCommand(domainController));
			}
			if (keyCode == KeyEvent.VK_Z) {
				try {
					Robot robot = new Robot();
					if (maskedKeyBag.getCtrl() && !maskedKeyBag.getShift()) {
						commandHandler.undo();
//						robot.keyPress(KeyEvent.VK_CONTROL);
					}
					if (maskedKeyBag.getCtrl() && maskedKeyBag.getShift()) {
						commandHandler.redo();
						// robot.keyPress(KeyEvent.VK_CONTROL);
//						robot.keyPress(KeyEvent.VK_SHIFT);
					}
				} catch (AWTException e) {
					throw new RuntimeException(e);
				}

			}

			if (keyCode == KeyEvent.VK_U) {
				commandHandler.undo();
			}
			if (keyCode == KeyEvent.VK_R) {
				commandHandler.redo();
			}

//			System.out.println((maskedKeyBag.getCtrl() ? "CONTROL" : "NO CONTROL") + "      "
//					+ (maskedKeyBag.getShift() ? "SHIFT" : "NO SHIFT"));
		}
		if (id == KeyEvent.KEY_TYPED) {

			if (keyChar == 'd') {
				// d key 68
				debugModus = debugModus.getNext();
				repaint();
			}
		}

	}

	private Set<Shape> mapSetOfIdsToShapes(Set<String> ids) {
		HashSet<Shape> shapes = new HashSet<Shape>();
		HashMap<String, Coordinate> coordinates = new HashMap<String, Coordinate>();
		if (currentSnapshot != null) {
			coordinates.putAll(currentSnapshot.getSavedCoordinates());
		}
		for (String id : ids) {
			BlockType type = domainController.getBlockType(id);
			shapes.add(shapeFactory.createShape(id, type, coordinates.get(id)));
		}
		return shapes;
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

		/*
		 * for (Pair<Integer, Integer> pair : toAdd.getCoordinatesShape()) {
		 * this.alreadyFilledInCoordinates.remove(pair); }
		 */

		if (!event.getLinkedBlockID().equals("")) {
			Shape linkedShape = programArea.getShapeById(event.getLinkedBlockID());

			toAdd.setConnectedVia(event.getLinkedType(), true);
			toAdd.clipOn(linkedShape, toAdd.getConnectedVia());
			toAdd.defineConnectionTypes();

			linkedShape.setConnectedVia(getOppositeConnectionType(event.getLinkedType(), linkedShape), true);

			toAdd.setConnectedVia(event.getLinkedType(), false);

			toAdd.setCoordinatesShape();
			System.out.println(toAdd.getConnectedVia() + "        " + linkedShape.getId());

			programArea.addShapeToProgramArea(linkedShape);
		}

		programArea.addShapeToProgramArea(toAdd);

		programArea.clearAlreadyFilledInCoordinates();

		determineTotalHeightControlShapes();

		for (Shape shape : domainController.getAllHeadControlBlocks().stream().map(e -> programArea.getShapeById(e))
				.collect(Collectors.toSet())) {

			HashSet<String> idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(toAdd.getId());

			for (String id : idsToMove) {
				Shape shapeje = null;
				try {
					shapeje = programArea.getShapeById(id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				shapeje.setY_coord(shapeje.getY_coord() + (shape.getHeight() - shape.getPreviousHeight()));
			}

		}

		for (Shape shape : programArea.getShapesInProgramArea()) {
			shape.setCoordinatesShape();
			programArea.addToAlreadyFilledInCoordinates(shape);
			shape.defineConnectionTypes();
		}

		// programArea.addToAlreadyFilledInCoordinates(toAdd);
		programArea.setHighlightedShape(null);
		this.setCurrentShape(null);

		removeFromShapesInMovement(toAdd);
		super.repaint();
	}

	/**
	 * Determine the height of all the controlshapes
	 */
	private void determineTotalHeightControlShapes() {
		for (Shape shape : programArea.getShapesInProgramArea().stream().filter(s -> s instanceof ControlShape)
				.collect(Collectors.toSet())) {
			if (shape != null && domainController.isBlockPresent(shape.getId())) {
				shape.determineTotalHeight(mapSetOfIdsToShapes(domainController.getAllBlockIDsInBody(shape.getId())));
			}
		}
	}

	private ConnectionType getOppositeConnectionType(ConnectionType connection, Shape parent) {
		switch (connection) {
		case BODY:
			return ConnectionType.UP;
		case CONDITION:
			return ConnectionType.LEFT;
		case DOWN:
			return ConnectionType.UP;
		case LEFT:
			if (parent instanceof ControlShape) {
				return ConnectionType.CONDITION;
			} else {
				return ConnectionType.OPERAND;
			}
		case NOCONNECTION:
			return ConnectionType.NOCONNECTION;
		case OPERAND:
			return ConnectionType.LEFT;
		case UP:
			return ConnectionType.DOWN;
		default:
			return ConnectionType.NOCONNECTION;
		}
	}

	@SuppressWarnings("unchecked")
	private HashSet<String> shapeIdsToBeMovedAfterUpdateOfControlShape(String changedBlockId) {
		Set<String> ids = new HashSet<String>();
		HashSet<String> enclosingShapes = getAllEnclosingShapeIds(changedBlockId);

		for (String id : enclosingShapes) {
			Set<String> blocksBelow = domainController.getAllBlockIDsBelowCertainBlock(id).stream()
					.filter(s -> !s.equals(id)).collect(Collectors.toSet());
			for (String idBlockBelow : blocksBelow) {
				ids.addAll(domainController.getAllBlockIDsUnderneath(idBlockBelow));
			}
		}

		Set<String> idsShapesInMovement = getShapesInMovement().stream().map(b -> b.getId())
				.collect(Collectors.toSet());
		ids = ids.stream().filter(s -> !idsShapesInMovement.contains(s)).collect(Collectors.toSet());

		return (HashSet<String>) ids;
	}

	private HashSet<String> getAllEnclosingShapeIds(String changedBlockId) {
		HashSet<String> ids = new HashSet<String>();
		Shape shape = getShapeByID(changedBlockId, programArea.getShapesInProgramArea());
		if (shape instanceof ControlShape) {
			ids.add(shape.getId());
		}
		String tempId = domainController.getEnclosingControlBlock(changedBlockId);
		while (tempId != null) {
			ids.add(tempId);
			tempId = domainController.getEnclosingControlBlock(tempId);
		}
		return ids;
	}

	private Shape getShapeByID(String id, Collection<Shape> collection) {
		try {
			return collection.stream().filter(e -> e.getId().equals(id)).findFirst().get();

		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onBlockRemoved(BlockRemovedEvent event) {
		Set<Shape> shapesToBeRemovedFromProgramArea = programArea.getShapesInProgramArea().stream()
				.filter(s -> s.getId().equals(event.getRemovedBlockId())).collect(Collectors.toSet());

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			programArea.removeShapeFromProgramArea(shape);
		}

		programArea.clearAlreadyFilledInCoordinates();

		// update internals of controlshapes
		determineTotalHeightControlShapes();

		updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(event.getRemovedBlockId(), "",
				event.getBeforeRemoveBlockId());

		// handle add to programArea in practice, all coordinates etc are set.
		for (Shape shape : programArea.getShapesInProgramArea()) {
			shape.setCoordinatesShape();
			programArea.addToAlreadyFilledInCoordinates(shape);
			shape.defineConnectionTypes();
		}

//		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nSHAPES IN MOVEMENT");
//		System.out.println(toRemove.getId());
//		for(Shape s: getShapesInMovement()) {
//			System.out.println(s.getId());
//		}
//		removeFromShapesInMovement(toRemove);

//		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nSHAPES IN MOVEMENT AFTER REMOVE");
//		for(Shape s: getShapesInMovement()) {
//			System.out.println(s.getId());
//		}

		this.setCurrentShape(null);
		this.resetShapesInMovement();
		super.repaint();
	}

	private void removeFromShapesInMovement(Shape shape) {

		HashSet<Shape> newShapesInMovement = new HashSet<Shape>();

		newShapesInMovement.addAll(
				shapesInMovement.stream().filter(s -> !(s.getId().equals(shape.getId()))).collect(Collectors.toSet()));
		shapesInMovement = newShapesInMovement;

		HashSet<Shape> newshapeClonesInMovement = new HashSet<Shape>();
		newshapeClonesInMovement.addAll(shapeClonesInMovement.stream().filter(s -> !(s.getId().equals(shape.getId())))
				.collect(Collectors.toSet()));
		shapeClonesInMovement = newshapeClonesInMovement;

//		Optional<Shape> ms = shapesInMovement.stream().filter(s->s.getId().equals(shape.getId())).findAny();
//		
//		if(ms.isPresent()) {
//			shapesInMovement.remove(ms.get());
//		}
//		
//		 ms = shapeClonesInMovement.stream().filter(s->s.getId().equals(shape.getId())).findAny();
//		
//		if(ms.isPresent()) {
//			shapeClonesInMovement.remove(ms.get());
//			
//		}
	}

	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) {

		try {

//			Shape changedShape = getShapesInMovement().stream().filter(s -> s.getId().equals(event.getChangedBlockId()))
//					.findFirst().get();
//
//			Shape topOfChainShape = getShapesInMovement().stream()
//					.filter(s -> s.getId().equals(event.getTopOfMovedChainId())).findFirst().get();

			Shape changedShape = shapeFactory.createShape(event.getChangedBlockId(),
					domainController.getBlockType(event.getChangedBlockId()),
					currentSnapshot.getSavedCoordinates().get(event.getChangedBlockId()));
			Shape topOfChainShape = shapeFactory.createShape(event.getTopOfMovedChainId(),
					domainController.getBlockType(event.getTopOfMovedChainId()),
					currentSnapshot.getSavedCoordinates().get(event.getTopOfMovedChainId()));

			/**
			 * newly connected shape
			 */
			Shape changedLinkedShape = null;
			/**
			 * the previous connected shape
			 */
			Shape decoupledShape = null;

			if (!event.getBeforeMoveBlockId().equals("")) {
				decoupledShape = getShapeByID(event.getBeforeMoveBlockId(), programArea.getShapesInProgramArea());
			}

			if (!event.getChangedLinkedBlockId().equals("")) {
				changedLinkedShape = getShapeByID(event.getChangedLinkedBlockId(),
						programArea.getShapesInProgramArea());
			}

			// handle add to programArea in theory

			for (Shape movedShape : getShapesInMovement()) {

				movedShape.setCoordinatesShape();
				movedShape.defineConnectionTypes();
				Boolean removeOnUndo = movedShape.getHasToBeRemovedOnUndo();
				movedShape.setHasToBeRemovedOnUndo(false);
				programArea.addShapeToProgramArea(movedShape);
				movedShape.setHasToBeRemovedOnUndo(removeOnUndo);

			}

			programArea.clearAlreadyFilledInCoordinates();

			// update internals of controlshapes
			determineTotalHeightControlShapes();

			// Update the position of all blocks according to the changes of the
			// controlshapes

			updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(event.getChangedBlockId(),
					event.getChangedLinkedBlockId(), event.getBeforeMoveBlockId());

			// changedShape is not in the programArea at the moment.
			if (changedShape != null) {
				if (changedShape instanceof ControlShape) {
					changedShape.determineTotalHeight(
							mapSetOfIdsToShapes(domainController.getAllBlockIDsInBody(changedShape.getId())));
				}
				Boolean removeOnUndo = changedShape.getHasToBeRemovedOnUndo();
				changedShape.setHasToBeRemovedOnUndo(false);
				programArea.addShapeToProgramArea(changedShape);
				changedShape.setHasToBeRemovedOnUndo(removeOnUndo);
			}
			if (changedLinkedShape != null) {
				Boolean removeOnUndo = changedLinkedShape.getHasToBeRemovedOnUndo();
				changedLinkedShape.setHasToBeRemovedOnUndo(false);
				programArea.addShapeToProgramArea(changedLinkedShape);
				changedLinkedShape.setHasToBeRemovedOnUndo(removeOnUndo);
			}
			if (decoupledShape != null) {
				Boolean removeOnUndo = decoupledShape.getHasToBeRemovedOnUndo();
				decoupledShape.setHasToBeRemovedOnUndo(false);
				programArea.addShapeToProgramArea(decoupledShape);
				decoupledShape.setHasToBeRemovedOnUndo(removeOnUndo);
			}

			// handle add to programArea in practice, all coordinates etc are set.
			for (Shape shape : programArea.getShapesInProgramArea()) {
				shape.setCoordinatesShape();
				programArea.addToAlreadyFilledInCoordinates(shape);
				shape.defineConnectionTypes();
			}

			// Reset global variables
			programArea.setHighlightedShape(null);
			setCurrentShape(null);
			resetShapesInMovement();

			super.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(String changedBlockId,
			String changedConnectedBlockId, String beforeBlockId) {

		Shape changedShape = getShapesInMovement().stream().filter(s -> s.getId().equals(changedBlockId)).findFirst()
				.get();
		/**
		 * newly connected shape
		 */
		Shape changedLinkedShape = null;
		/**
		 * the previous connected shape
		 */
		Shape decoupledShape = null;

		// begin handle couplings
		if (!beforeBlockId.equals("")) {
			decoupledShape = getShapeByID(beforeBlockId, programArea.getShapesInProgramArea());
		}

		if (!changedConnectedBlockId.equals("")) {
			changedLinkedShape = getShapeByID(changedConnectedBlockId, programArea.getShapesInProgramArea());
		}

		Set<Shape> allTopLevelControlBlocks = domainController.getAllHeadControlBlocks().stream()
				.map(e -> getShapeByID(e, programArea.getShapesInProgramArea())).collect(Collectors.toSet());
		Set<Shape> allHeadControlBlocks = new HashSet<Shape>(allTopLevelControlBlocks);

		// use only the controlblocks that are the top of the chain, no controlblocks
		// above them in any way.
		for (Iterator<Shape> it = allHeadControlBlocks.iterator(); it.hasNext();) {
			Shape head = it.next();
			boolean remove = false;
			for (Shape shape : allHeadControlBlocks.stream().filter(s -> s != head).collect(Collectors.toSet())) {
				if (domainController.getAllBlockIDsUnderneath(shape.getId()).contains(head.getId())) {
					remove = true;
					break;
				}
			}
			if (remove) {
				it.remove();
			}

		}

		for (Shape shape : allHeadControlBlocks) {
			HashSet<String> idsToMove = new HashSet<String>();

			int diffYPosition = shape.getHeight() - shape.getPreviousHeight();
			Set<String> idsUnderneathShape = domainController.getAllBlockIDsUnderneath(shape.getId()).stream()
					.filter(s -> !s.equals(shape.getId())).collect(Collectors.toSet());

			// does this movement affect the height of the current stack?
			if (idsUnderneathShape.contains(changedShape.getId())) {

				if (shape.getHeight() == shape.getPreviousHeight()) {
					HashSet<String> idsToMoveUnderneath = new HashSet<String>();
					// no, it does not affect the height of the current stack

					if (changedLinkedShape != null && decoupledShape != null) {

						// going up or down?
						if (changedShape.getY_coord() < changedShape.getPreviousY_coord()) {
							// up
							idsToMoveUnderneath
									.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(changedLinkedShape.getId()));

							Shape decoupledControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(decoupledShape.getId()),
									programArea.getShapesInProgramArea());
							if (decoupledControlShape == null || decoupledControlShape == shape) {
								decoupledControlShape = decoupledShape;
							}

							if (decoupledControlShape.getHeight() != decoupledControlShape.getPreviousHeight()) {
								final String decoupledControlShapeId = decoupledControlShape.getId();
								idsToMoveUnderneath.removeAll(domainController
										.getAllBlockIDsBelowCertainBlock(decoupledControlShape.getId()).stream()
										.filter(s -> !s.equals(decoupledControlShapeId)).collect(Collectors.toSet()));
							}

							Shape linkedControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(changedLinkedShape.getId()),
									programArea.getShapesInProgramArea());
							if (linkedControlShape == null || linkedControlShape == shape) {
								linkedControlShape = changedLinkedShape;
							}
							diffYPosition = linkedControlShape.getHeight() - linkedControlShape.getPreviousHeight();
						} else {
							// down
							idsToMoveUnderneath
									.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(decoupledShape.getId()));
							idsToMoveUnderneath.addAll(
									getShapesInMovement().stream().map(s -> s.getId()).collect(Collectors.toSet()));

							Shape linkedControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(changedLinkedShape.getId()),
									programArea.getShapesInProgramArea());
//							if (linkedControlShape != shape) {
//								// linkedControlShape = changedLinkedShape;
//
//								if (linkedControlShape.getHeight() != linkedControlShape.getPreviousHeight()) {
//									final String linkedControlShapeId = linkedControlShape.getId();
//									idsToMoveUnderneath.removeAll(domainController
//											.getAllBlockIDsBelowCertainBlock(linkedControlShape.getId()).stream()
//											.filter(s -> !s.equals(linkedControlShapeId)).collect(Collectors.toSet()));
//								}
//							}

							Shape decoupledControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(decoupledShape.getId()),
									programArea.getShapesInProgramArea());
							if (decoupledControlShape == null || decoupledControlShape == shape) {
								decoupledControlShape = decoupledShape;
							}
							diffYPosition = decoupledControlShape.getHeight()
									- decoupledControlShape.getPreviousHeight();

						}

						// if the height of the shape did not change, none of the shapes below height
						// should be moved
						idsToMoveUnderneath.removeAll(shapeIdsToBeMovedAfterUpdateOfControlShape(shape.getId()));

						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMoveUnderneath, diffYPosition);
					}
					// add from move
					else if (changedLinkedShape != null) {
						Shape linkedControlShape = getShapeByID(
								domainController.getEnclosingControlBlock(changedLinkedShape.getId()),
								programArea.getShapesInProgramArea());
						if (linkedControlShape == null || linkedControlShape == shape) {
							linkedControlShape = changedLinkedShape;
						}
						diffYPosition = linkedControlShape.getHeight() - linkedControlShape.getPreviousHeight();

						idsToMoveUnderneath
								.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(changedLinkedShape.getId()));

						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMoveUnderneath, diffYPosition);

					}
					// remove from move
					else if (decoupledShape != null) {
						Shape decoupledControlShape = getShapeByID(
								domainController.getEnclosingControlBlock(decoupledShape.getId()),
								programArea.getShapesInProgramArea());
						if (decoupledControlShape == null || decoupledControlShape == shape) {
							decoupledControlShape = decoupledShape;
						}
						diffYPosition = decoupledControlShape.getHeight() - decoupledControlShape.getPreviousHeight();

						idsToMoveUnderneath.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(decoupledShape.getId()));

						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMoveUnderneath, diffYPosition);
					}

				} else {
					// yes

					if (domainController.getAllBlockIDsBelowCertainBlock(shape.getId())
							.contains(changedLinkedShape.getId())) {
						// yes
						HashSet<String> idsToMoveUnderneath = new HashSet<String>();
						idsToMoveUnderneath.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(shape.getId()));

						if ((idsUnderneathShape.contains(beforeBlockId) || shape.getId().equals(beforeBlockId))
								&& changedShape.getY_coord() > changedShape.getPreviousY_coord()) {

							Shape decoupledControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(decoupledShape.getId()),
									programArea.getShapesInProgramArea());
							if (decoupledControlShape == null || decoupledControlShape == shape) {
								decoupledControlShape = decoupledShape;
							}
							idsToMoveUnderneath
									.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(decoupledControlShape.getId()));

							idsToMoveUnderneath.addAll(
									getShapesInMovement().stream().map(s -> s.getId()).collect(Collectors.toSet()));
						}

						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMoveUnderneath,
								shape.getHeight() - shape.getPreviousHeight());
					} else {
						// no
						idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(changedShape.getId());
						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMove, diffYPosition);
					}

				}
			} else {
				// idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(beforeBlockId);

				if (decoupledShape != null) {
					Shape decoupledControlShape = getShapeByID(
							domainController.getEnclosingControlBlock(decoupledShape.getId()),
							programArea.getShapesInProgramArea());
					if (decoupledControlShape == null || decoupledControlShape == shape) {
						decoupledControlShape = decoupledShape;
					}
//					diffYPosition = decoupledControlShape.getHeight()
//							- decoupledControlShape.getPreviousHeight();

					diffYPosition = shape.getHeight() - shape.getPreviousHeight();

					idsToMove.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(decoupledShape.getId()));

					moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMove, diffYPosition);
				}

			}

//			moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMove, diffYPosition);
		}
	}

	private void moveAllGivenShapesVerticallyWithTheGivenOffset(Set<String> set, int diffYPosition) {
		for (String id : set) {
			Shape shape = null;
			try {
				shape = programArea.getShapesInProgramArea().stream().filter(e -> e.getId().equals(id)).findFirst()
						.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			shape.setY_coord(shape.getY_coord() + diffYPosition);
			shape.setCoordinatesShape();
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

	public Shape getCurrentShape() {
		return this.currentShape;
	}

	private void setCurrentShape(Shape shape) {
		this.currentShape = shape;
	}

	public int getX_offsetCurrentShape() {
		return x_offsetCurrentShape;
	}

	public void setX_offsetCurrentShape(int x_offsetCurrentShape) {
		this.x_offsetCurrentShape = x_offsetCurrentShape;
	}

	public int getY_offsetCurrentShape() {
		return y_offsetCurrentShape;
	}

	public void setY_offsetCurrentShape(int y_offsetCurrentShape) {
		this.y_offsetCurrentShape = y_offsetCurrentShape;
	}

	public ShapeFactory getShapeFactory() {
		return shapeFactory;
	}

	public void setShapeFactory(ShapeFactory shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

	public void setCurrentSnapshot(GuiSnapshot snapshot) {
		currentSnapshot = snapshot;

	}

}