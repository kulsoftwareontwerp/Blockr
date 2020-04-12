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
	 * @param block
	 * @param parentId
	 * @param parentConnection
	 */
	public BlockSnapshot(Block block, Block connectedBlockAfterSnapshot, Block connectedBlockBeforeSnapshot)  {
		super();
		if(block==null) {
			throw new NullPointerException("No valid snapshot");
		}
		try {
		this.block = (Block) block.clone();
		if(connectedBlockAfterSnapshot!=null) {
			this.connectedBlockAfterSnapshot=(Block) connectedBlockAfterSnapshot.clone();
		}
		if(connectedBlockBeforeSnapshot!=null) {
			this.connectedBlockBeforeSnapshot=(Block) connectedBlockBeforeSnapshot.clone();
		}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}


	public Block getBlock() {
		return block;
	}


	public Block getConnectedBlockAfterSnapshot() {		
		return connectedBlockAfterSnapshot;
	}


	public Block getConnectedBlockBeforeSnapshot() {
		return connectedBlockBeforeSnapshot;
	}



	
	



}
