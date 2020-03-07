package guiLayer;

import applicationLayer.*;
import events.BlockAddedEvent;
import events.BlockChangeEvent;
import events.BlockRemovedEvent;
import events.ElementAddedEvent;
import events.GUIListener;
import events.PanelChangeEvent;
import events.RobotAddedEvent;
import events.RobotChangeEvent;
import events.UpdateHighlightingEvent;

public class CanvasWindow implements GUIListener {

	private PaletteArea paletteArea;
	private ProgramArea programArea;
	private GameArea gameArea;
	private DomainController domainController;
	@Override
	public void onBlockAdded(BlockAddedEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onBlockRemoved(BlockRemovedEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPanelChangedEvent(PanelChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onBlockChangeEvent(BlockChangeEvent event) {
		
		
	}
	@Override
	public void onUpdateHighlightingEvent(UpdateHighlightingEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRobotChangeEvent(RobotChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRobotAddedEvent(RobotAddedEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onElementAddedEvent(ElementAddedEvent event) {
		// TODO Auto-generated method stub
		
	}

}