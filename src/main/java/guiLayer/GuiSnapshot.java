/**
 * 
 */
package guiLayer;

import java.util.HashSet;
import java.util.Set;

/**
 * /** GuiAction
 * 
 * @version 0.1
 * @author group17
 *
 */
public class GuiSnapshot {
	private Shape connectedShapeAfterSnapshot;
	private Boolean isDomainInvolved;
	private Set<Shape> shapesInMovement;
	private Shape currentShape;
	private Shape highlightedShape;

	/**
	 * @param currentShape                 TODO
	 * @param highlightedShape             TODO
	 * @param connectedShapeAfterSnapshot
	 * @param shape
	 * @param connectedShapeBeforeSnapshot
	 */
	public GuiSnapshot(Shape currentShape, Shape highlightedShape, Set<Shape> shapesInMovement,
			Shape connectedShapeAfterSnapshot, Boolean isDomainInvolved) {
		super();
		setShapesInMovement(shapesInMovement);

		this.isDomainInvolved = isDomainInvolved;
		if (currentShape != null) {
			this.currentShape = currentShape.clone();
		}
		if (highlightedShape != null) {
			this.highlightedShape = highlightedShape.clone();
		}

	}

	public void setConnectedShapeAfterSnapshot(Shape connectedShapeAfterSnapshot) {
		if (connectedShapeAfterSnapshot != null) {
			this.connectedShapeAfterSnapshot = connectedShapeAfterSnapshot.clone();
		}
	}

	public Shape getConnectedShapeAfterSnapshot() {
		return connectedShapeAfterSnapshot;
	}

	public Boolean isDomainInvolved() {
		return isDomainInvolved;
	}

	public void setShapesInMovement(Set<Shape> shapesInMovement) {
		if (shapesInMovement != null) {
			this.shapesInMovement = new HashSet<Shape>(shapesInMovement);
		}
	}

	public Set<Shape> getShapesInMovement() {
		return shapesInMovement;
	}

	public Shape getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(Shape currentShape) {
		if (currentShape != null) {
			this.currentShape = currentShape.clone();
		}
	}

	public Shape getHighlightedShape() {
		return highlightedShape;
	}

	public void setHighlightedShape(Shape highlightedShape) {
		if (highlightedShape != null) {
			this.highlightedShape = highlightedShape.clone();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GuiSnapshot [connectedShapeAfterSnapshot=");
		builder.append(connectedShapeAfterSnapshot);
		builder.append(", isDomainInvolved=");
		builder.append(isDomainInvolved);
		builder.append("\n");
		builder.append(", shapesInMovement=");
		builder.append("\n");
		for(Shape shape : shapesInMovement) {
			builder.append(shape);
			builder.append("\n");
		}
		builder.append(", currentShape=");
		builder.append(currentShape);
		builder.append(", highlightedShape=");
		builder.append(highlightedShape);
		builder.append("]");
		return builder.toString();
	}
	
	

}
