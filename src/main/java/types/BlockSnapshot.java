package types;

import java.util.HashSet;
import java.util.Set;

import domainLayer.blocks.Block;

/**
 * BlockSnapshot, a snapshot of a blockOperation.
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
	private Set<BlockSnapshot> associatedSnapshots;

	/**
	 * Create a new BlockSnapshot
	 * 
	 * @param block                        The block that's changed.
	 * @param connectedBlockBeforeSnapshot The connectedBlock before the snapshot,
	 *                                     can be null.
	 * @param connectedBlockAfterSnapshot  The connectedBlock after the snapshot,
	 *                                     can be null.
	 * @param changingBlocks               A set with all the blocks affected by the
	 *                                     change.
	 * @param associatedSnapshots 		   A set with all the snapshots associated with this snapshot.
	 * @throws NullPointerException When the block is null.
	 */
	public BlockSnapshot(Block block, Block connectedBlockBeforeSnapshot, Block connectedBlockAfterSnapshot,
			Set<Block> changingBlocks, Set<BlockSnapshot> associatedSnapshots) {
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
		
		this.changingBlocks = new HashSet<Block>();
		if (changingBlocks != null) {
			for (Block b : changingBlocks) {
				this.changingBlocks.add(b.clone());
			}
		}
		
		this.associatedSnapshots = new HashSet<BlockSnapshot>();
		if (associatedSnapshots != null) {
			for (BlockSnapshot b : associatedSnapshots) {
				//BlockSnapshots are immutable, no clone needed.
				this.associatedSnapshots.add(b);
			}
		}

	}

	/**
	 * Retrieve a clone of the changed block.
	 * 
	 * @return a clone of the changed block.
	 */
	public Block getBlock() {
		return block.clone();
	}

	/**
	 * Retrieve a clone of the connected block after the snapshot.
	 * 
	 * @return a clone of the connected block after the snapshot or null if there is
	 *         no connected block after the snapshot
	 */
	public Block getConnectedBlockAfterSnapshot() {
		if (connectedBlockAfterSnapshot != null) {
			return connectedBlockAfterSnapshot.clone();
		}
		return null;
	}

	/**
	 * Retrieve a clone of the connected block before the snapshot.
	 * 
	 * @return a clone of the connected block before the snapshot or null if there is
	 *         no connected block before the snapshot
	 */
	public Block getConnectedBlockBeforeSnapshot() {
		if (connectedBlockBeforeSnapshot != null) {
			return connectedBlockBeforeSnapshot.clone();
		}
		return null;
	}

	/**
	 * Retrieve all the changing (moved) blocks associated with this snapshot.
	 * 
	 * @return all the changing (moved) blocks associated with this snapshot.
	 */
	public Set<Block> getChangingBlocks() {
		return new HashSet<Block>(changingBlocks);
	}

	/**
	 * Retrieve the associated snapshots
	 * @return the associatedSnapshots
	 */
	public Set<BlockSnapshot> getAssociatedSnapshots() {
		return new HashSet<BlockSnapshot>(associatedSnapshots);
	}
	
	

}
