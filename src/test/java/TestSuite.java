import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import applicationLayer.BlockControllerTest;
import applicationLayer.DomainControllerTest;
import applicationLayer.GameControllerTest;
import commands.CommandHandlerTest;
import domainLayer.blocks.BlockFactoryTest;
import domainLayer.blocks.BlockIDGenerator;
import domainLayer.blocks.BlockIDGeneratorTest;
import domainLayer.blocks.BlockRepositoryTest;
import domainLayer.blocks.BlocksTest;
import domainLayer.gamestates.InExecutionStateTest;
import domainLayer.gamestates.InValidProgramStateTest;
import domainLayer.gamestates.ResettingStateTest;
import domainLayer.gamestates.ValidProgramStateTest;
import types.BlockType;

@RunWith(Suite.class)
@SuiteClasses({ DomainControllerTest.class, GameControllerTest.class, BlockControllerTest.class, InExecutionStateTest.class,
	ResettingStateTest.class, ValidProgramStateTest.class, InValidProgramStateTest.class, BlockRepositoryTest.class, BlocksTest.class, BlockIDGeneratorTest.class,
	BlockFactoryTest.class, CommandHandlerTest.class, guiLayer.commands.CommandHandlerTest.class})
public class TestSuite {
	
}