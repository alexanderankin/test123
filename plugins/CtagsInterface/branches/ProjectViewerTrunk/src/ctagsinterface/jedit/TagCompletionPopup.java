package ctagsinterface.jedit;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.CompletionPopup;

import ctagsinterface.main.Tag;
import ctagsinterface.options.GeneralOptionPane;

@SuppressWarnings("serial")
public class TagCompletionPopup extends CompletionPopup {

	private JTextArea desc;
	
	public TagCompletionPopup(View view, Point location)
	{
		super(view, location);
		if (! GeneralOptionPane.getCompleteDesc())
			return;
		desc = new JTextArea();
		desc.append("The description\nof this item");
		desc.setBorder(BorderFactory.createEtchedBorder());
		Container c = getContentPane();
		JPanel p = new JPanel(new GridLayout(1, 0));
		p.add(c);
		p.add(desc);
		setContentPane(p);
	}
	
	public void setSelectedTag(Tag tag)
	{
		if (! GeneralOptionPane.getCompleteDesc())
			return;
		desc.setText(tag.getFile() + "\n" + tag.getLine());
	}
	
}
