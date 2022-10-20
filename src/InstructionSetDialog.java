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
	 * Create the dialog.
	 */
	public InstructionSetDialog() {
		
		String content = "";
		content +=   " Code Name Description";
		content += "\n 10   READ  xx xx xx	    Read value from address xx xx xx into accumulator";
		content += "\n 11   WRITE xx xx xx	    Write value in accumulator to address xx xx xx";
		content += "\n 20   ADD   xx xx xx	    Add value at address xx xx xx to accumulator";
		content += "\n 21   SUB   xx xx xx	    Subtract value at address xx xx xx from accumulator";
		content += "\n 22   MULT  xx xx xx	    Multiply accumulator value by value at address xx xx xx";
		content += "\n 23   DIV   xx xx xx	    Divide accumulator value by value at address xx xx xx.";
		content += "\n 7F   STOP  -- -- --	    Stop program execution immediately";

		setBounds(100, 100, 642, 300);
		getContentPane().setLayout(null);
		this.setTitle("Instruction Set");
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
