package domainLayer.elements;

public enum Orientation {
	UP {
		public String toString() {
			return "up";
		}
	},
	LEFT {
		public String toString() {
			return "left";
		}
	},
	DOWN {
		public String toString() {
			return "down";
		}
	},
	RIGHT {
		public String toString() {
			return "right";
		}
	};

	private Orientation left;
	private Orientation right;

	// https://stackoverflow.com/questions/18883646/java-enum-methods
	static {
		UP.left = LEFT;
		LEFT.left = DOWN;
		DOWN.left = RIGHT;
		RIGHT.left = UP;

		UP.right = RIGHT;
		LEFT.right = UP;
		DOWN.right = LEFT;
		RIGHT.right = DOWN;
	}

	public Orientation getLeft() {
		return left;
	}

	public Orientation getRight() {
		return right;
	}

}