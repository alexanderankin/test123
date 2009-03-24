/*
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer;

//{{{ Imports
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Window;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;

import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSFile;
import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import projectviewer.action.Action;
import projectviewer.action.LaunchBrowserAction;
//}}}

/**
 *  A collection of actions accessible through jEdit's Action mechanism, and
 *	other utility methods that may be interesting for interacting with the
 *	plugin.
 *
 *	@author		Marcelo Vanzin
 *  @version	$Id$
 */
public final class PVActions {

	//{{{ +_openAllProjectFiles(View)_ : void
	/** If a project is currently active, open all its files. */
	public static void openAllProjectFiles(View view) {
		VPTProject active = ProjectViewer.getActiveProject(view);
		if (active != null) {
			for (VPTNode n : active.getOpenableNodes()) {
				n.open();
			}
		}
	} //}}}

	//{{{ +_closeAllProjectFiles(View)_ : void
	/** If a project is currently active, close all its files. */
	public static void closeAllProjectFiles(View view) {
		VPTProject active = ProjectViewer.getActiveProject(view);
		if (active != null) {
			Buffer[] bufs = jEdit.getBuffers();
			for (int i = 0; i < bufs.length; i++) {
				if (active.getChildNode(bufs[i].getPath()) != null) {
					jEdit.closeBuffer(view, bufs[i]);
				}
			}
		}
	} //}}}

