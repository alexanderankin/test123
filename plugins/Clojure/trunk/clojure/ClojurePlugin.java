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
import java.io.IOException;
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
import org.gjt.sp.util.Log;
import org.gjt.sp.util.ThreadUtilities;
//}}}
public class ClojurePlugin extends EditPlugin {

	public static final String CMD_PATH = "/clojure/bsh";
	public static final String[] COMMANDS = new String[] { "clojure.xml" };

	public void start() {
		// Add the script path to beanshell's command path
		BeanShell.getNameSpace().addCommandPath(CMD_PATH, getClass());

		String clojurePath = jEdit.getProperty("clojure.path");
		if (clojurePath == null || clojurePath.equals("")) {
			jEdit.setProperty("clojure.path", MiscUtilities.constructPath(super.getPluginHome().getAbsolutePath(), "clojure.jar"));
		}

		File home = getPluginHome();
		if (!home.exists()) {
			home.mkdir();
		}

		setVars();

		ClojureDownloader.downloadIfMissing();

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
	 * If Console is installed, set some environment variables
	 * - Set CLOJURE to the path of the clojure jar
	 */
	public void setVars() {
		if (jEdit.getPlugin("console.ConsolePlugin") != null) {
			console.ConsolePlugin.setSystemShellVariableValue("CLOJURE", jEdit.getProperty("clojure.path"));
		}
	}

	/**
 	 * Returns the path of the installed Clojure jar, plus all libs, separated by the path separator.
	 */
	public String getClojure() {
		StringBuilder builder = new StringBuilder();
		builder.append(jEdit.getProperty("clojure.path"));

		String[] libs = jEdit.getProperty("clojure.libs").split(" ");
		for (String lib : libs) {
			if ("".equals(lib)) {
				continue;
			}
			builder.append(File.pathSeparator + jEdit.getProperty("clojure." + lib + ".path"));
		}

		return builder.toString();
	}
}
