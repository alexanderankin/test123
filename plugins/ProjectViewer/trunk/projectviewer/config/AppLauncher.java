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
 * GNU General Public License for more detaProjectTreeSelectionListenerils.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectviewer.config;

//{{{ Imports
import java.io.*;
import java.text.*;
import java.util.*;

import org.gjt.sp.util.Log;
import projectviewer.ProjectPlugin;
//}}}

/**
* @author payne
*
* To change this generated comment edit the template variable "typecomment":
* Window>Preferences>Java>Templates.
* To enable and disable the creation of type comments go to
* Window>Preferences>Java>Code Generation.
*/
public class AppLauncher {

	//{{{ Factory method & variable
	
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
	
	//{{{ getExec() method
	public String getExec(String fileExt) {

		if (fileExt.length() > 0)

			return (String)appCol.get(fileExt.trim());
		else

			return "";
	} //}}}

	//{{{ getAppList() method
	public Set getAppList() {
	   //return all the values
		return appCol.entrySet(); 
		
	} //}}}
	
	//{{{ addAppExt() method
	public void addAppExt(String fileExt, String execPath) {
		Log.log(Log.DEBUG, this, "Addin mapping: " + fileExt + " , " + execPath);
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
	public void launchApp(String ext,String sFileName) {
		String executable = (String)appCol.get(ext);
		if (appCol.containsKey(ext)) {
			Runtime rt = Runtime.getRuntime();
			String[] callAndArgs = { executable, sFileName};
			try {
			   Process child = rt.exec(callAndArgs);
			   child.waitFor();
			   System.out.println("Process exit code is: " + child.exitValue());
			} catch(java.io.IOException e) {
				System.err.println("IOException starting process!");
			} catch(InterruptedException e) {
			   System.err.println("Interrupted waiting for process!");
			}
		} else {
		
			//javax.swing.JOptionPane.showMessageDialog(null, "No application set for this extension!");	
			int retval = javax.swing.JOptionPane.showConfirmDialog(null, "Would you like to select one?", 
					"No application set for this extension!",  javax.swing.JOptionPane.YES_NO_OPTION);			
		     if (retval == javax.swing.JOptionPane.YES_OPTION)
				 	pickApp(ext);	
					// could retry launch here		
		}
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
	private void pickApp(String ext) {
		// Used for selected and executable file
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
		chooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		if (chooser.showDialog(null, "Choose") != javax.swing.JFileChooser.APPROVE_OPTION)
			return;
		try {
			
			//this.addAppExt(ext, chooser.getSelectedFile().getPath().replaceAll("\\","hellow"));
			
			this.addAppExt(ext, this.replaceString(chooser.getSelectedFile().getPath(), "\\", "/"));
			this.storeExts();
			
		} catch (Exception Excp) { 
			javax.swing.JOptionPane.showMessageDialog(null, "Problem setting application for:" + ext);
			
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
	
}

