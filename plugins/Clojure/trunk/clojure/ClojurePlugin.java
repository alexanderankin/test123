package clojure;
/**
 * @author Damien Radtke
 * class ClojurePlugin
 * The main class for the clojure plugin
 * Handles all loading/unloading of clojure jars
 */
//{{{ Imports
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.gjt.sp.jedit.BeanShell;
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.ThreadUtilities;
//}}}
public class ClojurePlugin extends EditPlugin {

	public static final String CORE = "options.clojure.clojure-core-path";
	public static final String CONTRIB = "options.clojure.clojure-contrib-path";

	public static final String INCLUDED_CORE = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-core-jar"));

	public static final String INCLUDED_CONTRIB = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-contrib-jar"));
	
	public static final String CMD_PATH = "/clojure/bsh";
	public static final String[] COMMANDS = new String[] { "clojure.xml" };
	
	private static String INSTALLED_CORE = null;
	private static String INSTALLED_CONTRIB = null;
	
	public void start() {
		// Add the script path to beanshell's command path
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());
		
		// These lines just check to see which libraries are installed
		// If none, then the included jars are used
		if (jEdit.getProperty(CORE) == null) {
			jEdit.setProperty(CORE, INCLUDED_CORE);
		}
		if (jEdit.getProperty(CONTRIB) == null) {
			jEdit.setProperty(CONTRIB, INCLUDED_CONTRIB);
		}

		INSTALLED_CORE = getClojureCore();
		if (!INSTALLED_CORE.equals(INCLUDED_CORE)) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(INCLUDED_CORE), false);
			jEdit.addPluginJAR(INSTALLED_CORE);
		}
		INSTALLED_CONTRIB = getClojureContrib();
		if (!INSTALLED_CONTRIB.equals(INCLUDED_CONTRIB)) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(INCLUDED_CONTRIB), false);
			jEdit.addPluginJAR(INSTALLED_CONTRIB);
		}

		setVars();
		
		// This background thread installs the commando files, if necessary
		ThreadUtilities.runInBackground(new Runnable() {
			public void run() {
				if (jEdit.getPlugin("console.ConsolePlugin") != null) {
					for (int i = 0; i < COMMANDS.length; i++) {
						String command = COMMANDS[i];
						File f = new File(console.ConsolePlugin.getUserCommandDirectory(), command);
						if (!f.exists()) {
							try {
								InputStream in = getClass().getResourceAsStream("/clojure/commands/"+command);
								OutputStream out = new FileOutputStream(f);
								IOUtilities.copyStream(null, in, out, false);
								IOUtilities.closeQuietly(in);
								IOUtilities.closeQuietly(out);
								console.ConsolePlugin.rescanCommands();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
	}
	public void stop() {}

	/**
	 * Set the loaded embeddable clojure core jar
	 */
	public void setClojureCore(String path) {
		jEdit.setProperty(CORE, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(INSTALLED_CORE), false);
		jEdit.addPluginJAR(path);
		INSTALLED_CORE = path;
	}

	/**
	 * Set the loaded embeddable clojure contrib jar
	 */
	public void setClojureContrib(String path) {
		jEdit.setProperty(CONTRIB, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(INSTALLED_CONTRIB), false);
		jEdit.addPluginJAR(path);
		INSTALLED_CONTRIB = path;
	}

	/**
	 * If Console is installed, set some environment variables
	 * - Set CLOJURE to the path of the clojure jar
	 */
	public void setVars() {
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			console.ConsolePlugin.setSystemShellVariableValue("CLOJURE", getClojure());
		}
	}

	/**
	 * Returns the location of the clojure core jar
	 */
	public String getClojureCore() {
		return jEdit.getProperty(CORE);
	}

	/**
	 * Returns the location of the clojure contrib jar
	 */
	public String getClojureContrib() {
		return jEdit.getProperty(CONTRIB);
	}

	/**
	 * Returns the paths of core and contrib respectively, separated by a path separator
	 * Ideal for setting environment paths and for use in the system shell
	 */
	public String getClojure() {
		String core = getClojureCore();
		String contrib = getClojureContrib();
		return core + File.pathSeparator + contrib;
	}

}
