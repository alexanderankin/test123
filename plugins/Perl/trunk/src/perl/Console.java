package perl;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.gjt.sp.jedit.View;

import perl.PerlProcess.LineHandler;

@SuppressWarnings("serial")
public class Console extends JPanel
{
	private JComboBox combo;
	private DefaultComboBoxModel model;
	private JTextArea textArea;
	private Map<PerlProcess, StringBuilder> consoles;
	private View view;
	public Console(View view)
	{
		this.view = view;
		model = new DefaultComboBoxModel();
		combo = new JComboBox(model);
		setLayout(new BorderLayout());
		add(combo, BorderLayout.NORTH);
		combo.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.DESELECTED)
					return;
				if (! (e.getItem() instanceof PerlProcess))
					return;
				setSession((PerlProcess) e.getItem());
			}
		});
		textArea = new JTextArea();
		add(textArea, BorderLayout.CENTER);
		consoles = new HashMap<PerlProcess, StringBuilder>();
	}
	public void openSession(final PerlProcess p)
	{
		consoles.put(p, new StringBuilder());
		model.addElement(p);
		model.setSelectedItem(p);
		setSession(p);
		p.getOutput().addHandler(new LineHandler() {
			@Override
			public void handle(String line)
			{
				append(p, line);
			}
		});
	}
	public void setSession(PerlProcess p)
	{
		textArea.setText(consoles.get(p).toString());
	}
	public void append(PerlProcess p, String s)
	{
		consoles.get(p).append(s);
		if (combo.getSelectedItem() == p)
			textArea.append(s);
	}
}
