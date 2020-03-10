package domainLayer.blocks;

public class BlockIDGenerator {

	private static BlockIDGenerator generator;
	private Integer id;

	private BlockIDGenerator() {
		this.id = -1;
	}

	public String getBlockID() {
		this.id+=1;
		return id.toString();
	}

	public static BlockIDGenerator getInstance() {
		if(generator==null) {
			generator = new BlockIDGenerator();
		}
		return generator;
	}

}