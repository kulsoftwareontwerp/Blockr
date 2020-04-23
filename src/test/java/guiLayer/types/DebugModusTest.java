/**
 * 
 */
package guiLayer.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
/**
 * DebugModusTest
 * 
 * @version 0.1
 * @author group17
 *
 */
public class DebugModusTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link guiLayer.types.DebugModus#getNext()}.
	 */
	@Test
	public void testGetNext() {
		assertEquals(DebugModus.NONE, DebugModus.FILLINGS.getNext());
		assertEquals(DebugModus.IDS, DebugModus.NONE.getNext());
		assertEquals(DebugModus.CONNECTIONS, DebugModus.IDS.getNext());
		assertEquals(DebugModus.CONNECTIONSTATUS, DebugModus.CONNECTIONS.getNext());
		assertEquals(DebugModus.FILLINGS, DebugModus.CONNECTIONSTATUS.getNext());
	}
	
	
	/**
	 * Test method for {@link guiLayer.types.DebugModus#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("Debug disabled", DebugModus.NONE.toString());
		assertEquals("IDS shown", DebugModus.IDS.toString());
		assertEquals("Connections shown", DebugModus.CONNECTIONS.toString());
		assertEquals("ConnectionStatus shown", DebugModus.CONNECTIONSTATUS.toString());
		assertEquals("Fillings shown", DebugModus.FILLINGS.toString());
		
		
		
	}
	
	

}
