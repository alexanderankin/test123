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
package projectviewer.config;

//{{{ Imports
import java.awt.Component;

import java.io.File;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import gnu.regexp.RE;
import gnu.regexp.REException;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.io.VFSManager;

import projectviewer.ProjectPlugin;
import projectviewer.gui.ModalJFileChooser;
//}}}

/**
 *	Holds information on what applications to use to open certain types of
 *	files, based on user-configured file name globs.
 *
 *	@author		Matthew Payne
 *	@version	$Id$
 */
public class AppLauncher {

	//{{{ Singleton method & variable

	private static AppLauncher instance;
	static {
		// make sure ProjectViewerConfig is loaded before the instance is
		// initialized.
		ProjectViewerConfig.getInstance();
	}

	//{{{ +_getInstance()_ : AppLauncher
	public static synchronized AppLauncher getInstance() {
		if (instance == null) {
			instance = new AppLauncher();
			try {
				instance.loadExts();
			} catch (IOException ioe) {
				Log.log(Log.ERROR, instance, ioe);
			}
		}
		return instance;
	} //}}}

	//}}}

	//{{{ +AppLauncher() : <init>
	public AppLauncher() {
		appCol = new TreeMap();
	} //}}}

	//{{{ Private members & variables

	private TreeMap appCol;

	//}}}

	//{{{ Public methods

	//{{{ +getAppList() : Set
	public Set getAppList() {
	   //return all the values
		return appCol.entrySet();
	} //}}}

	//{{{ +addAppExt(String, String) : void
	public void addAppExt(String fileExt, String execPath) {
		if (fileExt.trim().length() > 0) {
			try {
				RE re = new ComparableRE(fileExt);
				appCol.put(re, execPath);
			} catch (REException re) {
				Log.log(Log.ERROR, this, re);
			}
		}
	} //}}}

	//{{{ +removeAppExt(String) : void
	public void removeAppExt(String fileExt) {
		appCol.remove(fileExt);
	} //}}}

	//{{{ +getCount() : int
	public int getCount() {
	   return appCol.size();
	} //}}}

	//{{{ +loadExts() : void
	/** load extension properties from file **/
	public void loadExts() throws IOException {

 		Properties props = new Properties();
		InputStream inprops =
			ProjectPlugin.getResourceAsStream("fileassocs.properties");

		appCol.clear();

		if (inprops != null) {
			props.load(inprops);

			for (Iterator iter = props.keySet().iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				String value = props.getProperty(key);
				this.addAppExt(key, value);
			}

			inprops.close();
		}
	} //}}}

	//{{{ +storeExts() : void
	public void storeExts() throws IOException {
		PrintWriter out = new PrintWriter(
			new OutputStreamWriter(
				ProjectPlugin.getResourceAsOutputStream("fileassocs.properties")
			) );

		for (Iterator iter = appCol.keySet().iterator(); iter.hasNext(); ) {
			ComparableRE key = (ComparableRE) iter.next();
			Object value = appCol.get(key);
			out.println(key.glob + "=" + value);
		}

		out.println("");
		out.close();
	} //}}}

	//{{{ +launchApp(File, Component) : void
	/**
	 *	@deprecated	Use {@link #launchApp(String, Component)
	 *	launchApp(String, Component)} instead.
	 */
	public void launchApp(File f, Component comp) {
		launchApp(f.getAbsolutePath(), comp);
	} //}}}

