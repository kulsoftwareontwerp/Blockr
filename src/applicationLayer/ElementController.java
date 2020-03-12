package applicationLayer;

import java.util.*;

import domainLayer.elements.ElementRepository;
import domainLayer.elements.ElementType;
import events.DomainListener;
import events.DomainSubject;
import events.GUIListener;
import events.GUISubject;

public class ElementController implements GUISubject, DomainSubject {

	private Collection<GUIListener> guiListeners;
	private Collection<DomainListener> domainListeners;
	private ElementRepository elementRepository;
	
	
	public ElementController() {
		guiListeners=new HashSet<GUIListener>();
		domainListeners=new HashSet<DomainListener>();
		elementRepository=ElementRepository.getInstance();
		
		initializeElements();
	}

	public void fireElementAddedEvent() {
		// TODO - implement ElementController.fireElementAddedEvent
		throw new UnsupportedOperationException();
	}

	public void fireRobotAddedEvent() {
		// TODO - implement ElementController.fireRobotAddedEvent
		throw new UnsupportedOperationException();
	}

	public void initializeNewElementRepository() {
		// TODO - implement ElementController.initializeNewElementRepository
		throw new UnsupportedOperationException();
	}

	private void initializeElements() {
		elementRepository.initializeRobot();
		
		elementRepository.addElement(ElementType.WALL,0,0);
		elementRepository.addElement(ElementType.WALL,4,0);
		elementRepository.addElement(ElementType.WALL,1,2);
		elementRepository.addElement(ElementType.WALL,2,2);
		elementRepository.addElement(ElementType.WALL,3,2);
		
		elementRepository.addElement(ElementType.GOAL,2,1);
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