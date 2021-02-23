import java.util.Random;

public class Simulator {
	
//	Instruction	Value	Description
//	READ	10	xx	xx	xx	Read value from address xxxxxx into accumulator
//	WRITE	11	xx	xx	xx	Write value in accumulator to address xxxxxx
//	ADD	    20	xx	xx	xx	Add value at address xxxxxx to accumulator
//	SUB		21	xx	xx	xx	Subtract value at address xxxxxx to accumulator
//	MULT	22	xx	xx	xx	Multiply accumulator value by value at address xxxxxx
//	DIV		23	xx	xx	xx	Divide accumulator value by value at address xxxxxx. Integer division, no remainder!
//  DMULT	26 	yy  yy  yy  Multiply accumulator value by value yyyyyy
//  DDIV	27 	yy  yy  yy  Divide accumulator value by value yyyyyy. Integer division, no remainder!

//	JUMP	51	xx	xx	xx	Jump to address xxxxxx
//	JUMPIF	52	xx	xx	xx	Jump to address xxxxxx if compare flag is TRUE
//	AND		60	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//	OR		61	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//	NOT		62				Logical NOT accumulator value	
//	XOR		63	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//  CLEAR	70	00	00	00	Set the accumulator to zero
//	STOP	7F	FF	FF	FF	
	public final int READ = 0x10;
	public final int WRITE = 0x11;	
	public final int ADD = 0x20;
	public final int SUB = 0x21;
	public final int MULT = 0x22;
	public final int DIV = 0x23;
	public final int STOP = 0x7F;
	
	public final long INPUT_ADDRESS = 0xFF0000;
	public final long OUTPUT_ADDRESS = 0xFF0004;
	
	private long instructionRegister = 0;
	private long accumulator = 0;
	private int programCounter = 0;
	private long input = 0;
	private long output = 0;
			
	private State state = State.START;
	public int BYTES_PER_WORD = 4;
	public int WORDS_IN_PROGRAM = 8;
	
	DisplayMode displayMode = DisplayMode.CODES;
	
	
	private long[] memory;

	private Simulator previousState = null;
	
	public Simulator(ProgramMode programMode, DisplayMode displayMode) {
		super();
		this.instructionRegister = 0;
		this.accumulator = 0;
		this.programCounter = 0;
		this.input = 0;
		this.output = 0;
		this.displayMode = displayMode;
		
		Random random = new Random(System.currentTimeMillis());
				
		if (programMode == ProgramMode.SIMPLE) {
			//randomize program - for now just a simple addition of two values, of which one comes from the input
			WORDS_IN_PROGRAM = 5;
			memory = new long[WORDS_IN_PROGRAM];
			
			long valueOne = (long)(Math.random() * 256 * 256);
			long valueTwo = (long)(Math.random() * 256 * 256);
			
			memory[0] = createInstruction(READ, INPUT_ADDRESS);
			memory[1] = createInstruction(ADD, getWordAsAddress(4));
			memory[2] = createInstruction(WRITE, OUTPUT_ADDRESS);
			memory[3] = createInstruction(STOP, 000000); 

			memory[4] = valueOne;		//1C	value to add
			
			input = valueTwo;
			output = 0;
						
		}
		else if (programMode == ProgramMode.INTERMEDIATE) {
			WORDS_IN_PROGRAM = 7;
			memory = new long[WORDS_IN_PROGRAM];
			
			long valueOne = (long)(Math.random() * 256 * 256);
			long valueTwo = (long)(Math.random() * valueOne);
			long valueThree = (long)(Math.random() * 14) + 2;

			memory[0] = createInstruction(READ, INPUT_ADDRESS);
			if ((int)(Math.random() * 2) == 0) {
				memory[1] = createInstruction(ADD,getWordAsAddress(5));
			}
			else {
				memory[1] = createInstruction(SUB, getWordAsAddress(5));
			}
			if ((int)(Math.random() * 2) == 0) {
				memory[2] = createInstruction(MULT, getWordAsAddress(6));
			}
			else {
				memory[2] = createInstruction(DIV,getWordAsAddress(6));
			}
			memory[3] = createInstruction(WRITE, OUTPUT_ADDRESS);
			memory[4] = createInstruction(STOP, 000000); 

			memory[5] = valueTwo;						//20	value to add / subtract
			memory[6] = valueThree;						//24	value to multiply / divide
			
			input = valueOne;
			output = 0;
						
		}
		else if (programMode == ProgramMode.TEMPERATURE_CONVERSION ) {

			WORDS_IN_PROGRAM = 12;
			memory = new long[WORDS_IN_PROGRAM];
						
			memory[0] = createInstruction(READ, INPUT_ADDRESS);
			memory[1] = createInstruction(SUB,getWordAsAddress(6));
			memory[2] = createInstruction(MULT,getWordAsAddress(7));
			memory[3] = createInstruction(DIV,getWordAsAddress(8));
			memory[4] = createInstruction(WRITE, OUTPUT_ADDRESS);
			memory[5] = createInstruction(STOP, 000000);

			memory[6] = 0x00000020;					//value to subtract
			memory[7] = 0x00000005;					//value to multiply
			memory[8] = 0x00000009;					//value to divide
			
			input = 0x48;
			output = 0;
						
		}
		
	}

	public State getState() {
		return state;
	}
	