	//{{{ +_removeAllProjectFiles(View)_ : void
	/** Removes all the children from the project active in the view. */
	public static void removeAllProjectFiles(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer == null) return;
		VPTNode sel = viewer.getRoot();
		if (!sel.isRoot()) {
			((VPTProject)sel).removeAllChildren();
			ProjectManager.getInstance().saveProject((VPTProject)sel);
			ProjectViewer.nodeStructureChanged((VPTProject)sel);
		}
	} //}}}

	//{{{ +_focusProjectViewer(View)_ : void
	/**
	 *	If a project viewer window is open in the current view, make sure it's
	 *	visible and them focus the active tree; if it's not open, open it and
	 *	then focus the active tree. If no active tree exists in the dockable,
	 *	focus will not be explicitly requested (even though the dockable
	 *	will be opened if it was closed, potentially requesting the focus).
	 *
	 *	@since	PV 2.1.0
	 */
	public static void focusProjectViewer(final View view) {
		jEdit.getAction("projectviewer").invoke(view);
		ProjectViewer pv = ProjectViewer.getViewer(view);
		while (pv == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
				// ignore
			}
			pv = ProjectViewer.getViewer(view);
		}

		if (pv.getCurrentTree() != null) {
			requestFocus(pv.getCurrentTree());
		}
	} //}}}

	//{{{ +_requestFocus(Component)_ : void
	/**
	 *	Requests the focus for the given component. If the component is not
	 *	in the active window, then activates the window first, then focus
	 *	the component.
	 */
	public static void requestFocus(final Component c) {
		Window w = (Window) SwingUtilities.getAncestorOfClass(Window.class, c);
		if (!w.isFocused()) {
			w.setVisible(true);
		}

		SwingUtilities.invokeLater(
			new Runnable() {
				//{{{ +run() : void
				public void run() {
					c.requestFocusInWindow();
				} //}}}
			}
		);
	} //}}}

	//{{{ +_launchCurrentBuffer(View)_ : void
	/**
	 *	Tries to launch the current buffer in the configured browser for
	 *	project viewer. Works only if the current buffer has a file that
	 *	is part of the current active project for the given view.
	 */
	public static void launchCurrentBuffer(View view) {
		VPTProject curr = null;
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer != null) {
			if (viewer.getRoot().isProject()) {
				curr = (VPTProject) viewer.getRoot();
			} else {
				viewer.setStatus(jEdit.getProperty("projectviewer.no_active_project"));
				return;
			}
		} else {
			return;
		}

		VPTNode file;
		if ( (file = curr.getChildNode(view.getBuffer().getPath())) != null) {
			if (file.isFile()) {
				LaunchBrowserAction lba = new LaunchBrowserAction((VPTFile)file);
				lba.actionPerformed(null);
			}
		} else {
			viewer.setStatus(jEdit.getProperty("projectviewer.path_not_in_project"));
		}

	} //}}}

	//{{{ +_focusActiveBuffer(View, VPTNode)_ : boolean
	/**
	 *	Check the current active node in the given view and see if
	 *	a node representing the current active buffer is a descendant
	 *	of that node. If it is, focus that node in the tree, expanding
	 *	the tree as necessary.
	 *
	 *	@param	v	The view to ask for the active buffer.
	 *	@param	where	Where to look for the node (null will start the
	 *					search at the active node for the given view).
	 */
	public static boolean focusActiveBuffer(View v, VPTNode where) {
		if (where == null)
			where = ProjectViewer.getActiveNode(v);
		if (where != null) {
			if (where.isProject()) {
				VPTNode n = ((VPTProject)where).getChildNode(v.getBuffer().getPath());
				if (n != null) {
					ProjectViewer pv = ProjectViewer.getViewer(v);
					if (pv != null) {
						JTree current = pv.getCurrentTree();
						TreeNode[] ns = ((DefaultTreeModel)current.getModel()).getPathToRoot(n);
						TreePath path = new javax.swing.tree.TreePath(ns);
						current.setSelectionPath(path);
						current.scrollPathToVisible(path);
					}
					return true;
				}
			} else {
				// group
				for (int i = 0; i < where.getChildCount(); i++)
					if (focusActiveBuffer(v, (VPTNode) where.getChildAt(i)))
						return true;
			}
		}
		return false;
	} //}}}

	//{{{ +_newFile(View)_ : void
	public static void newFile(View v) {
		ProjectViewer pv = ProjectViewer.getViewer(v);
		if (pv == null)
			return;

		VPTNode n = pv.getSelectedNode();
		if (n != null) {
			String path = n.getNodePath();
			if (path != null) {
				File f = new File(path);
				if (!f.isDirectory()) {
					f = f.getParentFile();
					if (!f.isDirectory()) {
						return; // give up
					}
				}
				jEdit.newFile(v, f.getAbsolutePath());
			}
			else {
				jEdit.newFile(v);
			}
		} else {
			jEdit.newFile(v);
		}
	} //}}}

	//{{{ +_pvActionWrapper(Action, View, boolean)_ : void
	/**
	 *	<p>Used to execute a PV action from the jEdit action mechanism. This is
	 *	meant to be called from beanshell code in the action.xml file. It
	 *	will check if the action accepts the current selected tree nodes
	 *	before executing it by using the
	 *	{@link Action#prepareForNode(VPTNode) prepareForNode()} method and then
	 *	checking the visibility of the component returned by
	 *	{@link Action#getMenuItem() getMenuItem()}, unless the "force" parameter
	 *	is set to true. When forcing the execution, no checks are made, so it
	 *	may be the case that the viewer doesn't exist yet and there's no
	 *	active project for the current view.</p>
	 *
	 *	<p>When executing the action, a null is passed as the event in the
	 *	actionPerformed method, so this won't work for actions that expect
	 *	a proper event notification.<p>
	 */
	public static void pvActionWrapper(Action a, View v, boolean force) {
		ProjectViewer viewer = ProjectViewer.getViewer(v);
		a.setViewer(viewer);
		if (!force && viewer != null && viewer.getCurrentTree() != null) {
			a.getMenuItem();
			switch (viewer.getCurrentTree().getSelectionCount()) {
				case 1: // single selection
					a.prepareForNode(viewer.getSelectedNode());
					break;

				default: // multiple selection or no selection
					a.prepareForNode(null);
					break;
			}
			if (a.getMenuItem().isVisible()) {
				a.actionPerformed(null);
				return;
			}
		} else {
			a.actionPerformed(null);
			return;
		}
		v.getStatus().setMessageAndClear(
			jEdit.getProperty("projectviewer.error.cannot_exec_action",
							  new Object[] { a.getText() }));
	} //}}}

	//{{{ +_writeXML(String, Writer)_ : void
	/**
	 *	Writes the given String to the output, taking care to escape any
	 *	caharcters that need escaping.
	 *
	 *	@since	PV 2.1.0
	 */
	public static void writeXML(String str, Writer out) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
				case '<':
					out.write("&lt;");
					break;
				case '>':
					out.write("&gt;");
					break;
				case '&':
					out.write("&amp;");
					break;
				case '"':
					out.write("&quot;");
					break;
				case '\'':
					out.write("&apos;");
					break;
				case '\n':
					out.write("&#10;");
					break;
				case '\r':
					out.write("&#13;");
					break;
				default:
					out.write(str.charAt(i));
			}
		}
	} //}}}

	//{{{ +_swingInvoke(Runnable)_ : void
	/**
	 *	Invokes the given runnable in the AWT Event Dispatcher Thread.
	 */
	public static void swingInvoke(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException ie) {
				// not gonna happen
				Log.log(Log.ERROR, PVActions.class, ie);
			} catch (java.lang.reflect.InvocationTargetException ite) {
				// not gonna happen
				Log.log(Log.ERROR, PVActions.class, ite);
			}
		}
	}
	//}}}

	//{{{ +_newXMLParser()_ : SAXParser
	/**
	 *	Returns a new SAX 2.0 parser; convenience method that catches
	 *	all exceptions and prints a log message in case they occur
	 *	(returning null).
	 *
	 *	@since	PV 2.1.3.4
	 */
	public static XMLReader newXMLReader(DefaultHandler handler) {
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			if (handler != null) {
				reader.setContentHandler(handler);
				reader.setDTDHandler(handler);
				reader.setEntityResolver(handler);
				reader.setErrorHandler(handler);
			}
			return reader;
		} catch (Exception e) {
			Log.log(Log.ERROR, PVActions.class, e);
			return null;
		}
	} //}}}

	//{{{ +_clipText()_ : String
	/**
	 * Returns a string that fits in the given bounds based on the
	 * given metrics. If the given string doesn't fit, it will be
	 * clipped either at the start or the end, being prepended or
	 * appended with an ellipsis to signal the clipping.
	 *
	 * @param	base		The base string.
	 * @param	maxwidth	Width within which the string should fit.
	 * @param	metrics		Font metrics to use.
	 * @param	clipEnd		true if should clip the end of the string.
	 *
	 * @return A clipped version of the base string, or the base one
	 *         if it fits.
	 *
	 * @since	PV 3.0.0
	 */
	public static String clipText(String base,
								  int maxWidth,
								  FontMetrics metrics,
								  boolean clipEnd)
	{
		int width = metrics.stringWidth(base);

		if (width > maxWidth) {
			int i;
			maxWidth -= metrics.stringWidth("...");
			for (i = base.length(); i > 1 && width > maxWidth; i--) {
				width = (clipEnd)
					  ? metrics.stringWidth(base.substring(0, i))
					  : metrics.stringWidth(base.substring(base.length() - i));
			}

			if (clipEnd) {
				return base.substring(0, i) + "...";
			} else {
				return  "..." + base.substring(base.length() - i);
			}
		}
		return base;
	} //}}}

}

