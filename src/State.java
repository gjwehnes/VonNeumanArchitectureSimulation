
public enum State {
	START,
	AFTER_FETCH,
	AFTER_EXECUTE,
	COMPLETE;

	public String getDescription() {
		switch(this) {
		case START: 				return "The computer is ready to run the program";
		case AFTER_FETCH: 			return "The 'Fetch' has completed. What is the state of the computer?";
		case AFTER_EXECUTE: 		return "The 'Execute' has completed. What is the state of the computer?";
		case COMPLETE:				return "The program has executed correctly. Well done!!!";
		default: return "No description";
		}
	}

}
