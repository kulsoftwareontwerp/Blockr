/**
 * 
 */
package types;

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

	/**
	 * 
	 * @param block
	 * @param connectedBlockBeforeSnapshot
	 * @param connectedBlockAfterSnapshot
	 */
	public BlockSnapshot(Block block, Block connectedBlockBeforeSnapshot, Block connectedBlockAfterSnapshot) {
		super();
		if (block == null) {
			throw new NullPointerException("No valid snapshot");
		}
		try {
			this.block = (Block) block.clone();
			if (connectedBlockAfterSnapshot != null) {
				this.connectedBlockAfterSnapshot = (Block) connectedBlockAfterSnapshot.clone();
			}
			if (connectedBlockBeforeSnapshot != null) {
				this.connectedBlockBeforeSnapshot = (Block) connectedBlockBeforeSnapshot.clone();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Block getBlock() {
		return block.clone();
	}

	public Block getConnectedBlockAfterSnapshot() {
		if(connectedBlockAfterSnapshot!=null) {
			return connectedBlockAfterSnapshot.clone();			
		}
		return null;
	}

	public Block getConnectedBlockBeforeSnapshot() {
		if(connectedBlockBeforeSnapshot!=null) {
			return connectedBlockBeforeSnapshot.clone();			
		}
		return null;
	}

}
