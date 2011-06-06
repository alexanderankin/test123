package gatchan.jedit.lucene;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class NewIndexDialog extends JDialog
{
	static public final String OPTION = "lucene.option.";
	static public final String MESSAGE = "lucene.message.";
	static private String GEOMETRY = OPTION + "NewLuceneIndexDialog";
	private JTextField name;
	private JComboBox type;
	private JComboBox analyzer;
	private boolean accepted = false;
	
	private void saveGeometry()
	{
		GUIUtilities.saveGeometry(this, GEOMETRY);
	} 

	public NewIndexDialog(Frame frame) {
		this(frame, null);
	}
	public NewIndexDialog(Frame frame, String initialName) {
		super(frame, jEdit.getProperty(MESSAGE + "NewIndexDialogTitle"), true);
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				saveGeometry();	
			}
		});
		setLayout(new GridLayout(0, 1));
		// Name panel
		JPanel p = new JPanel();
		add(p);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "IndexName")));
		name = new JTextField(30);
		p.add(name);
		if (initialName != null)
		{
			name.setText(initialName);
			name.setEditable(false);
		}
		// Index type panel
		p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		add(p);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "IndexType")));
		type = new JComboBox(IndexFactory.getIndexNames());
		p.add(type);
		// Analyzer panel
		p = new JPanel(new FlowLayout(FlowLayout.LEADING));
		add(p);
		p.add(new JLabel(jEdit.getProperty(MESSAGE + "Analyzer")));
		analyzer = new JComboBox(AnalyzerFactory.getAnalyzerNames());
		p.add(analyzer);
		String defaultAnalyzer = jEdit.getProperty(LucenePlugin.LUCENE_DEFAULT_ANALYZER);
		if (defaultAnalyzer != null)
			analyzer.setSelectedItem(defaultAnalyzer);
		// Button panel
		JPanel buttons = new JPanel();
		add(buttons);
		final JButton ok = new JButton("Ok");
		buttons.add(ok);
		final JButton cancel = new JButton("Cancel");
		buttons.add(cancel);
		ok.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					saveGeometry();
					save();
					setVisible(false);
				}
			}
		);
		cancel.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setVisible(false);
				}
			}
		);
		
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		ActionListener cancelListener =
			new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					cancel.doClick();
				}
			};
		rootPane.registerKeyboardAction(cancelListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		pack();
		GUIUtilities.loadGeometry(this, GEOMETRY);
	}
	
	private void save()
	{
		accepted = true;
	}
	
	public String getIndexName()
	{
		return name.getText();
	}

	public String getIndexAnalyzer()
	{
		return analyzer.getSelectedItem().toString();
	}

	public String getIndexType()
	{
		return type.getSelectedItem().toString();
	}

	public boolean accepted()
	{
		return accepted;
	}
}
