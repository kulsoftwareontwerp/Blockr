package guiLayer;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import java.util.NoSuchElementException;
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
import types.BlockType;
import types.ConnectionType;

public class CanvasWindow extends CanvasResource implements GUIListener, Constants {
	
	private ProgramArea programArea;
	private GameArea gameArea;
	private PaletteArea paletteArea;

	private DomainController domainController;
	private ShapeFactory shapeFactory;
	private boolean isHandleEvent = true;
	private HashSet<Shape> shapesInMovement;
	private HashMap<Shape, Pair<Integer, Integer>> previousCoordinates;
	private ConnectionType previouslyConnectedVia;

	private Set<String> blocksUnderneath;

	private boolean isGameAreaUpdated = true; // to initialise, it has to be true
	private boolean isPaletteShown = true;

	private Shape currentShape = null;
	private Pair<Integer, Integer> currentShapeCoord = null;

	private Shape highlightedForExecution = null;
	private Shape tempStaticShape = null;
	private Shape tempDynamicShape = null;

	private int x_offsetCurrentShape = 0;
	private int y_offsetCurrentShape = 0;
	private ArrayList<Shape> shapesInProgramArea; // shapes with Id == null SHOULDN'T exist!!!!, only if dragged from
													// Palette, Id == "PALETTE"

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

