package foldTools;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

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
		setContext(false);
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
}
