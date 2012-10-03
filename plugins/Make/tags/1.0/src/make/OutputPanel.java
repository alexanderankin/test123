package make;

import javax.swing.*;
import java.awt.BorderLayout;

public class OutputPanel extends JPanel {
	public JTextArea textArea;
	
	public OutputPanel() {
		setLayout(new BorderLayout());
		
		this.textArea = new JTextArea(MakePlugin.output.toString());
		this.textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(this.textArea);
		this.add(scrollPane, BorderLayout.CENTER);
	}
}
