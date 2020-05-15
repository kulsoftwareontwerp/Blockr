package commands;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CommandHandlerTest.class, AddBlockCommandTest.class, ExecuteBlockCommandTest.class, 
	MoveBlockCommandTest.class, RemoveBlockCommand.class })
public class Commands {

}
