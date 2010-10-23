package foldTools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
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
	private DefaultListModel handlerModel;
	private JList handlers;
	private JButton addHandler, removeHandler, editHandler;

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
		addComponent(new JLabel(jEdit.getProperty(MESSAGE + "compositeHandlers")));
		handlerModel = new DefaultListModel();
		handlers = new JList(handlerModel);
		handlers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addComponent(new JScrollPane(handlers));
		JPanel p = new JPanel();
		addHandler = new JButton(jEdit.getProperty(MESSAGE + "addHandler"));
		p.add(addHandler);
		removeHandler = new JButton(jEdit.getProperty(MESSAGE + "removeHandler"));
		p.add(removeHandler);
		editHandler = new JButton(jEdit.getProperty(MESSAGE + "editHandler"));
		p.add(editHandler);
		addComponent(p);
		addHandler.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addHandler();
			}
		});
		removeHandler.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeHandler();
			}
		});
		editHandler.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				editHandler();
			}
		});
	}
	private void addHandler()
	{
		CompositeHandlerDialog d = new CompositeHandlerDialog(
			jEdit.getActiveView());
		d.setVisible(true);
		if (d.wasCancelled())
			return;
		handlerModel.addElement(d.getHandler());
	}
	private void removeHandler()
	{
		int i = handlers.getSelectedIndex();
		if (i >= 0)
			handlerModel.remove(i);
	}
	private void editHandler()
	{
		int i = handlers.getSelectedIndex();
		if (i < 0)
			return;
		CompositeHandlerDialog d = new CompositeHandlerDialog(
			jEdit.getActiveView(), (HandlerItem)handlerModel.get(i));
		d.setVisible(true);
		if (d.wasCancelled())
			return;
		handlerModel.set(i, d.getHandler());
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
