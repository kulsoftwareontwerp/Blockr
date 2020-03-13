package applicationLayer;

import java.util.*;

import domainLayer.elements.ElementRepository;
import domainLayer.elements.ElementType;
import domainLayer.elements.Orientation;
import events.DomainListener;
import events.DomainSubject;
import events.ElementAddedEvent;
import events.GUIListener;
import events.GUISubject;
import events.RobotAddedEvent;

public class ElementController implements GUISubject, DomainSubject {

	private Collection<GUIListener> guiListeners;
	private Collection<DomainListener> domainListeners;
	private ElementRepository elementRepository;
	
	
	public ElementController() {
		guiListeners=new HashSet<GUIListener>();
		domainListeners=new HashSet<DomainListener>();
		elementRepository=ElementRepository.getInstance();
	}

	private void fireElementAddedEvent(ElementType element, int x,int y) {
		ElementAddedEvent event = new ElementAddedEvent(x, y, element);
		for(GUIListener listener:guiListeners) {
			listener.onElementAddedEvent(event);
		}
	}

	private void fireRobotAddedEvent(int x,int y) {
		RobotAddedEvent event = new RobotAddedEvent(x, y, Orientation.UP);
		
		for(GUIListener listener:guiListeners) {
			listener.onRobotAddedEvent(event);
		}
	}

	public void initializeNewElementRepository() {
		// TODO - implement ElementController.initializeNewElementRepository
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Add an element to the domain.
	 * @param 	element
	 * 			The type of element.
	 * @param 	X
	 * 			The X coordinate
	 * @param 	Y
	 * 			The Y coordinate
	 * @event 	ElementAddedEvent
	 * 			When the operation is successful and elementType is not robot ElementAddedEvent will be thrown to all the listeners.
	 * @event 	RobotAddedEvent
	 * 			When the operation is successful and elementType is robot the RobotAddedEvent with an Orientation of UP will be thrown to all the listeners.
	 */
	public void addElement(ElementType element, int x, int y) {
		elementRepository.addElement(element, x, y);
		
		if(element==ElementType.ROBOT) {
			fireRobotAddedEvent(x, y);
		}
		else {
			fireElementAddedEvent(element, x, y);
		}		
	}
	


	


	@Override
	public void addDomainListener(DomainListener listener) {
		domainListeners.add(listener);
		
	}

	@Override
	public void removeDomainListener(DomainListener listener) {
		domainListeners.remove(listener);
		
	}

	@Override
	public void removeListener(GUIListener listener) {
		guiListeners.remove(listener);
		
	}

	@Override
	public void addListener(GUIListener listener) {
		guiListeners.add(listener);
		
	}

}