	//{{{ +launchApp(String, Component) : void
	/**
	 *	Launches an external app depending on the extension of the path
	 *	provided, passing the path as an argument to the executable.
	 *
	 *	@since	PV 2.1.0
	 */
	public void launchApp(String path, Component comp) {
		String executable = getAppName(path);
		if (executable == null) {
			String ext = "*." + getFileExtension(path);
			if (JOptionPane.showConfirmDialog(comp,
				jEdit.getProperty("projectviewer.launcher.no_app", new Object[] { ext }),
					jEdit.getProperty("projectviewer.launcher.no_app_title"),
					javax.swing.JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				 executable = pickApp(ext, comp);
			 } else {
				 return;
			 }
		}
		if (executable != null) {
			Runtime rt = Runtime.getRuntime();
			String[] callAndArgs = { executable, path };
			try {
			   rt.exec(callAndArgs);
			} catch(java.io.IOException ioe) {
				JOptionPane.showMessageDialog(comp,
					jEdit.getProperty("projectviewer.launcher.io_error", new Object[] { ioe.getMessage() }),
					jEdit.getProperty("projectviewer.error"),
					JOptionPane.ERROR_MESSAGE);
			}
		}
	} //}}}

	//{{{ +copy(AppLauncher) : void
	/** Copies the data from another AppLauncher into this one. */
	public void copy(AppLauncher other) {
		appCol.clear();
		for (Iterator it = other.appCol.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			appCol.put(key, other.appCol.get(key));
		}
	} //}}}

	//{{{ +getAppName(File) : String
	/**
	 *	Returns the application name associated to the given file extension.
	 *
	 *	@deprecated	Use {@link #getAppName(String) getAppName(String)} instead.
	 */
	public String getAppName(File f) {
		return getAppName(f.getAbsolutePath());
	} //}}}

	//{{{ +getAppName(String) : String
	/**
	 *	Returns the application name associated to the given file extension.
	 *	The file path can be any URL recognized by jEdit's VFSManager.
	 *
	 *	@since PV 2.1.0
	 */
	public String getAppName(String path) {
		String name = VFSManager.getVFSForPath(path).getFileName(path);
		for (Iterator i = appCol.keySet().iterator(); i.hasNext(); ) {
			RE re = (RE) i.next();
			if (re.isMatch(name)) {
				return (String) appCol.get(re);
			}
		}

		return null;
	} //}}}

	//}}}

	//{{{ Private methods

	//{{{ -getFileExtension(String) : String
	/**
	 *	Returns the file's extension, or the file name if no extension can be
	 *	recognized.
	 *
	 *@param  filename
	 *@return	   The fileExtension value
	 */
	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1 || dotIndex == fileName.length() - 1)
			return fileName;
		return fileName.substring(dotIndex + 1);
	} //}}}

	//{{{ -_replaceString(String, String, String)_ : String
	private static String replaceString(String aSearch, String aFind, String aReplace)
	{ /* MP could not get regex replace to work.
		so I am including this function for now */
		String result = aSearch;
		if (result != null && result.length() > 0) {
			int a = 0;
			int b = 0;
			while (true) {
				a = result.indexOf(aFind, b);
				if (a != -1) {
					result = result.substring(0, a) + aReplace + result.substring(a + aFind.length());
					b = a + aReplace.length();
				}
				else
				break;
			}
		}
		return result;
	} //}}}

	//{{{ -pickApp(String, Component) : String
	/**
	 *	Prompts the user for an application to run, and returns the path to the
	 *	executable file.
	 */
	private String pickApp(String ext, Component comp) {
		// Used for selected and executable file
		JFileChooser chooser = new ModalJFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showDialog(null, jEdit.getProperty("projectviewer.launcher.choose_app")) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		String exec = replaceString(chooser.getSelectedFile().getPath(), "\\", "/");
		this.addAppExt(ext, exec);

		try {
			this.storeExts();
		} catch (Exception e) {
			Log.log(Log.ERROR, this, e);
		}

		return exec;
	} //}}}

	//}}}

	//{{{ -class _ComparableRE_
	/**
	 *	An RE that is comparable by comparing the glob string used to
	 *	create it. Stores the glob internally for ordering and for
	 *	saving the AppLauncher information to the config file.
	 */
	private static class ComparableRE extends RE implements Comparable {

		private String glob;

		//{{{ +ComparableRE(String) : <init>
		public ComparableRE(String glob) throws REException {
			super(MiscUtilities.globToRE(glob));
			this.glob = glob;
		} //}}}

		//{{{ +compareTo(Object) : int
		public int compareTo(Object o) {
			try {
				return this.glob.compareTo(((ComparableRE)o).glob);
			} catch (ClassCastException cce) {
				return -1;
			}
		} //}}}

		//{{{ +toString() : String
		public String toString() {
			return glob;
		} //}}}

	} //}}}

}

