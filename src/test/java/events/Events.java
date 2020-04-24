package events;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BlockAddedEventTest.class, BlockChangeEventTest.class, BlockRemovedEventTest.class,
		PanelChangeEventTest.class, UpdateHighlightingEventTest.class })
public class Events {

}
