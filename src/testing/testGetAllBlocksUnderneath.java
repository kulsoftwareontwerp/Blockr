package testing;

import static org.junit.Assert.*;
import static org.mockito.Mockito.lenient;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.BlockController;
import domainLayer.blocks.ActionBlock;
import domainLayer.blocks.AssessableBlock;
import domainLayer.blocks.BlockRepository;
import domainLayer.blocks.ControlBlock;
import domainLayer.blocks.ExecutableBlock;
import domainLayer.blocks.IfBlock;
import domainLayer.blocks.MoveForwardBlock;
import domainLayer.blocks.NotBlock;
import domainLayer.blocks.TurnLeftBlock;
import domainLayer.blocks.TurnRightBlock;
import domainLayer.blocks.WallInFrontBlock;
import domainLayer.blocks.WhileBlock;

@RunWith(MockitoJUnitRunner.class)
public class testGetAllBlocksUnderneath {

	private ControlBlock startingControlBlock;
	private Set<String> idsForChainControlBlock;

	private ActionBlock startingActionBlock;
	private Set<String> idsForChainActionBlock;

	private AssessableBlock startingAssessable;
	private Set<String> idsForChainAssessableBlock;

	@Spy
	private BlockRepository programBlockRepository;
	@Spy @InjectMocks
	private BlockController blockController;



	@Before
	public void setUp() throws Exception {
		startingControlBlock = new IfBlock("startingControl");
		startingActionBlock = new MoveForwardBlock("startingAction");
		startingAssessable = new NotBlock("startingAssessable");

		// chain controlBlock
		idsForChainControlBlock = Set.of("startingControl", "C1", "C2", "C3", "A1", "A2", "A3", "W1", "WC1", "WC2",
				"WC3", "WA1", "WA2", "WA3","A4");
		startingControlBlock.setConditionBlock(new NotBlock("C1") {
			public AssessableBlock getOperand() {
				return new NotBlock("C2") {
					public AssessableBlock getOperand() {
						return new WallInFrontBlock("C3");
					};
				};
			};
		});
		startingControlBlock.setFirstBlockOfBody(new MoveForwardBlock("A1") {
			public ExecutableBlock getNextBlock() {
				return new TurnLeftBlock("A2") {
					public ExecutableBlock getNextBlock() {
						return new TurnRightBlock("A3") {
							public ExecutableBlock getNextBlock() {
								return new WhileBlock("W1") {
									public AssessableBlock getConditionBlock() {
										return new NotBlock("WC1") {
											public AssessableBlock getOperand() {
												return new NotBlock("WC2") {
													public AssessableBlock getOperand() {
														return new WallInFrontBlock("WC3");
													};
												};
											};
										};
									};

									@Override
									public ExecutableBlock getFirstBlockOfBody() {
										return new MoveForwardBlock("WA1") {
											public ExecutableBlock getNextBlock() {
												return new TurnLeftBlock("WA2") {
													public ExecutableBlock getNextBlock() {
														return new TurnRightBlock("WA3");
													};
												};
											};
										};
									};
								};
							};
						};
					};
				};
			};
		});
		startingControlBlock.setNextBlock(new MoveForwardBlock("A4"));

		
		//chain actionBlock
		idsForChainActionBlock = Set.of("startingAction","W2","C1", "C2", "C3", "A1", "A2", "A3", "W1", "WC1", "WC2",
				"WC3", "WA1", "WA2", "WA3","A4");
		startingActionBlock.setNextBlock(new WhileBlock("W2") {
			@Override
			public AssessableBlock getConditionBlock() {
				return new NotBlock("C1") {
					public AssessableBlock getOperand() {
						return new NotBlock("C2") {
							public AssessableBlock getOperand() {
								return new WallInFrontBlock("C3");
							};
						};
					};
				};
			};
			
			@Override
			public ExecutableBlock getFirstBlockOfBody() {
				return new MoveForwardBlock("A1") {
					public ExecutableBlock getNextBlock() {
						return new TurnLeftBlock("A2") {
							public ExecutableBlock getNextBlock() {
								return new TurnRightBlock("A3") {
									public ExecutableBlock getNextBlock() {
										return new WhileBlock("W1") {
											public AssessableBlock getConditionBlock() {
												return new NotBlock("WC1") {
													public AssessableBlock getOperand() {
														return new NotBlock("WC2") {
															public AssessableBlock getOperand() {
																return new WallInFrontBlock("WC3");
															};
														};
													};
												};
											};

											@Override
											public ExecutableBlock getFirstBlockOfBody() {
												return new MoveForwardBlock("WA1") {
													public ExecutableBlock getNextBlock() {
														return new TurnLeftBlock("WA2") {
															public ExecutableBlock getNextBlock() {
																return new TurnRightBlock("WA3");
															};
														};
													};
												};
											};
										};
									};
								};
							};
						};
					};
				};
			}
			@Override
			public ExecutableBlock getNextBlock() {
				return new MoveForwardBlock("A4");
			}
			
		});

		idsForChainAssessableBlock = Set.of("startingAssessable","C1","C2","C3");
		startingAssessable.setOperand(new NotBlock("C1") {
					public AssessableBlock getOperand() {
						return new NotBlock("C2") {
							public AssessableBlock getOperand() {
								return new WallInFrontBlock("C3");
							};
						};
					};
				});
		
		
		lenient().doReturn(startingActionBlock).when(programBlockRepository).getBlockByID("startingAction");
		lenient().doReturn(startingControlBlock).when(programBlockRepository).getBlockByID("startingControl");
		lenient().doReturn(startingAssessable).when(programBlockRepository).getBlockByID("startingAssessable");

	}



	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(String)}.
	 */
	@Test
	public void testGetAllBlockIdsUnderneathBCPositiveStartingControl() {
		Set<String> ids = blockController.getAllBlockIDsUnderneath("startingControl");
		
		assertTrue(ids.equals(idsForChainControlBlock));
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(String)}.
	 */
	@Test
	public void testGetAllBlockIdsUnderneathBCPositiveStartingAction() {
		Set<String> ids = blockController.getAllBlockIDsUnderneath("startingAction");
		
		assertTrue(ids.equals(idsForChainActionBlock));	
	}
	
	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#getAllBlockIDsUnderneath(String)}.
	 */
	@Test
	public void testGetAllBlockIdsUnderneathBCPositiveStartingAssessable() {
		Set<String> ids = blockController.getAllBlockIDsUnderneath("startingAssessable");
		
		assertTrue( ids.equals(idsForChainAssessableBlock));	
	}

}
