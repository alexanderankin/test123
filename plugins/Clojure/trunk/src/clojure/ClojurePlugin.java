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

	public static final String includedCore = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-core-jar"));
	public static final String includedContrib = MiscUtilities.constructPath(
		jEdit.getSettingsDirectory(),
		"jars/"+jEdit.getProperty("options.clojure.clojure-contrib-jar"));

	private String installedCore = includedCore;
	private String installedContrib = includedContrib;

	public void start() {
		if (jEdit.getProperty("options.clojure.clojure-core-path") == null) {
			jEdit.setProperty("options.clojure.clojure-core-path", includedCore);
		}
		if (jEdit.getProperty("options.clojure.clojure-contrib-path") == null) {
			jEdit.setProperty("options.clojure.clojure-contrib-path", includedContrib);
		}

		if (!getClojureCore().equals(includedCore)) {
			setClojureCore(getClojureCore());
		}
		if (!getClojureContrib().equals(includedContrib)) {
			setClojureContrib(getClojureContrib());
		}

		setVars();
	}
	public void stop() {}

	/**
	 * Set the loaded embeddable clojure core jar
	 */
	public void setClojureCore(String path) {
		if (installedCore != null) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(installedCore), false);
		}
		jEdit.setProperty("options.clojure.clojure-core-path", path);
		jEdit.addPluginJAR(path);
		installedCore = path;
	}

	/**
	 * Set the loaded embeddable clojure contrib jar
	 */
	public void setClojureContrib(String path) {
		if (installedContrib != null) {
			jEdit.removePluginJAR(jEdit.getPluginJAR(installedContrib), false);
		}
		jEdit.setProperty("options.clojure.clojure-contrib-path", path);
		jEdit.addPluginJAR(path);
		installedContrib = path;
	}

	/**
	 * If Console is installed, set some environment variables
	 * - Set CLOJURE to the path of the clojure jar
	 */
	public void setVars() {
		console.ConsolePlugin.setSystemShellVariableValue("CLOJURE", getClojure());
	}

	/**
	 * Returns the location of the clojure core jar
	 */
	public String getClojureCore() {
		return jEdit.getProperty("options.clojure.clojure-core-path");
	}

	/**
	 * Returns the location of the clojure contrib jar
	 */
	public String getClojureContrib() {
		return jEdit.getProperty("options.clojure.clojure-contrib-path");
	}

	public String getClojure() {
		String core = getClojureCore();
		String contrib = getClojureContrib();
		return core+File.pathSeparator+contrib;
	}

}
