package foldTools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.gjt.sp.jedit.View;

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
}
