import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import applicationLayer.ApplicationLayer;
import commands.Commands;
import domainLayer.DomainLayer;
import guiLayer.GuiLayer;

@RunWith(Suite.class)
@SuiteClasses({ ApplicationLayer.class,DomainLayer.class,GuiLayer.class,
	Commands.class})
public class TestSuite {
	
}