
public enum State {
	FIRST_FETCH,
	FIRST_EXECUTE,
	SECOND_FETCH,
	SECOND_EXECUTE,
	THIRD_FETCH,
	THIRD_EXECUTE,
	FOURTH_FETCH,
	FOURTH_EXECUTE,
	COMPLETION;
	
	 public String getDescription() {
	    switch(this) {
	    	case FIRST_FETCH: 		return "The first instruction has been fetched. Where is this instruction now stored?";
			case FIRST_EXECUTE: 	return "The first instruction has been executed. The values in the program counter and the accumulator will have changed!";
			case SECOND_FETCH:		return "The second instruction has been fetched.";
			case SECOND_EXECUTE:	return "The second instruction has been executed. Again, the values in the program counter and the accumulator will have changed!";
			case THIRD_FETCH:		return "The third instruction has been fetched.";
			case THIRD_EXECUTE:		return "The third instruction has been executed. This time, there is a change in the memory!";
			case FOURTH_FETCH:		return "The fourth instruction has been fetched";
			case FOURTH_EXECUTE:	return "The fourth instruction has been executed. This one is easy!";
			case COMPLETION:		return "The program has executed correctly. Well done!!!";
			default: return "No description";
	    }
	  }
	 
	 public State next() {
		 switch(this) {
			case FIRST_FETCH:		return FIRST_EXECUTE;
			case FIRST_EXECUTE: 	return SECOND_FETCH;
			case SECOND_FETCH:		return SECOND_EXECUTE;
			case SECOND_EXECUTE:	return THIRD_FETCH;
			case THIRD_FETCH:		return THIRD_EXECUTE;
			case THIRD_EXECUTE:		return FOURTH_FETCH;
			case FOURTH_FETCH:		return FOURTH_EXECUTE;
			case FOURTH_EXECUTE:	return COMPLETION;
		 }

		 return this;
	 }
}