		shapesInProgramArea = new ArrayList<Shape>();
		this.blocksUnderneath = new HashSet<String>();
		this.shapesInMovement = new HashSet<Shape>();

	}

	@Override
	protected void paint(Graphics g) {
		if (currentShape != null)
			currentShape.draw(g);
		// Partition CanvasWindow in different sections

		paletteArea.paint(g);
		gameArea.draw(g);

		// draw all shapes in shapesInProgramArea
		if (shapesInProgramArea != null && !shapesInProgramArea.isEmpty()) {
			this.shapesInProgramArea.stream().forEach(((Shape e) -> e.draw(g)));
		}

		if (programArea.getHighlightedShape() != null) {
			drawHighlightedGREEN(g, programArea.getHighlightedShape());
		}

		if (highlightedForExecution != null) {
			drawHighlightedBLUE(g, highlightedForExecution);
		}

		if (this.shapesInMovement != null) {

			for (Shape shape : shapesInMovement) {
				if (shape != currentShape)
					currentShape.draw(g);
			}
			// shapesInMovement.forEach(e-> drawShape(g, e));

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
				this.currentShape = paletteArea.getShapeFromCoordinate(x, y);
				if (currentShape != null) {
					var temp = calculateOffsetMouse(x, y, currentShape.getX_coord(), currentShape.getY_coord());
					this.x_offsetCurrentShape = temp[0];
					this.y_offsetCurrentShape = temp[1];
				}
			}

			if ((id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_PRESSED) && currentShape != null) {

				int offsetX = x - x_offsetCurrentShape;
				int offsetY = y - y_offsetCurrentShape;

				int diffX = offsetX - currentShape.getX_coord();
				int diffy = offsetY - currentShape.getY_coord();

				currentShape.setX_coord(x - x_offsetCurrentShape);
				currentShape.setY_coord(y - y_offsetCurrentShape);
				currentShape.updateConnectionTypes();
				programArea.setHighlightedShape(determineHighlightShape());

				if (!shapesInMovement.isEmpty()) {
					for (Shape shape : shapesInMovement) {

						if (shape != currentShape) {
							shape.setX_coord(shape.getX_coord() + diffX);
							shape.setY_coord(shape.getY_coord() + diffy);
						}
					}
				}

			}

//			if (id == MouseEvent.MOUSE_PRESSED && x > PROGRAM_START_X && x < PROGRAM_END_X) {}//
//				this.previousCoordinates = new HashMap<Shape, Pair<Integer, Integer>>()
//				Shape shape = getShapeFromCoordinateFromProgramArea(x, y);;//
//				if shapee != null) {//
//					blocksUnderneath = domainController.getAllBlockIDsUnderneathshapee.getId());//
////
//					for (String shapeId : blocksUnderneath) {//
//						Shape temp = this.getShapesInProgramArea().stream().filter(e -> e.getId().equals(shapeId))//
//								.findFirst().get();//
//						if (temp != null) {//
//							shapesInMovement.add(temp);//
//							previousCoordinates.put(temp,//
//									new Pair<Integer, Integer>(temp.getX_coord(), temp.getY_coord()));//
//						}//
////
//					}//
//					setTempDynamicShape(shape);
////
//					this.currentShapeCoord = new Pair<Integer, Integer>shapee.getX_coord(),shapee.getY_coord());//
//					previousCoordinates.putshapee, new Pair<Integer, Integer>shapee.getX_coord(),shapee.getY_coord()))
//					this.currentShape = shape;;//
//					this.previouslyConnectedVia =shapee.getConnectedVia();//
//					var mouseOffset = calculateOffsetMouse(x, y,getTempDynamicShape()e.getX_coord(),//							getTempDynamicShape()e.getY_coord());//
//					setX_offsetCurrentShape(mouseOffset[0]);//
//					setY_offsetCurrentShape(mouseOffset[1]);//
//					//
//					for (Shape shapeIM : shapesInMovement) {//
//						shapesInProgramArea.remove(shapeIM)
//						for (Pair<Integer, Integer> pair : shapeIM.getCoordinatesShape()) {;//
//						alreadyFilledInCoordinates.remove(pair);;//
//					}	//
//					}//
////
//					///					for (Shape shape2 : controlBlockAreas) {///						if (domainController.getAllBlockIDsInBody(shape2.getId()).contains(shape.getId())) {///							shape.getCoordinatesShape().forEach(e -> this.alreadyFilledInCoordinates.remove(e));///							shape2.getCoordinatesShape().forEach(e -> this.alreadyFilledInCoordinates.remove(e));///							shape2.getInternals().remove(shape);///							shape2.determineTotalHeight(shape2.getInternals());///							shape2.getCoordinatesShape().forEach(e -> this.alreadyFilledInCoordinates.add(e));///						}///					}//
//				}//
//			}

			if (id == MouseEvent.MOUSE_RELEASED && currentShape != null && paletteArea.checkIfInPalette(x)) {

				if (currentShape.getId().equals(PALETTE_BLOCK_IDENTIFIER)) {
					setCurrentShape(null);
				} else {
					domainController.removeBlock(currentShape.getId());
				}
			}

			if (id == MouseEvent.MOUSE_RELEASED && programArea.checkIfInProgramArea(x) && currentShape != null) {

				HashSet<Pair<Integer, Integer>> currentCoordinates = currentShape
						.createCoordinatePairs(currentShape.getX_coord(), currentShape.getY_coord());

				boolean placeable = programArea.checkIfPlaceable(currentCoordinates, getCurrentShape());

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
//					else if(getTempStaticShape() != null && getTempDynamicShape().getConnectedVia() != previouslyConnectedVia) {
//						
//						if(getTempDynamicShape().getConnectedVia().equals(ConnectionType.NOCONNECTION)) {
//							domainController.moveBlock(getTempDynamicShape().getId(), "", ConnectionType.NOCONNECTION);
//						
//						}else {					
//							domainController.moveBlock(getTempDynamicShape().getId(), getTempStaticShape().getId(), getTempDynamicShape().getConnectedVia());
//						}
//					}
					// ONLY GRAPHICAL MOVEMENT:
					else {

						for (Shape shape : shapesInMovement) {

							shape.setCoordinatesShape(
									shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
							this.shapesInProgramArea.add(shape);
							programArea.addToAlreadyFilledInCoordinates(shape);

						}

					}

//					else {
//						if (getTempStaticShape() != null && getTempDynamicShape() != null) {
//							domainController.moveBlock(getTempDynamicShape().getId(), getTempStaticShape().getId(), getTempDynamicShape().getConnectedVia());}
//							//domainController.moveBlock(movedBlockId, connectedBeforeMoveBlockId, connectionBeforeMove, connectedAfterMoveBlockId, connectionAfterMove);
//							/*this.onBlockChangeEvent(new BlockChangeEvent(getTempDynamicShape().getId(),
//									getTempDynamicShape().getId(), getTempStaticShape().getConnectedVia()));*/
//						else 
//						if(getTempDynamicShape() != null){
//							try {
//							domainController.moveBlock(getTempDynamicShape().getId(), "", ConnectionType.NOCONNECTION);
//							}catch (Exception e) {
//								// TODO: handle exception
//							}
//							/*this.onBlockChangeEvent(new BlockChangeEvent(getTempDynamicShape().getId(),
//									getTempDynamicShape().getId(), ConnectionType.NOCONNECTION));*/
//						}
//					}

					// NOT PLACEABLE =>
				} else {
					for (Shape shape : shapesInMovement) {
						shape.setX_coord(previousCoordinates.get(shape).getLeft());
						shape.setY_coord(previousCoordinates.get(shape).getRight());
						shape.setCoordinatesShape(shape.createCoordinatePairs(shape.getX_coord(), shape.getY_coord()));
						programArea.addToAlreadyFilledInCoordinates(shape);
						shape.updateConnectionTypes();
						shapesInProgramArea.add(shape);

					}
				}

				setCurrentShape(null);
				setX_offsetCurrentShape(0);
				setY_offsetCurrentShape(0);

				this.currentShapeCoord = null;
				this.previousCoordinates = null;
				this.shapesInMovement = new HashSet<Shape>();
				blocksUnderneath = new HashSet<String>();
				// this.setHandleEvent(false);

			}

			repaint();
		} else {
			// Consume event;
		}
	}

	// De relation between shape and shapeToClip is already established in a
	// different method;
