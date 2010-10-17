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
	private JSpinner before, after, delay;

	public OptionPane()
	{
		super("foldTools");
		setBorder(new EmptyBorder(5, 5, 5, 5));

		before = getContextLinesUi(getLinesBefore());
		addComponent(jEdit.getProperty(MESSAGE + "linesBefore"), before);
		after = getContextLinesUi(getLinesAfter());
		addComponent(jEdit.getProperty(MESSAGE + "linesAfter"), after);
		delay = new JSpinner(new SpinnerNumberModel(getFollowCaretDelay(), 0, 5000, 50));
		addComponent(jEdit.getProperty(MESSAGE + "followCaretDelay"), delay);
	}
	@Override
	public void _save() {
		setLinesBefore(before);
		setLinesAfter(after);
		jEdit.setIntegerProperty(PROP + "followCaretDelay", Integer.valueOf(
				(Integer)delay.getValue()));
	}
	public static JSpinner getContextLinesUi(int value)
	{
		return new JSpinner(new SpinnerNumberModel(value, 1, 10, 1));
	}
	public static int getLinesBefore()
	{
		return jEdit.getIntegerProperty(PROP + "linesBefore", 1);
	}
	public static void setLinesBefore(JSpinner spinner)
	{
		jEdit.setIntegerProperty(PROP + "linesBefore", ((Integer)spinner.getValue()).intValue());
	}
	public static int getLinesAfter()
	{
		return jEdit.getIntegerProperty(PROP + "linesAfter", 1);
	}
	public static void setLinesAfter(JSpinner spinner)
	{
		jEdit.setIntegerProperty(PROP + "linesAfter", ((Integer)spinner.getValue()).intValue());
	}
	public static int getFollowCaretDelay()
	{
		return jEdit.getIntegerProperty(PROP + "followCaretDelay", 500);
	}
}
