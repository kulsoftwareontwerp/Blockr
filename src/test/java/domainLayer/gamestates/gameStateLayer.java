package domainLayer.gamestates;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses({ InExecutionStateTest.class, InValidProgramStateTest.class,ResettingStateTest.class,ValidProgramStateTest.class })
public class gameStateLayer {

}
