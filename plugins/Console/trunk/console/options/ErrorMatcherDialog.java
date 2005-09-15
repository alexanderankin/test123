package console.options;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.gui.VariableGridLayout;
import org.gjt.sp.util.Log;

import console.ErrorMatcher;
import console.utils.StringList;

class ErrorMatcherDialog extends EnhancedDialog
{

	private static final long serialVersionUID = 128731556115770210L;

	// {{{ Private members
	private ErrorMatcher matcher;
	private ErrorMatcher tempMatcher;

	private JTextField name;

	private JTextField error;

	private JTextField warning;

	private JTextField extra;

	private JTextField filename;

	private JTextField line;

	private JTextField message;

	private JTextArea testArea;

	private JButton ok;

	private JButton test;

	private JButton cancel;

	private boolean isOK;

	// {{{ ErrorMatcherDialog constructor
	public ErrorMatcherDialog(Component comp, ErrorMatcher matcher)
	{
		super(JOptionPane.getFrameForComponent(comp), jEdit
				.getProperty("options.console.errors.title"), true);
		this.matcher = matcher;

		JPanel content = new JPanel(new BorderLayout(12, 12));
		content.setBorder(new EmptyBorder(12, 12, 12, 12));
		setContentPane(content);

		JPanel panel = new JPanel(new VariableGridLayout(
				VariableGridLayout.FIXED_NUM_COLUMNS, 2, 12, 6));
		JLabel label = new JLabel(jEdit
				.getProperty("options.console.errors.name"), JLabel.RIGHT);
		panel.add(label);
		panel.add(name = new JTextField(matcher.name));
		name.setColumns(20);

		label = new JLabel(jEdit.getProperty("options.console.errors.match"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(error = new JTextField(matcher.error));
		error.setColumns(20);

		label = new JLabel(jEdit.getProperty("options.console.errors.warning"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(warning = new JTextField(matcher.warning));
		warning.setColumns(20);

		label = new JLabel(jEdit.getProperty("options.console.errors.extra"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(extra = new JTextField(matcher.extraPattern));
		extra.setColumns(20);

		label = new JLabel(
				jEdit.getProperty("options.console.errors.filename"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(filename = new JTextField(matcher.fileBackref));
		filename.setColumns(20);

		label = new JLabel(jEdit.getProperty("options.console.errors.line"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(line = new JTextField(matcher.lineBackref));
		line.setColumns(20);

		label = new JLabel(jEdit.getProperty("options.console.errors.message"),
				JLabel.RIGHT);
		panel.add(label);
		panel.add(message = new JTextField(matcher.messageBackref));
		message.setColumns(20);

		label = new JLabel(jEdit
				.getProperty("options.console.errors.testarea.label"),
				JLabel.RIGHT);
		panel.add(label);
		testArea = new JTextArea(jEdit
				.getProperty("options.console.errors.testarea"));
		panel.add(testArea);

		content.add(BorderLayout.CENTER, panel);

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(new ActionHandler());
		getRootPane().setDefaultButton(ok);
		box.add(ok);
		box.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(new ActionHandler());
		box.add(cancel);

		test = new JButton(jEdit.getProperty("options.console.errors.test"));
		test.addActionListener(new ActionHandler());
		box.add(test);

		box.add(Box.createGlue());
		content.add(BorderLayout.SOUTH, box);

		pack();
		setLocationRelativeTo(GUIUtilities.getParentDialog(comp));
		setVisible(true);
	} // }}}

	public void validateRegex()
	{
		tempMatcher = new ErrorMatcher();
		// Check values
		tempMatcher.name = name.getText();
		tempMatcher.error = error.getText();
		tempMatcher.warning = warning.getText();
		tempMatcher.extraPattern = extra.getText();
		tempMatcher.fileBackref = filename.getText();
		tempMatcher.lineBackref = line.getText();
		tempMatcher.messageBackref = message.getText();
		isOK = tempMatcher.isValid();
		if (isOK) matcher = tempMatcher;
	}

	public void testRegex()
	{
		validateRegex();
		String testString = testArea.getText();
		jEdit.setProperty("options.console.errors.testarea", testString);
		StringList matches = tempMatcher.findMatches(testString);
		if (matches.size() == 0) {
			tempMatcher.errors.add("No Matches");
		}
		else {
			tempMatcher.errors.addAll(matches);
		}
		if (tempMatcher.errors.size() == 0) {
			matcher = tempMatcher;
		}
		String errorString = tempMatcher.errors.join("\n");
//		Log.log(Log.WARNING, ErrorMatcherDialog.class, errorString);
		GUIUtilities.error(JOptionPane.getFrameForComponent(this),
				"options.console.errors.checking", new String[] {errorString});
		tempMatcher.errors.clear();
		matcher.errors.clear();
	}

	// {{{ ok() method
	public void ok()
	{
		validateRegex();
		if (matcher.errors.size() > 0) {
			String errorString = matcher.errors.join("\n");
			GUIUtilities.error(JOptionPane.getFrameForComponent(this),
					"options.console.errors.checking", new String[] {errorString});
		}
		else {
			dispose();
		}
	} // }}}

	// {{{ cancel() method
	public void cancel()
	{
		dispose();
	} // }}}

	// {{{ isOK() method
	public boolean isOK()
	{
		return isOK;
	} // }}}

	// }}}

	// {{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if (evt.getSource() == ok)
				ok();
			else if (evt.getSource() == test)
			{
				testRegex();
			} else
				cancel();

		}
	} // }}}
} // }}}
