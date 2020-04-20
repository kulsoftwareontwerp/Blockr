import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import applicationLayer.BlockControllerTest;
import applicationLayer.DomainControllerTest;
import applicationLayer.GameControllerTest;
import domainLayer.gamestates.InExecutionStateTest;
import domainLayer.gamestates.ResettingStateTest;
import domainLayer.gamestates.ValidProgramStateTest;

@RunWith(Suite.class)
@SuiteClasses({ DomainControllerTest.class, GameControllerTest.class, BlockControllerTest.class, InExecutionStateTest.class,
	ResettingStateTest.class, ValidProgramStateTest.class})

public class TestSuite {
	
}