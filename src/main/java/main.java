import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;

import applicationLayer.DomainController;
import domainLayer.elements.ElementType;
import guiLayer.CanvasWindow;

public class main {

	public static void main(String[] args) {	
		GameWorld gameWorld = GameWorldType.createInstance("com.kuleuven.swop.group17.RobotGameWorld.applicationLayer.RobotGameWorld");
		
		
		DomainController dc = new DomainController(gameWorld);

//		Hier wordt ook de UI aangemaakt.
		

		//Test push voor de UI-branch
		java.awt.EventQueue.invokeLater(() -> {
	         new CanvasWindow("Blockr", dc).show();
	         initializeElements(dc);
	  });

		
		
	}
	
	
	
	private static void initializeElements(DomainController dc) {
		dc.addElement(ElementType.ROBOT, 2, 3);
		dc.addElement(ElementType.WALL,0,0);
		dc.addElement(ElementType.WALL,4,0);
		dc.addElement(ElementType.WALL,1,2);
		dc.addElement(ElementType.WALL,2,2);
		dc.addElement(ElementType.WALL,3,2);
		dc.addElement(ElementType.GOAL,2,1);
	}

}
