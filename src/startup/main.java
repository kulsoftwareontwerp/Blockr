package startup;

import applicationLayer.DomainController;
import guiLayer.CanvasWindow;

public class main {

	public static void main(String[] args) {
		DomainController dc = new DomainController();

//		Hier wordt ook de UI aangemaakt.
		
		//Test push voor de UI-branch
		java.awt.EventQueue.invokeLater(() -> {
	         new CanvasWindow("Blockr", dc).show();
	  });
	}

}
