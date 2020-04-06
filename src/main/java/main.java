


import java.net.URL;

import applicationLayer.DomainController;
import domainLayer.elements.ElementType;
import guiLayer.CanvasWindow;

public class main {

	public static void main(String[] args) {	
		
		
		DomainController dc = new DomainController();

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
