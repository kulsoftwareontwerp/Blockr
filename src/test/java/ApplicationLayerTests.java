import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddBlockTest.class, ExecuteBlockTest.class, domainLayer.blocks.MoveBlockBRTest.class, RemoveBlockTest.class,
		ResetGameTest.class, testGetAllBlocksInBody.class, testGetAllBlocksUnderneath.class, UpdateStateTest.class })

public class ApplicationLayerTests {

}
