package xslt;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.xalan.trace.*;
import org.gjt.sp.jedit.*;

public class XSLTProgressDialog extends JDialog implements TraceListener
, ActionListener
{
	private int generatedNode = 0;
	private JLabel generated;
	private JButton close;

	public XSLTProgressDialog(View parent, String name) {
		super(parent, name);
		GridBagLayout layout = new GridBagLayout();
		getContentPane().setLayout(layout);

		GridBagConstraints constraints = new GridBagConstraints();
		JLabel title = new JLabel(jEdit.getProperty("xslt.progressdialog.label"));
		constraints.gridx=0;
		constraints.gridy=0;

		constraints.insets = new Insets(5,5,5,5);
		layout.setConstraints(title, constraints);

		JLabel generatedTitle = new JLabel(jEdit.getProperty("xslt.progressdialog.generated"));
		constraints.gridx=0;
		constraints.gridy=1;
		layout.setConstraints(generatedTitle, constraints);

		generated = new JLabel("0");
		constraints.gridx=1;
		constraints.gridy=1;
		layout.setConstraints(generated, constraints);

		close = new JButton(jEdit.getProperty("xslt.progressdialog.close"));
		constraints.gridx=2;
		constraints.gridy=2;
		layout.setConstraints(close, constraints);

		close.addActionListener(this);
		close.setEnabled(false);

		getContentPane().add(title);
		getContentPane().add(generatedTitle);
		getContentPane().add(generated);
		getContentPane().add(close);
		pack();
	}

	public void selected(SelectionEvent event) throws javax.xml.transform.TransformerException
	{

	}

	public void trace(TracerEvent event)
	{

	}

	protected void reset() {
		generatedNode = 0;
		close.setEnabled(true);
	}

	public void generated(GenerateEvent event)
	{
		generatedNode++;
		generated.setText(Integer.toString(generatedNode));
	}

	public void actionPerformed(ActionEvent e) {
		setVisible(false);
		generated.setText("0");
		close.setEnabled(false);
	}

}
