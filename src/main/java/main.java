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
	  });

		
		
	}
	
	
	


}