	public String getMemoryWordAsString(int index) {
		if (displayMode == DisplayMode.CODES) {
			return getMemoryWordAsString(memory[index]);
		}
		else {
			String word = getMemoryWordAsString(memory[index]);
			int instruction = Integer.parseInt(word.substring(0, 2), 16) ;				
			return getInstruction(instruction) + word.substring(2);
		}
	}
	
	public String getInstructionRegister() {
		if (displayMode == DisplayMode.CODES) {
			return getMemoryWordAsString(instructionRegister);
		}
		else {
			String word = getMemoryWordAsString(instructionRegister);
			int instruction = Integer.parseInt(word.substring(0, 2), 16) ;				
			return getInstruction(instruction) + word.substring(2);
		}
	}
	
	public int getProgramCounter() {
		return programCounter;
	}
	
	public String getProgramCounterAsString() {
		return getMemoryAddressAsString(programCounter * 4);
	}
	
	public long getWordAsAddress(int index) {
		return (long)(index * 4);
	}
	
	public String getAccumulator() {
		return getMemoryWordAsString(this.accumulator);
	}

	public String getInputBase10() {
		return String.format("%d", this.input);
	}
	
	public String getInput() {
		return getMemoryWordAsString(this.input);
	} 
	
	public String getOutput() {
		return getMemoryWordAsString(this.output);
	}

	public String getOutputBase10() {
		return String.format("%d", this.output);
	}
	
	public Simulator getPreviousState() {
		return this.previousState;
	}
	
	public long createInstruction(int instruction, long address) {
		return (instruction * 256 * 256 * 256) + address;
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
	
	private String getInstruction(int instruction) {
		
		switch (instruction) {
			case READ:
					return "READ";
			case WRITE:
					return "WRITE";
			case ADD:
					return "ADD";
			case SUB:
				return "SUB";
			case MULT:
				return "MULT";
			case DIV:
				return "DIV";
			case STOP:
				return "STOP";
			default:
				return String.format("  %02X", instruction);
		}
	}
	
	private String getMemoryWordAsString(int byte0, int byte1, int byte2, int byte3) {
		return String.format("%02X %02X %02X %02X", byte0, byte1, byte2, byte3);
	}
		
	private String getMemoryAddressAsString(byte byte0, byte byte1, byte byte2) {
		return String.format("%02X %02X %02X", byte0, byte1, byte2);
	}	
	
	public String getMemoryAddressAsString(long word) {
		return getMemoryAddressAsString((byte)(word>>16), (byte)((word & 0x00ff00)>>8), (byte)(word & 0x0000ff));	
	}
		
	public Simulator clone() {
		
		Simulator clone = new Simulator(null, displayMode);
		
		clone.accumulator = this.accumulator;
		clone.input = this.input;
		clone.instructionRegister = this.instructionRegister;
		clone.memory = this.memory.clone();
		clone.output = this.output;
		clone.programCounter = this.programCounter;
		clone.state = this.state;
		clone.previousState = this.previousState;

		clone.BYTES_PER_WORD = this.BYTES_PER_WORD;
		clone.WORDS_IN_PROGRAM = this.WORDS_IN_PROGRAM;
		
		return clone;
	}
	
	public void moveNextStep() {
		
		this.previousState = this.clone();
		
		if (state == State.START) {
			state = State.AFTER_FETCH;
			this.instructionRegister = memory[programCounter];
			this.programCounter+=1;
			
		}		
		else if (state == State.AFTER_FETCH) {
			state = State.AFTER_EXECUTE;
			//instruction has executed...
			int instruction = (int) (instructionRegister / 0xffffff);
			long address = instructionRegister % (0xffffff);
			address = instructionRegister & 0xffffff;
						
			switch(instruction) {
				case READ:	//READ
					if (address == INPUT_ADDRESS) {
						accumulator = input;
					}
					else if (address == OUTPUT_ADDRESS) {
						accumulator = output;
					} else {
						accumulator = memory[(int) (address / 4)];						
					}
					break;
				case WRITE:	//WRITE
					if (address == INPUT_ADDRESS) {
						input = accumulator;
					}
					else if (address == OUTPUT_ADDRESS) {
						output = accumulator;
					} else {
						memory[(int) (address / 4)] = accumulator;
					}
					break;
				case ADD:	//add
					accumulator += memory[(int) (address / 4)];
					break;
				case SUB:	//subtract
					accumulator -= memory[(int) (address / 4)];
					break;
				case MULT:	//multiply
					accumulator *= memory[(int) (address / 4)];
					break;
				case DIV:	//multiply
					accumulator /= memory[(int) (address / 4)];
					break;
				case STOP: //stop
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
	
	public void printCurrentState() {

		System.out.println(String.format("%s\t%s", "CIR     ", getMemoryWordAsString(this.instructionRegister)));
		System.out.println(String.format("%s\t%s", "PC      ", getMemoryWordAsString((long)this.programCounter * 4)));
		System.out.println(String.format("%s\t%s", "ACC     ", getMemoryWordAsString(this.accumulator)));
		System.out.println();
		System.out.println(String.format("%s\t%s", "ADDRESS  ", "CONTENTS"));
		for (int i = 0; i < WORDS_IN_PROGRAM; i++) {
			System.out.println(String.format("%s\t%s", getMemoryAddressAsString(i), getMemoryWordAsString(i)));
		}
		System.out.println();
		System.out.println(String.format("%s\t%s", "IN      ", getMemoryWordAsString(this.input)));
		System.out.println(String.format("%s\t%s", "OUT     ", getMemoryWordAsString(this.output)));
		
	}
	
}
