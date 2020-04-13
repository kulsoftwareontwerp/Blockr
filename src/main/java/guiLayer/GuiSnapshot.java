/**
 * 
 */
package guiLayer;

import java.util.HashSet;
import java.util.Set;

/**
/**
 * GuiAction
 * 
 * @version 0.1
 * @author group17
 *
 */
public class GuiSnapshot {
	private Shape connectedShapeAfterSnapshot;
	private Boolean isDomainInvolved;
	private Set<Shape> shapesInMovement;
	/**
	 * @param shape
	 * @param connectedShapeAfterSnapshot
	 * @param connectedShapeBeforeSnapshot
	 */
	public GuiSnapshot(Set<Shape> shapesInMovement, Shape connectedShapeAfterSnapshot,Boolean isDomainInvolved) {
		super();
		this.shapesInMovement=new HashSet<Shape>(shapesInMovement);
		if(connectedShapeAfterSnapshot!=null) {
		this.connectedShapeAfterSnapshot = connectedShapeAfterSnapshot.clone();
		}
		this.isDomainInvolved=isDomainInvolved;
	}

	public Shape getConnectedShapeAfterSnapshot() {
		return connectedShapeAfterSnapshot;
	}

	public Boolean isDomainInvolved() {
		return isDomainInvolved;
	}

	public Set<Shape> getShapesInMovement() {
		return shapesInMovement;
	}
	
	
	



	
	
	

}
