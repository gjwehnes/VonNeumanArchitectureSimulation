import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import java.util.Random;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;

import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class SimulatorFrame extends JFrame {

	private final int MEMORY_GRID_TOP = 60;
	private final int MEMORY_CELL_ROW_HEIGHT = 26;
	private final static Mode mode = Mode.DEMO;
	
	private JPanel contentPane;
	private boolean incorrectAnswer = false;
	private boolean correctAnswer = true;
	private String verhoeffCode;
	private JButton btnNext;
	private JButton btnCheck;
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
				try {					
					SimulatorFrame frame = new SimulatorFrame(mode);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimulatorFrame(Mode mode) {
		
		
		Random random = new Random(System.currentTimeMillis());
//		VerhoeffAlgorithm verhoeff = new VerhoeffAlgorithm();
		int randomInt =  (int)(random.nextDouble() * 1000000);
//		String verhoeffDigit = verhoeff.generateVerhoeff(Integer.toString(randomInt));
//		this.verhoeffCode = Integer.toString(randomInt) + verhoeffDigit;
		//mod(98 - mod(number * 100, 97), 97)
		long modulus = (randomInt * 100) % 97;
		long checkSum = (98 - ((randomInt * 100) % 97) );
		String passCode = Long.toString(randomInt) + Long.toString(checkSum);
		System.out.println(Math.floorMod(Long.parseLong(passCode),97));
		simulator = new Simulator(mode);
		this.setTitle("Von Neumann Simulator: MODE = " + mode.toString());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 778, 663);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
        contentPane.setLayout(null);
        
        txtInstructionRegister = new JTextField();
        txtInstructionRegister.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtInstructionRegister.setBounds(124, 112, 128, 27);
        contentPane.add(txtInstructionRegister);
        txtInstructionRegister.setText(simulator.getInstructionRegister());
        txtInstructionRegister.setColumns(10);
        
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
        txtProgramCounter.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtProgramCounter.setColumns(10);
        txtProgramCounter.setBounds(124, 175, 128, 27);
        txtProgramCounter.setText(this.simulator.getProgramCounterAsString());
        contentPane.add(txtProgramCounter);
        
        lblProgramCounter = new JLabel(" PC");
        lblProgramCounter.setOpaque(true);
        lblProgramCounter.setHorizontalAlignment(SwingConstants.LEFT);
        lblProgramCounter.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblProgramCounter.setBackground(Color.LIGHT_GRAY);
        lblProgramCounter.setBounds(80, 161, 195, 58);
        contentPane.add(lblProgramCounter);
        
        txtAccumulator = new JTextField();
        txtAccumulator.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtAccumulator.setColumns(10);
        txtAccumulator.setBounds(124, 307, 128, 27);
        txtAccumulator.setText(simulator.getAccumulator());
        contentPane.add(txtAccumulator);
        
        lblAccumulator = new JLabel(" Acc.");
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
        
        lblBusses = new JLabel("Busses");
        lblBusses.setBounds(326, 360, 120, 30);
        lblBusses.setIcon(new ImageIcon("res\\busses.png"));
        
        contentPane.add(lblBusses);
        
        lblArrow = new JLabel(" --->");
        lblArrow.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblArrow.setIcon(new ImageIcon("res\\program counter.png"));        
        lblArrow.setBounds(405, 60, 48, 30);
        contentPane.add(lblArrow);
                
        lblAddress = new JLabel();
        lblAddress.setBackground(Color.LIGHT_GRAY);
        lblAddress.setText("Address");
        lblAddress.setOpaque(true);
        lblAddress.setFont(new Font("Courier New", Font.PLAIN, 16));
        lblAddress.setBounds(470, MEMORY_GRID_TOP, 128, MEMORY_CELL_ROW_HEIGHT - 2);
        contentPane.add(lblAddress);
        
        lblContent = new JLabel();
        lblContent.setBackground(Color.LIGHT_GRAY);
        lblContent.setText("   +1 +2 +3");
        lblContent.setOpaque(true);
        lblContent.setFont(new Font("Courier New", Font.PLAIN, 16));
        lblContent.setBounds(608, MEMORY_GRID_TOP, 128, MEMORY_CELL_ROW_HEIGHT - 2);
        contentPane.add(lblContent);
        //        contentPane.add(lblBackground);
                
        lblMemory = new JLabel("MEMORY");
        lblMemory.setForeground(Color.WHITE);
        lblMemory.setVerticalAlignment(SwingConstants.TOP);
        lblMemory.setOpaque(true);
        lblMemory.setHorizontalAlignment(SwingConstants.CENTER);
        lblMemory.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblMemory.setBackground(Color.DARK_GRAY);
        lblMemory.setBounds(453, 11, 298, 391);
        contentPane.add(lblMemory);

        txtCompletionCode = new JTextField(passCode);
        txtCompletionCode.setOpaque(true);
        txtCompletionCode.setHorizontalAlignment(SwingConstants.CENTER);
        txtCompletionCode.setFont(new Font("Tahoma", Font.BOLD, 16));
        txtCompletionCode.setBackground(new Color (0, 102, 255));
        txtCompletionCode.setForeground(Color.WHITE);
        txtCompletionCode.setBounds(186, 506, 141, 49);
        txtCompletionCode.setEditable(false);
        contentPane.add(txtCompletionCode);

        lblCorrect = new JLabel("Correct!");
        lblCorrect.setOpaque(true);
        lblCorrect.setHorizontalAlignment(SwingConstants.CENTER);
        lblCorrect.setFont(new Font("Tahoma", Font.PLAIN, 32));
        lblCorrect.setBackground(Color.GREEN);
        lblCorrect.setBounds(186, 506, 141, 49);
        contentPane.add(lblCorrect);
        
        lblIncorrect = new JLabel("Incorrect");
        lblIncorrect.setOpaque(true);
        lblIncorrect.setHorizontalAlignment(SwingConstants.CENTER);
        lblIncorrect.setFont(new Font("Tahoma", Font.PLAIN, 32));
        lblIncorrect.setBackground(Color.ORANGE);
        lblIncorrect.setBounds(35, 506, 141, 49);
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
        btnCheck.setBounds(35, 566, 141, 49);
        contentPane.add(btnCheck);
        
        btnNext = new JButton("Next");
        btnNext.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		btnNext_mouseClicked(arg0);
        	}
        });
        btnNext.setBounds(186, 566, 141, 49);
        contentPane.add(btnNext);
        
        btnInstructionSet = new JButton("Instruction Set");
        btnInstructionSet.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent arg0) {
        		btnInstructionSet_mouseClicked(arg0);
        	}
        });
        btnInstructionSet.setBounds(457, 566, 141, 49);
        contentPane.add(btnInstructionSet);
        
        txtInput = new JTextField();
        txtInput.setText(simulator.getInput());
        txtInput.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtInput.setColumns(10);
        txtInput.setBounds(600, 431, 128, 27);
        contentPane.add(txtInput);
        
        lblInput = new JLabel(" IN");
        lblInput.setOpaque(true);
        lblInput.setHorizontalAlignment(SwingConstants.LEFT);
        lblInput.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblInput.setBackground(Color.LIGHT_GRAY);
        lblInput.setBounds(556, 413, 195, 58);
        contentPane.add(lblInput);
        
        txtOutput = new JTextField();
        txtOutput.setText(simulator.getOutput());
        txtOutput.setFont(new Font("Courier New", Font.PLAIN, 16));
        txtOutput.setColumns(10);
        txtOutput.setBounds(600, 498, 128, 27);
        contentPane.add(txtOutput);
        
        lblOutput = new JLabel(" OUT");
        lblOutput.setOpaque(true);
        lblOutput.setHorizontalAlignment(SwingConstants.LEFT);
        lblOutput.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblOutput.setBackground(Color.LIGHT_GRAY);
        lblOutput.setBounds(556, 480, 195, 58);
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
		JTextField[] address = new JTextField[simulator.WORDS_IN_PROGRAM];
		
		for (int row = 0; row < address.length; row++) {
			address[row] = new JTextField();
			address[row].setFont(new Font("Courier New", Font.PLAIN, 16));
			address[row].setBounds(lblAddress.getX(), MEMORY_GRID_TOP + (row + 1) * MEMORY_CELL_ROW_HEIGHT, 128, MEMORY_CELL_ROW_HEIGHT - 2);
			address[row].setText(simulator.getMemoryAddressAsString(row));				
			address[row].setEditable(false);
	        contentPane.add(address[row]);
	        contentPane.setComponentZOrder(address[row], 0);
		}
		
		for (int row = 0; row < memory.length; row++) {
			memory[row] = new JTextField();
			memory[row].setFont(new Font("Courier New", Font.PLAIN, 16));
			memory[row].setBounds(lblContent.getX(), MEMORY_GRID_TOP + (row + 1) * MEMORY_CELL_ROW_HEIGHT, 128, MEMORY_CELL_ROW_HEIGHT - 2);
	        memory[row].setText(simulator.getMemoryWordAsString(row));
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

		correctAnswer = this.txtInstructionRegister.getText().equalsIgnoreCase(simulator.getInstructionRegister());
		correctAnswer &= this.txtProgramCounter.getText().equalsIgnoreCase(simulator.getProgramCounterAsString());
		correctAnswer &= this.txtAccumulator.getText().equalsIgnoreCase(simulator.getAccumulator());
		correctAnswer &= this.txtInput.getText().equalsIgnoreCase(simulator.getInput());
		correctAnswer &= this.txtOutput.getText().equalsIgnoreCase(simulator.getOutput());

		for (int i = 0; i < simulator.WORDS_IN_PROGRAM; i++) {
			correctAnswer &= this.memory[i].getText().equalsIgnoreCase(simulator.getMemoryWordAsString(i));
		}
		
		incorrectAnswer = ! correctAnswer;
	}
	
	private void setControls() {
		
		this.lblCorrect.setVisible(correctAnswer && simulator.getState() != State.START && mode != Mode.DEMO);
		this.lblIncorrect.setVisible(incorrectAnswer && mode != Mode.DEMO);
		this.txtCompletionCode.setVisible(simulator.getState() == State.COMPLETE);
		
		this.btnCheck.setEnabled(! correctAnswer && simulator.getState() != State.COMPLETE);
		this.btnNext.setEnabled(correctAnswer && simulator.getState() != State.COMPLETE);
		
		this.lblCurrentStep.setText(simulator.getState().toString());
		this.txtCurrentStepDescription.setText(simulator.getState().getDescription());				
		
		int currentAddress = simulator.getProgramCounter();
		this.lblArrow.setLocation(this.lblArrow.getX(), this.lblAddress.getY() + (currentAddress + 1) * MEMORY_CELL_ROW_HEIGHT);

		this.lblArrow.setVisible(mode == Mode.DEMO);
		this.lblCurrentStep.setVisible(mode != Mode.DEMO);
		this.txtCurrentStepDescription.setVisible(mode != Mode.DEMO);
		
	
	
		this.contentPane.setEnabled(simulator.getState() != State.COMPLETE);
		
	}
		
	protected void btnNext_mouseClicked(MouseEvent arg0) {
		transition(Action.NEXT);
		setControls();
	}

	
	protected void btnCheck_mouseClicked(MouseEvent e) {
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
	
}
