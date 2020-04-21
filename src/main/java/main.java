import com.kuleuven.swop.group17.GameWorldApi.GameWorld;
import com.kuleuven.swop.group17.GameWorldApi.GameWorldType;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;

public class main {

	public static void main(final String[] args) {	
//		GameWorld gameWorld = GameWorldType.createInstance("com.kuleuven.swop.group17.RobotGameWorld.applicationLayer.RobotGameWorld");
		GameWorld gameWorld = GameWorldType.createInstance("com.kuleuven.swop.group17.CoolGameWorld.applicationLayer.CoolGameWorld");

		
		
		DomainController dc = new DomainController(gameWorld);

		

		//Test push voor de UI-branch
		java.awt.EventQueue.invokeLater(() -> {
	         new CanvasWindow("Blockr", dc).show();
	  });

		
		
	}

}
