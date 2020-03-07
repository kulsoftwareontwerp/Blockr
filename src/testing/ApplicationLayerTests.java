package testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddBlockTest.class, ExecuteBlockTest.class, MoveBlockTest.class, RemoveBlockTest.class,
		ResetGameTest.class, UpdateStateTest.class })

public class ApplicationLayerTests {

}
