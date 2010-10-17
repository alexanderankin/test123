package foldTools;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.textarea.JEditTextArea;

import foldTools.FoldContext.LineContext;

@SuppressWarnings("serial")
public class FoldContextDockable extends JPanel
{
	private View view;
	private JTextPane textPane;
	private FoldContext context;
	private JButton update;
	private JCheckBox followCaret;
	private CaretListener caretListener;
	private Timer followCaretTimer;
	private ActionListener updateContext;

	public FoldContextDockable(View view)
	{
		this.view = view;
		context = new FoldContext(view);
		setLayout(new BorderLayout());
		textPane = new JTextPane();
		textPane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() < 2)
					return;
				FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
				int line = e.getY() / fm.getHeight();
				LineContext lc = context.getLineContext(line);
				if (lc == null)
					return;
				jumpTo(lc.line);
			}
		});
		add(new JScrollPane(textPane), BorderLayout.CENTER);
		JPanel p = new JPanel();
		add(p, BorderLayout.NORTH);
		update = new JButton("Update");
		p.add(update);
		update.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setContext(true);
			}
		});
		followCaret = new JCheckBox("Follow caret", false);
		p.add(followCaret);
		followCaret.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				followCaretChanged();
			}
		});
		setContext(false);
		updateContext = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setContext(true);
			}
		};
		followCaretTimer = new Timer(0, updateContext);	// will be configured later
		followCaretTimer.setRepeats(false);
	}
	public void setContext(boolean update)
	{
		if (update)
			context.update(view);
		textPane.setText(context.toString());
	}
	public void jumpTo(int line)
	{
		JEditTextArea ta = view.getTextArea();
		if (line >= 0 && line < ta.getLineCount())
			ta.setCaretPosition(ta.getLineStartOffset(line));
	}
	private void followCaretChanged()
	{
		if (followCaret.isSelected() == (caretListener != null))
			return;
		JEditTextArea ta = view.getTextArea();
		if (followCaret.isSelected())
		{
			if (OptionPane.getFollowCaretDelay() > 0)
				updateContext.actionPerformed(null);
			caretListener = new CaretListener()
			{
				public void caretUpdate(CaretEvent e)
				{
					int delay = OptionPane.getFollowCaretDelay();
					followCaretTimer.setInitialDelay(delay);
					followCaretTimer.restart();
				}
			};
			ta.addCaretListener(caretListener);
		}
		else
		{
			ta.removeCaretListener(caretListener);
			caretListener = null;
		}
	}
}
