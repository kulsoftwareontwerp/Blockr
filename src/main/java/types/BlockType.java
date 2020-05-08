package types;

import java.util.Arrays;
import java.util.Optional;

import com.kuleuven.swop.group17.GameWorldApi.Action;
import com.kuleuven.swop.group17.GameWorldApi.Predicate;

public class BlockType extends DynaEnum<BlockType> {

	public final static BlockType IF = new BlockType("If", BlockCategory.CONTROL);
	public final static BlockType WHILE = new BlockType("While", BlockCategory.CONTROL);
	public final static BlockType NOT = new BlockType("Not", BlockCategory.OPERATOR);
	public final static BlockType DEFINITION = new BlockType("Definition", BlockCategory.DEFINITION);
//	public final static BlockType CALL = new BlockType("Call", BlockCategory.CALL);

	public BlockType(String type, BlockCategory cat) {

		this(type, cat, null, null);
	}

	public BlockType(String type, BlockCategory cat, Action action) {
		this(type, cat, action, null);
	}

	public BlockType(String type, BlockCategory cat, Predicate predicate) {
		this(type, cat, null, predicate);
	}

	public BlockType(String type, BlockCategory cat, String definition) {
		super(type, cat, null, null, definition);
	}

	private BlockType(String type, BlockCategory cat, Action action, Predicate predicate) {
		super(type, cat, action, predicate, null);
	}

	/**
	 * Remove a blockType, only BlockTypes of the category Call are able to be
	 * removed.
	 * 
	 * @param id The ID of the referenced definition Block
	 */
	public static void removeBlockType(String id) {
		Optional<DynaEnum<? extends DynaEnum<?>>> b = Arrays.asList(values()).stream().filter(s->s.type().equals("Call "+id)&&s.cat==BlockCategory.CALL).findAny();
		
		if (b.isPresent()) {
			remove(b.get());
		}
	}


	public static <E> DynaEnum<? extends DynaEnum<?>>[] values() {
		return values(BlockType.class);
	}

	public static <E> BlockType valueOf(String type) {
		return valueOf(BlockType.class, type);
	}

}
