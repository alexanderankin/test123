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
import java.io.File;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Properties;


import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import org.gjt.sp.util.Log;
import org.gjt.sp.jedit.jEdit;
import projectviewer.ProjectPlugin;
import projectviewer.ProjectViewer;
import projectviewer.gui.ModalJFileChooser;
//}}}

/**
 *	Holds information on what applications to use to open certain types of
 *	files, based on extension or complete file name.
 *
 *	@author		Matthew Payne
 *	@version	$Id$
 */
public class AppLauncher {

	//{{{ Singleton method & variable

	private static AppLauncher instance;

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
	}

	//}}}

	//{{{ Constructors

	public AppLauncher() {
		appCol = new TreeMap();
	}

	//}}}

	//{{{ Private members & variables

	private TreeMap appCol;

	//}}}

	//{{{ Public methods

	//{{{ getAppList() method
	public Set getAppList() {
	   //return all the values
		return appCol.entrySet();
	} //}}}

	//{{{ addAppExt() method
	public void addAppExt(String fileExt, String execPath) {
		if (fileExt.trim().length() > 0)
			appCol.put(fileExt.trim(), execPath);
	} //}}}

	//{{{ removeAppExt() method
	public void removeAppExt(String fileExt) {
		appCol.remove(fileExt);
	} //}}}

	//{{{ getCount() method
	public int getCount() {
	   return appCol.size();
	} //}}}

	//{{{ loadExts() method
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

	//{{{ storeExts() method
	public void storeExts() throws IOException {

		Properties props = new Properties();
		PrintWriter out = new PrintWriter(
			new OutputStreamWriter(
				ProjectPlugin.getResourceAsOutputStream("fileassocs.properties")
			) );

		for (Iterator iter = appCol.keySet().iterator(); iter.hasNext(); ) {
			Object key = iter.next();
			Object value = appCol.get(key);
			out.println(key + "=" + value);
		}

		out.println("");
		out.close();
	} //}}}

	//{{{ launchApp() method
	// was private, but gets called from TreeContextMenuListener.java::LaunchExternal()
	public void launchApp(File f, ProjectViewer viewer) {
		String ext = getFileExtension(f.getName());
		String executable = (String) appCol.get(ext);
		if (executable == null) {
			if (JOptionPane.showConfirmDialog(viewer,
					jEdit.getProperty("projectviewer.launcher.no_app", new Object[] { ext }),
					jEdit.getProperty("projectviewer.launcher.no_app_title"),
					javax.swing.JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				 executable = pickApp(ext, viewer);
			 } else {
				 return;
			 }
		}
		if (executable != null) {
			Runtime rt = Runtime.getRuntime();
			String[] callAndArgs = { executable, f.getAbsolutePath() };
			try {
			   rt.exec(callAndArgs);
			} catch(java.io.IOException ioe) {
				JOptionPane.showMessageDialog(viewer,
					jEdit.getProperty("projectviewer.launcher.io_error", new Object[] { ioe.getMessage() }),
					jEdit.getProperty("projectviewer.error"),
					JOptionPane.ERROR_MESSAGE);
			}
		}
	} //}}}

	//{{{ copy() method
	/** Copies the data from another AppLauncher into this one. */
	public void copy(AppLauncher other) {
		appCol.clear();
		for (Iterator it = other.appCol.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			appCol.put(key, other.appCol.get(key));
		}
	} //}}}

	//{{{ getAppName(String) method
	/**
	 *	Returns the application name associated to the given file extension.
	 */
	public String getAppName(File f) {
		return (String) appCol.get(getFileExtension(f.getName()));
	} //}}}

	//}}}

	//{{{ Private methods

	//{{{ getFileExtension() method
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

	//{{{ replaceString() method
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

	//{{{ pickApp() method
	/**
	 *	Prompts the user for an application to run, and returns the path to the
	 *	executable file.
	 */
	private String pickApp(String ext, ProjectViewer viewer) {
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

}

