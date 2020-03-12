package guiLayer;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime; //For Hardcoded Random ID
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;


import applicationLayer.*;
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

public class CanvasWindow extends CanvasResource implements GUIListener {
	
	private int counter = 1;

	// Hard-Coded Parameters that are checked frequently:
	public final static int HEIGHT_GAME_AREA = 4;
	public final static int WIDTH_GAME_AREA = 5;
	public final static int MAX_NB_BLOCKS = 20;

	public final static int PALETTE_START_X = 0;
	public final static int PALETTE_END_X = 100;
	public final static int PALETTE_OFFSET_BLOCKS = 5;

	public final static int PROGRAM_START_X = 100;
	public final static int PROGRAM_END_X = 750;

	public final static int GAME_START_X = 750;
	public final static int GAME_END_Y = 1000;

	public final static int ACTION_BLOCK_MOVE_FORWARD_UPPER = 50;
	public final static int ACTION_BLOCK_MOVE_FORWARD_LOWER = 90;
	public final static int ACTION_BLOCK_TURN_LEFT_UPPER = 95;
	public final static int ACTION_BLOCK_TURN_LEFT_LOWER = 135;
	public final static int ACTION_BLOCK_TURN_RIGHT_UPPER = 140;
	public final static int ACTION_BLOCK_TURN_RIGHT_LOWER = 180;

	public final static int CONTROL_BLOCK_IF_UPPER = 225;
	public final static int CONTROL_BLOCK_IF_LOWER = 325;
	public final static int CONTROL_BLOCK_WHILE_UPPER = 330;
	public final static int CONTROL_BLOCK_WHILE_LOWER = 430;

	public final static int OPERATOR_BLOCK_NOT_UPPER = 475;
	public final static int OPERATOR_BLOCK_NOT_LOWER = 505;

	public final static int CONDITION_BLOCK_WALL_UPPER = 555;
	public final static int CONDITION_BLOCK_WALL_LOWER = 585;

	private DomainController domainController;
	private CanvasResource resource;
	private boolean isHandleEvent = true;

	public boolean isHandleEvent() {
		return isHandleEvent;
	}
	private HashSet<Pair<Integer, Integer>> alreadyFilledInCoordinates;
	private HashSet<Shape> controlBlockAreas;

	private boolean isGameAreaUpdated = true; // to initialise, it has to be true

	private Shape currentShape = null;
	private Pair<Integer, Integer> currentShapeCoord =null;
	private Shape highlightedShape = null;
	private Shape tempStaticShape = null;
	private Shape tempDynamicShape = null;

	private int x_offsetCurrentShape = 0;
	private int y_offsetCurrentShape = 0;
	private Collection<Cell> cells;
	private ArrayList<Shape> shapesInProgramArea; // shapes with Id == null SHOULDN'T exist!!!!, only if dragged from
													// Palette, Id == "PALETTE"

	private void initCells() {
		cells = new ArrayList<Cell>();
		// cells.add(new Cell(350, 150, "Grass"));

		// Upper Fill Cells
		for (int x = GAME_START_X; x <= GAME_START_X + 200; x += 50) {
			for (int y = 0; y <= 150; y += 50) {
				cells.add(new Cell(x, y, "Wall"));
			}
		}
		// Real Game Cells
		// Row 1
		cells.add(new Cell(GAME_START_X, 200, "Wall"));
		cells.add(new Cell(GAME_START_X + 50, 200, "Sand"));
		cells.add(new Cell(GAME_START_X + 100, 200, "Sand"));
		cells.add(new Cell(GAME_START_X + 150, 200, "Sand"));
		cells.add(new Cell(GAME_START_X + 200, 200, "Wall"));
		// Row 2
		cells.add(new Cell(GAME_START_X, 250, "Sand"));
		cells.add(new Cell(GAME_START_X + 50, 250, "Sand"));
		cells.add(new Cell(GAME_START_X + 100, 250, "Goal"));
		cells.add(new Cell(GAME_START_X + 150, 250, "Sand"));
		cells.add(new Cell(GAME_START_X + 200, 250, "Sand"));
		// Row 3
		cells.add(new Cell(GAME_START_X, 300, "Sand"));
		cells.add(new Cell(GAME_START_X + 50, 300, "Wall"));
		cells.add(new Cell(GAME_START_X + 100, 300, "Wall"));
		cells.add(new Cell(GAME_START_X + 150, 300, "Wall"));
		cells.add(new Cell(GAME_START_X + 200, 300, "Sand"));
		// Row 4
		cells.add(new Cell(GAME_START_X, 350, "Sand"));
		cells.add(new Cell(GAME_START_X + 50, 350, "Sand"));
		cells.add(new Cell(GAME_START_X + 100, 350, "RobotUP"));
		cells.add(new Cell(GAME_START_X + 150, 350, "Sand"));
		cells.add(new Cell(GAME_START_X + 200, 350, "Sand"));

		// Lower Fill Cells
		for (int x = GAME_START_X; x <= GAME_START_X + 200; x += 50) {
			for (int y = 400; y <= 550; y += 50) {
				cells.add(new Cell(x, y, "Wall"));
			}
		}
	}

