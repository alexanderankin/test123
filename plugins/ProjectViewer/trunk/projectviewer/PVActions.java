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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.PluginJAR;

import projectviewer.vpt.VPTFile;
import projectviewer.vpt.VPTNode;
import projectviewer.vpt.VPTProject;

import projectviewer.action.Action;
import projectviewer.action.EditProjectAction;
import projectviewer.action.LaunchBrowserAction;

import projectviewer.config.ProjectViewerConfig;
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

	//{{{ +_getCurrentProject(View)_ : VPTProject
	/**
	 *	Returns the active project. If no viewer is opened for the given view,
	 *	returns the last known active project (config.getLastProject()). Is one
	 *	exists but is currently being used in "All Projects" mode, return null.
	 *
	 *	@return	The currently active project, or null if no project is active and
	 *			ProjectViewerConfig.getLastProject() returns null.
	 */
	public static VPTProject getCurrentProject(View view) {
		ProjectViewer viewer = ProjectViewer.getViewer(view);
		if (viewer != null) {
			if (viewer.getRoot().isProject()) {
				return (VPTProject) viewer.getRoot();
			} else {
				return null;
			}
		} else {
			String pName = ProjectViewerConfig.getInstance().getLastProject();
			return (pName != null) ?
					ProjectManager.getInstance().getProject(pName) : null;
		}
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

	//{{{ +_pvActionWrapper(Action, View)_ : void
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
		if (!force && viewer != null && viewer.getCurrentTree() != null) {
			a.setViewer(viewer);
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
			jEdit.getProperty("projectviewer.error.cannot_exec_action"));
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
					Class lstnr = jar.getClassLoader().loadClass(clazz);
					if (base.isAssignableFrom(lstnr)) {
						objs.add(lstnr.newInstance());
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

		for (int i = 0; i < data.length(); i++) {
			octets = 0;

			// decodes the stream
			octets = (reverse[data.charAt(i) - 43]) << 18;
			i++;
			octets = octets | ((reverse[data.charAt(i) - 43]) << 12);
			i++;

			if (bCount > 1) {
				octets = octets | ((reverse[data.charAt(i) - 43]) << 6);
				i++;
				if (bCount > 2) {
					octets = octets | (reverse[data.charAt(i) - 43]);
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

	//}}}

}

