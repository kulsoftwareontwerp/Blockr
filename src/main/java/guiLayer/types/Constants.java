package guiLayer.types;

/**
 * Constants, This interface contains all the constants used in the GUI layer of the blockr application.
 * 
 * @version 0.1
 * @author group17
 *
 */
public interface Constants {
	
	public static final int MASKEDKEY_DURATION = 500;
	public static final int ALERTMESSAGE_DURATION = 10000;
	
	public final static int HEIGHT = 600;
	public final static int WIDTH = 1000;
	public final static int ORIGIN = 0;
	
	public static final int STANDARD_HEIGHT_BLOCK = 30;
	public static final int STANDARD_HEIGHT_CONTROL_BLOCK = 90;
	
	public static final int TRIGGER_RADIUS_CLIPON = 8;
	
	public static final int OFFSET_GAMEAREA_CELLS = 4;
	


	public final static int PALETTE_START_X = 0;
	public final static int PALETTE_END_X = 100;
	public final static int PALETTE_OFFSET_BLOCKS = 5;

	public final static int PROGRAM_START_X = 100;
	public final static int INITIAL_PROGRAM_GAME_BORDER_X = 750;


	public static final int GAME_HEIGHT = 600;
	public static final int GAME_WIDTH = 250;
	public final static int INITIAL_GAME_END_X = 1000;
	
	public static final int COUNTER_HEIGHT = 25;
	public static final int COUNTER_WIDTH = 50;
	
	
	public static final int INVALID_COORDINATE = -1;
	
	
	
	
	public static final String PALETTE_BLOCK_IDENTIFIER = "PALETTE";

	public final static int ACTION_BLOCK_INIT_OFFSET = 5;
	public final static int CONTROL_BLOCK_INIT_OFFSET = 5;
	public final static int OPERATOR_BLOCK_INIT_OFFSET = 5;
	public final static int CONDITION_BLOCK_INIT_OFFSET = 5;
	
	public final static int ACTION_BLOCK_MOVE_FORWARD_UPPER = 50;
	public final static int ACTION_BLOCK_MOVE_FORWARD_LOWER = 90;	
	public final static int ACTION_BLOCK_TURN_LEFT_UPPER = 95;
	public final static int ACTION_BLOCK_TURN_LEFT_LOWER = 135;
	public final static int ACTION_BLOCK_TURN_RIGHT_UPPER = 140;
	public final static int ACTION_BLOCK_TURN_RIGHT_LOWER = 180;

	public final static int CONTROL_BLOCK_IF_UPPER = 225;
	public final static int CONTROL_BLOCK_IF_LOWER = 325;
	public final static int CONTROL_BLOCK_WHILE_UPPER = 330;
	public final static int CONTROL_BLOCK_WHILE_LOWER = 430;

	public final static int OPERATOR_BLOCK_NOT_UPPER = 475;
	public final static int OPERATOR_BLOCK_NOT_LOWER = 505;

	public final static int CONDITION_BLOCK_WALL_UPPER = 555;
	public final static int CONDITION_BLOCK_WALL_LOWER = 585;
	
	public final static int X_COORD_ROBOT_INIT = 2;
	public final static int Y_COORD_ROBOT_INIT = 3;

}
