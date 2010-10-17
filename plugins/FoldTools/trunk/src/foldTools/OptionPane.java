package foldTools;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

@SuppressWarnings("serial")
public class OptionPane extends AbstractOptionPane
{
	private static final String MESSAGE = "messages.foldTools.";
	private static final String PROP = "props.foldTools.";
	private JSpinner before, after;
	
	public OptionPane()
	{
		super("foldTools");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		before = new JSpinner(new SpinnerNumberModel(getLinesBefore(), 0, 10, 1));
		addComponent(jEdit.getProperty(MESSAGE + "linesBefore"), before);
		after = new JSpinner(new SpinnerNumberModel(getLinesAfter(), 0, 10, 1));
		addComponent(jEdit.getProperty(MESSAGE + "linesAfter"), after);
	}
	@Override
	public void _save() {
		jEdit.setIntegerProperty(PROP + "linesBefore", Integer.valueOf(
			(Integer)before.getValue()));
		jEdit.setIntegerProperty(PROP + "linesAfter", Integer.valueOf(
			(Integer)after.getValue()));
	}
	public static int getLinesBefore()
	{
		return jEdit.getIntegerProperty(PROP + "linesBefore", 1);
	}
	public static int getLinesAfter()
	{
		return jEdit.getIntegerProperty(PROP + "linesAfter", 1);
	}
}
