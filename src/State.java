
public enum State {
	START,
	AFTER_FETCH,
	AFTER_EXECUTE,
	COMPLETE;

	public String getDescription() {
		switch(this) {
		case START: 				return "Start";
		case AFTER_FETCH: 			return "Fetch";
		case AFTER_EXECUTE: 		return "Execute";
		case COMPLETE:			return "The program has executed correctly. Well done!!!";
		default: return "No description";
		}
	}

}
