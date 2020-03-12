package types;
/**
 * The types of possible blocks in the domain.
 * 
 * @version 0.1
 * @author group17
 */
public enum BlockType {
	MoveForward{
		@Override
		public String toString() {
			return "Move Forward";
		}
	},
	TurnLeft{
		@Override
		public String toString() {
			return "Turn Left";
		}
	},
	TurnRight{
		@Override
		public String toString() {
			return "Turn Right";
		}
	},
	While{
		@Override
		public String toString() {
			return "While";
		}
	},
	If{
		@Override
		public String toString() {
			return "If";
		}
	},
	Not{
		@Override
		public String toString() {
			return "Not";
		}
	},
	WallInFront{
		@Override
		public String toString() {
			return "Wall In Front";
		}
	}
}