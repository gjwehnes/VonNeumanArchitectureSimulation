import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Color;
import javax.swing.UIManager;

public class InstructionSetDialog extends JDialog {

	JTextArea txtaInstructionSet = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			InstructionSetDialog dialog = new InstructionSetDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public InstructionSetDialog() {
		
		String content = "";
		content +=   "Instruction	Value	    Description";
		content += "\nREAD      10 xx xx xx	    Read value from address xx xx xx into accumulator";
		content += "\nWRITE     11 xx xx xx	    Write value in accumulator to address xx xx xx";
		content += "\nADD       20 xx xx xx	    Add value at address xx xx xx to accumulator";
		content += "\nSUB       21 xx xx xx	    Subtract value at address xx xx xx to accumulator";
		content += "\nMULT      22 xx xx xx	    Multiply accumulator value by value at address xx xx xx";
		content += "\nDIV       23 xx xx xx	    Divide accumulator value by value at address xx xx xx.";
		content += "\nSTOP      7F -- -- --	    Stop program execution immediately";

		setBounds(100, 100, 642, 300);
		getContentPane().setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 228, 584, 33);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						okButton_mouseClicked(e);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		txtaInstructionSet = new JTextArea();
		txtaInstructionSet.setBounds(10, 11, 606, 219);
		txtaInstructionSet.setRows(12);
		getContentPane().add(txtaInstructionSet);
		txtaInstructionSet.setBackground(UIManager.getColor("Button.background"));
		txtaInstructionSet.setFont(new Font("Consolas", Font.PLAIN, 12));
		txtaInstructionSet.setText(content);
		
		
	}


	protected void okButton_mouseClicked(MouseEvent e) {
		this.setVisible(false);		
	}
}
