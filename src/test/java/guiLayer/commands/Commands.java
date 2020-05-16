package guiLayer.commands;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CommandHandlerTest.class, DomainMoveCommandTest.class, GuiMoveCommandTest.class})
public class Commands {

}
