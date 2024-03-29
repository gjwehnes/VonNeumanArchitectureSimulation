import java.awt.BorderLayout;
import java.awt.EventQueue;

//import javax.activation.ActivationDataFlavor;
//import javax.activation.DataHandler;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.Random;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimulatorFrame extends JFrame {

	private final int MEMORY_GRID_TOP = 60;
	private final int MEMORY_CELL_ROW_HEIGHT = 26;
	private static ProgramMode PROGRAM_MODE = ProgramMode.SIMPLE;
	private static DisplayMode DISPLAY_MODE = DisplayMode.INSTRUCTIONS;
	private static RunMode RUN_MODE = RunMode.INTERACTIVE;
	private static IOMode IO_MODE = IOMode.BASE_16;
	private static boolean ALLOW_COMPLETION = false;
	
	private JPanel contentPane;
	private boolean incorrectAnswer = false;
	private boolean correctAnswer = true;
	private String verhoeffCode;
	private JButton btnNext;
	private JButton btnCheck;
	private JButton btnPrevious;
	private Simulator simulator;
	
	private JTextField txtInstructionRegister;
	private JTextField txtProgramCounter;
	private JTextField txtAccumulator;
	private JTextField txtInput;
	private JTextField txtOutput;

	private JTable table;
	private JTextField txtCompletionCode;
	private JLabel lblCorrect;
	private JLabel lblIncorrect;
	private JLabel lblCurrentStep;
	private JLabel lblProgramCounter;
	private JLabel lblAccumulator;
	private JLabel lblCu;
	private JLabel lblALU;
	private JLabel lblCPU;
	private JLabel lblMemory;
	private JLabel lblAddress;
	private JLabel lblContent;
	private JLabel lblBusses;
	private JLabel lblArrow;
	private JTextField[] memory;
	private JLabel[] address;
	JTextArea txtCurrentStepDescription;
	private JLabel lblInput;
	private JLabel lblOutput;
	private JButton btnInstructionSet;
	
	public interface Reorderable {
		   public void reorder(int fromIndex, int toIndex);
		}
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				ImageIcon ii = new ImageIcon("res\\busses.png");
				if (ii.getIconWidth() <= 0) {
					//somehow cannot read the res folder; exit
					JOptionPane.showMessageDialog(null,
						    "Cannot open resource folder","VN-simulator", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					if (args != null && args.length > 0) {
						if (args[0].toUpperCase().contains(ProgramMode.SIMPLE.toString())) {
							PROGRAM_MODE = ProgramMode.SIMPLE;
						}
						else if (args[0].toUpperCase().contains(ProgramMode.INTERMEDIATE.toString())) {
							PROGRAM_MODE = ProgramMode.INTERMEDIATE;
						}
						else if (args[0].toUpperCase().contains(ProgramMode.TEMPERATURE_CONVERSION.toString())) {
							PROGRAM_MODE = ProgramMode.TEMPERATURE_CONVERSION;
						}
						else {
							//use default
						}
						
						if (args[1].toUpperCase().contains(RunMode.DEMO_SILENT.toString())) {
							RUN_MODE = RunMode.DEMO_SILENT;
						}
						else if (args[1].toUpperCase().contains(RunMode.DEMO_DESCRIPTIVE.toString())) {
							RUN_MODE = RunMode.DEMO_DESCRIPTIVE;
						}
						else if (args[1].toUpperCase().contains(RunMode.INTERACTIVE.toString())) {
							RUN_MODE = RunMode.INTERACTIVE;
						}
						else {
							//use default
						}
						
						if (args[2].toUpperCase().contains(DisplayMode.INSTRUCTIONS.toString())) {
							DISPLAY_MODE = DisplayMode.INSTRUCTIONS;
						}
						else if (args[2].toUpperCase().contains(DisplayMode.CODES.toString())) {
							DISPLAY_MODE = DisplayMode.CODES;
						}
						else {
							//use default
						}
						
						if (args[3].toUpperCase().contains(IOMode.BASE_10.toString())) {
							IO_MODE = IOMode.BASE_10;
						}
						else if (args[3].toUpperCase().contains(IOMode.BASE_16.toString())) {
							IO_MODE = IOMode.BASE_16;
						}
						else {
							//use default
						}
						
						
					}
					SimulatorFrame frame = new SimulatorFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class KeyDispatcher implements KeyEventDispatcher {
		//https://planetjon.ca/3089/java-global-jframe-key-listener/
	    public boolean dispatchKeyEvent(KeyEvent e) {
	        if(e.getID() == KeyEvent.KEY_TYPED && (RUN_MODE != RunMode.INTERACTIVE)) {
	        	this_keyTyped(e);	 
	        }
	        //Allow the event to be redispatched
	        return false;
	    }
	}	
	
	/**
	 * Create the frame.
	 */
	public SimulatorFrame() {		
		
		Random random = new Random(System.currentTimeMillis());
		int randomInt =  (int)(random.nextDouble() * 1000);	
		long minuteOfYear = (LocalDateTime.now().getDayOfYear() * 24 * 60) + (LocalDateTime.now().getHour() * 60) + (LocalDateTime.now().getMinute());		
		long mod97 = (minuteOfYear * 1000) + randomInt;
		long checkSum = (98 - ((mod97 * 100) % 97) );
		String passCode = String.format("%09d%02d", mod97, checkSum);

		simulator = new Simulator(PROGRAM_MODE, DISPLAY_MODE);

		this.setTitle("Von Neumann Simulator: Program = " + PROGRAM_MODE.toString());		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 778, 663);
		KeyboardFocusManager manager =
		         KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher( new KeyDispatcher() );
		 

		contentPane = new JPanel();
		contentPane.setFocusable(RUN_MODE == RunMode.DEMO_SILENT);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
        contentPane.setLayout(null);
        
        lblBusses = new JLabel("Busses");
        lblBusses.setBounds(320, 280, 120, 246);
        lblBusses.setIcon(new ImageIcon("res\\busses.png"));        
        contentPane.add(lblBusses);
        
        txtInstructionRegister = new JTextField();
        txtInstructionRegister.setFont(new Font("Consolas", Font.BOLD, 16));
        txtInstructionRegister.setBounds(116, 112, 136, 27);
        contentPane.add(txtInstructionRegister);
        txtInstructionRegister.setText(simulator.getInstructionRegister());
        txtInstructionRegister.setColumns(10);
        txtInstructionRegister.setHorizontalAlignment(JTextField.RIGHT);

        
        btnCheck = new JButton("Check");
        btnCheck.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		btnCheck_mouseClicked(e);
        	}
        });
        
        JLabel lblInstructionRegister = new JLabel(" IR");
        lblInstructionRegister.setHorizontalAlignment(SwingConstants.LEFT);
        lblInstructionRegister.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblInstructionRegister.setBackground(Color.LIGHT_GRAY);
        lblInstructionRegister.setOpaque(true);
        lblInstructionRegister.setBounds(80, 94, 195, 58);
        contentPane.add(lblInstructionRegister);
        
        txtProgramCounter = new JTextField();
        txtProgramCounter.setFont(new Font("Consolas", Font.BOLD, 16));
        txtProgramCounter.setColumns(10);
        txtProgramCounter.setBounds(116, 175, 136, 27);
        txtProgramCounter.setText(this.simulator.getProgramCounterAsString());
        txtProgramCounter.setHorizontalAlignment(JTextField.RIGHT);
        contentPane.add(txtProgramCounter);
        
        lblProgramCounter = new JLabel(" PC");
        lblProgramCounter.setOpaque(true);
        lblProgramCounter.setHorizontalAlignment(SwingConstants.LEFT);
        lblProgramCounter.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblProgramCounter.setBackground(Color.LIGHT_GRAY);
        lblProgramCounter.setBounds(80, 161, 195, 58);
        contentPane.add(lblProgramCounter);
        
        txtAccumulator = new JTextField();
        txtAccumulator.setFont(new Font("Consolas", Font.BOLD, 16));
        txtAccumulator.setColumns(10);
        txtAccumulator.setBounds(124, 307, 128, 27);
        txtAccumulator.setText(simulator.getAccumulator());
        txtAccumulator.setHorizontalAlignment(JTextField.RIGHT);
        contentPane.add(txtAccumulator);
        
        lblAccumulator = new JLabel(" Acc");
        lblAccumulator.setOpaque(true);
        lblAccumulator.setHorizontalAlignment(SwingConstants.LEFT);
        lblAccumulator.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblAccumulator.setBackground(Color.LIGHT_GRAY);
        lblAccumulator.setBounds(80, 290, 195, 58);
        contentPane.add(lblAccumulator);
        
        lblCu = new JLabel("CU");
        lblCu.setVerticalAlignment(SwingConstants.TOP);
        lblCu.setOpaque(true);
        lblCu.setHorizontalAlignment(SwingConstants.CENTER);
        lblCu.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblCu.setBackground(Color.GRAY);
        lblCu.setBounds(53, 60, 249, 182);
        contentPane.add(lblCu);
        
        lblALU = new JLabel("ALU");
        lblALU.setVerticalAlignment(SwingConstants.TOP);
        lblALU.setOpaque(true);
        lblALU.setHorizontalAlignment(SwingConstants.CENTER);
        lblALU.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblALU.setBackground(Color.GRAY);
        lblALU.setBounds(53, 253, 249, 137);
        contentPane.add(lblALU);
        
        lblCPU = new JLabel("CPU");
        lblCPU.setForeground(Color.WHITE);
        lblCPU.setVerticalAlignment(SwingConstants.TOP);
        lblCPU.setOpaque(true);
        lblCPU.setHorizontalAlignment(SwingConstants.CENTER);
        lblCPU.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblCPU.setBackground(Color.DARK_GRAY);
        lblCPU.setBounds(35, 11, 286, 391);
        contentPane.add(lblCPU);
                
        lblArrow = new JLabel(" >>");
        lblArrow.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblArrow.setIcon(new ImageIcon("res\\program counter.png"));        
        lblArrow.setBounds(390, 60, 48, 30);
        contentPane.add(lblArrow);
                        
        lblAddress = new JLabel();
        lblAddress.setBackground(Color.LIGHT_GRAY);
        lblAddress.setText("Address");
        lblAddress.setOpaque(true);
        lblAddress.setFont(new Font("Courier New", Font.BOLD + Font.ITALIC, 16));
        lblAddress.setBounds(452, MEMORY_GRID_TOP, 128, MEMORY_CELL_ROW_HEIGHT - 2);
        contentPane.add(lblAddress);
        
        lblContent = new JLabel();
        lblContent.setBackground(Color.LIGHT_GRAY);
        lblContent.setText("    +1    +2    +3");
        lblContent.setOpaque(true);
        lblAddress.setFont(new Font("Courier New", Font.BOLD + Font.ITALIC, 16));
        lblContent.setBounds(592, MEMORY_GRID_TOP, 136, MEMORY_CELL_ROW_HEIGHT - 2);
        contentPane.add(lblContent);
        //        contentPane.add(lblBackground);
                
        lblMemory = new JLabel("MEMORY");
        lblMemory.setForeground(Color.WHITE);
        lblMemory.setVerticalAlignment(SwingConstants.TOP);
        lblMemory.setOpaque(true);
        lblMemory.setHorizontalAlignment(SwingConstants.CENTER);
        lblMemory.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblMemory.setBackground(Color.DARK_GRAY);
        lblMemory.setBounds(440, 11, 298, 391);
        contentPane.add(lblMemory);

        txtCompletionCode = new JTextField(passCode);
        txtCompletionCode.setOpaque(true);
        txtCompletionCode.setHorizontalAlignment(SwingConstants.CENTER);
        txtCompletionCode.setFont(new Font("Tahoma", Font.BOLD, 12));
        txtCompletionCode.setBackground(new Color (0, 102, 255));
        txtCompletionCode.setForeground(Color.WHITE);
        txtCompletionCode.setBounds(141, 528, 96, 27);
        txtCompletionCode.setEditable(false);
        contentPane.add(txtCompletionCode);

        lblCorrect = new JLabel("Correct!");
        lblCorrect.setOpaque(true);
        lblCorrect.setHorizontalAlignment(SwingConstants.CENTER);
        lblCorrect.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblCorrect.setBackground(Color.GREEN);
        lblCorrect.setBounds(141, 528, 96, 27);
        contentPane.add(lblCorrect);
        
        lblIncorrect = new JLabel("Incorrect");
        lblIncorrect.setOpaque(true);
        lblIncorrect.setHorizontalAlignment(SwingConstants.CENTER);
        lblIncorrect.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblIncorrect.setBackground(Color.ORANGE);
        lblIncorrect.setBounds(141, 528, 96, 27);
        contentPane.add(lblIncorrect);

        lblCurrentStep = new JLabel("current step");
        lblCurrentStep.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblCurrentStep.setBounds(35, 413, 292, 27);
        contentPane.add(lblCurrentStep);
                
        txtCurrentStepDescription = new JTextArea();
        txtCurrentStepDescription.setBackground(UIManager.getColor("Button.background"));
        txtCurrentStepDescription.setWrapStyleWord(true);
        txtCurrentStepDescription.setEditable(false);
        txtCurrentStepDescription.setBounds(34, 448, 287, 49);
        txtCurrentStepDescription.setLineWrap(true);
        contentPane.add(txtCurrentStepDescription);
        btnCheck.setBounds(141, 566, 96, 30);
        contentPane.add(btnCheck);
        
        btnNext = new JButton("Next");
        btnNext.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		btnNext_mouseClicked(arg0);
        	}
        });
        btnNext.setBounds(247, 566, 96, 30);
        contentPane.add(btnNext);
        
        btnPrevious = new JButton("Prev");
        btnPrevious.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		btnPrevious_mouseClicked(arg0);
        	}
        });
        btnPrevious.setBounds(35, 566, 96, 30);
        contentPane.add(btnPrevious);
        
        
        btnInstructionSet = new JButton("Instruction Set");
        btnInstructionSet.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		btnInstructionSet_mouseClicked(arg0);
        	}
        });
        btnInstructionSet.setBounds(440, 566, 125, 27);
        contentPane.add(btnInstructionSet);
        
        JLabel txtInputAddress = new JLabel(simulator.getMemoryAddressAsString(simulator.INPUT_ADDRESS));
        txtInputAddress.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtInputAddress.setBounds(452, 436 , 128, MEMORY_CELL_ROW_HEIGHT - 2);
        txtInputAddress.setFont(new Font("Courier New", Font.ITALIC, 16));
        txtInputAddress.setBackground(Color.LIGHT_GRAY);
        txtInputAddress.setOpaque(true);
        contentPane.add(txtInputAddress);

        txtInput = new JTextField();
        txtInput.setText(IO_MODE == IOMode.BASE_10 ? simulator.getInputBase10() : simulator.getInput());
        txtInput.setFont(new Font("Consolas", Font.BOLD, 16));
        txtInput.setColumns(10);
        txtInput.setBounds(592, 436, 136, MEMORY_CELL_ROW_HEIGHT - 2);
        txtInput.setHorizontalAlignment(JTextField.RIGHT);
        txtInput.setForeground(Color.RED);
        txtInput.setBackground(Color.BLACK);
        contentPane.add(txtInput);
        
        lblInput = new JLabel("INPUT " +  (IO_MODE == IOMode.BASE_10 ? " (base 10)" : " (base 16)"));
        lblInput.setFont(new Font("Consolas", Font.BOLD, 16));
        lblInput.setOpaque(true);
        lblInput.setHorizontalAlignment(SwingConstants.CENTER);
        lblInput.setVerticalAlignment(SwingConstants.TOP);
        lblInput.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblInput.setBackground(Color.LIGHT_GRAY);
        lblInput.setBounds(440, 412, 298, 58);
        contentPane.add(lblInput);
                
        JLabel txtOutputAddress = new JLabel(simulator.getMemoryAddressAsString(simulator.OUTPUT_ADDRESS));
        txtOutputAddress.setBounds(452, 504 , 128, MEMORY_CELL_ROW_HEIGHT - 2);
        txtOutputAddress.setFont(new Font("Courier New", Font.ITALIC, 16));
        txtOutputAddress.setBackground(Color.LIGHT_GRAY);
        txtOutputAddress.setOpaque(true);
        contentPane.add(txtOutputAddress);

        txtOutput = new JTextField();
        txtOutput.setEditable(RUN_MODE == RunMode.INTERACTIVE);
        txtOutput.setText(IO_MODE == IOMode.BASE_10 ? simulator.getOutputBase10() : simulator.getOutput());
        txtOutput.setFont(new Font("Consolas", Font.BOLD, 16));
        txtOutput.setColumns(10);
        txtOutput.setBounds(592, 504, 136, MEMORY_CELL_ROW_HEIGHT - 2);
        txtOutput.setHorizontalAlignment(JTextField.RIGHT);
        txtOutput.setForeground(Color.RED);
        txtOutput.setBackground(Color.BLACK);
        contentPane.add(txtOutput);        
        
        lblOutput = new JLabel("OUTPUT " +  (IO_MODE == IOMode.BASE_10 ? " (base 10)" : " (base 16)"));
        lblOutput.setFont(new Font("Consolas", Font.BOLD, 16));
        lblOutput.setOpaque(true);
        lblOutput.setHorizontalAlignment(SwingConstants.CENTER);
        lblOutput.setVerticalAlignment(SwingConstants.TOP);
        lblOutput.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblOutput.setBackground(Color.LIGHT_GRAY);
        lblOutput.setBounds(440, 480, 298, 58);
        contentPane.add(lblOutput);
        
        initializeGrid();
        
        JLabel lblBackground = new JLabel("New label");
        lblBackground.setIcon(new ImageIcon("C:\\Users\\gjwehnes\\Desktop\\von-neuman-architecture.png"));
        lblBackground.setBounds(5, 5, 960, 711);
        
