package domainLayer.blocks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BlockFactoryTest.class, BlockIDGeneratorTest.class, BlockRepositoryTest.class, BlocksTest.class,
		TestMoveBlockBlockRepository.class })
public class Blocks {

}
