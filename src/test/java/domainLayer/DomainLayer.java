package domainLayer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import domainLayer.blocks.Blocks;
import domainLayer.gamestates.GameStates;

@RunWith(Suite.class)
@SuiteClasses({Blocks.class,GameStates.class})
public class DomainLayer {

}
