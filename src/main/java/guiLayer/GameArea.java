package guiLayer;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;

import domainLayer.elements.Orientation;
import events.ElementAddedEvent;
import events.RobotAddedEvent;

public class GameArea implements Constants {
	private Collection<Cell> cells;
	
	public GameArea() {
		cells = new ArrayList<Cell>();
		initCells();
	}
	
	
	public void draw(Graphics g) {
		try {
			for (Cell cell : cells) {

				g.drawImage(cell.getImage(), cell.getXcoord(), cell.getYcoord(), null);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCell(Cell cell) {
		cells.add(cell);
	}
	
	public void moveRobot(int x, int y, Orientation orientation) {
		try {
			Cell cell = getCells().stream().filter(e -> e.getType().contains("robot")).findFirst().get();
			cell.setType(null);
			cell.setXcoord(x);
			cell.setYcoord(y);
			getCells().add(new Cell(x, y, "robot"+orientation.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void initCells() {

		// Upper Fill Cells
		for (int x = 0; x <= 4; x++) {
			for (int y = 0; y <= 3; y++) {
				cells.add(new Cell(x, y, "wall"));
			}
		}
		
		//Intermediate Fill Cells (REAL CELLS)
		for (int x = 0; x <= 4; x++) {
			for (int y = 4; y <= 7; y++) {
				cells.add(new Cell(x, y, "sand"));
			}
		}
		
		// Lower Fill Cells
		for (int x = 0; x <= 4; x++) {
			for (int y = 8; y <= 11; y++) {
				cells.add(new Cell(x, y, "wall"));
			}
		}
;

	}


	public Collection<Cell> getCells() {
		return cells;
	}

}
