package domainLayer.blocks;


/**
 * The BlockIDGenerator creates unique identifiers for blocks.
 * 
 * @version 0.1
 * @author group17
 */
public class BlockIDGenerator {

	private static BlockIDGenerator generator;
	private Integer id;

	private BlockIDGenerator() {
		this.id = -1;
	}


	/**
	 * Generate a new BlockID and return it
	 * @return A unique newly generated BlockID
	 */
	public String generateBlockID() {
		this.id+=1;
		return id.toString();
	}

	
	/**
	 * Retrieve the instantiation of BlockIDGenerator.
	 * 
	 * @return	The instantiation of BlockIDGenerator.
	 */
	public static BlockIDGenerator getInstance() {
		if(generator==null) {
			generator = new BlockIDGenerator();
		}
		return generator;
	}

}