	// methods of CanvasResource that need to be overridden:

	public CanvasWindow(String title, DomainController dc) {
		this(title);
		this.domainController = dc;
	}

	public CanvasWindow(String title) {
		super(title);
		super.width = 1000;
		initCells();
		shapesInProgramArea = new ArrayList<Shape>();
		alreadyFilledInCoordinates = new HashSet<Pair<Integer,Integer>>();
		controlBlockAreas = new HashSet<Shape>();
	}

	@Override
	protected void paint(Graphics g) {
		if (currentShape != null)
			drawShape(g, currentShape);
		// Partition CanvasWindow in different sections

		// Palette
		// ActionBlocks
		g.drawLine(0, 10, 100, 10); // whiteSpace
		g.drawLine(0, 40, 100, 40); // "Action Block"

		// ControlBlocks
		g.drawLine(0, 185, 100, 185); // whiteSpace
		g.drawLine(0, 215, 100, 215); // "Control Block"

		// OperatorBlocks
		g.drawLine(0, 435, 100, 435); // whiteSpace
		g.drawLine(0, 465, 100, 465); // "Operator Block"

		// ConditionBlocks
		g.drawLine(0, 515, 100, 515); // whiteSpace
		g.drawLine(0, 545, 100, 545); // "Condition Block"

		// Rest of the Frame
		g.drawLine(100, 0, 100, 600);
		g.drawLine(GAME_START_X, 0, GAME_START_X, 600);
		g.drawLine(GAME_START_X, 200, GAME_END_Y, 200);
		g.drawLine(GAME_START_X, 400, GAME_END_Y, 400);

		//
		drawFullPalette(g);

		drawGameArea(g);

		// draw all shapes in shapesInProgramArea
		if (shapesInProgramArea != null) {
			this.shapesInProgramArea.stream().forEach(((Shape e) -> this.drawShape(g, e)));
		}

		if (highlightedShape != null) {
			drawHighlighted(g, highlightedShape);
		}

	}

	private void drawHighlighted(Graphics g, Shape shape) {
		g.setColor(Color.GREEN);
		drawShape(g, shape);
		g.setColor(Color.BLACK);
	}

	private void drawFullPalette(Graphics g) {
		g.drawString("Action Blocks", 15, 30);
		drawActionBlock(g, 10, ACTION_BLOCK_MOVE_FORWARD_UPPER, BlockType.MoveForward);
		drawActionBlock(g, 10, ACTION_BLOCK_TURN_LEFT_UPPER, BlockType.TurnLeft);
		drawActionBlock(g, 10, ACTION_BLOCK_TURN_RIGHT_UPPER, BlockType.TurnRight);

		g.drawString("Control Blocks", 10, 205);
		drawControlBlock(g, 5, CONTROL_BLOCK_IF_UPPER, BlockType.If);
		drawControlBlock(g, 5, CONTROL_BLOCK_WHILE_UPPER, BlockType.While);

		g.drawString("Operator Blocks", 5, 455);
		drawOperatorBlock(g, 5, OPERATOR_BLOCK_NOT_UPPER, BlockType.Not);

		g.drawString("Condition Blocks", 5, 535);
		drawConditionBlock(g, 5, CONDITION_BLOCK_WALL_UPPER, BlockType.WallInFront);
	}

	private void drawActionBlock(Graphics g, int startX, int startY, BlockType type) {
		g.drawLine(startX, startY, startX, startY + 30);
		g.drawArc(startX + 30, startY - 10, 20, 20, 0, -180);
		g.drawLine(startX + 50, startY, startX + 80, startY);
		g.drawLine(startX + 80, startY, startX + 80, startY + 30);
		g.drawLine(startX, startY, startX + 30, startY);
		g.drawLine(startX, startY + 30, startX + 30, startY + 30);
		g.drawLine(startX + 50, startY + 30, startX + 80, startY + 30);
		g.drawArc(startX + 30, startY + 20, 20, 20, 0, -180);
		g.drawString(type.toString(), startX + 3, startY + 23);
	}
	
