package clojure;
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

	public static final String includedJarPath = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-jar"));

	private String installedJar = null;
	private String oldBin = null;

	public void start() {
		if (!jEdit.getProperty("options.clojure.install", "jar").equals("jar")) {
			setClojureJar();
		} else {
			installedJar = includedJarPath;
		}
		setVars();
	}
	public void stop() {}

	/**
	 * Set the loaded embeddable clojure jar
	 * @param path the path of the new clojure jar
	 */
	public void setClojureJar() {
		String path = getClojureJar();
		if (installedJar != null) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(installedJar), false);
		}
		jEdit.addPluginJAR(path);
		installedJar = path;
	}

	/**
	 * If Console is installed, set some environment variables
	 * - Set CLOJURE to the path of the clojure jar
	 */
	public void setVars() {
		console.ConsolePlugin.setSystemShellVariableValue("CLOJURE", getClojureJar());
	}

	/**
	 * Returns the location of the clojure jar
	 */
	public String getClojureJar() {
		String install = jEdit.getProperty("options.clojure.install", "jar");
		String path = (install.equals("jar") ? includedJarPath : 
			jEdit.getProperty("options.clojure.clojure-path"));
		return path;
	}

}
