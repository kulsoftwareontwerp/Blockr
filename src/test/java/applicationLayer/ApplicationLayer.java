package applicationLayer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BlockControllerTest.class, DomainControllerTest.class, GameControllerTest.class})
public class ApplicationLayer {

}
