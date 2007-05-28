package gatchan.jedit.ancestor;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;

/**
 * @author Matthieu Casanova
 * @version $Id: Server.java,v 1.33 2007/01/05 15:15:17 matthieu Exp $
 */
public class Ancestor
{
	private View view;

	private String path;
	private String name;
	public Ancestor(View view, String path, String name)
	{
		this.view = view;
		this.path = path;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void doAction()
	{
		VFSBrowser.browseDirectory(view, path);
	}
}
