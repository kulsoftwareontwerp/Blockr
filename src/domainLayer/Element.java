package domainLayer;

public abstract class Element {

	private int xCoordinate;
	private int yCoordinate;

	/**
	 * 
	 * @param xCoordinate
	 * @param yCoordinate
	 */
	public Element(int xCoordinate, int yCoordinate) {
		setxCoordinate(xCoordinate);
		setyCoordinate(yCoordinate);
	}

	public int getXCoordinate() {
		return this.xCoordinate;
	}

	public int getYCoordinate() {
		return this.yCoordinate;
	}

	
	
	protected void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	protected void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}


}