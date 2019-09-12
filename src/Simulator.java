public class Simulator {

	private long instructionRegister = 0;
	private long accumulator = 0;
	private int programCounter = 0;
	private long input = 0;
	private long output = 0;
			
	private State state = State.START;
	public final int BYTES_PER_WORD = 4;
	public final int WORDS_IN_PROGRAM = 7;
	private long valueOne = 0x0000ABCD;
	private long valueTwo = 0x11111111;
	private long result = 0;
	private String valueOneS;
	private String valueTwoS;
	private String resultS;
	
	private long[] memory = new long[WORDS_IN_PROGRAM];

	public Simulator() {
		super();
		this.instructionRegister = 0;
		this.accumulator = 0;
		this.programCounter = 0;
		this.input = 0;
		this.output = 0;
		
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
	
	public String getMemoryWordAsString(int index) {
		return getMemoryWordAsString(memory[index]);
	}

	public String getInstructionRegister() {
		return getMemoryWordAsString(instructionRegister);
	}

	public int getProgramCounter() {
		return programCounter;
	}
	
	public String getProgramCounterAsString() {
		return getMemoryAddressAsString(programCounter);
	}
	
	public String getAccumulator() {
		return getMemoryWordAsString(this.accumulator);
	}

	public String getInput() {
		return getMemoryWordAsString(this.input);
	} 
	
	public String getOutput() {
		return getMemoryWordAsString(this.output);
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
		
	private String getMemoryAddressAsString(int byte0, int byte1, int byte2) {
		return String.format("%02X %02X %02X", byte0, byte1, byte2);
	}	
	
	public String getMemoryAddressAsString(int word) {
		return getMemoryAddressAsString(0, (word * 4) / 256, (word * 4) % 256 );	
	}
		
	public void moveNextStep() {
		if (state == State.START) {
			state = State.AFTER_FETCH;
			this.instructionRegister = memory[programCounter];
			this.programCounter+=1;
			
		}		
		else if (state == State.AFTER_FETCH) {
			state = State.AFTER_EXECUTE;
			//instruction has executed...
			int instruction = (int) (instructionRegister / (256 * 256 * 256));
			long address = instructionRegister % 256;
			switch(instruction) {
				case 0x10:	//READ
					accumulator = memory[(int) (address / 4)];
					break;
				case 0x11:	//WRITE
					memory[(int) (address / 4)] = accumulator;
					break;
				case 0x20:	//add
					accumulator += memory[(int) (address / 4)];
					break;
				case 0x21:	//subtract
					accumulator -= memory[(int) (address / 4)];
					break;
				case 0x22:	//multiply
					accumulator *= memory[(int) (address / 4)];
					break;
				case 0x23:	//multiply
					accumulator /= memory[(int) (address / 4)];
					break;
				case 0x7F: //stop
					state = State.COMPLETE;
				default:	//UNKNOWN
					
			}
		}
		else if (state == State.AFTER_EXECUTE) {
			if (false) {  //if current instruction is 'STOP'...
				state = State.COMPLETE;
			}
			else {
				state = State.AFTER_FETCH;
				this.instructionRegister = memory[programCounter];
				this.programCounter+=1;
			}
	
		}
	}
	
}
