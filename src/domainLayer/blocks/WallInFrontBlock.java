package domainLayer.blocks;

import java.util.HashSet;
/**
 * The concrete class for the concept of a WallInFront block.
 * 
 * @version 0.1
 * @author group17
 */
public class WallInFrontBlock extends ConditionBlock {

	/**
	 * Create a WallInFront Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public WallInFrontBlock(String blockId) {
		super(blockId);
	}

//	public boolean assess(HashSet<Element> gameElements) {
//		Iterator<Element> iterator = gameElements.iterator();
//		Robot robot;
//		while(iterator.hasNext()){
//			Element nextElem = iterator.next();
//			if(nextElem.getClass().equals(Robot.class)) {
//				robot = nextElem;
//			}
//		}
//	}

}