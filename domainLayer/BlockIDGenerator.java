package domainLayer;

public class BlockIDGenerator {

	private static BlockIDGenerator generator;
	private Integer id;

	private BlockIDGenerator() {
		this.id = 0;
	}

	public String getBlockID() {
		return id.toString();
	}

	public static BlockIDGenerator getInstance() {
		if(generator==null) {
			generator = new BlockIDGenerator();
		}
		return generator;
	}

}