//	private void clipOn(Shape shape, ConnectionType connection, Shape shapeToClip) {
//		BlockType type = shape.getType();
//		switch (type) {
//		case MoveForward:
//			switch (connection) {
//			case UP:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() + 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shapeToClip.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			case DOWN:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() - 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case TurnLeft:
//			switch (connection) {
//			case UP:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() - 10);
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			case DOWN:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() - 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case TurnRight:
//			switch (connection) {
//			case UP:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() - 10);
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			case DOWN:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord() - 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case If:
//			switch (connection) {
//			case UP:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord() + 10);
//					shapeToClip.setY_coord(shape.getY_coord() - 30);
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			case DOWN:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord() + 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//					// drawShape(g, shapeToClip);
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				}
//				break;
//			case BODY:
//				shapeToClip.setX_coord(shape.getX_coord() + 10);
//				shapeToClip.setY_coord(shape.getY_coord() + 30);
//				// drawShape(g, shapeToClip);
//				break;
//			case CONDITION:
//				shapeToClip.setX_coord(shape.getX_coord() + shape.getWidth() - 10);
//				shapeToClip.setY_coord(shape.getY_coord());
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case While:
//			switch (connection) {
//			case UP:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord() + 10);
//					shapeToClip.setY_coord(shape.getY_coord() - 30);
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
//				}
//				// drawShape(g, shapeToClip);
//				break;
//			case DOWN:
//				if (shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft
//						|| shapeToClip.getType() == BlockType.TurnRight) {
//					shapeToClip.setX_coord(shape.getX_coord() + 10);
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//					// drawShape(g, shapeToClip);
//				} else {
//					shapeToClip.setX_coord(shape.getX_coord());
//					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
//				}
//				break;
//			case BODY:
//				shapeToClip.setX_coord(shape.getX_coord() + 10);
//				shapeToClip.setY_coord(shape.getY_coord() + 30);
//				// drawShape(g, shapeToClip);
//				break;
//			case CONDITION:
//				shapeToClip.setX_coord(shape.getX_coord() + shape.getWidth() - 10);
//				shapeToClip.setY_coord(shape.getY_coord());
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case Not:
//			switch (connection) {
//			case LEFT:
//				shapeToClip.setX_coord(shape.getX_coord() - 80);
//				shapeToClip.setY_coord(shape.getY_coord());
//				// drawShape(g, shapeToClip);
//				break;
//			case OPERAND:
//				shapeToClip.setX_coord(shape.getX_coord() + 80);
//				shapeToClip.setY_coord(shape.getY_coord());
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		case WallInFront:
//			switch (connection) {
//			case LEFT:
//				shapeToClip.setX_coord(shape.getX_coord() - 80);
//				shapeToClip.setY_coord(shape.getY_coord());
//				// drawShape(g, shapeToClip);
//				break;
//			default:
//				; // Do nothing
//			}
//			break;
//		default:
//			;
//		} // Nothing has to happen
//	}

	private Shape determineHighlightShape() {
		HashSet<Pair<Integer, Integer>> connectionTriggerSetUP = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetDOWN = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetLEFT = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetCONDITION = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetOPERAND = new HashSet<Pair<Integer, Integer>>();

		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaUpMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaDownMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaBodyMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaConditionMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaOperandMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaLeftMap = new HashMap<Shape, Pair<Integer, Integer>>();

		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					connectionTriggerSetUP.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.DOWN)) {
					shapesInProgramAreaDownMap.put(shape, shape.getCoordinateConnectionMap().get(ConnectionType.DOWN));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.BODY)) {
					shapesInProgramAreaBodyMap.put(shape, shape.getCoordinateConnectionMap().get(ConnectionType.BODY));
				}
			}
		}
		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.DOWN) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.DOWN).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.DOWN).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					connectionTriggerSetDOWN.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.UP)) {
					shapesInProgramAreaUpMap.put(shape, shape.getCoordinateConnectionMap().get(ConnectionType.UP));
				}
			}
		}
		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.LEFT) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.LEFT).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.LEFT).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					connectionTriggerSetLEFT.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.CONDITION)) {
					shapesInProgramAreaConditionMap.put(shape,
							shape.getCoordinateConnectionMap().get(ConnectionType.CONDITION));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.OPERAND)) {
					shapesInProgramAreaOperandMap.put(shape,
							shape.getCoordinateConnectionMap().get(ConnectionType.OPERAND));
				}
			}
		}
		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					connectionTriggerSetCONDITION.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.LEFT)) {
					shapesInProgramAreaLeftMap.put(shape, shape.getCoordinateConnectionMap().get(ConnectionType.LEFT));
				}
			}
		}
		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.OPERAND) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.OPERAND).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.OPERAND).getRight();

			for (int i = x_current - TRIGGER_RADIUS_CLIPON; i < x_current + TRIGGER_RADIUS_CLIPON; i++) {
				for (int j = y_current - TRIGGER_RADIUS_CLIPON; j < y_current + TRIGGER_RADIUS_CLIPON; j++) {
					connectionTriggerSetOPERAND.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.LEFT)) {
					shapesInProgramAreaLeftMap.put(shape, shape.getCoordinateConnectionMap().get(ConnectionType.LEFT));
				}
			}
		}
		try {
			Shape shape = shapesInProgramAreaUpMap.entrySet().stream()
					.filter(e -> connectionTriggerSetDOWN.contains(e.getValue())).findFirst().get().getKey();
			getCurrentShape().setConnectedVia(ConnectionType.UP);
			return shape;
		} catch (NoSuchElementException e) {
			try {
				Shape shape = shapesInProgramAreaDownMap.entrySet().stream()
						.filter(p -> connectionTriggerSetUP.contains(p.getValue())).findFirst().get().getKey();
				getCurrentShape().setConnectedVia(ConnectionType.DOWN);
				return shape;
			} catch (NoSuchElementException e2) {
				try {
					Shape shape = shapesInProgramAreaBodyMap.entrySet().stream()
							.filter(q -> connectionTriggerSetUP.contains(q.getValue())).findFirst().get().getKey();
					getCurrentShape().setConnectedVia(ConnectionType.BODY);
					return shape;
				} catch (NoSuchElementException e3) {
					try {
						Shape shape = shapesInProgramAreaConditionMap.entrySet().stream()
								.filter(q -> connectionTriggerSetLEFT.contains(q.getValue())).findFirst().get()
								.getKey();
						getCurrentShape().setConnectedVia(ConnectionType.CONDITION);
						return shape;
					} catch (NoSuchElementException e4) {
						try {
							Shape shape = shapesInProgramAreaLeftMap.entrySet().stream()
									.filter(q -> connectionTriggerSetCONDITION.contains(q.getValue())).findFirst().get()
									.getKey();
							getCurrentShape().setConnectedVia(ConnectionType.LEFT);
							return shape;
						} catch (NoSuchElementException e5) {
							try {
								Shape shape = shapesInProgramAreaOperandMap.entrySet().stream()
										.filter(q -> connectionTriggerSetLEFT.contains(q.getValue())).findFirst().get()
										.getKey();
								getCurrentShape().setConnectedVia(ConnectionType.OPERAND);
								return shape;
							} catch (NoSuchElementException e6) {
								try {
									Shape shape = shapesInProgramAreaLeftMap.entrySet().stream()
											.filter(q -> connectionTriggerSetOPERAND.contains(q.getValue())).findFirst()
											.get().getKey();
									getCurrentShape().setConnectedVia(ConnectionType.LEFT);
									return shape;
								} catch (NoSuchElementException e7) {
									return null;
								}
							}
						}
					}
				}
			}
		}
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

		}
	}

	// methods that are inherited from GUIListener:

	@Override
	public void onBlockAdded(BlockAddedEvent event) {
		this.setHandleEvent(true);

		// normaal is ID van event, en geen random DateTime

		Shape toAdd = shapeFactory.createShape(event.getAddedBlockID(), getCurrentShape().getType(),
				getCurrentShape().getX_coord(), getCurrentShape().getY_coord());

		/*
		 * for (Pair<Integer, Integer> pair : toAdd.getCoordinatesShape()) {
		 * this.alreadyFilledInCoordinates.remove(pair); }
		 */

		if (programArea.getHighlightedShape() != null) {
			toAdd.setConnectedVia(currentShape.getConnectedVia());
			toAdd.clipOn(programArea.getHighlightedShape(), toAdd.getConnectedVia());
			toAdd.updateConnectionTypes();
			toAdd.setCoordinatesShape(toAdd.createCoordinatePairs(toAdd.getX_coord(), toAdd.getY_coord()));
			System.out.println(toAdd.getConnectedVia() + "        " + programArea.getHighlightedShape().getId());
		}

		// Normally already removed
		// this.shapesInProgramArea.remove(getCurrentShape());
		// IMPORTANT
		// this.shapesInProgramArea.stream().forEach(e-> e.determineTotalDimensions());

		this.shapesInProgramArea.add(toAdd);

		if(!(toAdd instanceof UnaryOperatorShape || toAdd instanceof ConditionShape)) {
		String enclosingControlShapeId = domainController.getEnclosingControlBlock(toAdd.getId());
		System.out.println("enclosingControlShapeId: " + enclosingControlShapeId);
		String pepo = domainController.getEnclosingControlBlock(toAdd.getId());
		Shape enclosingControlShape = null;
		try {
			enclosingControlShape = this.getShapesInProgramArea().stream()
					.filter(e -> e.getId().equals(enclosingControlShapeId)).findFirst().get();
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

//		for (Shape shape : shapesInProgramArea) {
//			//Set<String> idsInBody= domainController.getAllBlockIDsInBody(shape.getId());
//				programArea.removeFromAlreadyFilledInCoordinates(shape);
//				shape.determineTotalDimensions();
//				programArea.addToAlreadyFilledInCoordinates(shape);
//		}

		for (Shape shape : getShapesInProgramArea()) {
			programArea.removeFromAlreadyFilledInCoordinates(shape);
		}

		
		for (Shape shape : domainController.getAllHeadControlBlocks().stream().map(e-> getShapeByID(e)).collect(Collectors.toSet())) {
				shape.determineTotalDimensions();
		}
		for (Shape shape : domainController.getAllHeadControlBlocks().stream().map(e-> getShapeByID(e)).collect(Collectors.toSet())) {
			for (String id : domainController.getAllBlockIDsBelowCertainBlock(shape.getId())) {
				if (!id.equals(shape.getId())) {
					Shape shapeje = null;
					try {
						shapeje = getShapesInProgramArea().stream().filter(e -> e.getId().equals(id)).findFirst().get();
					}catch (Exception e) {
						e.printStackTrace();
					}
					shapeje.setY_coord(shapeje.getY_coord() + (shape.getHeight() - shape.getPreviousHeight()));
				}
			}
			
		}
		
		for (Shape shape : getShapesInProgramArea()) {
			programArea.addToAlreadyFilledInCoordinates(shape);
		}
		
		programArea.addToAlreadyFilledInCoordinates(toAdd);
		programArea.setHighlightedShape(null);
		this.setCurrentShape(null);
		super.repaint();
	}
	
	private Shape getShapeByID(String id) {
		try {
		return getShapesInProgramArea().stream().filter(e-> e.getId().equals(id)).findFirst().get();
		
		}catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onBlockRemoved(BlockRemovedEvent event) {

		this.setHandleEvent(true);

		Shape toRemove = shapeFactory.createShape(event.getRemovedBlockId(), getCurrentShape().getType(),
				getCurrentShape().getX_coord(), getCurrentShape().getY_coord());

		for (Shape shape : shapesInProgramArea) {
			if (domainController.getAllBlockIDsInBody(shape.getId()).contains(toRemove.getId())) {
				shape.getInternals().remove(toRemove);
			}
		}
		for (Shape shape : shapesInProgramArea) {
			programArea.removeFromAlreadyFilledInCoordinates(shape);
			shape.getHeight(); // if this is a controlShape, it also updates the dimensions based on the
								// internals
			programArea.addToAlreadyFilledInCoordinates(shape);
		}

		this.shapesInProgramArea.remove(toRemove);
		programArea.removeFromAlreadyFilledInCoordinates(getCurrentShape());

		this.setCurrentShape(null);
		programArea.setHighlightedShape(null);
		super.repaint();
	}

	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) { // TO DO REMOVE FIRST, THEN ADD

		this.setHandleEvent(true);
		Shape toAdd = shapeFactory.createShape(event.getChangedBlockId(), getCurrentShape().getType(),
				getCurrentShape().getX_coord(), getCurrentShape().getY_coord());

		if (programArea.getHighlightedShape() != null) {
			toAdd.clipOn(programArea.getHighlightedShape(), getCurrentShape().getConnectedVia());
		}

		for (Shape shape : shapesInProgramArea) {
			if (domainController.getAllBlockIDsInBody(shape.getId()).contains(toAdd.getId())) {
				shape.getInternals().add(toAdd);
			}
		}

		// update all ControlBlockAreas:
		// set the length of all control block correct
		for (Shape shape : shapesInProgramArea) {
			programArea.removeFromAlreadyFilledInCoordinates(shape);
			shape.determineTotalDimensions();
			programArea.addToAlreadyFilledInCoordinates(shape);
		}
		//

		toAdd.setCoordinatesShape(toAdd.createCoordinatePairs(toAdd.getX_coord(), toAdd.getY_coord()));
		this.shapesInProgramArea.add(toAdd);
		programArea.addToAlreadyFilledInCoordinates(toAdd);
		programArea.setHighlightedShape(null);
		setCurrentShape(null);
		super.repaint();

	}

	@Override
	public void onPanelChangedEvent(PanelChangeEvent event) {
		isPaletteShown = event.isShown();
		super.repaint();
	}

	@Override
	public void onUpdateHighlightingEvent(UpdateHighlightingEvent event) {
		try {
			highlightedForExecution = shapesInProgramArea.stream().filter(e -> e.getId() == event.getHighlightBlockId())
					.findFirst().get();
		} catch (Exception e) {
			highlightedForExecution = null;
		} finally {
			super.repaint();
		}
	}

	@Override
	public void onRobotChangeEvent(RobotChangeEvent event) {
		// look for robot, set that cell to SAND
		gameArea.moveRobot(event.getxCoordinate(), event.getyCoordinate()+OFFSET_GAMEAREA_CELLS, event.getOrientation());
		super.repaint();
	}

	@Override
	public void onRobotAddedEvent(RobotAddedEvent event) {

		int x_coord = event.getxCoordinate();
		int y_coord = event.getyCoordinate();
		Orientation orientation = event.getOrientation();

		ElementType type = ElementType.ROBOT;

		gameArea.addCell(new Cell(x_coord, y_coord+OFFSET_GAMEAREA_CELLS, type.toString().toLowerCase() + orientation.toString()));

		super.repaint();
	}

	@Override
	public void onElementAddedEvent(ElementAddedEvent event) {
		ElementType type = event.getType();

		int x_coord = event.getxCoordinate();
		int y_coord = event.getyCoordinate();

		if (type == null) {
			gameArea.addCell(new Cell(x_coord, y_coord+OFFSET_GAMEAREA_CELLS, "sand"));
		} else {
			gameArea.addCell(new Cell(x_coord, y_coord+OFFSET_GAMEAREA_CELLS, type.toString().toLowerCase()));
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

	public ArrayList<Shape> getShapesInProgramArea() {
		return shapesInProgramArea;
	}

	private void disableEvents() {
		this.isHandleEvent = false;
	}

	private void enableEvents() {
		this.isHandleEvent = true;
	}

	public Pair<Integer, Integer> getCurrentShapeCoord() {
		return currentShapeCoord;
	}

	public void setCurrentShapeCoord(Pair<Integer, Integer> currentShapeCoord) {
		this.currentShapeCoord = currentShapeCoord;
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