/**
 * 
 */
package domainLayer.blocks;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * BlockIDGeneratorTest
 *
 * @version 0.1
 * @author group17
 */
public class BlockIDGeneratorTest {

	private BlockIDGenerator gen;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		gen = BlockIDGenerator.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link domainLayer.blocks.BlockIDGenerator#generateBlockID()}.
	 */
	@Test
	public void testGenerateBlockID() {
		String id1 = gen.generateBlockID();
		String id2 = gen.generateBlockID();
		assertTrue(id1 != null && id2 != null && !id1.equals(id2));
	}

}
