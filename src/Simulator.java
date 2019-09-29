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

//	INPUT	40	xx	xx	xx	Load value from input device into address xxxxxx
//	OUTPUT	41	xx	xx	xx	Store value at address xxxxxx in output device

//	JUMP	51	xx	xx	xx	Jump to address xxxxxx
//	JUMPIF	52	xx	xx	xx	Jump to address xxxxxx if compare flag is TRUE
//	AND		60	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//	OR		61	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//	NOT		62				Logical NOT accumulator value	
//	XOR		63	xx	xx	xx	Logical AND accumulator value and value at address xxxxxx
//  CLEAR	70	00	00	00	Set the accumulator to zero
//	STOP	7F	FF	FF	FF	
	
	private long instructionRegister = 0;
	private long accumulator = 0;
	private int programCounter = 0;
	private long input = 0;
	private long output = 0;
			
	private State state = State.START;
	public int BYTES_PER_WORD = 4;
	public int WORDS_IN_PROGRAM = 8;
	
	private long[] memory;

	public Simulator(Mode mode) {
		super();
		this.instructionRegister = 0;
		this.accumulator = 0;
		this.programCounter = 0;
		this.input = 0;
		this.output = 0;
		
		Random random = new Random(System.currentTimeMillis());
				
		if (mode == Mode.SIMPLE) {
			//randomize program - for now just a simple addition of two values, of which one comes from the input
			WORDS_IN_PROGRAM = 9;
			memory = new long[WORDS_IN_PROGRAM];
			
			long valueOne = (long)(Math.random() * 256 * 256);
			long valueTwo = (long)(Math.random() * 256 * 256);
			
			memory[0] = 0x4000001C;		//00	INPUT 	00 00 1C
			memory[1] = 0x10000018;		//04	READ  	00 00 18
			memory[2] = 0x2000001C;		//08	ADD   	00 00 1C
			memory[3] = 0x11000020;		//0C	WRITE 	00 00 20
			memory[4] = 0x41000020;		//10	OUTPUT	00 00 20	
			memory[5] = 0x7FFFFFFF;		//14	STOP		

			memory[6] = valueTwo;		//18	input value
			memory[7] = 0x00000000;		//1C	value to add
			memory[8] = 0x00000000;		//20	sum
			
			input = valueOne;
			output = 0;
						
		}
		else if (mode == Mode.INTERMEDIATE) {
			WORDS_IN_PROGRAM = 10;
			memory = new long[WORDS_IN_PROGRAM];
			
			long valueOne = (long)(Math.random() * 256 * 256);
			long valueTwo = (long)(Math.random() * 256 * 256);
			long valueThree = (long)(Math.random() * 16);
			
			memory[0] = 0x4000001C;				//00	INPUT 	00 00 1C
			memory[1] = 0x1000001C;				//04	READ  	00 00 1C
			if ((int)(Math.random() * 2) == 0) {
				memory[2] = 0x20000020;			//08	ADD   	00 00 20
			}
			else {
				memory[2] = 0x20000020;			//08	SUB   	00 00 20
			}
			if ((int)(Math.random() * 2) == 0) {
				memory[3] = 0x22000024;			//0C	MULT   	00 00 24
			}
			else {
				memory[3] = 0x23000024;			//0C	DIV   	00 00 24
			}
			memory[4] = 0x1100001C;				//10	WRITE 	00 00 1C
			memory[5] = 0x4100001C;				//14	OUTPUT	00 00 1C	
			memory[6] = 0x7FFFFFFF;				//18	STOP		

			memory[7] = 0x00000000;				//1C	input value
			memory[8] = valueTwo;				//20	value to add / subtract
			memory[9] = valueThree;				//24	value to multiply / divide
			
			input = valueOne;
			output = 0;
						
		}
		else if (mode == Mode.DEMO_SILENT || mode == Mode.DEMO_DESCRIPTIVE) {

			WORDS_IN_PROGRAM = 12;
			memory = new long[WORDS_IN_PROGRAM];
						
			memory[0] = 0x40000020;		//00	INPUT 	00 00 1C
			memory[1] = 0x10000020;		//04	READ  	00 00 18
			memory[2] = 0x21000024;		//08	SUB   	00 00 1C
			memory[3] = 0x22000028;		//0C	MULT   	00 00 1C
			memory[4] = 0x2300002C;		//10	DIV 	00 00 20
			memory[5] = 0x41000020;		//14	WRITE	00 00 20
			memory[6] = 0x41000020;		//18	OUTPUT	
			memory[7] = 0x7FFFFFFF;		//1C	STOP		

			memory[8] = 0x00000000;		//20	input value
			memory[9] = 0x00000020;		//24	value to subtract
			memory[10] = 0x00000005;	//28	value to multiply
			memory[11] = 0x00000009;	//2C	value to divide
			
			input = 0x48;
			output = 0;
						
		}
		
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
				case 0x40:	//input
					memory[(int) (address / 4)] = input;
					break;					
				case 0x41:  //output
					output = memory[(int) (address / 4)];
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
