package ctagsinterface.jedit;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.gui.CompletionPopup;

import ctagsinterface.jedit.TagCompletion.TagCandidates;
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
		p.add(new JScrollPane(desc));
		setContentPane(p);
	}
	
	
	public void setSelectedTag(Tag tag)
	{
		if (! GeneralOptionPane.getCompleteDesc())
			return;
		StringBuffer sb = new StringBuffer();
		sb.append(tag.getFile() + ":" + tag.getLine() + "\n");
		for (String ext: tag.getExtensions())
			sb.append(ext + ": " + tag.getExtension(ext) + "\n");
		desc.setText(sb.toString());
	}

	@Override
	protected void keyTyped(KeyEvent e)
	{
		char ch = e.getKeyChar();
		int index = ((TagCandidates) getCandidates()).indexForKey(ch);
		if (index >= 0 && index < getCandidates().getSize())
		{
			setSelectedIndex(index);
			if(doSelectedCompletion())
			{
				e.consume();
				dispose();
			}
			return;
		}
		super.keyTyped(e);
	}

}
