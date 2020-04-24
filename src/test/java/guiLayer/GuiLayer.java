package guiLayer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import guiLayer.types.Types;

@RunWith(Suite.class)
@SuiteClasses({ CanvasWindowTest.class, PaletteAreaTest.class, ProgramAreaTest.class,Types.class})
public class GuiLayer {

}
