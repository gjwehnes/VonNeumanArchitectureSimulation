public class Simulator {

	private String instructionRegister = "";
	private String accumulator = "";
	private String programCounter = "";
	private State state = State.FIRST_FETCH;
	private final int BYTES_PER_WORD = 4;
	private final int WORDS_IN_PROGRAM = 7;
	private long valueOne = 0x0000ABCD;
	private long valueTwo = 0x11111111;
	private long result = 0;
	private String valueOneS;
	private String valueTwoS;
	private String resultS;
	
	private long[] memory = new long[WORDS_IN_PROGRAM];

	public Simulator(String instructionRegister, String accumulator, String programCounter) {
		super();
		this.instructionRegister = instructionRegister;
		this.accumulator = accumulator;
		this.programCounter = programCounter;
		
		//randomize program - for now just a simple addition of two values
		
		valueOne = (long)(Math.random() * 256 * 256);
		valueTwo = (long)(Math.random() * 256 * 256);
		
		result = valueOne + valueTwo;				
		
		memory[0] = 0x10000010;
		memory[1] = 0x20000014;
		memory[2] = 0x11000018;
		memory[3] = 0x7F000000;

		memory[4] = valueOne;
		memory[5] = valueTwo;
		memory[6] = 0x00000000;
		
		
	}

	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		//TO DO - this should trigger calculations eventually...
		this.state = state;
	}

	public String getMemoryWordAsString(int index) {
		return getMemoryWordAsString(memory[index]);
	}

	public String getInstructionRegister() {
		return instructionRegister;
	}

	public String getProgramCounter() {
		return programCounter;
	}
	
	public String getAccumulator() {
		
		switch(this.state) {
		case FIRST_FETCH:
			return valueOneS;
		case FIRST_EXECUTE:
		case SECOND_FETCH:
			return getMemoryWordAsString(4);
		case SECOND_EXECUTE:
		case THIRD_FETCH:
		case THIRD_EXECUTE:
		case FOURTH_FETCH:
		case FOURTH_EXECUTE:
		case COMPLETION:
			return getMemoryWordAsString(result);
		default:
			return getMemoryWordAsString(result);
		}
	}
	
	public String getOutput() {
		
		switch(this.state) {
		case FIRST_FETCH:
		case FIRST_EXECUTE:
		case SECOND_FETCH:
		case SECOND_EXECUTE:
		case THIRD_FETCH:
			return "00 00 00 00";
		case THIRD_EXECUTE:
		case FOURTH_FETCH:
		case FOURTH_EXECUTE:
		case COMPLETION:
			return getMemoryWordAsString(result);
		default:
			return "";
		}
		
	}
		
	private String getMemoryWordAsString(long word) {
		long remainder = word;
		int byte0 = (int)(remainder / (256 * 256 * 256));
		remainder = (int)(remainder % (256 * 256 * 256));
		int byte1 = (int)(remainder / (256 * 256));
		remainder = (int)(remainder % (256 * 256));
		int byte2 = (int)(remainder / (256));
		remainder = (int)(remainder % (256));
		int byte3 = (int)remainder;
		return getMemoryWordAsString(byte0, byte1, byte2, byte3);
	}
	
	private String getMemoryWordAsString(int byte0, int byte1, int byte2, int byte3) {
		return String.format("%02X %02X %02X %02X", byte0, byte1, byte2, byte3);
	}
		
	private String getAddressAsString(int byte0, int byte1, int byte2) {
		return String.format("%02X %02X %02X", byte0, byte1, byte2);
	}	
	
	public String getMemoryAddressAsString(int word) {
		return getAddressAsString(0, (word * 4) / 256, (word * 4) % 256 );	
	}
		
	public void nextStep() {
		//
		//Instruction	Value	Description
		//READ	10	xx	xx	xx	Read value from address xxxxxx into accumulator
		//WRITE	11	xx	xx	xx	Write value in accumulator to address xxxxxx
		//ADD	10	xx	xx	xx	Add value at address xxxxxx to accumulator
		//SUB	11	xx	xx	xx	Subtract value at address xxxxxx to accumulator
		//MULT	12	xx	xx	xx	Multiply accumulator value by value at address xxxxxx
		//DIV	13	xx	xx	xx	Divide accumulator value by value at address xxxxxx. Integer division, no remainder!
		//INPUT	40	xx	xx	xx	Input value from input device into address xxxxxx
		//OUTPUT41	xx	xx	xx	Output value at address xxxxxx to output device
		//STOP	7F	FF	FF	FF	
		
		switch(this.state) {
			case FIRST_FETCH:
				
				break;
			case FIRST_EXECUTE:
				break;
			case SECOND_FETCH:
				break;
			case SECOND_EXECUTE:
				break;
			case THIRD_FETCH:
				break;
			case THIRD_EXECUTE:
				break;
			case COMPLETION:
				break;
			default:
				break;
		
		}
		
		
	}
	
}
