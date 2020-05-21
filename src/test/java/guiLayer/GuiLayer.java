package guiLayer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import guiLayer.shapes.Shapes;
import guiLayer.types.Types;

@RunWith(Suite.class)
@SuiteClasses({ CanvasWindowTest.class, PaletteAreaTest.class, ProgramAreaTest.class,Types.class,Shapes.class})
public class GuiLayer {

}
