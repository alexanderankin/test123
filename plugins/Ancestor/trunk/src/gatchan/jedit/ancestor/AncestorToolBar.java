package gatchan.jedit.ancestor;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class AncestorToolBar extends JToolBar
{
	private View view;
	private Component glue = Box.createGlue();
	private final LinkedList<String> list = new LinkedList<String>();

	public AncestorToolBar(View view)
	{
		this.view = view;
		add(glue);
		setFloatable(false);
	}

	void setBuffer(Buffer buffer)
	{
		String path = buffer.getPath();
		VFS _vfs = VFSManager.getVFSForPath(path);
		list.clear();
		while (true)
		{
			list.addFirst(path);
			String parent = _vfs.getParentOfPath(path);
			if (path == null || MiscUtilities.pathsEqual(path,parent))
				break;
			path = parent;
		}
		int count = getComponentCount() - 1;

		int nbTokens = list.size();
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
		int nb = list.size();
		for (String fileName : list)
		{
			AncestorButton button = (AncestorButton) getComponent(i);
			button.setAncestor(new Ancestor(view, fileName, _vfs.getFileName(fileName)));
			i++;
			button.setEnabled(nb != i);
		}
	}
}