	private void drawControlBlock(Graphics g, int startX, int startY, BlockType type) {
		
		int total=0;
		g.drawArc(startX + 40, startY - 10, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + 20, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + 50+total, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + 80 + total, 20, 20, 0, -180);
		g.drawArc(startX + 80, startY + 5, 20, 20, -90, -180);
		
		g.drawLine(startX, startY, startX, startY + 90+total);
		g.drawLine(startX, startY, startX + 10, startY);
		g.drawLine(startX, startY + 90+total, startX + 10, startY + 90+total);
		
		g.drawLine(startX + 10, startY + 30, startX + 10, startY + 60+total);
		g.drawLine(startX + 60, startY, startX + 90, startY);
		g.drawLine(startX + 60, startY + 60+total, startX + 90, startY + 60+total);
		g.drawLine(startX + 90, startY, startX + 90, startY + 5);
		g.drawLine(startX + 90, startY + 25, startX + 90, startY + 30);
		g.drawLine(startX + 90, startY + 60+total, startX + 90, startY + 90+total);
		g.drawLine(startX + 10, startY, startX + 40, startY);
		g.drawLine(startX + 10, startY + 60+total, startX + 40, startY + 60+total);
		g.drawLine(startX + 10, startY + 30, startX + 40, startY + 30);
		g.drawLine(startX + 10, startY + 90+total, startX + 40, startY + 90+total);
		g.drawLine(startX + 60, startY + 30, startX + 90, startY + 30);
		g.drawLine(startX + 60, startY + 90+total, startX + 90, startY + 90+total);
		
		g.drawString(type.toString(), startX + 10, startY + 23);
	}
	

	private void drawControlBlock(Graphics g, int startX, int startY, Shape controlShape, HashSet<Shape> internals) {
		
		controlShape.determineTotalHeight(internals);
		controlShape.setCoordinatesShape(controlShape.createCoordinatePairs(startX, startY));
		int total = controlShape.getHeight();

		g.drawArc(startX + 40, startY - 10, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + 20, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + total-40, 20, 20, 0, -180);
		g.drawArc(startX + 40, startY + total-10, 20, 20, 0, -180);
		g.drawArc(startX + 80, startY + 5, 20, 20, -90, -180);
		
		g.drawLine(startX, startY, startX, startY + total);
		g.drawLine(startX, startY, startX + 10, startY);
		g.drawLine(startX, startY + total, startX + 10, startY + total);
		
		g.drawLine(startX + 10, startY + 30, startX + 10, startY + total-30);
		g.drawLine(startX + 60, startY, startX + 90, startY);
		g.drawLine(startX + 60, startY + total-30, startX + 90, startY + total-30);
		g.drawLine(startX + 90, startY, startX + 90, startY + 5);
		g.drawLine(startX + 90, startY + 25, startX + 90, startY + 30);
		g.drawLine(startX + 90, startY + total-30, startX + 90, startY + total);
		g.drawLine(startX + 10, startY, startX + 40, startY);
		g.drawLine(startX + 10, startY + total-30, startX + 40, startY + total-30);
		g.drawLine(startX + 10, startY + 30, startX + 40, startY + 30);
		g.drawLine(startX + 10, startY + total, startX + 40, startY + total);
		g.drawLine(startX + 60, startY + 30, startX + 90, startY + 30);
		g.drawLine(startX + 60, startY + total, startX + 90, startY + total);
		
		g.drawString(controlShape.getType().toString(), startX + 10, startY + 23);
	}

