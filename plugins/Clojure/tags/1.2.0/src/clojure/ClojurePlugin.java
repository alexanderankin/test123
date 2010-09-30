package clojure;
/**
 * @author Damien Radtke
 * class ClojurePlugin
 * The main class for the clojure plugin.
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
import org.gjt.sp.jedit.EditPlugin;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
//}}}
public class ClojurePlugin extends EditPlugin {

	public static final String home = EditPlugin.getPluginHome(
		ClojurePlugin.class).getPath();

	public static final String coreProp = "options.clojure.clojure-core-path";
	public static final String contribProp = "options.clojure.clojure-contrib-path";

	public static final String includedCore = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-core-jar"));

	public static final String includedContrib = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-contrib-jar"));
	
	private String installedCore = null;
	private String installedContrib = null;
	
	public void start() {
		// If core/contrib are not defined, set them to defaults
		if (jEdit.getProperty(coreProp) == null) {
			jEdit.setProperty(coreProp, includedCore);
		}

		if (jEdit.getProperty(contribProp) == null) {
			jEdit.setProperty(contribProp, includedContrib);
		}

		installedCore = getClojureCore();
		if (!installedCore.equals(includedCore)) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(includedCore), false);
			jEdit.addPluginJAR(installedCore);
		}

		installedContrib = getClojureContrib();
		if (!installedContrib.equals(includedContrib)) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(includedContrib), false);
			jEdit.addPluginJAR(installedContrib);
		}

		setVars();
		
		new Thread() {
			public void run() {
				if (jEdit.getPlugin("console.ConsolePlugin") != null) {
					File clojureCommand = new File(console.ConsolePlugin.getUserCommandDirectory(), "clojure.xml");
					if (!clojureCommand.exists()) {
						try {
							InputStream in = getClass().getResourceAsStream("/commands/clojure.xml");
							OutputStream out = new FileOutputStream(clojureCommand);
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
		}.start();
	}
	public void stop() {}

	/**
	 * Set the loaded embeddable clojure core jar
	 */
	public void setClojureCore(String path) {
		jEdit.setProperty(coreProp, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(installedCore), false);
		jEdit.addPluginJAR(path);
		installedCore = path;
	}

	/**
	 * Set the loaded embeddable clojure contrib jar
	 */
	public void setClojureContrib(String path) {
		jEdit.setProperty(contribProp, path);
		jEdit.removePluginJAR(jEdit.getPluginJAR(installedContrib), false);
		jEdit.addPluginJAR(path);
		installedContrib = path;
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
		return jEdit.getProperty(coreProp);
	}

	/**
	 * Returns the location of the clojure contrib jar
	 */
	public String getClojureContrib() {
		return jEdit.getProperty(contribProp);
	}

	/**
	 * Returns the paths of core and contrib respectively, separated by a path separator
	 * Ideal for setting environment paths and for use in the system shell
	 */
	public String getClojure() {
		String core = getClojureCore();
		String contrib = getClojureContrib();
		return core+File.pathSeparator+contrib;
	}

}
