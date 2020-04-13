package types;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

public class BlockType extends DynaEnum<BlockType>{
	
	public final static BlockType IF = new BlockType("If", BlockCategory.CONTROL);
	public final static BlockType WHILE = new BlockType("While", BlockCategory.CONTROL);
	public final static BlockType NOT = new BlockType("Not", BlockCategory.OPERATOR);
	
	//TODO ffkes flexen
	
//	public final static BlockType BANAAN = new BlockType("BANAAN", BlockCategory.CONDITION);
//	public final static BlockType PEER = new BlockType("PEER", BlockCategory.CONTROL);
//	public final static BlockType KIWI = new BlockType("KIWI", BlockCategory.ACTION);
//	public final static BlockType APPEL = new BlockType("APPEL", BlockCategory.OPERATOR);

	
	public BlockType(String type, BlockCategory cat) {
		
		this(type, cat, null, null);
	}
	public BlockType(String type, BlockCategory cat, Action action) {
		this(type, cat, action, null);
	}
	public BlockType(String type, BlockCategory cat, Predicate predicate) {
		this(type, cat, null, predicate);
	}
	
	private BlockType(String type, BlockCategory cat, Action action, Predicate predicate) {
		super(type, cat, action, predicate);
	}
	
	

    public static <E> DynaEnum<? extends DynaEnum<?>>[] values() {
    	return values(BlockType.class);
    }
    
    public static <E> BlockType valueOf(String type) {
    	return valueOf(BlockType.class, type);
    }

}
