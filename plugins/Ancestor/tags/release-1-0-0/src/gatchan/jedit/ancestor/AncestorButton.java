package gatchan.jedit.ancestor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorButton extends JButton
{
	private Ancestor ancestor;

	/**
	 * Creates a button with no set text or icon.
	 */
	public AncestorButton()
	{
		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (ancestor != null)
				{
					ancestor.doAction();
				}
			}
		});
	}

	public void setAncestor(Ancestor ancestor)
	{
		this.ancestor = ancestor;
		setText(ancestor.getName());
	}


}
