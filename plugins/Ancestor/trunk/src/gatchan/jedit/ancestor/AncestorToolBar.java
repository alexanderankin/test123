package gatchan.jedit.ancestor;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorToolBar extends JToolBar
{
	private View view;
	private Component glue = Box.createGlue();

	/**
	 * Creates a new tool bar; orientation defaults to <code>HORIZONTAL</code>.
	 */
	public AncestorToolBar(View view)
	{
		this.view = view;
		add(glue);
		setFloatable(false);
	}

	void setBuffer(Buffer buffer)
	{
		String path = buffer.getPath();


		StringTokenizer tokenizer = new StringTokenizer(path, File.separatorChar + "/");
		int count = getComponentCount() - 1;

		int nbTokens = tokenizer.countTokens();
		if (nbTokens < count)
		{
			for (int i = 0;i<count - nbTokens;i++)
			{
				remove(0);
			}
		}
		else if (nbTokens > count)
		{
			for (int i = 0;i<nbTokens-count;i++)
			{
				add(new AncestorButton(), 0);
			}
		}
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while (tokenizer.hasMoreTokens())
		{
			AncestorButton button = (AncestorButton) getComponent(i);
			String token = tokenizer.nextToken();
			if (i != 0)
			{
				builder.append(path.charAt(builder.length()));
			}
			builder.append(token);
			button.setAncestor(new Ancestor(view, builder.toString(), token));
			i++;
		}
	}
}
