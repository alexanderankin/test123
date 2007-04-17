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
import java.awt.Window;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

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
import org.gjt.sp.jedit.PluginJAR;

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

	private static final HashMap PJAR_MAPPING = new HashMap();

	//{{{ +_openAllProjectFiles(View)_ : void
	/** If a project is currently active, open all its files. */
	public static void openAllProjectFiles(View view) {
		VPTProject active = ProjectViewer.getActiveProject(view);
		if (active != null) {
			for (Iterator i = active.getOpenableNodes().iterator(); i.hasNext(); ) {
				((VPTNode)i.next()).open();
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

	//{{{ +_getCurrentProject(View)_ : VPTProject
	/**
	 *	Returns the active project for the given view.
	 *
	 *	@return	The currently active project, or null if no project is active.
	 *
	 *	@deprecated	Use {@link ProjectViewer#getActiveProject(View)} instead.
	 */
	public static VPTProject getCurrentProject(View view) {
		return ProjectViewer.getActiveProject(view);
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
		ProjectViewer pv = ProjectViewer.getProjectViewer(v);
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

	//{{{ +_listToObjectCollection(String, PluginJAR, Class)_ : Collection
	/**
	 *	Creates a collection of instances from a comma-separated list of class
	 *	names. Classes that are not subclasses of "base" will be considered an
	 *	error and will not be instantiated.
	 *
	 *	@param	list	Comma-separated list of class names of which to create instances.
	 *	@param	jar		The plugin JAR that provides the class loader for the classes.
	 *	@param	base	The minimal base class to accept for the created objects.
	 */
	public static Collection listToObjectCollection(String list, PluginJAR jar, Class base) {
		if (list != null) {
			ArrayList objs = new ArrayList();
			StringTokenizer st = new StringTokenizer(list, ",");
			while (st.hasMoreTokens()) {
				String clazz = st.nextToken().trim();
				try {
					Class klazz = jar.getClassLoader().loadClass(clazz);
					if (base.isAssignableFrom(klazz)) {
						objs.add(klazz.newInstance());
						Collection classes = (Collection) PJAR_MAPPING.get(jar);
						if (classes == null) {
							classes = new HashSet();
							PJAR_MAPPING.put(jar, classes);
						}
						classes.add(clazz);
					} else {
						Log.log(Log.WARNING, PVActions.class,
							"Class is not instance of " + base.getName() + ": " + clazz);
					}
				} catch (Exception e) {
					Log.log(Log.WARNING, PVActions.class,
						"Error instantiating: " + clazz + ", " + e.getMessage());
				}
			}
			return objs;
		}
		return null;
	} //}}}

	//{{{ +_prune(Collection, PluginJAR)_ : Collection
	/**
	 *	Iterates through the objects in the given collection, removing
	 *	any objects that were loaded from the given plugin. This assumes
	 *	that the objects were loaded by calling the method
	 *	{@link #listToObjectCollection(String,PluginJAR,Class)}, so that
	 *	the relationship class -> pluginJAR is registered.
	 *
	 *	@return	A list with the removed objects (may be null).
	 *	@since	PV 2.1.1
	 */
	public static Collection prune(Collection c, PluginJAR jar) {
		Collection removed = null;
		Collection classes = (Collection) PJAR_MAPPING.get(jar);
		if (classes != null) {
			for (Iterator i = c.iterator(); i.hasNext();) {
				Object o = i.next();
				Class clazz = o.getClass();
				if (classes.contains(clazz.getName())) {
					i.remove();
					if (removed == null)
						removed = new ArrayList();
					removed.add(o);
				}
			}
		}
		return removed;
	} //}}}

	//{{{ #_cleanup(PluginJAR)_ : void
	/** Used internally to clean up resources when unloading other plugins. */
	protected static void cleanup(PluginJAR jar) {
		PJAR_MAPPING.remove(jar);
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

	//{{{ Base64 CoDec (See RFC 3548)

	/**
	 *	Base64 encoding alphabet. Maps a 6-bit value to a character in the
	 *	US-ASCII encoding.
	 */
	private final static byte[] alphabet = new byte[] {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
		'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3',
		'4', '5', '6', '7',	'8', '9', '+', '/'
	};

	/**
	 *	Base64 decoding alphabet. Maps a US-ASCII character value to a 6-bit
	 *	integer value. The table is shifted 43 positions, so that position 0
	 *	maps to character '+' (ASCII value 43). It's larger than the encoding
	 *	alphabet because the alphabet is not contiguous in the ASCII table.
	 */
	private final static byte[] reverse = new byte[] {
		0x3E, 0x00, 0x00, 0x00, 0x3F, 0x34, 0x35, 0x36,
		0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
		0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
		0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11,
		0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1A, 0x1B,
		0x1C, 0x1D, 0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23,
		0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x2B,
		0x2C, 0x2D, 0x2E, 0x2F, 0x30, 0x31, 0x32, 0x33
	};

	/** The character to use for padding the base64 encoded string. */
	private final static byte padding = '=';

	//{{{ +_encodeBase64(byte[])_ : byte[]
	/**
	 *	Encodes a byte array into a base64-encoded byte array. For more
	 *	details about the algorithm, see
	 *	<a href="http://www.faqs.org/rfcs/rfc3548.html">RFC 3548</a>. This
	 *	implementation does not create (76-byte) chunks for MIME encoding.
	 *
	 *	<p>Note by Marcelo Vanzin: I know about Jakarta Commons Codec. In fact,
	 *	I used it when my first implementation was not working well. But I don't
	 *	want to add another jar to the plugin, and I don't want to add a
	 *	dependency on the Jakarta Commons plugin (and add Codec there) either.</p>
	 */
	public static byte[] encodeBase64(byte[] data) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("Null or empty data!");
		}

		int octets, pads, idx;
		byte[] result;

		// the resulting array of chars
		idx = (data.length / 3) * 4;
		if (data.length % 3 != 0) {
			idx += 4;
		}
		result = new byte[idx];
		idx = 0;

		for (int i = 0; i < data.length; i++) {
			octets = 0x0;
			pads = 0;

			// first octet
			octets = (data[i] & 0x7F) << 16;
			if (data[i] < 0) {
				octets = octets | 0x800000;
			}
			i++;
			// second octet
			if (i < data.length) {
				octets = octets | ((data[i] & 0x7F) << 8);
				if (data[i] < 0) {
					octets = octets | 0x8000;
				}
				i++;
				// third octet
				if (i < data.length) {
					octets = octets | (data[i] & 0x7F);
					if (data[i] < 0) {
						octets = octets | 0x80;
					}
				} else {
					pads = 1;
				}
			} else {
				pads = 2;
			}

			// encodes into base64
			result[idx++] = alphabet[(octets >>> 18) & 0x3F];
			result[idx++] = alphabet[(octets >>> 12) & 0x3F];

			switch (pads) {
				case 2:
					result[idx++] = padding;
					result[idx++] = padding;
					break;

				case 1:
					result[idx++] = alphabet[(octets >>> 6) & 0x3F];
					result[idx++] = padding;
					break;

				default:
					result[idx++] = alphabet[(octets >>> 6) & 0x3F];
					result[idx++] = alphabet[octets & 0x3F];
					break;
			}
		}

		return result;
	} //}}}

	//{{{ +_decodeBase64(String)_ : byte[]
	/**
	 *	Decodes a Base64-encoded string into a byte array. Very little error
	 *	checking is done here - it's not guaranteed that an invalid base64
	 *	string will cause errors, since the alphabet is not being checked
	 *	for the sake of speed.
	 *
	 *	@throws	IllegalArgumentException	If the string length is not a multiple
	 *										of 4 (generally, lack of padding).
	 *	@throws	ArrayIndexOutOfBoundsException	If an unrecognized character is
	 *											found in the stream.
	 */
	public static byte[] decodeBase64(String data) {
		if (data.length() % 4 != 0) {
			throw new IllegalArgumentException("Wrong size for base64 stream.");
		}

		int octets, bCount, idx;
		byte[] result;

		bCount = (data.length() / 4) * 3;
		if (data.charAt(data.length() - 1) == padding) bCount--;
		if (data.charAt(data.length() - 2) == padding) bCount--;

		result = new byte[bCount];
		idx = 0;

		for (int i = 0; bCount > 0; i++) {
			octets = 0;

			// decodes the stream
			octets = (reverse[data.charAt(i) - 43]) << 18;
			octets = octets | ((reverse[data.charAt(++i) - 43]) << 12);

			if (bCount > 1) {
				octets = octets | ((reverse[data.charAt(++i) - 43]) << 6);
				if (bCount > 2) {
					octets = octets | (reverse[data.charAt(++i) - 43]);
				}
			}

			// breaks the bits into 3 bytes
			switch (bCount) {
				case 2:
					result[idx++] = (byte) (octets >>> 16);
					result[idx++] = (byte) (octets >>> 8);
					bCount -= 2;
					break;

				case 1:
					result[idx++] = (byte) (octets >>> 16);
					bCount --;
					break;

				default:
					result[idx++] = (byte) (octets >>> 16);
					result[idx++] = (byte) (octets >>> 8);
					result[idx++] = (byte) (octets & 0xFF);
					bCount -= 3;
					break;
			}
		}

		return result;
	} //}}}

	//{{{ +_serialize(Object)_ : String
	public static String serialize(Object o) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bytes);
			oos.writeObject(o);
			oos.flush();

			return new String(PVActions.encodeBase64(bytes.toByteArray()), "US-ASCII");
		} catch (Exception e) {
			Log.log(Log.ERROR, PVActions.class, "Error writing object to project file.");
			Log.log(Log.ERROR, PVActions.class, e);
			return null;
		}

	} //}}}

	//}}}

}