	private void drawOperatorBlock(Graphics g, int startX, int startY, BlockType type) {
		g.drawArc(startX + 80, startY + 5, 20, 20, -90, -180);
		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 5);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 90, startY + 25, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		g.drawString(type.toString(), startX + 35, startY + 19);
	}

	private void drawConditionBlock(Graphics g, int startX, int startY, BlockType type) {
		g.drawArc(startX, startY + 5, 20, 20, -90, -180);
		g.drawLine(startX + 10, startY, startX + 90, startY);
		g.drawLine(startX + 90, startY, startX + 90, startY + 30);
		g.drawLine(startX + 10, startY, startX + 10, startY + 5);
		g.drawLine(startX + 10, startY + 25, startX + 10, startY + 30);
		g.drawLine(startX + 10, startY + 30, startX + 90, startY + 30);
		g.drawString(type.toString(), startX + 15, startY + 19);
	}

	private void drawGameArea(Graphics g) {
		try {
			for (Cell cell : cells) {
				/*
				 * g.drawImage(ImageIO.read(getClass().getResource(cell.getResourcePath())),
				 * cell.getXcoord(), cell.getYcoord(), null);
				 */
				g.drawImage(cell.getImage(), cell.getXcoord(), cell.getYcoord(), null);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Shape determineShapeFromPalette(int y) {

		if (y > ACTION_BLOCK_MOVE_FORWARD_UPPER && y < ACTION_BLOCK_MOVE_FORWARD_LOWER)
			return new Shape("PALETTE", BlockType.MoveForward, PALETTE_OFFSET_BLOCKS, ACTION_BLOCK_MOVE_FORWARD_UPPER);
		if (y > ACTION_BLOCK_TURN_LEFT_UPPER && y < ACTION_BLOCK_TURN_LEFT_LOWER)
			return new Shape("PALETTE", BlockType.TurnLeft, PALETTE_OFFSET_BLOCKS, ACTION_BLOCK_TURN_LEFT_UPPER);
		if (y > ACTION_BLOCK_TURN_RIGHT_UPPER && y < ACTION_BLOCK_TURN_RIGHT_LOWER)
			return new Shape("PALETTE", BlockType.TurnRight, PALETTE_OFFSET_BLOCKS, ACTION_BLOCK_TURN_RIGHT_UPPER);

		if (y > CONTROL_BLOCK_IF_UPPER && y < CONTROL_BLOCK_IF_LOWER)
			return new Shape("PALETTE", BlockType.If, PALETTE_OFFSET_BLOCKS, CONTROL_BLOCK_IF_UPPER);
		if (y > CONTROL_BLOCK_WHILE_UPPER && y < CONTROL_BLOCK_WHILE_LOWER)
			return new Shape("PALETTE", BlockType.While, PALETTE_OFFSET_BLOCKS, CONTROL_BLOCK_WHILE_UPPER);

		if (y > OPERATOR_BLOCK_NOT_UPPER && y < OPERATOR_BLOCK_NOT_LOWER)
			return new Shape("PALETTE", BlockType.Not, PALETTE_OFFSET_BLOCKS, OPERATOR_BLOCK_NOT_UPPER);

		if (y > CONDITION_BLOCK_WALL_UPPER && y < CONDITION_BLOCK_WALL_LOWER)
			return new Shape("PALETTE", BlockType.WallInFront, PALETTE_OFFSET_BLOCKS, CONDITION_BLOCK_WALL_UPPER);

		return null;

	}

	private int[] calculateOffsetMouse(int x_Mouse, int y_Mouse, int x_Shape, int y_Shape) {
		int[] returnValue = new int[2];
		returnValue[0] = x_Mouse - x_Shape;
		returnValue[1] = y_Mouse - y_Shape;
		return returnValue;
	}
	

	private void drawShape(Graphics g, Shape shape) {
		BlockType type = shape.getType();
		switch (type) {
		case MoveForward:
			drawActionBlock(g, shape.getX_coord(), shape.getY_coord(), type);
			break;
		case TurnLeft:
			drawActionBlock(g, shape.getX_coord(), shape.getY_coord(), type);
			break;
		case TurnRight:
			drawActionBlock(g, shape.getX_coord(), shape.getY_coord(), type);
			break;

		case If:
			drawControlBlock(g, shape.getX_coord(), shape.getY_coord(), shape, shape.getInternals());
			break;
		case While:
			drawControlBlock(g, shape.getX_coord(), shape.getY_coord(), shape, shape.getInternals());
			break;

		case Not:
			drawOperatorBlock(g, shape.getX_coord(), shape.getY_coord(), type);
			break;

		case WallInFront:
			drawConditionBlock(g, shape.getX_coord(), shape.getY_coord(), type);
			break;
		default:
			;
		} // Nothing has to happen
	}


	

	@Override
	protected void handleMouseEvent(int id, int x, int y, int clickCount) {
		
		if(isHandleEvent()) {
		// super.handleMouseEvent(id, x, y, clickCount);
		// Graphics g = super.panel.getGraphics();
		if (x > PALETTE_START_X + PALETTE_OFFSET_BLOCKS && // Offset gaat nog anders moeten
				x <= PALETTE_END_X - PALETTE_OFFSET_BLOCKS && id == MouseEvent.MOUSE_PRESSED) {
			this.currentShape = determineShapeFromPalette(y);
			if (currentShape != null) {
				var temp = calculateOffsetMouse(x, y, currentShape.getX_coord(), currentShape.getY_coord());
				this.x_offsetCurrentShape = temp[0];
				this.y_offsetCurrentShape = temp[1];
			}
		}

		if ((id == MouseEvent.MOUSE_DRAGGED || id == MouseEvent.MOUSE_PRESSED) && currentShape != null) {
			currentShape.setX_coord(x - x_offsetCurrentShape);
			currentShape.setY_coord(y - y_offsetCurrentShape);
			currentShape.updateConnectionTypesToShapeBasedOnType();
			this.highlightedShape = determineHighlightShape();
		}
		
		boolean overControlArea = false;
		if(currentShape != null) {
		for (Shape shape : controlBlockAreas) {
			if(domainController.getAllBlockIdsInBody(shape.getId()).contains(currentShape.getId())) {
				overControlArea = true;
			}
		}}
		
		if (id == MouseEvent.MOUSE_DRAGGED && currentShape != null && overControlArea) {
			//check if highlightedShape is ControlBlock, zo ja-> expand die controlBlock... anders problemen met placeable
			for (Shape shape : controlBlockAreas) {
				shape.getInternals().add(this.currentShape);
			}
			
		}

		if (id == MouseEvent.MOUSE_RELEASED && x > PROGRAM_START_X && x < PROGRAM_END_X && currentShape != null) { // nog extra offset nodig

			Shape temp = new Shape(getCurrentShape().getId(), getCurrentShape().getType(), getCurrentShape().getX_coord(), getCurrentShape().getY_coord());
			temp.setConnectedVia(getCurrentShape().getConnectedVia());
			setTempStaticShape(highlightedShape);
			setTempDynamicShape(temp);
			
			// Doorgeven van gegevens van Shape naar DC en disableEvent() en uiteindelijk gewoon repaint.
			
			
			//Trigger wordt HARDCODED manueel opgeroepen
			//Check if there isn't already a block at that coordinate
			
			
			
			boolean placeable = !(getTempDynamicShape().getCoordinatesShape().stream().anyMatch(i -> this.alreadyFilledInCoordinates.contains(i)));
			
			if(placeable) {
			if(getTempDynamicShape().getId() == "PALETTE") {
			//tijdelijk opslaan van tempStaticShape en tempDynamicShape

			
				if(getTempDynamicShape().getType() == BlockType.If) {
					this.onBlockAdded(new BlockAddedEvent("IF"));
				}else if (getTempDynamicShape().getType() == BlockType.While) {
					this.onBlockAdded(new BlockAddedEvent("WHILE"));
				}else {
					this.onBlockAdded(new BlockAddedEvent(""+counter));
					counter++;
				}
				
			}
			else {
					if(getTempStaticShape() != null) {
					this.onBlockChangeEvent(new BlockChangeEvent(getTempDynamicShape().getId(), getTempDynamicShape().getId(), getTempStaticShape().getConnectedVia()));
					//this.onBlockChangeEvent(new BlockChangeEvent(getTempDynamicShape().getId(), getTempDynamicShape().getId(), getTempStaticShape().getConnectedVia()));
				}
					else {
						this.onBlockChangeEvent(new BlockChangeEvent(getTempDynamicShape().getId(), getTempDynamicShape().getId(), ConnectionType.NOCONNECTION));
					}
					}
			}
			else {
				if(currentShapeCoord != null) {
				currentShape.setX_coord(currentShapeCoord.getLeft());
				currentShape.setY_coord(currentShapeCoord.getRight());
				this.shapesInProgramArea.add(currentShape);
				for (Pair<Integer, Integer> pair : currentShape.getCoordinatesShape()) {
					alreadyFilledInCoordinates.add(pair);
				}
				}
			}
			
			
			setCurrentShape(null);
			setX_offsetCurrentShape(0);
			setY_offsetCurrentShape(0);
			this.currentShapeCoord = null;
			//
			// Voorlopig Hardcoded toevoegen aan shapesInProgramArea
			/*Shape toAdd = new Shape(LocalDateTime.now().toString(), temp.getType(), temp.getX_coord(),
					temp.getY_coord());
			toAdd.setCoordinatesShape(createPairs(toAdd.getType(), temp.getX_coord(), temp.getY_coord()));
			this.shapesInProgramArea.add(toAdd);*/
		}

		if (id == MouseEvent.MOUSE_PRESSED && x > PROGRAM_START_X && x < PROGRAM_END_X) {
			Shape shape = getShapeFromCoordinateFromProgramArea(x, y);

			if (shape != null) {
				this.currentShapeCoord = new  Pair<Integer, Integer>(shape.getX_coord(), shape.getY_coord());
				this.currentShape = shape;
				var temp = calculateOffsetMouse(x, y, currentShape.getX_coord(), currentShape.getY_coord());
				this.x_offsetCurrentShape = temp[0];
				this.y_offsetCurrentShape = temp[1];
				shapesInProgramArea.remove(shape);
				for (Pair<Integer, Integer> pair : shape.getCoordinatesShape()) {
					alreadyFilledInCoordinates.remove(pair);
				}
				for (Shape shape2 : controlBlockAreas) {
					if(domainController.getAllBlockIdsInBody(shape2.getId()).contains(shape.getId())) {
						shape.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.remove(e));
						shape2.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.remove(e));
						shape2.getInternals().remove(shape);
						shape2.determineTotalHeight(shape2.getInternals());
						shape2.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.add(e));
					}
				}
			}
		}

		repaint();
	}
		else {
			//Consume event;
		}
	}

	// De relation between shape and shapeToClip is already established in a
	// different method;
	private void clipOn(Shape shape, ConnectionType connection, Shape shapeToClip) {
		BlockType type = shape.getType();
		switch (type) {
		case MoveForward:
			switch (connection) {
			case UP:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
				shapeToClip.setX_coord(shape.getX_coord());
				shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());}
				else {
					shapeToClip.setX_coord(shape.getX_coord()-10);
					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
				}
				//drawShape(g, shapeToClip);
				break;
			case DOWN:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
					shapeToClip.setX_coord(shape.getX_coord());
					shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());}
					else {
						shapeToClip.setX_coord(shape.getX_coord()-10);
						shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
					}
				//drawShape(g, shapeToClip);
				break;
			default:
				; // Do nothing
			}
			break;
		case TurnLeft:
			switch (connection) {
			case UP:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
					shapeToClip.setX_coord(shape.getX_coord());
					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());}
					else {
						shapeToClip.setX_coord(shape.getX_coord()-10);
						shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
					}
					//drawShape(g, shapeToClip);
					break;
				case DOWN:
					if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
						shapeToClip.setX_coord(shape.getX_coord());
						shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());}
						else {
							shapeToClip.setX_coord(shape.getX_coord()-10);
							shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
						}
					//drawShape(g, shapeToClip);
					break;
				default:
					; // Do nothing
				}
				break;
		case TurnRight:
			switch (connection) {
			case UP:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
					shapeToClip.setX_coord(shape.getX_coord());
					shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());}
					else {
						shapeToClip.setX_coord(shape.getX_coord()-10);
						shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
					}
					//drawShape(g, shapeToClip);
					break;
				case DOWN:
					if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
						shapeToClip.setX_coord(shape.getX_coord());
						shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());}
						else {
							shapeToClip.setX_coord(shape.getX_coord()-10);
							shapeToClip.setY_coord(shape.getY_coord() + shape.getHeight());
						}
					//drawShape(g, shapeToClip);
					break;
				default:
					; // Do nothing
				}
				break;
		case If:
			switch (connection) {
			case UP:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord() - 30);
				}else {
				shapeToClip.setX_coord(shape.getX_coord());
				shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
				}
				//drawShape(g, shapeToClip);
				break;
			case DOWN:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord() +shape.getHeight());
				//drawShape(g, shapeToClip);
				}
				else {
					shapeToClip.setX_coord(shape.getX_coord());
					shapeToClip.setY_coord(shape.getY_coord() +shape.getHeight());
				}
				break;
			case BODY:
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord()+30);
				//drawShape(g, shapeToClip);
				break;
			case CONDITION:
				shapeToClip.setX_coord(shape.getX_coord()+shape.getWidth()-10);
				shapeToClip.setY_coord(shape.getY_coord());
				//drawShape(g, shapeToClip);
				break;
			default:
				; // Do nothing
			}
			break;
		case While:
			switch (connection) {
			case UP:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord() - 30);
				}else {
				shapeToClip.setX_coord(shape.getX_coord());
				shapeToClip.setY_coord(shape.getY_coord() - shapeToClip.getHeight());
				}
				//drawShape(g, shapeToClip);
				break;
			case DOWN:
				if(shapeToClip.getType() == BlockType.MoveForward || shapeToClip.getType() == BlockType.TurnLeft || shapeToClip.getType() == BlockType.TurnRight) {
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord() +shape.getHeight());
				//drawShape(g, shapeToClip);
				}
				else {
					shapeToClip.setX_coord(shape.getX_coord());
					shapeToClip.setY_coord(shape.getY_coord() +shape.getHeight());
				}
				break;
			case BODY:
				shapeToClip.setX_coord(shape.getX_coord()+10);
				shapeToClip.setY_coord(shape.getY_coord()+30);
				//drawShape(g, shapeToClip);
				break;
			case CONDITION:
				shapeToClip.setX_coord(shape.getX_coord()+shape.getWidth()-10);
				shapeToClip.setY_coord(shape.getY_coord());
				//drawShape(g, shapeToClip);
				break;
			default:
				; // Do nothing
			}
			break;
		case Not:
			switch (connection) {
			case LEFT:
				shapeToClip.setX_coord(shape.getX_coord()-80);
				shapeToClip.setY_coord(shape.getY_coord());
				//drawShape(g, shapeToClip);
				break;
			case CONDITION:
				shapeToClip.setX_coord(shape.getX_coord()+80);
				shapeToClip.setY_coord(shape.getY_coord());
				//drawShape(g, shapeToClip);
				break;
			default:
				; // Do nothing
			}
			break;
		case WallInFront:
			switch (connection) {
			case LEFT:
				shapeToClip.setX_coord(shape.getX_coord()-80);
				shapeToClip.setY_coord(shape.getY_coord());
				//drawShape(g, shapeToClip);
				break;
			default:
				; // Do nothing
			}
			break;
		default:
			;
		} // Nothing has to happen
	}

	private Shape determineHighlightShape() {
		HashSet<Pair<Integer, Integer>> connectionTriggerSetUP = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetDOWN = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetLEFT = new HashSet<Pair<Integer, Integer>>();
		HashSet<Pair<Integer, Integer>> connectionTriggerSetCONDITION = new HashSet<Pair<Integer, Integer>>();

		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaUpMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaDownMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaBodyMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaConditionMap = new HashMap<Shape, Pair<Integer, Integer>>();
		HashMap<Shape, Pair<Integer, Integer>> shapesInProgramAreaLeftMap = new HashMap<Shape, Pair<Integer, Integer>>();

		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.UP).getRight();

			for (int i = x_current - 8; i < x_current + 8; i++) {
				for (int j = y_current - 8; j < y_current + 8; j++) {
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

			for (int i = x_current - 8; i < x_current + 8; i++) {
				for (int j = y_current - 8; j < y_current + 8; j++) {
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

			for (int i = x_current - 8; i < x_current + 8; i++) {
				for (int j = y_current - 8; j < y_current + 8; j++) {
					connectionTriggerSetLEFT.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.CONDITION)) {
					shapesInProgramAreaConditionMap.put(shape,
							shape.getCoordinateConnectionMap().get(ConnectionType.CONDITION));
				}
			}
		}
		if (getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION) != null) {

			int x_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION).getLeft();
			int y_current = getCurrentShape().getCoordinateConnectionMap().get(ConnectionType.CONDITION).getRight();

			for (int i = x_current - 8; i < x_current + 8; i++) {
				for (int j = y_current - 8; j < y_current + 8; j++) {
					connectionTriggerSetCONDITION.add(new Pair<Integer, Integer>(i, j));
				}
			}
			for (Shape shape : getShapesInProgramArea()) {
				if (shape.getCoordinateConnectionMap().keySet().contains(ConnectionType.LEFT)) {
					shapesInProgramAreaLeftMap.put(shape,
							shape.getCoordinateConnectionMap().get(ConnectionType.LEFT));
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
							return null;
						}
					}
				}
			}
		}
	}

	// will return null if there is no Shape in those coordinates => MOET BETER
	// GEDAAN WORDEN MET EXCEPTIONS
	private Shape getShapeFromCoordinateFromProgramArea(int x, int y) {

		try {
			return this.getShapesInProgramArea().stream()
					.filter(e -> e.getCoordinatesShape().contains(new Pair<Integer, Integer>(x, y))).findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}

	}

	@Override
	protected void handleKeyEvent(int id, int keyCode, char keyChar) {
		// TODO Auto-generated method stub
		// super.handleKeyEvent(id, keyCode, keyChar);
	}

	// methods that are inherited from GUIListener:

	@Override
	public void onBlockAdded(BlockAddedEvent event) {
		//normaal is ID van event, en geen random DateTime
		
		Shape toAdd = new Shape(event.getAddedBlockID(), getTempDynamicShape().getType(), getTempDynamicShape().getX_coord(),getTempDynamicShape().getY_coord());
		
		/*for (Pair<Integer, Integer> pair : toAdd.getCoordinatesShape()) {
			this.alreadyFilledInCoordinates.remove(pair);
			}*/
		
		if(this.getTempStaticShape() !=null) {
			clipOn(getTempStaticShape(), getTempDynamicShape().getConnectedVia(), toAdd);
			}
		
		for (Shape shape : controlBlockAreas) {
			if(domainController.getAllBlockIdsInBody(shape.getId()).contains(toAdd.getId())) {
				shape.getInternals().add(toAdd);
			}
		}
		
		//update all ControlBlockAreas:
		//set the length of all control block correct
		for (Shape shape : controlBlockAreas) {
			shape.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.remove(e));
			shape.determineTotalHeight(shape.getInternals());
			shape.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.add(e));
		}
		//
		

		
		toAdd.setCoordinatesShape(toAdd.createCoordinatePairs(toAdd.getX_coord(), toAdd.getY_coord()));
		this.shapesInProgramArea.add(toAdd);
		
		if(toAdd.getType() == BlockType.If || toAdd.getType() == BlockType.While) {
			this.controlBlockAreas.add(toAdd);
		}
		
		
		toAdd.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.add(e));
		this.setTempDynamicShape(null);
		this.setTempStaticShape(null);
		super.repaint();
	}
	

	@Override
	public void onBlockRemoved(BlockRemovedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPanelChangedEvent(PanelChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) {
		
				Shape toAdd = new Shape(event.getChangedBlockId(), getTempDynamicShape().getType(), getTempDynamicShape().getX_coord(),getTempDynamicShape().getY_coord());
				
				if(this.getTempStaticShape() !=null) {
					clipOn(getTempStaticShape(), getTempDynamicShape().getConnectedVia(), toAdd);
					}
				
				for (Shape shape : controlBlockAreas) {
					if(domainController.getAllBlockIdsInBody(shape.getId()).contains(toAdd.getId())) {
						shape.getInternals().add(toAdd);
					}
				}
				
				//update all ControlBlockAreas:
				//set the length of all control block correct
				for (Shape shape : controlBlockAreas) {
					shape.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.remove(e));
					shape.determineTotalHeight(shape.getInternals());
					shape.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.add(e));
				}
				//
				
				toAdd.setCoordinatesShape(toAdd.createCoordinatePairs(toAdd.getX_coord(), toAdd.getY_coord()));
				this.shapesInProgramArea.add(toAdd);
				toAdd.getCoordinatesShape().forEach(e-> this.alreadyFilledInCoordinates.add(e));
				this.setTempDynamicShape(null);
				this.setTempStaticShape(null);
				super.repaint();

	}

	@Override
	public void onUpdateHighlightingEvent(UpdateHighlightingEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRobotChangeEvent(RobotChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRobotAddedEvent(RobotAddedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onElementAddedEvent(ElementAddedEvent event) {
		// TODO Auto-generated method stub

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
	public Shape getTempStaticShape() {
		return tempStaticShape;
	}

	public void setTempStaticShape(Shape tempShape) {
		this.tempStaticShape = tempShape;
	}
	
	public Shape getTempDynamicShape() {
		return tempDynamicShape;
	}

	public void setTempDynamicShape(Shape tempDynamicShape) {
		this.tempDynamicShape = tempDynamicShape;
	}

	public Pair<Integer, Integer> getCurrentShapeCoord() {
		return currentShapeCoord;
	}

	public void setCurrentShapeCoord(Pair<Integer, Integer> currentShapeCoord) {
		this.currentShapeCoord = currentShapeCoord;
	}

}