//        simulator.moveNextStep();
        setControls();
		
	}
	
	private void initializeGrid() {

		memory = new JTextField[simulator.WORDS_IN_PROGRAM];
		address = new JLabel[simulator.WORDS_IN_PROGRAM];
		
		for (int row = 0; row < address.length; row++) {
			address[row] = new JLabel();
			address[row].setBackground(Color.LIGHT_GRAY);
			address[row].setOpaque(true);
			address[row].setFont(new Font("Courier New", Font.ITALIC, 16));			
			address[row].setBounds(lblAddress.getX(), MEMORY_GRID_TOP + (row + 1) * MEMORY_CELL_ROW_HEIGHT, lblAddress.getWidth(), MEMORY_CELL_ROW_HEIGHT - 2);
			address[row].setText(simulator.getMemoryAddressAsString(row*4));				
	        contentPane.add(address[row]);
	        contentPane.setComponentZOrder(address[row], 0);
		}
		
		for (int row = 0; row < memory.length; row++) {
			memory[row] = new JTextField();
			memory[row].setFont(new Font("Consolas", Font.BOLD, 16));
			memory[row].setBounds(lblContent.getX(), MEMORY_GRID_TOP + (row + 1) * MEMORY_CELL_ROW_HEIGHT, lblContent.getWidth(), MEMORY_CELL_ROW_HEIGHT - 2);
	        memory[row].setText(simulator.getMemoryWordAsString(row));
	        memory[row].setHorizontalAlignment(JTextField.RIGHT);
	        contentPane.add(memory[row]);
	        contentPane.setComponentZOrder(memory[row], 0);
		}
		
		
	}
		
	private void initializeTable() {
		Object[][] data = new Object[7][9];
		
		data[0][0] = new String("Address");
		data[0][1] = "";
		data[0][2] = "+1";
		data[0][3] = "+2";
		data[0][4] = "+3";
		
		data[1][0] = "00 00 00 00";	data[1][1] = "40"; data[1][2] = "00"; data[1][3] = "00"; data[1][4] = "80";		
		
		TableColumn col0 = table.getColumnModel().getColumn(0);
		col0.setPreferredWidth(200);

		TableColumn col1 = table.getColumnModel().getColumn(1);
		col1.setPreferredWidth(50);

		TableColumn col2 = table.getColumnModel().getColumn(2);
		col2.setPreferredWidth(50);

		TableColumn col3 = table.getColumnModel().getColumn(3);
		col3.setPreferredWidth(50);

		TableColumn col4 = table.getColumnModel().getColumn(4);
		col4.setPreferredWidth(50);

		for (int row = 0; row < table.getRowCount(); row++) {
			table.setRowHeight(row, 40);
		}
		
	}
	
	private void transition(Action action) {
		if (action == Action.NEXT) {
			if (correctAnswer) {
				simulator.moveNextStep();
				incorrectAnswer = false;
				correctAnswer = false;
			}
		} else if (action == Action.CHECK) {
			getInputCorrect();
		}
		
	}
	
	private void getInputCorrect() {

		correctAnswer = isEquivalent(this.txtInstructionRegister.getText(),simulator.getInstructionRegister());
		correctAnswer &= isEquivalent(this.txtProgramCounter.getText(),simulator.getProgramCounterAsString());
		correctAnswer &= isEquivalent(this.txtAccumulator.getText(),simulator.getAccumulator());
		correctAnswer &= isEquivalent(this.txtInput.getText(),(IO_MODE == IOMode.BASE_10 ? simulator.getInputBase10() : simulator.getInput()));
		correctAnswer &= isEquivalent(this.txtOutput.getText(),(IO_MODE == IOMode.BASE_10 ? simulator.getOutputBase10() : simulator.getOutput()));

		for (int i = 0; i < simulator.WORDS_IN_PROGRAM; i++) {
			correctAnswer &= isEquivalent(this.memory[i].getText(),simulator.getMemoryWordAsString(i));
		}
		
		incorrectAnswer = ! correctAnswer;
	}
	
	private boolean isEquivalent(String a, String b) {
		return a.replaceAll("\\s+","").equalsIgnoreCase(b.replaceAll("\\s+",""));
	}
	
	private void setInputCorrect() {

		this.txtInstructionRegister.setText(simulator.getInstructionRegister());
		this.txtProgramCounter.setText(simulator.getProgramCounterAsString());
		this.txtAccumulator.setText(simulator.getAccumulator());
		this.txtInput.setText(IO_MODE == IOMode.BASE_10 ? simulator.getInputBase10() : simulator.getInput());
		this.txtOutput.setText(IO_MODE == IOMode.BASE_10 ? simulator.getOutputBase10() : simulator.getOutput());

		for (int i = 0; i < simulator.WORDS_IN_PROGRAM; i++) {
			this.memory[i].setText(simulator.getMemoryWordAsString(i));
		}
		
	}
	
	
	private void setControls() {
		
		this.lblCorrect.setVisible(correctAnswer && simulator.getState() != State.START && RUN_MODE != RunMode.DEMO_SILENT);
		this.lblIncorrect.setVisible(incorrectAnswer && RUN_MODE != RunMode.DEMO_SILENT);
		this.txtCompletionCode.setVisible(simulator.getState() == State.COMPLETE && (RUN_MODE == RunMode.INTERACTIVE) && ALLOW_COMPLETION);
		
		this.btnCheck.setEnabled(! correctAnswer && simulator.getState() != State.COMPLETE && simulator.getState() != State.START);
		this.btnNext.setEnabled(correctAnswer && simulator.getState() != State.COMPLETE);
		this.btnPrevious.setEnabled(simulator.getPreviousState() != null);
		
		this.lblCurrentStep.setText(simulator.getState().toString());
		this.txtCurrentStepDescription.setText(simulator.getState().getDescription());				
		
		int currentWord = simulator.getProgramCounter();
		this.lblArrow.setLocation(this.lblArrow.getX(), this.lblAddress.getY() + (currentWord + 1) * MEMORY_CELL_ROW_HEIGHT);

		this.lblArrow.setVisible(RUN_MODE != RunMode.INTERACTIVE);
		this.lblCurrentStep.setVisible(RUN_MODE != RunMode.DEMO_SILENT);
		this.txtCurrentStepDescription.setVisible(RUN_MODE != RunMode.DEMO_SILENT);
			
		this.contentPane.setEnabled(simulator.getState() != State.COMPLETE);
		
		this.btnCheck.setVisible(RUN_MODE == RunMode.INTERACTIVE);
		this.btnNext.setVisible(RUN_MODE == RunMode.INTERACTIVE);
		this.btnInstructionSet.setVisible(RUN_MODE != RunMode.DEMO_SILENT);

		Component[] components = this.getContentPane().getComponents();
		
	    for (Component component : components) {
	        if (component instanceof JTextField) {
	        	((JTextField) component).setEditable(RUN_MODE == RunMode.INTERACTIVE );
	        }
	    }		
		this.repaint();
		
	}

	protected void btnPrevious_mouseClicked(MouseEvent arg0) {

		if (this.btnPrevious.isEnabled() == false ) {
			return;
		}
		
		if (simulator.getPreviousState() != null) {
			correctAnswer = true;
			simulator = simulator.getPreviousState();
			this.setInputCorrect();
			setControls();
		}
	}

	
	protected void btnNext_mouseClicked(MouseEvent arg0) {
		
		if (this.btnNext.isEnabled() == false ) {
			return;
		}
		
		transition(Action.NEXT);
		setControls();
	}

	
	protected void btnCheck_mouseClicked(MouseEvent e) {
		
		if (this.btnCheck.isEnabled() == false ) {
			return;
		}
		
		simulator.printCurrentState();
		transition(Action.CHECK);
		setControls();
	}
	
	protected void btnInstructionSet_mouseClicked(MouseEvent arg0) {
		InstructionSetDialog is = new InstructionSetDialog();
		is.setLocationRelativeTo(this);
		is.setModalityType(ModalityType.APPLICATION_MODAL);
		is.setVisible(true);
				
		this.repaint();		
		is = null;
	}	
	
	private void this_keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == ' ') {
			correctAnswer = true;
			transition(Action.NEXT);
			this.setInputCorrect();
			setControls();
			System.out.println(simulator.getState().toString());
		}
		else if (arg0.getKeyChar() == 'b') {
			if (simulator.getPreviousState() != null) {
				correctAnswer = true;
				simulator = simulator.getPreviousState();
				this.setInputCorrect();
				setControls();
				System.out.println(simulator.getState().toString());
			}
		}
	}
}
