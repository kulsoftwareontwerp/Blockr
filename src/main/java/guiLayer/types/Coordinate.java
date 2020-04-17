package guiLayer.types;

/**
 * The concept of a coordinate, a point matching a certain location in the
 * robotGameworld. A coordinate is immutable.
 * 
 * @version 0.1
 * @author group17
 */
public class Coordinate {
	private final int x;
	private final int y;

	/**
	 * Create a coordinate with the given x and y values.
	 * 
	 * @param x The x value for the coordinate.
	 * @param y The y value for the coordinate.
	 */
	Coordinate(int x, int y) {
		super();
		this.x=x;
		this.y=y;
	}

	/**
	 * Retrieve the x value for the coordinate.
	 * 
	 * @return the x value for the coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the x value for the coordinate.
	 * 
	 * @param x The x value for the coordinate.
	 * @return a new Coordinate with the changed X value.
	 */
	public Coordinate setX(int x) {
		return new Coordinate(x, this.y);
	}

	/**
	 * Retrieve the y value for the coordinate.
	 * 
	 * @return the y value for the coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the y value for the coordinate.
	 * 
	 * @param y The y value for the coordinate.
	 * @return a new Coordinate with the changed X value.
	 */
	public Coordinate setY(int y) {
		return new Coordinate(this.x,y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
