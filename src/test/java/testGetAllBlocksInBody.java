import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.lenient;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import applicationLayer.BlockController;
import applicationLayer.DomainController;
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
public class testGetAllBlocksInBody {

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
		idsForChainControlBlock = Set.of("A1", "A2", "A3", "W1", "WA1", "WA2", "WA3");
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

//		
//		//chain actionBlock
//		idsForChainActionBlock = Set.of("W2", "A1", "A2", "A3", "W1","WA1", "WA2", "WA3");
//		startingActionBlock.setNextBlock(new WhileBlock("W2") {
//			@Override
//			public AssessableBlock getConditionBlock() {
//				return new NotBlock("C1") {
//					public AssessableBlock getOperand() {
//						return new NotBlock("C2") {
//							public AssessableBlock getOperand() {
//								return new WallInFrontBlock("C3");
//							};
//						};
//					};
//				};
//			};
//			
//			@Override
//			public ExecutableBlock getFirstBlockOfBody() {
//				return new MoveForwardBlock("A1") {
//					public ExecutableBlock getNextBlock() {
//						return new TurnLeftBlock("A2") {
//							public ExecutableBlock getNextBlock() {
//								return new TurnRightBlock("A3") {
//									public ExecutableBlock getNextBlock() {
//										return new WhileBlock("W1") {
//											public AssessableBlock getConditionBlock() {
//												return new NotBlock("WC1") {
//													public AssessableBlock getOperand() {
//														return new NotBlock("WC2") {
//															public AssessableBlock getOperand() {
//																return new WallInFrontBlock("WC3");
//															};
//														};
//													};
//												};
//											};
//
//											@Override
//											public ExecutableBlock getFirstBlockOfBody() {
//												return new MoveForwardBlock("WA1") {
//													public ExecutableBlock getNextBlock() {
//														return new TurnLeftBlock("WA2") {
//															public ExecutableBlock getNextBlock() {
//																return new TurnRightBlock("WA3");
//															};
//														};
//													};
//												};
//											};
//										};
//									};
//								};
//							};
//						};
//					};
//				};
//			}
//			@Override
//			public ExecutableBlock getNextBlock() {
//				return new MoveForwardBlock("A4");
//			}
//			
//		});

//		idsForChainAssessableBlock = Set.of("startingAssessable","C1","C2","C3");
//		startingAssessable.setOperand(new NotBlock("C1") {
//					public AssessableBlock getOperand() {
//						return new NotBlock("C2") {
//							public AssessableBlock getOperand() {
//								return new WallInFrontBlock("C3");
//							};
//						};
//					};
//				});

		lenient().doReturn(startingActionBlock).when(programBlockRepository).getBlockByID("startingAction");
		lenient().doReturn(startingControlBlock).when(programBlockRepository).getBlockByID("startingControl");
	
	
	}

	@After
	public void tearDown() throws Exception {
	}

	
	/**
	 * Test method for
	 * {@link applicationLayer.BlockController#getAllBlockIDsInBody(String)}.
	 */
	@Test
	public void testGetAllBlockIdsInBodyBCPositive() {
		Set<String> ids = blockController.getAllBlockIDsInBody("startingControl");
		
		assertTrue(ids.equals(idsForChainControlBlock));	
	}
	

}
