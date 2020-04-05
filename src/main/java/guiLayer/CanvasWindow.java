package guiLayer;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import applicationLayer.DomainController;
import domainLayer.elements.ElementType;
import domainLayer.elements.Orientation;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.ElementAddedEvent;
import events.GUIListener;
import events.PanelChangeEvent;
import events.RobotAddedEvent;
import events.RobotChangeEvent;
import events.UpdateHighlightingEvent;
import types.ConnectionType;

public class CanvasWindow extends CanvasResource implements GUIListener, Constants {
	private ProgramArea programArea;
	private GameArea gameArea;
	private PaletteArea paletteArea;

	private DomainController domainController;
	private ShapeFactory shapeFactory;
	private boolean isHandleEvent = true;
	private HashSet<Shape> shapesInMovement;

	public HashSet<Shape> getShapesInMovement() {
		return shapesInMovement;
	}

	private HashMap<Shape, Pair<Integer, Integer>> previousCoordinates;

	private Set<String> blocksUnderneath;

	private boolean isGameAreaUpdated = true; // to initialise, it has to be true
	private boolean isPaletteShown = true;

	private Shape currentShape = null;
	private Shape movedShape = null;

	private Shape highlightedForExecution = null;

	private int x_offsetCurrentShape = 0;
	private int y_offsetCurrentShape = 0;
	public static DebugModus debugModus = DebugModus.NONE;

	// methods of CanvasResource that need to be overridden:

	public CanvasWindow(String title, DomainController dc) {
		super(title);

		super.width = 1000;
		this.domainController = dc;
		this.domainController.addGameListener(this);
		setShapeFactory(new ShapeFactory());
		this.programArea = new ProgramArea();
		this.gameArea = new GameArea();
		this.paletteArea = new PaletteArea(getShapeFactory());

		this.blocksUnderneath = new HashSet<String>();
		this.shapesInMovement = new HashSet<Shape>();

	}

	@Override
	protected void paint(Graphics g) {
		// only for debugging purposes
		if (debugModus == DebugModus.FILLINGS) {
			for (Pair<Integer, Integer> filledInCoordinate : programArea.getAlreadyFilledInCoordinates()) {
				g.drawOval(filledInCoordinate.getLeft(), filledInCoordinate.getRight(), 1, 1);
			}
		}
		if (currentShape != null)
			currentShape.draw(g);

		// Partition CanvasWindow in different sections

		paletteArea.paint(g);
		gameArea.draw(g);

		// draw all shapes in shapesInProgramArea
		if (programArea.getShapesInProgramArea() != null && !programArea.getShapesInProgramArea().isEmpty()) {
			programArea.getShapesInProgramArea().stream().forEach(((Shape e) -> e.draw(g)));
		}

		if (highlightedForExecution != null) {
			drawHighlightedBLUE(g, highlightedForExecution);
		}

		if (this.shapesInMovement != null) {

			for (Shape shape : shapesInMovement) {
				if (shape != currentShape)
					shape.draw(g);
			}
		}

		if (programArea.getHighlightedShape() != null) {
			drawHighlightedGREEN(g, programArea.getHighlightedShape());
		}
		// only for debugging purposes
		if (DebugModus.CONNECTIONS.compareTo(debugModus) <= 0) {
			for (Shape shape : programArea.getShapesInProgramArea()) {
				for (var p : shape.getCoordinateConnectionMap().entrySet()) {
					int tempx = p.getValue().getLeft() - 3;
					int tempy = p.getValue().getRight();
					g.drawOval(tempx, tempy, 6, 6);

					if (DebugModus.CONNECTIONSTATUS.compareTo(debugModus) <= 0) {
						g.drawString(shape.checkIfOpen(p.getKey()).toString(), tempx, tempy);
					}
				}
			}

			for (Shape shape : shapesInMovement) {
				for (var p : shape.getCoordinateConnectionMap().values()) {
					int tempx = p.getLeft() - 3;
					int tempy = p.getRight();
					g.drawOval(tempx, tempy, 6, 6);
				}
			}
		}
	}

