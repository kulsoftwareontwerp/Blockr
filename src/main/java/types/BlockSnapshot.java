/**
 * 
 */
package types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import domainLayer.blocks.Block;

/**
 * /** BlockSnapshot
 * 
 * @version 0.1
 * @author group17
 *
 */
public class BlockSnapshot {
	private Block block;
	private Block connectedBlockAfterSnapshot;
	private Block connectedBlockBeforeSnapshot;
	private Set<Block> changingBlocks;

	/**
	 * 
	 * @param block
	 * @param connectedBlockBeforeSnapshot
	 * @param connectedBlockAfterSnapshot
	 * @param changingBlocks               TODO
	 */
	public BlockSnapshot(Block block, Block connectedBlockBeforeSnapshot, Block connectedBlockAfterSnapshot,
			Set<Block> changingBlocks) {
		super();
		if (block == null) {
			throw new NullPointerException("No valid snapshot");
		}

		this.block = (Block) block.clone();
		if (connectedBlockAfterSnapshot != null) {
			this.connectedBlockAfterSnapshot = (Block) connectedBlockAfterSnapshot.clone();
		}
		if (connectedBlockBeforeSnapshot != null) {
			this.connectedBlockBeforeSnapshot = (Block) connectedBlockBeforeSnapshot.clone();
		}
		this.changingBlocks=new HashSet<Block>();
		if(changingBlocks!=null) {
			for(Block b: changingBlocks) {
				this.changingBlocks.add(b.clone());
			}
		}

	}

	public Block getBlock() {
		return block.clone();
	}

	public Block getConnectedBlockAfterSnapshot() {
		if (connectedBlockAfterSnapshot != null) {
			return connectedBlockAfterSnapshot.clone();
		}
		return null;
	}

	public Block getConnectedBlockBeforeSnapshot() {
		if (connectedBlockBeforeSnapshot != null) {
			return connectedBlockBeforeSnapshot.clone();
		}
		return null;
	}

	
	/**
	 * Retrieve all the changing (moved) blocks associated with this snapshot.
	 * @return all the changing (moved) blocks associated with this snapshot.
	 */
	public Set<Block> getChangingBlocks() {
		return new HashSet<Block>(changingBlocks);
	}

}
