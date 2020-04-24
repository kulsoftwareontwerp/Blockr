package guiLayer.types;

/**
 * DebugModus, This enum serves to specify the different kinds of debugModes, give a method to cycle trough them and order them in order of specificity
 * 
 * @version 0.1
 * @author group17
 *
 */
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


	static {
		NONE.next=IDS;
		IDS.next=CONNECTIONS;
		CONNECTIONS.next=CONNECTIONSTATUS;
		CONNECTIONSTATUS.next=FILLINGS;
		FILLINGS.next=NONE;
	}

	/**
	 * Retrieve the next debugModus
	 * @return the next debugModus
	 */
	public DebugModus getNext() {
		System.out.println(next);
		return next;
	}
}
