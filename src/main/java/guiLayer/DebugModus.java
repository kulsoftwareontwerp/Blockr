package guiLayer;



public enum DebugModus {
	NONE {
		public String toString() {
			return "Debug disabled";
		}
	},
	IDS {
		public String toString() {
			return "IDS shown";
		}
	},
	CONNECTIONS {
		public String toString() {
			return "Connections shown";
		}
	},
	CONNECTIONSTATUS {
		public String toString() {
			return "ConnectionStatus shown";
		}
	},
	FILLINGS {
		public String toString() {
			return "Fillings shown";
		}
	};

	private DebugModus next;


//	// https://stackoverflow.com/questions/18883646/java-enum-methods
	static {
		NONE.next=IDS;
		IDS.next=CONNECTIONS;
		CONNECTIONS.next=CONNECTIONSTATUS;
		CONNECTIONSTATUS.next=FILLINGS;
		FILLINGS.next=NONE;
	}

	public DebugModus getNext() {
		System.out.println(next);
		return next;
	}
}