	private void drawHighlightedGREEN(Graphics g, Shape shape) {
		g.setColor(Color.GREEN);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	private void drawHighlightedBLUE(Graphics g, Shape shape) {
		g.setColor(Color.BLUE);
		shape.draw(g);
		g.setColor(Color.BLACK);
	}

	private int[] calculateOffsetMouse(int x_Mouse, int y_Mouse, int x_Shape, int y_Shape) {
		int[] returnValue = new int[2];
		returnValue[0] = x_Mouse - x_Shape;
		returnValue[1] = y_Mouse - y_Shape;
		return returnValue;
	}

	@Override
	protected void handleMouseEvent(int id, int x, int y, int clickCount) {

		if (isHandleEvent()) {

			if (paletteArea.checkIfInPalette(x) && id == MouseEvent.MOUSE_PRESSED) {
				setCurrentShape(paletteArea.getShapeFromCoordinate(x, y));
				if (getCurrentShape() != null) {
					var temp = calculateOffsetMouse(x, y, getCurrentShape().getX_coord(),
							getCurrentShape().getY_coord());
					this.x_offsetCurrentShape = temp[0];
					this.y_offsetCurrentShape = temp[1];
					getShapesInMovement().add(getCurrentShape());
				}
			}

			if ((id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_PRESSED) && currentShape != null) {

				int offsetX = x - x_offsetCurrentShape;
				int offsetY = y - y_offsetCurrentShape;

				int diffX = offsetX - currentShape.getX_coord();
				int diffy = offsetY - currentShape.getY_coord();

				currentShape.setX_coord(x - x_offsetCurrentShape);
				currentShape.setY_coord(y - y_offsetCurrentShape);
				currentShape.defineConnectionTypes();

				for (Shape shapeIM : shapesInMovement) {
					shapeIM.defineConnectionTypes();
				}

				programArea.setHighlightedShape(determineHighlightShape());

				updateAllShapesInMovementAccordingToChangeOfLeader(diffX, diffy, currentShape);

			}

			if (id == MouseEvent.MOUSE_PRESSED && x > PROGRAM_START_X && x < PROGRAM_END_X) {
				this.previousCoordinates = new HashMap<Shape, Pair<Integer, Integer>>();
				Shape shape = programArea.getShapeFromCoordinate(x, y);
				if (shape != null) {
					blocksUnderneath = domainController.getAllBlockIDsUnderneath(shape.getId());

					for (String shapeId : blocksUnderneath) {
						Shape temp = programArea.getShapesInProgramArea().stream()
								.filter(e -> e.getId().equals(shapeId)).findFirst().get();

						if (temp != null) {

							shapesInMovement.add(temp);

							temp.setPreviousX_coord(temp.getX_coord());
							temp.setPreviousY_coord(temp.getY_coord());

						}
					}

					setCurrentShape(shape);

					decoupleFromShape(currentShape);

					currentShape.setConnectedVia(ConnectionType.NOCONNECTION, true);

					var mouseOffset = calculateOffsetMouse(x, y, getCurrentShape().getX_coord(),
							getCurrentShape().getY_coord());

					setX_offsetCurrentShape(mouseOffset[0]);

					setY_offsetCurrentShape(mouseOffset[1]);
					for (Shape shapeIM : shapesInMovement) {
						programArea.removeShapeFromProgramArea(shapeIM);
					}

				}
			}

			if (id == MouseEvent.MOUSE_RELEASED) {
				if (currentShape != null && paletteArea.checkIfInPalette(currentShape.getX_coord())) {

					if (currentShape.getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						setCurrentShape(null);
						shapesInMovement.clear();
					} else {
						domainController.removeBlock(currentShape.getId());
					}
				}
			}

			if (id == MouseEvent.MOUSE_RELEASED && programArea.checkIfInProgramArea(x) && currentShape != null) {
				if (programArea.getHighlightedShape() != null) {
					// connectedVia of highlightedshape must be persisted.
					programArea.getHighlightedShape().persistConnectedVia(true);

					if (currentShape.getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
						// persist the connectedVia.
						currentShape.persistConnectedVia(true);

						// ADD
						currentShape.clipOn(programArea.getHighlightedShape(), currentShape.getConnectedVia());

					} else {

						// MOVE
						// if movedshape is null, then clip on is not necessary
						if (movedShape != null) {
							// movedShape connectevia must be persisted, currentshape connectedvia reverted
							// if they are different shapes
							// this order is important.
							movedShape.persistConnectedVia(true);
							currentShape.persistConnectedVia(false);

							int originalChangedShapeX = movedShape.getX_coord();
							int originalChangedShapeY = movedShape.getY_coord();
							System.out.println("BeforeClipon X: " + originalChangedShapeX);
							System.out.println("BeforeClipon Y: " + originalChangedShapeY);

							movedShape.clipOn(programArea.getHighlightedShape(), movedShape.getConnectedVia());
							System.out.println("AfterClipon X: " + movedShape.getX_coord());
							System.out.println("AfterClipon Y: " + movedShape.getY_coord());

							// Only if the shape that's being dragged is the moved shape than it should
							// be decoupled from the chain it's in
							if (movedShape == currentShape) {
								decoupleFromShape(movedShape);
							}

							int diffX = movedShape.getX_coord() - originalChangedShapeX;
							int diffy = movedShape.getY_coord() - originalChangedShapeY;
							System.out.println("diffX: " + diffX);
							System.out.println("diffY: " + diffy);

							updateAllShapesInMovementAccordingToChangeOfLeader(diffX, diffy, movedShape);
						}

					}
				}

				currentShape.setCoordinatesShape(
						currentShape.createCoordinatePairs(currentShape.getX_coord(), currentShape.getY_coord()));
				boolean placeable = programArea.checkIfPlaceable(getCurrentShape().getCoordinatesShape(),
						getCurrentShape());

				if (placeable) {

					if (getCurrentShape().getId().equals(PALETTE_BLOCK_IDENTIFIER)) {

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

						if (programArea.getHighlightedShape().getConnectedVia().equals(ConnectionType.NOCONNECTION)) {
							domainController.moveBlock(getCurrentShape().getId(), "", "", ConnectionType.NOCONNECTION);

						} else {

							domainController.moveBlock(currentShape.getId(), movedShape.getId(),
									programArea.getHighlightedShape().getId(), movedShape.getConnectedVia());
						}
					}
					// decouple chain of blocks from a block
					// Wanneer is decouple chain nodig?

					else if (getCurrentShape().getConnectedVia() != ConnectionType.NOCONNECTION
							|| (getCurrentShape().getConnectedVia() == ConnectionType.NOCONNECTION
									&& getCurrentShape().getPreviouslyConnectedVia() != ConnectionType.NOCONNECTION)
									&& !domainController.getAllHeadBlocks().contains(getCurrentShape().getId())) {
						// filter out the blocks that already a headblock.

						domainController.moveBlock(getCurrentShape().getId(), "", "", ConnectionType.NOCONNECTION);

					}

					// ONLY GRAPHICAL MOVEMENT:
					else {

						for (Shape shape : shapesInMovement) {

							shape.setCoordinatesShape(
									shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
							programArea.addShapeToProgramArea(shape);
							programArea.addToAlreadyFilledInCoordinates(shape);

						}

					}

					// NOT PLACEABLE =>
				} else {

					for (Iterator<Shape> iterator = shapesInMovement.iterator(); iterator.hasNext();) {
						Shape shape = (Shape) iterator.next();

						if (shape.getPreviousX_coord() == INVALID_COORDINATE
								|| shape.getPreviousY_coord() == INVALID_COORDINATE) {
							iterator.remove();
						} else {
							shape.setX_coord(shape.getPreviousX_coord());
							shape.setY_coord(shape.getPreviousY_coord());

							shape.setCoordinatesShape(
									shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
							programArea.addToAlreadyFilledInCoordinates(shape);
							shape.defineConnectionTypes();
							programArea.addShapeToProgramArea(shape);
						}

					}

					getCurrentShape().setConnectedVia(getCurrentShape().getPreviouslyConnectedVia(), true);
				}

				setCurrentShape(null);
				programArea.setHighlightedShape(null);
				movedShape = null;
				setX_offsetCurrentShape(0);
				setY_offsetCurrentShape(0);

				this.previousCoordinates = null;
				this.shapesInMovement = new HashSet<Shape>();
				blocksUnderneath = new HashSet<String>();
			}

			repaint();
		} else {
			// Consume event;
		}
	}

	private void decoupleFromShape(Shape shapeToDecouple) {
		ConnectionType connectionToDecouple = ConnectionType.NOCONNECTION;

		if ((shapeToDecouple instanceof ConditionShape || shapeToDecouple instanceof UnaryOperatorShape)
				&& !shapeToDecouple.checkIfOpen(ConnectionType.LEFT)) {
			connectionToDecouple = ConnectionType.LEFT;
		} else if ((shapeToDecouple instanceof ActionShape || shapeToDecouple instanceof ControlShape)
				&& !shapeToDecouple.checkIfOpen(ConnectionType.UP)) {
			connectionToDecouple = ConnectionType.UP;
		}

		shapeToDecouple.switchCavityStatus(connectionToDecouple);
	}

	private void updateAllShapesInMovementAccordingToChangeOfLeader(int diffX, int diffy, Shape excludedShape) {
		if (!shapesInMovement.isEmpty()) {
			for (Shape shape : shapesInMovement) {

				if (shape != excludedShape) {
					shape.setX_coord(shape.getX_coord() + diffX);
					shape.setY_coord(shape.getY_coord() + diffy);
				}
			}
		}
	}

	private Shape determineHighlightShape() {
		HashMap<ConnectionType, HashMap<Shape, Pair<Integer, Integer>>> shapesInProgramAreaConnectionMap = new HashMap<ConnectionType, HashMap<Shape, Pair<Integer, Integer>>>();

		for (ConnectionType connection : ConnectionType.values()) {
			shapesInProgramAreaConnectionMap.put(connection, new HashMap<Shape, Pair<Integer, Integer>>());
			for (Shape shape : programArea.getShapesInProgramArea().stream().filter(e -> e.checkIfOpen(connection))
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
		if (movedShape != null)
			System.out.println("Moved Shape ID " + movedShape.getId());

		return shape;
	}

	private boolean isConnectionPresent(HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaUpMap,
			HashSet<Pair<Integer, Integer>> connectionTriggerSetDOWN) {
		for (Map.Entry<Shape, Pair<Integer, Integer>> s : shapesInProgramAreaUpMap.entrySet()) {
			if (connectionTriggerSetDOWN.contains(s.getValue())) {
				return true;
			}

		}
		return false;
	}

	@Override
	protected void handleKeyEvent(int id, int keyCode, char keyChar) {
		if (id == KeyEvent.KEY_PRESSED) {

			if (keyCode == 116) {
				// F5-Key
				domainController.executeBlock();
			}

			if (keyCode == 27) {
				// ESC-Key
				domainController.resetGameExecution();
			}

			if (keyCode == 68) {
				debugModus = debugModus.getNext();
				repaint();
			}

		}
	}

	// methods that are inherited from GUIListener:

	@Override
	public void onBlockAdded(BlockAddedEvent event) {
		this.setHandleEvent(true);

		// normaal is ID van event, en geen random DateTime

		Shape toAdd = shapeFactory.createShape(event.getAddedBlockID(), getCurrentShape().getType(),
				getCurrentShape().getX_coord(), getCurrentShape().getY_coord());

		System.out.println("Block ADDED: " + toAdd.getId());

		/*
		 * for (Pair<Integer, Integer> pair : toAdd.getCoordinatesShape()) {
		 * this.alreadyFilledInCoordinates.remove(pair); }
		 */

		if (programArea.getHighlightedShape() != null) {
			toAdd.setConnectedVia(currentShape.getConnectedVia(), true);
			toAdd.clipOn(programArea.getHighlightedShape(), toAdd.getConnectedVia());
			toAdd.defineConnectionTypes();

			programArea.getHighlightedShape().switchCavityStatus(currentShape.getConnectedVia());
			toAdd.switchCavityStatus(programArea.getHighlightedShape().getConnectedVia());

			toAdd.setCoordinatesShape(toAdd.createCoordinatePairs(toAdd.getX_coord(), toAdd.getY_coord()));
			System.out.println(toAdd.getConnectedVia() + "        " + programArea.getHighlightedShape().getId());
		}

		programArea.addShapeToProgramArea(toAdd);

		if (!(toAdd instanceof UnaryOperatorShape || toAdd instanceof ConditionShape)) {
			String enclosingControlShapeId = domainController.getEnclosingControlBlock(toAdd.getId());
			System.out.println("enclosingControlShapeId: " + enclosingControlShapeId);
			Shape enclosingControlShape = null;
			try {
				enclosingControlShape = getShapeByID(enclosingControlShapeId, programArea.getShapesInProgramArea());
			} catch (Exception e) {
				enclosingControlShape = null;
			}

			if (enclosingControlShape != null) {
				enclosingControlShape.addInternal(toAdd);
				for (Shape shape : enclosingControlShape.getInternals()) {
					System.out.println("ID ENCLOSING " + enclosingControlShape.getId());
					System.out.println(shape.getId());
				}
			}
		}

		programArea.clearAlreadyFilledInCoordinates();

		for (Shape shape : domainController.getAllHeadControlBlocks().stream()
				.map(e -> getShapeByID(e, programArea.getShapesInProgramArea())).collect(Collectors.toSet())) {
			shape.determineTotalDimensions();
		}

		for (Shape shape : domainController.getAllHeadControlBlocks().stream()
				.map(e -> getShapeByID(e, programArea.getShapesInProgramArea())).collect(Collectors.toSet())) {

			HashSet<String> idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(toAdd.getId());

			for (String id : idsToMove) {
				Shape shapeje = null;
				try {
					shapeje = programArea.getShapesInProgramArea().stream().filter(e -> e.getId().equals(id))
							.findFirst().get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				shapeje.setY_coord(shapeje.getY_coord() + (shape.getHeight() - shape.getPreviousHeight()));
			}

		}

		for (Shape shape : programArea.getShapesInProgramArea()) {
			shape.setCoordinatesShape(shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
			programArea.addToAlreadyFilledInCoordinates(shape);
			shape.defineConnectionTypes();
		}

		// programArea.addToAlreadyFilledInCoordinates(toAdd);
		programArea.setHighlightedShape(null);
		this.setCurrentShape(null);
		super.repaint();
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

		Set<String> idsShapesInMovement = shapesInMovement.stream().map(b -> b.getId()).collect(Collectors.toSet());
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
		Shape toRemove = getShapeByID(event.getRemovedBlockId(), shapesInMovement);

		Set<Shape> shapesToBeRemovedFromProgramArea = programArea.getShapesInProgramArea().stream()
				.filter(s -> s.getId().equals(event.getRemovedBlockId())).collect(Collectors.toSet());

		for (Shape shape : shapesToBeRemovedFromProgramArea) {
			programArea.removeShapeFromProgramArea(shape);
		}

		if (!event.getBeforeRemoveBlockId().equals("")) {
			Shape decoupledShape = getShapeByID(event.getBeforeRemoveBlockId(), programArea.getShapesInProgramArea());
			decoupledShape.switchCavityStatus(event.getBeforeRemoveConnection());
		}

		// update internals of controlshapes
		updateInternalsControlShape();

		updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(event.getRemovedBlockId(), "",
				event.getBeforeRemoveBlockId());

		// handle add to programArea in practice, all coordinates etc are set.
		for (Shape shape : programArea.getShapesInProgramArea()) {
			shape.setCoordinatesShape(shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
			programArea.addToAlreadyFilledInCoordinates(shape);
			shape.defineConnectionTypes();
		}

		shapesInMovement.remove(toRemove);
		this.setCurrentShape(null);

		super.repaint();
	}

	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) {

		try {
			Shape changedShape = shapesInMovement.stream().filter(s -> s.getId().equals(event.getChangedBlockId()))
					.findFirst().get();
			/**
			 * newly connected shape
			 */
			Shape changedLinkedShape = null;
			/**
			 * the previous connected shape
			 */
			Shape decoupledShape = null;

			// begin handle couplings
			if (!event.getBeforeMoveBlockId().equals("")) {
				decoupledShape = getShapeByID(event.getBeforeMoveBlockId(), programArea.getShapesInProgramArea());
				decoupledShape.switchCavityStatus(event.getBeforeMoveConnection());
			}

			if (!event.getChangedLinkedBlockId().equals("")) {
				changedLinkedShape = getShapeByID(event.getChangedLinkedBlockId(),
						programArea.getShapesInProgramArea());
				changedLinkedShape.switchCavityStatus(event.getConnectionType());
				switch (event.getConnectionType()) {
				case BODY:
				case DOWN:
					changedShape.switchCavityStatus(ConnectionType.UP);
					break;
				case UP:
					changedShape.switchCavityStatus(ConnectionType.DOWN);
					break;
				case OPERAND:
				case CONDITION:
					changedShape.switchCavityStatus(ConnectionType.LEFT);
					break;
				case LEFT:
					changedShape.switchCavityStatus(ConnectionType.OPERAND);
					changedShape.switchCavityStatus(ConnectionType.CONDITION);
					break;
				default:
					break;

				}

			}
			// end handle couplings

			// handle add to programArea in theory
			for (Shape movedShape : shapesInMovement) {

				movedShape.setCoordinatesShape(
						movedShape.createCoordinatePairs(movedShape.getX_coord(), movedShape.getY_coord()));
				movedShape.defineConnectionTypes();
				programArea.addShapeToProgramArea(movedShape);

			}

			// update internals of controlshapes
			updateInternalsControlShape();

			// Update the position of all blocks according to the changes of the
			// controlshapes

			updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(event.getChangedBlockId(),
					event.getChangedLinkedBlockId(), event.getBeforeMoveBlockId());

			// changedShape is not in the programArea at the moment.
			programArea.addShapeToProgramArea(changedShape);
			// handle add to programArea in practice, all coordinates etc are set.
			for (Shape shape : programArea.getShapesInProgramArea()) {
				shape.setCoordinatesShape(shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
				programArea.addToAlreadyFilledInCoordinates(shape);
				shape.defineConnectionTypes();
			}

			// Reset global variables
			programArea.setHighlightedShape(null);
			setCurrentShape(null);
			shapesInMovement = new HashSet<Shape>();

			super.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePositionOfAllShapesAccordingToChangesOfTheControlShapes(String changedBlockId,
			String changedConnectedBlockId, String beforeBlockId) {

		Shape changedShape = shapesInMovement.stream().filter(s -> s.getId().equals(changedBlockId)).findFirst().get();
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
			Set<String> idsUnderneathShape = domainController.getAllBlockIDsUnderneath(shape.getId());

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
							idsToMoveUnderneath
									.addAll(shapesInMovement.stream().map(s -> s.getId()).collect(Collectors.toSet()));

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

				} else {
					// yes

					if (domainController.getAllBlockIDsBelowCertainBlock(shape.getId())
							.contains(changedLinkedShape.getId())) {
						// yes
						HashSet<String> idsToMoveUnderneath = new HashSet<String>();
						idsToMoveUnderneath.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(shape.getId()));

						if (idsUnderneathShape.contains(beforeBlockId)
								&& changedShape.getY_coord() > changedShape.getPreviousY_coord()) {

							Shape decoupledControlShape = getShapeByID(
									domainController.getEnclosingControlBlock(decoupledShape.getId()),
									programArea.getShapesInProgramArea());
							if (decoupledControlShape == null || decoupledControlShape == shape) {
								decoupledControlShape = decoupledShape;
							}
							idsToMoveUnderneath
									.addAll(shapeIdsToBeMovedAfterUpdateOfControlShape(decoupledControlShape.getId()));

							idsToMoveUnderneath
									.addAll(shapesInMovement.stream().map(s -> s.getId()).collect(Collectors.toSet()));
						}

						moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMoveUnderneath,
								shape.getHeight() - shape.getPreviousHeight());
					} else {
						// no
						idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(changedShape.getId());
					}

				}
			} else {
				idsToMove = shapeIdsToBeMovedAfterUpdateOfControlShape(beforeBlockId);
			}

			moveAllGivenShapesVerticallyWithTheGivenOffset(idsToMove, diffYPosition);
		}
	}

	private void updateInternalsControlShape() {
		for (Shape shape : programArea.getShapesInProgramArea().stream().filter(s -> s instanceof ControlShape)
				.collect(Collectors.toSet())) {
			Set<String> idsInBody = domainController.getAllBlockIDsInBody(shape.getId());
			for (Shape movedShape : shapesInMovement) {
				if (idsInBody.contains(movedShape.getId())) {
					String enclosingControlBlockId = domainController.getEnclosingControlBlock(movedShape.getId());
					if (enclosingControlBlockId != null && enclosingControlBlockId.equals(shape.getId())) {
						shape.addInternal(movedShape);
					}
				} else {
					shape.removeInternal(movedShape);
				}
			}
		}

		// remove all shapes from internal that were added to another internal shape
		for (Shape shape : programArea.getShapesInProgramArea().stream().filter(s -> s instanceof ControlShape)
				.collect(Collectors.toSet())) {
			for (Iterator<Shape> it = shape.getInternals().iterator(); it.hasNext();) {
				Shape internal = it.next();
				if (checkRecursivelyIfShapeIsInInternals(internal,
						shape.getInternals().stream().filter(s -> s != internal).collect(Collectors.toSet()))) {
					it.remove();
				}

			}
		}

		// update all ControlBlockAreas:
		// update the height of the controlShapes
		programArea.clearAlreadyFilledInCoordinates();
		for (Shape shape : domainController.getAllHeadControlBlocks().stream()
				.map(e -> getShapeByID(e, programArea.getShapesInProgramArea())).collect(Collectors.toSet())) {
			shape.determineTotalDimensions();
		}
	}

	private boolean checkRecursivelyIfShapeIsInInternals(Shape shape, Set<Shape> internals) {
		if (internals.contains(shape)) {
			return true;
		} else {
			for (Shape internalIn : internals) {
				if (checkRecursivelyIfShapeIsInInternals(shape, internalIn.getInternals())) {
					return true;
				}
			}
		}

		return false;
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
			shape.setCoordinatesShape(shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
		}
	}

	@Override
	public void onPanelChangedEvent(PanelChangeEvent event) {
		isPaletteShown = event.isShown();
		super.repaint();
	}

	@Override
	public void onUpdateHighlightingEvent(UpdateHighlightingEvent event) {
		try {
			highlightedForExecution = programArea.getShapesInProgramArea().stream()
					.filter(e -> e.getId() == event.getHighlightBlockId()).findFirst().get();
		} catch (Exception e) {
			highlightedForExecution = null;
		} finally {
			super.repaint();
		}
	}

	@Override
	public void onRobotChangeEvent(RobotChangeEvent event) {
		// look for robot, set that cell to SAND
		gameArea.moveRobot(event.getxCoordinate(), event.getyCoordinate() + OFFSET_GAMEAREA_CELLS,
				event.getOrientation());
		super.repaint();
	}

	@Override
	public void onRobotAddedEvent(RobotAddedEvent event) {

		int x_coord = event.getxCoordinate();
		int y_coord = event.getyCoordinate();
		Orientation orientation = event.getOrientation();

		ElementType type = ElementType.ROBOT;

		gameArea.addCell(new Cell(x_coord, y_coord + OFFSET_GAMEAREA_CELLS,
				type.toString().toLowerCase() + orientation.toString()));

		super.repaint();
	}

	@Override
	public void onElementAddedEvent(ElementAddedEvent event) {
		ElementType type = event.getType();

		int x_coord = event.getxCoordinate();
		int y_coord = event.getyCoordinate();

		if (type == null) {
			gameArea.addCell(new Cell(x_coord, y_coord + OFFSET_GAMEAREA_CELLS, "sand"));
		} else {
			gameArea.addCell(new Cell(x_coord, y_coord + OFFSET_GAMEAREA_CELLS, type.toString().toLowerCase()));
		}

		super.repaint();
	}

	private boolean checkIsGameAreaUpdated() {
		return isGameAreaUpdated;
	}

	private void setGameAreaUpdated(boolean isGameAreaUpdated) {
		this.isGameAreaUpdated = isGameAreaUpdated;
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

	private void disableEvents() {
		this.isHandleEvent = false;
	}

	private void enableEvents() {
		this.isHandleEvent = true;
	}

	public boolean isHandleEvent() {
		return isHandleEvent;
	}

	public void setHandleEvent(boolean isHandleEvent) {
		this.isHandleEvent = isHandleEvent;
	}

	public ShapeFactory getShapeFactory() {
		return shapeFactory;
	}

	public void setShapeFactory(ShapeFactory shapeFactory) {
		this.shapeFactory = shapeFactory;
	}

}