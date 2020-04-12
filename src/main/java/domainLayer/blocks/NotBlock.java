package domainLayer.blocks;


import java.util.HashSet;

import domainLayer.elements.ElementRepository;


/**
 * The abstract class for the concept of a Not block.
 * 
 * @version 0.1
 * @author group17
 */
public class NotBlock extends UnaryOperatorBlock {

	/**
	 * Create a Not Block
	 * @param 	blockId
	 * 			The ID for the block.
	 */
	public NotBlock(String blockId) {
		super(blockId);
	}

	@Override
	public boolean assess(ElementRepository elementsRepo) {
		if(getOperand() instanceof NotBlock) {
		return !((NotBlock) getOperand()).assess(elementsRepo);
		}else {
			
			//TODO Refactor needed
			return false;
		}
		
	}





}