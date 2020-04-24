import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import applicationLayer.ApplicationLayer;
import applicationLayer.BlockControllerTest;
import applicationLayer.DomainControllerTest;
import applicationLayer.GameControllerTest;
import domainLayer.DomainLayer;
import domainLayer.blocks.BlockFactoryTest;
import domainLayer.blocks.BlockIDGenerator;
import domainLayer.blocks.BlockIDGeneratorTest;
import domainLayer.blocks.BlockRepositoryTest;
import domainLayer.blocks.BlocksTest;
import domainLayer.gamestates.InExecutionStateTest;
import domainLayer.gamestates.ResettingStateTest;
import domainLayer.gamestates.ValidProgramStateTest;
import events.Events;
import guiLayer.GuiLayer;
import types.BlockType;

@RunWith(Suite.class)
@SuiteClasses({ ApplicationLayer.class,DomainLayer.class,GuiLayer.class,Events.class})

public class TestSuite {
	
}